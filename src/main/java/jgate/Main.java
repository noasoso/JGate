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

    /**
     * 解析配置文件
     * @return
     */
    public static int parseConfig(){
        try {
            //加载配置文件
            Config.xmlConfig = new XMLConfiguration("config.xml");
            Config.xmlConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            Config.enabaleNettyLogging = Config.xmlConfig.getBoolean("enableNettyLogging");
            Config.subChannel = Config.xmlConfig.getString("subscriber.channel");
            Config.redisHost = Config.xmlConfig.getString("redis.host");
            Config.redisPort = Integer.parseInt(Config.xmlConfig.getString("redis.port"));

            List<String> items = Config.xmlConfig.getList("publisher.channel");
            for (String item : items){
                String[] tokens = item.split("->");
                int port = Integer.parseInt( tokens[0] );
                String channel = tokens[1];
                Config.pubChannels.put(port,channel);
            }

            List<String> debugPorts = Config.xmlConfig.getList("publisher.debugPort");
            for (String port : debugPorts){
                Config.debugPorts.add(Integer.parseInt(port));
            }
        }
        catch (ConfigurationException e){
            log.error("e:" + e.toString());
            return -1;
        }
        catch (Exception e){
            log.error("main error:" + e);
            return -2;
        }

        return 0;
    }
    public static void main( String[] args )
    {
        log.info("main start ");

        try {
            //加载配置文件
            if (parseConfig() != 0){
                return;
            }

            //监听客户端连接
            for (Integer port :Config.pubChannels.keySet()){
                String channel = Config.pubChannels.get(port);

                JGateChannelListenerManager manager = JGateChannelListenerManager.getInstance();
                manager.addListener(channel,ProtocolType.PROTOCOL_TYPE_TCP,port,5,Config.enabaleNettyLogging);
                //manager.addListener("ddz",ProtocolType.PROTOCOL_TYPE_UDP,18800,5,true);

                log.info("listen on:" + port + ",pubChannel:" + channel);
            }


        }
        catch (Exception e){
            log.error("main error:" + e);
        }

        //阻塞等待
        JGateChannelListenerManager.getInstance().awaitClose();

        log.info("main exit");
    }
}
