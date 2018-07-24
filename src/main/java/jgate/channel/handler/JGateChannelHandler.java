package jgate.channel.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import jgate.Config;
import jgate.Util;
import jgate.message.MessageManager;
import jgate.message.MessageType;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class JGateChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(JGateChannelHandler.class);

    private ChannelHandlerContext context = null;

    private String cid = "";

    private String pubChannel = "";

    private boolean isDebug = false;

    /**
     * 获取表示唯一客户端的Id
     * @return
     */
    public String getCid(){
        return this.cid;
    }

    private String getPubChannel(){
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

                if (Config.debugPorts.contains(address.getPort())){
                    this.isDebug = true;
                }

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
                byte[] buffer = (byte[])msg;
                if (!this.isDebug){
                    MessageManager.getInstance().addPubMessage(getPubChannel(),getCid(),MessageType.MESSAGE_TYPE_DATA,buffer);
                }
                else {
                    //解析channel，格式为:一个字节的长度 + channel
                    int chLen = buffer[0];
                    if (chLen > 0 && chLen < 256 && chLen < (buffer.length +1)){
                        String ch = new String(ArrayUtils.subarray(buffer,1,chLen + 1), CharsetUtil.UTF_8);
                        log.debug("ch:" + ch);
                        MessageManager.getInstance().addPubMessage(ch ,getCid(),MessageType.MESSAGE_TYPE_DATA,ArrayUtils.subarray(buffer,chLen + 1,buffer.length));
                    }
                }
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
        MessageManager.getInstance().addPubMessage(getPubChannel(),getCid(), MessageType.MESSAGE_TYPE_CLOSE,null);
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
                if (Config.appendLengthField == 4){
                    //添加4个字节的包体长度
                    context.write(Unpooled.copiedBuffer(Util.intToByteArray(buffer.length)));
                }
                else if (Config.appendLengthField == 2){
                    //添加2个字节的包体长度
                    context.write(Unpooled.copiedBuffer(Util.shortToByteArray((short) buffer.length)));
                }

                context.write(Unpooled.copiedBuffer(buffer));
                context.flush();
            }
        }
    }



}
