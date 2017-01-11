package com.eci.roy.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

public class MagicUtils {

	private static final Logger logger = Logger.getLogger(MagicUtils.class);
	
	public static void lanWake(String macAddress, String destIp, int port) throws IOException {
		byte[] destMac = getMacBytes(macAddress);
		if (destMac == null) {
			return;
		}
		InetAddress destHost = InetAddress.getByName(destIp);
		byte[] magicBytes = new byte[102];
		for (int i = 0; i < 6; i++)
			magicBytes[i] = (byte) 0xFF;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < destMac.length; j++) {
				magicBytes[6 + destMac.length * i + j] = destMac[j];
			}
		}
		DatagramPacket dp = null;
		dp = new DatagramPacket(magicBytes, magicBytes.length, destHost, port);
		DatagramSocket ds = new DatagramSocket();
		ds.send(dp);
		ds.close();
		logger.info("Magic packet has been sent!");
	}

	private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
		byte[] bytes = new byte[6];
		String[] hex = macStr.split("(\\:|\\-)");
		if (hex.length != 6) {
			throw new IllegalArgumentException("Invalid MAC address.");
		}
		try {
			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) Integer.parseInt(hex[i], 16);
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid hex digit in MAC address.");
		}
		return bytes;
	}
}
