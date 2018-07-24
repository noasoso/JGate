package jgate;

import org.apache.commons.configuration.XMLConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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

    //调试端口
    public static List<Integer> debugPorts = new ArrayList<>();

    //解析器参数
    public static int maxFrameLength = 5*1024*1024;//最大长度
    public static int lengthFieldOffset = 0;//长度的偏移量
    public static int lengthFieldLength = 4;//长度的长度
    public static int lengthAdjustment = 0;//长度的修正值
    public static int initialBytesToStrip = 4;//解包时跳过的字节数


}
