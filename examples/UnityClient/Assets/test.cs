using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;

public class test : MonoBehaviour
{
    SharpClient client = new SharpClient();

    // Use this for initialization
    void Start()
    {
        try
        {
            client.OnConnect = () =>
            {
                UnityThread.Current.Queue(() =>
                        {
                            Debug.Log("OnConnect");
                            string msg = "hello from sharpclient";
                            client.Write(Encoding.UTF8.GetBytes(msg));
                        }
                    );

            };
            client.OnClose = () =>
            {
                UnityThread.Current.Queue(()=> {
                    Debug.Log("close now");
                });
            };
            client.OnMessage = (byte[] msg) =>
            {
                UnityThread.Current.Queue(()=> {
                    Debug.Log("read :" + Encoding.UTF8.GetString(msg));
                });
            };

            Debug.Log("connect now");

            client.Connect("localhost", 28800);

        }
        catch (Exception e)
        {
            Debug.LogError(e);
        }
    }

    // Update is called once per frame
    void Update()
    {

    }
}
