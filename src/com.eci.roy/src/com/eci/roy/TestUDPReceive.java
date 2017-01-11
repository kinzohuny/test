package com.eci.roy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class TestUDPReceive {
	public static void main(String[] args) {
		DatagramSocket datagramSocket = null;
		while(true){
			try {
				// 监视8081端口的内容
				datagramSocket = new DatagramSocket(54321);
				byte[] buf = new byte[1024];

				// 定义接收数据的数据包
				DatagramPacket datagramPacket = new DatagramPacket(buf, 0, buf.length);
				datagramSocket.receive(datagramPacket);

				// 从接收数据包取出数据
				String data = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
				System.out.println(data);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				datagramSocket.close();
			}
		}
	}
}