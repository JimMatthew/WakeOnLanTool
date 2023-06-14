package wolGui;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class wolSender {

	//Will return true if the provided String is in
	//the format 'FF FF FF FF FF FF' or 'FF:FF:FF:FF:FF:FF'
	public static boolean isValidMac(String mac) {
		if (mac == null) {
			throw new IllegalArgumentException("String mac cannot be null");
		}
		Matcher matcher = Pattern.compile("^(?:[0-9A-Fa-f]{2}[: ]){5}(?:[0-9A-Fa-f]{2})$").matcher(mac);
		return (matcher.find());
	}
	//This method will craft a wake on Lan packet, and send it to the MAC address passed to it 
	//Must be in format 'FF FF FF FF FF FF' or 'FF:FF:FF:FF:FF:FF'
	//MUST validate MAC address first with isValidMac(). Will throw IllegalArgumentException if invalid MAC supplied
	//If no exception is thrown, assume WOL packet was sent - WOL packets receive no reply
	public static void sendWolPacket(String mac) throws IOException {
		if (!isValidMac(mac)) {
			throw new IllegalArgumentException("MAC Address is not valid");
		}
		//mac = mac.replace(':', ' ').replace("\n", "");
		byte[] bytes = makePacketByteArray(mac);
		InetAddress inet = InetAddress.getByName("255.255.255.255");
		DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, inet, 3);
		DatagramSocket socket = new DatagramSocket();
		socket.send(packet);
		socket.close();
	}
	
	private static byte[] makePacketByteArray(String macAdd) {
		byte[] bytes = new byte[116];
		byte[] mac = new byte[6];
		int c = 0;
		for (int k = 0; k < macAdd.length(); k+=3) {
			mac[c] = Integer.valueOf(macAdd.substring(k, k + 2), 16).byteValue();
			c++;
		}
		c = 0; 
		for (;c < 6; c++) {		//First 6 bytes are set to FF
			bytes[c] = (byte) 0xFF;
		}
		for (int i = 0; i < 6; i++) {	//Next 6 bytes are the MAC address to wake up
			bytes[c] = mac[i];
			c++;
		}
		bytes[12] = (byte) 0x08;	//2 bytes for ethertype 08 42
		bytes[13] = (byte) 0x42;
		for (c = 14;c < 20; c++) {	//6 more bytes of FF
			bytes[c] = (byte) 0xFF;
		}
		for (int i = 0; i < 16; i++) {	//Lastly, the MAC is repeated 16 more times
			for (int j = 0; j < 6; j++) {
				bytes[c] = mac[j];
				c++;
			}
		}
		return bytes;
	}
}
