using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Configuration;
using ServiceStack.Redis;
using System.Threading;
using System.Collections.Concurrent;

namespace SharpService
{
    class MessageManager
    {
        #region singleton 接口

        private static MessageManager _instance = new MessageManager();

        private MessageManager()
        {
        }

        public static MessageManager Instance()
        {
            return _instance;
        }

        #endregion

        public Action<MessagePack> OnMessage;

        private List<MessagePack> messagePackList = new List<MessagePack>();
        //private ConcurrentQueue<MessagePack> messagePackQueue = new ConcurrentQueue<MessagePack>();
        private Thread dispatchThread;
        

        public int Init()
        {
            try
            {
                #region 启动消息分发线程

                dispatchThread = new Thread(DispatchThread);
                dispatchThread.Start();

                #endregion

                #region 订阅消息

                //订阅消息
                string subscribeChannel = ConfigurationSettings.AppSettings["SubscribeChannel"];
                string redisIp = ConfigurationSettings.AppSettings["RedisIp"];
                int redisPort = int.Parse(ConfigurationSettings.AppSettings["RedisPort"]);

                RedisClient redis = new RedisClient(redisIp, redisPort);//redis服务IP和端口

                //创建订阅
                IRedisSubscription subscription = redis.CreateSubscription();
                subscription.OnMessage = (channel, message) =>
                {
                    MessagePack messagePack = new MessagePack();
                    int len = message.Length;
                    bool result = messagePack.Parse(System.Text.Encoding.UTF8.GetBytes(message));
                    if (result)
                    {
                        lock (messagePackList)
                        {
                            messagePackList.Add(messagePack);
                        }
                    }

                };

                //订阅频道
                subscription.SubscribeToChannels(subscribeChannel);


                #endregion

               
            }
            catch (Exception e)
            {
                Console.WriteLine("MessageManager.Init error:" + e.ToString());
                return -1;
            }

            return 0;
        }

        private void DispatchThread()
        {
            while (true)
            {
                try
                {
                    MessagePack mp = null;
                    lock (messagePackList)
                    {
                        if (messagePackList.Count > 0)
                        {
                            int headIndex = 0;
                            mp = messagePackList[headIndex];
                            messagePackList.RemoveAt(headIndex);
                        }
                    }

                    if (mp != null)
                    {
                        this.OnMessage(mp);
                    }
                    else
                    {
                        Thread.Sleep(10);
                    }
                }
                catch(Exception e)
                {

                }
                
            }
        }

    }
}
