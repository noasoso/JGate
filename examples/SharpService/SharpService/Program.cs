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
        static void Main(string[] args)
        {
            Console.WriteLine("SharpService start");

            try
            {
                MessageManager.Instance().OnMessage = (msg) =>
                {
                    Console.WriteLine("OnMessage,type:" + msg.Type + ",msg:" + msg.UTF8Message);
                    msg.Message = Encoding.UTF8.GetBytes( "reply from sharpservice" );

                    if(msg.Type == MessageType.MESSAGE_TYPE_DATA)
                    {
                        MessageManager.Instance().Send(msg);

                    }
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
