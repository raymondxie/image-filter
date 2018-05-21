package fm.xie.filter;

import java.nio.ByteBuffer;

public class DataUtil {
/*
 * Using NIO ByteBuffer class, standard 4 byte <-> int
 * 

	byte[] toByteArray(int value) {
	     return  ByteBuffer.allocate(4).putInt(value).array();
	}

	int fromByteArray(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).getInt();
	}
 */

	
/**
 * using bit mask and shifting approach, to handle 2-byte data
 * 
 * @param value:  the integer to be converted into two bytes
 * @return a byte array of size 2
 */
	public static byte[] toByteArray(int value) {
	    return new byte[] { 
//	        (byte)(value >> 24),
//	        (byte)(value >> 16),
	        (byte)(value >> 8),
	        (byte)value };
	}

/**
 * 
 * @param bytes: a byte of array of size 2
 * @return an integer
 */
	public static int fromByteArray(byte[] bytes) {
	     return bytes[0] << 8 | (bytes[1] & 0xFF) ;
	     // standard 4 bytes
	     // return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}
	
	
}
