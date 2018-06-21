using ServiceStack.Redis;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


namespace SharpService
{
    class Program
    {
        //https://www.cnblogs.com/DreamOfLife/p/5667201.html
        static void Main(string[] args)
        {
            Console.WriteLine("Hello World");

            try
            {
                
                RedisClient redis = new RedisClient("localhost", 6379);//redis服务IP和端口
                //redis.Subscribe("ddz");

                //发布消息
                //redis.PublishMessage();
                string message = "hello from ddz";
                MessagePack mp = new MessagePack("cid","ddz",MessageType.MESSAGE_TYPE_DATA,System.Text.Encoding.UTF8.GetBytes(message));
                redis.Publish("jgate", mp.Serialize());

                //byte[] buf = mp.Serialize();
                //MessagePack tmp = new MessagePack();
                //bool ret = tmp.Parse(buf);

                MessageManager.Instance().OnMessage = (msg) =>
                {
                    Console.WriteLine("OnMessage:" + msg.UTF8Message);
                };

                MessageManager.Instance().Init();

            }
            catch (Exception e)
            {
                Console.WriteLine("error:" + e.ToString());
            }

            Console.ReadKey();
        }
    }
}
