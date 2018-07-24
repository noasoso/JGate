package jgate.channel.protocol;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jgate.channel.handler.JGateChannelHandler;

public class JGateUdpChannelInitializer extends ChannelInitializer<DatagramChannel> {

    //是否开启调试日志
    private boolean openDebugLogging = false;

    public JGateUdpChannelInitializer(boolean openDebugLogging){
        this.openDebugLogging = openDebugLogging;
    }

    @Override
    protected void initChannel(DatagramChannel ch) throws Exception {
        ChannelPipeline pl = ch.pipeline();

        // 用于调试，netty会在控制台输出详细日志
        if (this.openDebugLogging){
            pl.addLast(new LoggingHandler(LogLevel.INFO));
        }

        pl.addLast("bytesDecoder", new ByteArrayDecoder());


        // 业务处理器
        pl.addLast(new JGateChannelHandler());// 业务逻辑处理

    }
}
