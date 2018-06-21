package jgate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;


public class Util {
	public final int DELTA_TIME = 100;

	/**
	 * 获取出错的堆栈信息
	 * 
	 * @param e
	 * @return
	 */
	public static String getExceptionStack(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		String stack = sw.toString();
		pw.close();
		return stack;
	}

	/**
	 * 将int转换为网络字节序的byte数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] intToByteArray(int value) {
		byte[] array = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array();
		return array;
	}


	/**
	 * 控制台程序实现任意键退出
	 */
	public static void readKey() {
		try {
			new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 将字节数组转换为十六进制的字符串格式
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toHexString(byte[] bytes) {

		if (bytes == null || bytes.length <= 0) {
			return "";
		}

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}

			builder.append(hex);
			builder.append(" ");
		}

		return builder.toString();
	}
	
	/**
	 * 获取本机的Ip列表
	 * 
	 * @return
	 */
	public static List<String> getLocalIPList() {
		List<String> ipList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			NetworkInterface networkInterface;
			Enumeration<InetAddress> inetAddresses;
			InetAddress inetAddress;
			String ip;
			while (networkInterfaces.hasMoreElements()) {
				networkInterface = networkInterfaces.nextElement();
				inetAddresses = networkInterface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					inetAddress = inetAddresses.nextElement();
					if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
						ip = inetAddress.getHostAddress();
						ipList.add(ip);
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ipList;
	}

	/**
	 * 返回日期时间,格式:2014-5-05 00:00:00
	 * @return
	 */
	public static String getNowDateTime(){
		Date date = new Date();

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static void sleep(long millis){
	    try {
            Thread.sleep(millis);
        }
        catch (Exception e){
			System.out.println("sleep :" + e.toString());
        }
    }

}
