package jgate;

import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class MessagePack{
    //客户端的唯一Id
    public String cid;

    //接收响应的通道
    public String channel;

    //消息类型
    public MessageType type;

    //消息内容
    public byte[] message = null;

    public String getUTF8Message(){
        if (message == null){
            return "";
        }
        else {
            return new String(message,CharsetUtil.UTF_8);
        }
    }

    public MessagePack(){

    }

    public MessagePack(String cid,String channel,MessageType type,byte[] message){
        this.cid = cid;
        this.channel = channel;
        this.type = type;
        this.message = message;
    }

    public byte[] serialize(){
        int len = 2 + (2 + this.cid.length()) + (2 + this.channel.length());
        if (this.message != null){
            len += (4 + this.message.length);
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(len).order(ByteOrder.BIG_ENDIAN);

        //类型
        byteBuffer.putShort((short) this.type.ordinal());

        //cid
        byteBuffer.putShort((short)this.cid.length());
        byteBuffer.put(this.cid.getBytes(CharsetUtil.UTF_8));

        //channel
        byteBuffer.putShort((short)this.channel.length());
        byteBuffer.put(this.channel.getBytes(CharsetUtil.UTF_8));

        //data
        if (this.message != null){
            byteBuffer.putInt(this.message.length);
            byteBuffer.put(this.message);
        }

        return byteBuffer.array();
    }

    public boolean parse(byte[] bytes){

        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            byteBuffer.order(ByteOrder.BIG_ENDIAN);

            //类型
            int typeIndex = byteBuffer.getShort();
            this.type = MessageType.values()[typeIndex];

            //cid
            int len = byteBuffer.getShort();
            byte[] array = new byte[len];
            byteBuffer.get(array);
            this.cid = new String(array,CharsetUtil.UTF_8);

            //channel
            len = byteBuffer.getShort();
            array = new byte[len];
            byteBuffer.get(array);
            this.channel = new String(array,CharsetUtil.UTF_8);

            //data
            if (byteBuffer.hasRemaining()){
                len = byteBuffer.getInt();
                this.message = new byte[len];
                byteBuffer.get(this.message);


            }

            return true;
        }
        catch (Exception e){

        }
        return false;
    }
}
