package fm.xie.filter;

/**
 * Define the pixel data as defined:
 * 3 color of R, G, B, with each color having 2 bytes.
 * 
 * We should read from a properties file to defined the "pixel" definition
 * 
 * @author yuhua
 *
 */
public class RGBPixel extends BaseConfig {	
	// pixel bytes: e.g. each pixel has 6 bytes
	private int LENGTH;
	private byte[] data;

	public RGBPixel() {
		super("rgb-pixel.properties");
		
		String length = properties.getProperty("length");
		LENGTH = Integer.parseInt(length);
			
		data = new byte[LENGTH];
	}
	
	public void setData(byte[] bytes) {
		// copy the first 6 bytes
		for(int i=0; i<LENGTH; i++) {
			data[i] = bytes[i];
		}	
	}
	
	/**
	 * Set a byte value to a specified offset
	 * 
	 * @param offset
	 * @param b
	 */
	public void setByte(int offset, byte b) {
		data[offset] = b;
	}
	
	public int length() {
		return LENGTH;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public int red() {
		return data[0] & 0x00FF << 8 | data[1] & 0x00FF; 
	}
	
	public int green() {
		return data[2] & 0x00FF << 8 | data[3] & 0x00FF; 
	}
	
	public int blue() {
		return data[4] & 0x00FF << 8 | data[5] & 0x00FF; 
	}
	
	
	
}
