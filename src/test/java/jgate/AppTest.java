package jgate;

import static org.junit.Assert.assertTrue;

import io.netty.util.CharsetUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    private final static Logger log = LoggerFactory.getLogger(AppTest.class);

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testUtil(){
        log.debug(Util.getNowDateTime());


    }


    @Test
    public void test(){
        MessagePack messagePack = new MessagePack("cid","ch",MessageType.MESSAGE_TYPE_CLOSE,null);
        byte[] pack = messagePack.serialize();

        MessagePack unpack = new MessagePack();
        boolean ret = unpack.parse(pack);
        log.info("ret:" + ret);

    }

    @Test
    public void testPublish(){
        try {
            String message = "hello from jgate";
            MessagePack messagePack = new MessagePack("cid","ch",MessageType.MESSAGE_TYPE_CLOSE,message.getBytes(CharsetUtil.UTF_8));
            byte[] pack = messagePack.serialize();

            Jedis jedis = new Jedis(Config.redisHost,Config.redisPort);
//            jedis.publish("ddz".getBytes(CharsetUtil.UTF_8),pack);
            jedis.publish("ddz",new String(pack,CharsetUtil.UTF_8));
        }
        catch (Exception e){
            log.error(e.toString());
        }

    }

}
