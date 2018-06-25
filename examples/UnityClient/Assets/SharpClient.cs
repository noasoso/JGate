using System;
using System.IO;
using System.Net;
using System.Net.Sockets;

public enum Error
{
    Ok,
    Timeout,
    Exception,
    Disconnect,
}
public class SharpClient
{
    private TcpClient client = null;

    NetworkStream stream = null;
    private BinaryReader reader = null;
    private BinaryWriter writer = null;


    private const int MAX_READ = 8192;
    private byte[] readBuffer = new byte[MAX_READ];
    private MemoryStream messageBuffer = new MemoryStream();



    #region 网络接口回调

    public Action OnConnect;
    public Action<byte[]> OnMessage;
    public Action OnClose;

    #endregion



    // Use this for initialization
    public SharpClient()
    {

    }

    /// <summary>
    /// 建立连接
    /// </summary>
    /// <param name="host"></param>
    /// <param name="port"></param>
    public void Connect(string host, int port)
    {
        client = null;
        try
        {
            IPAddress[] address = Dns.GetHostAddresses(host);
            if (address.Length == 0)
            {
                return;
            }
            if (address[0].AddressFamily == AddressFamily.InterNetworkV6)
            {
                client = new TcpClient(AddressFamily.InterNetworkV6);
            }
            else
            {
                client = new TcpClient(AddressFamily.InterNetwork);
            }
            client.NoDelay = true;
            client.BeginConnect(host, port, new AsyncCallback(OnConnect_), null);
        }
        catch (Exception e)
        {
            client = null;
        }

        if (client == null)
        {
            Close();
        }
    }

    /// <summary>
    /// 关闭连接
    /// </summary>
    public void Close()
    {
        if (client != null)
        {
            if (client.Connected) client.Close();
            client = null;
        }

        if (reader != null)
        {
            reader.Close();
        }

        if (writer != null)
        {
            writer.Close();
        }

        if (OnClose != null)
        {
            OnClose();
        }

    }


    /// <summary>
    /// 连接上服务器
    /// </summary>
    void OnConnect_(IAsyncResult asr)
    {
        stream = client.GetStream();
        reader = new BinaryReader(messageBuffer);
        writer = new BinaryWriter(stream);

        if (OnConnect != null)
        {
            OnConnect();
        }

        stream.BeginRead(readBuffer, 0, MAX_READ, new AsyncCallback(OnRead), null);
    }

    /// <summary>
    /// 发送消息
    /// </summary>
    public void Write(byte[] message)
    {
        if (message == null)
        {
            return;
        }

        using (MemoryStream ms = new MemoryStream())
        {
            ms.Position = 0;
            BinaryWriter writer = new BinaryWriter(ms);
            int len = message.Length;
            writer.Write(IPAddress.HostToNetworkOrder(len));
            writer.Write(message);
            writer.Flush();

            byte[] payload = ms.ToArray();
            stream.BeginWrite(payload, 0, payload.Length, new AsyncCallback(OnWrite), null);
        }
    }

    /// <summary>
    /// 读取消息
    /// </summary>
    void OnRead(IAsyncResult ar)
    {
        try
        {
            int bytesRead = stream.EndRead(ar);
            if (bytesRead <= 0)
            {//包尺寸有问题，断线处理
                Close();
                return;
            }
            ParseMessage(readBuffer, bytesRead);

            Array.Clear(readBuffer, 0, readBuffer.Length);
            stream.BeginRead(readBuffer, 0, MAX_READ, new AsyncCallback(OnRead), null);
        }
        catch (Exception ex)
        {
            //PrintBytes();
            Close();
        }
    }

    /// <summary>
    /// 向链接写入数据流
    /// </summary>
    void OnWrite(IAsyncResult ar)
    {
        try
        {
            stream.EndWrite(ar);
        }
        catch (Exception ex)
        {
        }
    }

    /// <summary>
    /// 解析消息
    /// </summary>
    void ParseMessage(byte[] bytes, int length)
    {
        messageBuffer.Seek(0, SeekOrigin.End);
        messageBuffer.Write(bytes, 0, length);
        messageBuffer.Seek(0, SeekOrigin.Begin);

        //需要循环解析消息，消息长度4个字节
        while (messageBuffer.Length - messageBuffer.Position > 4)
        {
            int messageLen = IPAddress.NetworkToHostOrder(reader.ReadInt32());
            if (messageBuffer.Length - messageBuffer.Position >= messageLen)
            {
                byte[] message = reader.ReadBytes(messageLen);
                if (OnMessage != null)
                {
                    try
                    {
                        OnMessage(message);
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
            else
            {
                messageBuffer.Position -= 4;
                break;
            }
        }

        //将剩余的字节数挪到开头[NOTE:和循环队列相比，为了简单易理解，牺牲一点点性能]
        byte[] left = reader.ReadBytes((int)(messageBuffer.Length - messageBuffer.Position));
        messageBuffer.SetLength(0);
        messageBuffer.Write(left, 0, left.Length);
    }

}
