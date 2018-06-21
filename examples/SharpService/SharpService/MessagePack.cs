using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SharpService
{
    class MessagePack
    {
        public string Cid { get; set; }

        public string Channel { get; set; }

        public MessageType Type { get; set; }

        public byte[] Message { get; set; }

        public string UTF8Message {
            get {
                if(this.Message == null)
                {
                    return string.Empty;
                }
                else
                {
                    return System.Text.Encoding.UTF8.GetString(this.Message);
                }
            } }

        public MessagePack()
        {

        }

        public MessagePack(string cid,string channel,MessageType type,byte[] message)
        {
            this.Cid = cid;
            this.Channel = channel;
            this.Type = type;
            this.Message = message;
        }

        public byte[] Serialize()
        {
            using (MemoryStream ms = new MemoryStream())
            {
                BinaryWriter writer = new BinaryWriter(ms);
                int len = 2 + (2 + this.Cid.Length) + (2 + this.Channel.Length);
                if(this.Message != null)
                {
                    len += (4 + this.Message.Length);
                }

                //类型
                short t = (short)this.Type;
                short s = (short)IPAddress.HostToNetworkOrder(t);
                byte[] buf = BitConverter.GetBytes(s);

                writer.Write(BitConverter.GetBytes( IPAddress.HostToNetworkOrder( (short)this.Type ) ) );

                //cid
                writer.Write(BitConverter.GetBytes( IPAddress.HostToNetworkOrder( (short)this.Cid.Length )) );
                writer.Write(System.Text.Encoding.UTF8.GetBytes(this.Cid));

                //channel
                writer.Write(BitConverter.GetBytes( IPAddress.HostToNetworkOrder((short)this.Channel.Length) ) );
                writer.Write(System.Text.Encoding.UTF8.GetBytes(this.Channel));

                //data
                if(this.Message != null)
                {
                    writer.Write(BitConverter.GetBytes( IPAddress.HostToNetworkOrder((int)this.Message.Length)));
                    writer.Write(this.Message);
                }

                writer.Flush();
                return ms.ToArray();
            }

        }

        public bool Parse(byte[] bytes)
        {
            try
            {
                using (MemoryStream ms = new MemoryStream())
                {
                    BinaryReader reader = new BinaryReader(ms);
                    ms.Write(bytes, 0, bytes.Length);
                    ms.Position = 0;

                    //类型
                    int typeIndex = IPAddress.NetworkToHostOrder( reader.ReadInt16() );
                    this.Type = (MessageType)typeIndex;

                    //cid
                    int len = IPAddress.NetworkToHostOrder( reader.ReadInt16() );
                    byte[] buf = reader.ReadBytes(len);
                    this.Cid = System.Text.Encoding.UTF8.GetString(buf);

                    //channel
                    len = IPAddress.NetworkToHostOrder( reader.ReadInt16() );
                    buf = reader.ReadBytes(len);
                    this.Channel = System.Text.Encoding.UTF8.GetString(buf);

                    //data
                    if( ms.Length - ms.Position > 0)
                    {
                        len = IPAddress.NetworkToHostOrder( reader.ReadInt32() );
                        this.Message = reader.ReadBytes(len);
                    }

                }
            }
            catch(Exception e)
            {
                return false;
            }

            return true;
        }

    }
}
