package jgate;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try{
            String host = "localhost";
            int port = 18800;
            Socket socket = new Socket(host, port);
            OutputStream outputStream = socket.getOutputStream();
            String info ="hello from java client";
            byte[] message = info.getBytes("UTF-8");

            //写入四个自己的长度
            outputStream.write(Util.intToByteArray(message.length));
            outputStream.write(message);


            InputStream inputStream = socket.getInputStream();

            //读取4个字节长度
            byte[] lengthBytes = new byte[4];
            inputStream.read(lengthBytes);

            //读取包体
            byte[] bodyBytes = new byte[1024];
            int len = inputStream.read(bodyBytes);
            if (len > 0){
                String body = new String(bodyBytes,0,len,"UTF-8");
                System.out.println("resp:" + body);
            }

            outputStream.close();
            inputStream.close();
            socket.close();


//            Thread.sleep(3 * 1000L);
        }
        catch (Exception e){
            System.out.println(e);
        }

        Util.readKey();
    }
}
