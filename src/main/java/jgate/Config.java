package jgate;

import org.apache.commons.configuration.XMLConfiguration;
import java.util.HashMap;

public class Config {
    public static XMLConfiguration xmlConfig;

    //redis配置
    public static String REDIS_HOST = "127.0.0.1";
    public static int REDIS_PORT = 6379;


    //订阅channel
    public static String SUB_CHANNEL = "jgate";

    //发布channel
    public static HashMap<Integer,String> PUB_CHANNELS = new HashMap<>();


}
