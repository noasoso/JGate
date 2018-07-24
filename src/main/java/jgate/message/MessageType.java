package jgate.message;

public enum  MessageType {
    MESSAGE_TYPE_CONNECT,//建立连接
    MESSAGE_TYPE_DATA,//数据包
    MESSAGE_TYPE_CLOSE,//断开连接
    MESSAGE_TYPE_TIMEOUT,//超时
}
