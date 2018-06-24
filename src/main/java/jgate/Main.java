package jgate;

import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class Main
{
    private final static Logger log = LoggerFactory.getLogger(Main.class);
    public static void main( String[] args )
    {
        log.info("main start");

        JGateChannelListenerManager manager = JGateChannelListenerManager.getInstance();
        manager.addListener("ddz",ProtocolType.PROTOCOL_TYPE_TCP,18800,5,true);
//        manager.addListener("ddz",ProtocolType.PROTOCOL_TYPE_UDP,18800,5,true);

        Util.sleep(1000*5);
        String message = "hello world";
        MessageManager.getInstance().addPubMessage(Config.CHANNEL_GATE,"cid111",MessageType.MESSAGE_TYPE_CLOSE,message.getBytes(CharsetUtil.UTF_8));

        log.info("waitKey...");
        Util.readKey();

    }
}
