package jgate;

import org.apache.commons.configuration.XMLConfiguration;
import java.util.HashMap;

public class Config {
    public static XMLConfiguration xmlConfig;

    //是否开启netty调试日志
    public static boolean enabaleNettyLogging = false;

    //redis配置
    public static String redisHost = "127.0.0.1";
    public static int redisPort = 6379;


    //订阅channel
    public static String subChannel = "jgate";

    //发布channel
    public static HashMap<Integer,String> pubChannels = new HashMap<>();


}
