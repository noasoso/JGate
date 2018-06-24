package jgate;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

        try {
            //加载配置文件
            Config.xmlConfig = new XMLConfiguration("config.xml");
            Config.xmlConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            Config.SUB_CHANNEL = Config.xmlConfig.getString("subscriber.channel");
            Config.REDIS_HOST = Config.xmlConfig.getString("redis.host");
            Config.REDIS_PORT = Integer.parseInt(Config.xmlConfig.getString("redis.port"));

            List<String> items = Config.xmlConfig.getList("publisher.channel");
            for (String item : items){
                String[] tokens = item.split("->");
                int port = Integer.parseInt( tokens[0] );
                String channel = tokens[1];
                Config.PUB_CHANNELS.put(port,channel);
            }

            for (Integer port :Config.PUB_CHANNELS.keySet()){
                String channel = Config.PUB_CHANNELS.get(port);

                JGateChannelListenerManager manager = JGateChannelListenerManager.getInstance();
                manager.addListener(channel,ProtocolType.PROTOCOL_TYPE_TCP,port,5,true);
                //manager.addListener("ddz",ProtocolType.PROTOCOL_TYPE_UDP,18800,5,true);

                log.info("listen on:" + port + ",pubChannel:" + channel);
            }


        }
        catch (ConfigurationException e){
            log.error("e:" + e.toString());
        }
        catch (Exception e){
            log.error("main error:" + e);
        }



        log.info("waitKey...");
        Util.readKey();

    }
}
