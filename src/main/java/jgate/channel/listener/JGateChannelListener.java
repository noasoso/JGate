package jgate.channel.listener;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jgate.channel.protocol.JGateTcpChannelInitializer;
import jgate.channel.protocol.JGateUdpChannelInitializer;
import jgate.channel.protocol.ProtocolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGateChannelListener {
    private final static Logger log = LoggerFactory.getLogger(JGateChannelListener.class);

    //协议
    private ProtocolType protocol;

    //监听端口
    private int port;

    //tcp启动类
    private ServerBootstrap tcpBootstrap;

    //udp启动类
    private Bootstrap udpBootstrap;

    //listen channel
    private Channel listenChannel;

    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;

    private int maxIdleTimeSeconds;
    boolean openDebugLogging;

    public JGateChannelListener(ProtocolType protocol, int port,int maxIdleTimeSeconds,boolean openDebugLogging)
    {
        this.protocol = protocol;
        this.port = port;

        this.maxIdleTimeSeconds = maxIdleTimeSeconds;
        this.openDebugLogging = openDebugLogging;

    }

    public int listen(){
        try {
            if (this.protocol == ProtocolType.PROTOCOL_TYPE_TCP){
                parentGroup = new NioEventLoopGroup(1);// 只有一个监听线程
                childGroup = new NioEventLoopGroup();// 默认个数的IO线程

                tcpBootstrap = new ServerBootstrap();
                tcpBootstrap.group(parentGroup, childGroup);
                tcpBootstrap.channel(NioServerSocketChannel.class);
                tcpBootstrap.option(ChannelOption.SO_BACKLOG, 100);
                tcpBootstrap.childHandler(new JGateTcpChannelInitializer(maxIdleTimeSeconds,openDebugLogging));
                tcpBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
                listenChannel = tcpBootstrap.bind(this.port).sync().channel();

            }
            else if (this.protocol == ProtocolType.PROTOCOL_TYPE_UDP){
                childGroup = new NioEventLoopGroup();// 默认个数的IO线程

                udpBootstrap = new Bootstrap();
                udpBootstrap.group(childGroup);
                udpBootstrap.channel(NioDatagramChannel.class);
                udpBootstrap.handler( new JGateUdpChannelInitializer(openDebugLogging) );

                listenChannel = udpBootstrap.bind(this.port).sync().channel();
            }

            log.info("listen on port:" + this.port + ",protocol:" +this.protocol + ",openDebugLogging:" + this.openDebugLogging);
            return 0;
        }
        catch (Exception e){
            log.error("listen error:" +e.toString());
            return -1;
        }

    }

    public boolean isListening(){
        return this.listenChannel != null;
    }

    public void awaitClose(){
        try {
            listenChannel.closeFuture().sync();
        }
        catch (Exception e){
            log.error("awaitClose error:" +e.toString());
        }
    }
}
