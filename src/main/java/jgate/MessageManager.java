package jgate;

import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageManager {
    private static final Logger log = LoggerFactory.getLogger(MessageManager.class);

    //是否处于循环中
    private boolean loop = true;

    private Jedis jedis = new Jedis(Config.REDIS_HOST,Config.REDIS_PORT);

    // 发布队列，接收来自客户端的消息
    private BlockingQueue<MessagePack> pubQueue = new LinkedBlockingQueue<>();

    //发布线程
    private Thread pubThread = new Thread(new Runnable() {
        @Override
        public void run() {
            log.info("pubThread start");

            while (loop){
                try {
                    MessagePack mp = pubQueue.poll(5, TimeUnit.SECONDS);
                    if (mp == null){
                        continue;
                    }

                    publish(mp);
                }
                catch (Exception e){
                    log.error("pubThread error:" + e.toString());
                }
            }

        }
    });

    //订阅线程
    private Thread subThread = new Thread(new Runnable() {
        @Override
        public void run() {
            log.info("subThread start");

            class Subscriber extends BinaryJedisPubSub{
                public void onMessage(byte[] channel, byte[] message) {
                    try {
                        MessagePack mp = new MessagePack();
                        mp.parse(message);

                        log.info("onMessage,ch:" + new String(channel,CharsetUtil.UTF_8)
                                + ",type:" + mp.type + ",ch:" + mp.channel + ",cid:" + mp.cid
                                + ",msg:" + new String(mp.message,CharsetUtil.UTF_8));

                        JGateChannelHandler channelHandler = JGateChannelHandlerManager.getInstance().getChannelHandler(mp.cid);
                        if (channelHandler != null){
                            channelHandler.send(mp.message);
                        }
                    }
                    catch (Exception e){
                        log.error("Subscriber error:" + e.toString());
                    }

                }
            }

            Jedis jedis1 = new Jedis(Config.REDIS_HOST,Config.REDIS_PORT);

            Subscriber subscriber = new Subscriber();
            jedis1.subscribe(subscriber,Config.CHANNEL_GATE.getBytes(CharsetUtil.UTF_8));

            log.info("subThread end");
        }
    });

    // 接收来自redis发布的消息
    private BlockingQueue<MessagePack> subQueue = new LinkedBlockingQueue<>();

    //单例模式
    private static MessageManager instance = null;
    public static synchronized MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
            instance.init();
        }
        return instance;
    }

    public int init(){
        pubThread.start();
        subThread.start();
        return 0;
    }

    public void addPubMessage(String channel, String jid,MessageType type,byte[] message){
        MessagePack mp = new MessagePack(jid,channel,type,message);

        synchronized (pubQueue){
            pubQueue.add(mp);
        }

    }

    public void publish(MessagePack mp){
        try {
            jedis.publish(Config.CHANNEL_DDZ.getBytes(CharsetUtil.UTF_8),mp.serialize());
            log.debug("publish now");
        }
        catch (Exception e){
            log.error("publish error:" +e.toString());
        }
    }

    public void addSubMessage(String channel, String jid,MessageType type,byte[] message){
        MessagePack mp = new MessagePack(jid,channel,type,message);

        synchronized (subQueue){
            subQueue.add(mp);
        }
    }

    public void stop(){
        this.loop = false;
    }
}
