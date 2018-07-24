using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


class Program
{
    static void Main(string[] args)
    {
        try
        {
            SharpClient client = new SharpClient();
            client.SetChannel("test_1");

            client.OnConnect = () =>
            {
                Console.WriteLine("OnConnect");
                string msg = "hello from sharpclient";
                client.Write(Encoding.UTF8.GetBytes(msg));
            };
            client.OnClose = () =>
            {
                Console.WriteLine("close now");
            };
            client.OnMessage = (byte[] msg) =>
            {
                Console.WriteLine( "read :" + Encoding.UTF8.GetString(msg));
                
            };
            

            client.Connect("jgate.qipai.io", 18666);//公网
            //client.Connect("localhost", 18666);//内网

        }
        catch (Exception e)
        {
            Console.WriteLine(e);
        }
        Console.ReadKey();

    }
}
