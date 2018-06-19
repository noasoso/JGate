package jgate;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
//import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
//import io.netty.util.CharsetUtil;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGateTcpChannelInitializer extends ChannelInitializer<SocketChannel>{
    private final static Logger log = LoggerFactory.getLogger(JGateTcpChannelInitializer.class);

    //超过该时间则会主动断开连接,单位秒
    private int maxIdleTimeSeconds = 0;

    //是否开启调试日志
    private boolean openDebugLogging = false;


    public JGateTcpChannelInitializer(int maxIdleTimeSeconds,boolean openDebugLogging){
        this.maxIdleTimeSeconds = 0;
        this.openDebugLogging = openDebugLogging;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pl = ch.pipeline();

        // 用于调试，netty会在控制台输出详细日志
        if (this.openDebugLogging){
	    	pl.addLast(new LoggingHandler(LogLevel.INFO));
        }

        // 解码器
        int maxFrameLength = 1024 * 1024 * 5;
        int lengthFieldOffset = 0;
        int lengthFieldLength = 4;
        int lengthAdjustment = 0;
        int initialBytesToStrip = 4;

        pl.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip));

		pl.addLast("bytesDecoder", new ByteArrayDecoder());
//        pl.addLast("stringDecoder",new StringDecoder(CharsetUtil.UTF_8));

        if (maxIdleTimeSeconds > 0) {
            pl.addLast(new IdleStateHandler(maxIdleTimeSeconds, 0, 0));
        }

        // 业务处理器
        pl.addLast(new JGateChannelHandler());// 业务逻辑处理
    }
}
