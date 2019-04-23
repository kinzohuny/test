package com.eci.roy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class TestUDPSend {
	public static void main(String[] args) {
		String data = "hello UDP";
		DatagramSocket datagramSocket = null;
		try {
			// 实例化套接字，并指定发送端口
			datagramSocket = new DatagramSocket(8080);
			// 指定数据目的地的地址，以及目标端口
			InetAddress destination = InetAddress.getByName("localhost");
			DatagramPacket datagramPacket = new DatagramPacket(data.getBytes(), data.getBytes().length, destination, 8081);
			// 发送数据
			datagramSocket.send(datagramPacket);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			datagramSocket.close();
		}
	}
}
