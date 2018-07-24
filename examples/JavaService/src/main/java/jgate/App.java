package jgate;

import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;

/**
 * Hello world!
 *
 */
public class App
{
    private static final Logger log = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {

        log.debug( "Hello World!" );

        String redisHost = "jgate.qipai.io";
        int redisPort = 6379;
        String subChannel = "test_1";
        try {
            Jedis jedisPub = new Jedis(redisHost,redisPort);

            Thread subThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    log.info("subThread start");

                    class Subscriber extends BinaryJedisPubSub {
                        public void onMessage(byte[] channel, byte[] message) {
                            try {
                                log.debug("ch:" + channel.length +",msg:" + message);

                                MessagePack mp = new MessagePack();
                                mp.parse(message);

                                if (mp.type == MessageType.MESSAGE_TYPE_CONNECT){
                                    log.info("MESSAGE_TYPE_CONNECT,cid:" + mp.cid);
                                }
                                else if (mp.type == MessageType.MESSAGE_TYPE_CLOSE){
                                    log.info("MESSAGE_TYPE_CLOSE,cid:" + mp.cid);
                                }
                                else if (mp.type == MessageType.MESSAGE_TYPE_DATA){
                                    log.info("onMessage,ch:" + new String(channel, CharsetUtil.UTF_8)
                                            + ",type:" + mp.type + ",ch:" + mp.channel + ",cid:" + mp.cid
                                            + ",msg:" + new String(mp.message,CharsetUtil.UTF_8));


                                    //给客户端回应
                                    String resp = "reply from java service";
                                    mp.message = resp.getBytes(CharsetUtil.UTF_8);
                                    jedisPub.publish(mp.channel.getBytes(CharsetUtil.UTF_8),mp.serialize());
                                }
                                else {
                                    log.info("unknown type:" + mp.type + ",cid:" + mp.cid);
                                }

                            }
                            catch (Exception e){
                                log.error("Subscriber error:" + Util.getExceptionStack(e));
                            }

                        }
                    }

                    Jedis jedisSub = new Jedis(redisHost,redisPort);

                    Subscriber subscriber = new Subscriber();
                    jedisSub.subscribe(subscriber,subChannel.getBytes(CharsetUtil.UTF_8));

                    log.info("subThread end");
                }
            });

            subThread.start();

            Thread.sleep(Long.MAX_VALUE);
        }
        catch (Exception e){
            log.error(e.toString());
        }

    }
}
