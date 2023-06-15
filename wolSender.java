package wolGui;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class wolSender {

	// Will return true if the provided String is in
	// the format 'FF:FF:FF:FF:FF:FF'
	public static boolean isValidMac(String mac) {
		if (mac == null) {
			throw new IllegalArgumentException("String mac cannot be null");
		}
		Matcher matcher = Pattern.compile("^(?:[0-9A-Fa-f]{2}[:]){5}(?:[0-9A-Fa-f]{2})$").matcher(mac);
		return (matcher.find());
	}

	// This method will craft a wake on Lan packet, and send it to the MAC address
	// passed to it
	// Must be in format 'FF:FF:FF:FF:FF:FF'
	// MUST validate MAC address first with isValidMac(). Will throw
	// IllegalArgumentException if invalid MAC supplied
	// If no exception is thrown, assume WOL packet was sent - WOL packets receive
	// no reply
	public static void sendWolPacket(String mac) throws IOException {
		if (!isValidMac(mac)) {
			throw new IllegalArgumentException("MAC Address is not valid");
		}
		byte[] bytes = makeByteArray(mac);
		InetAddress inet = InetAddress.getByName("255.255.255.255");
		DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, inet, 3);
		DatagramSocket socket = new DatagramSocket();
		socket.send(packet);
		socket.close();
	}
	
	private static byte[] makeByteArray(String macAdd) {
		byte[] packet = new byte[102];
		byte[] macByteArray = new byte[6];
		String[] macBytes = macAdd.split(":");
		
		for (int i = 0; i < 6; i++) {
			macByteArray[i] = (byte)Integer.parseInt(macBytes[i], 16);			
		}
		
		for (int c = 0; c < 6; c++) { //First six bytes are FF
			packet[c] = (byte) 0xFF;
		}
		
		for (int i = 0; i < 16; i++) {  //MAC Address is repeated 16 times
			System.arraycopy(macByteArray, 0, packet, (i+1)*6, 6);
		}
		return packet;
	}
}
