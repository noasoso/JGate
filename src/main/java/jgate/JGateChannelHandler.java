package jgate;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class JGateChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(JGateChannelHandler.class);

    private ChannelHandlerContext context = null;

    private String cid = "";

    private String pubChannel = "";

    /**
     * 获取表示唯一客户端的Id
     * @return
     */
    public String getCid(){
        return this.cid;
    }

    public String getPubChannel(){
        return this.pubChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            super.channelActive(ctx);

            this.context = ctx;

            this.cid = ctx.channel().remoteAddress() + ":" + ctx.channel().localAddress();

            if (ctx.channel().remoteAddress() != null){
                //客户端建立连接,如果remoteAddress为null，则为服务端监听

                log.info("channelActive remoteAddress:" + ctx.channel().remoteAddress()+",localAddress:"+ctx.channel().localAddress());
//            MessageManager.getInstance().pushMessage(MessageType.MESSAGE_TYPE_CONNECT,null,this);

                //建立连接
                JGateChannelHandlerManager.getInstance().addChannelHandler(this);

                InetSocketAddress address = (InetSocketAddress) context.channel().localAddress();
                this.pubChannel = Config.pubChannels.get(address.getPort());

                MessageManager.getInstance().addPubMessage(getPubChannel(), getCid(),MessageType.MESSAGE_TYPE_CONNECT,null);
            }


        }
        catch (Exception e){
            log.error("channelActive error:" + e.toString());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof byte[]){
                MessageManager.getInstance().addPubMessage(getPubChannel(),getCid(),MessageType.MESSAGE_TYPE_DATA,(byte[]) msg);
            }
        }
        catch (Exception e){
            log.error("channelRead e:" + e.toString());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        JGateChannelHandlerManager.getInstance().removeChannelHandler(getCid());
        MessageManager.getInstance().addPubMessage(getPubChannel(),getCid(),MessageType.MESSAGE_TYPE_CLOSE,null);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 给客户端发送消息,直接发送byte数组
     *
     * @param buffer
     */
    public void send(byte[] buffer) {
        if (this.context != null) {
            if (buffer != null && buffer.length > 0) {
                context.write(Unpooled.copiedBuffer(Util.intToByteArray(buffer.length)));
                context.write(Unpooled.copiedBuffer(buffer));
                context.flush();
            }
        }
    }



}
