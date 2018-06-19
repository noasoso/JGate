package jgate;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        byte[] pack = messagePack.toBytes();

        MessagePack unpack = new MessagePack();
        boolean ret = unpack.fromBytes(pack);
        log.info("ret:" + ret);

    }

}
