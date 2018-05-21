package fm.xie.filter;

/**
 * Define the pixel data format in data stream.
 * For example, 3 color of R, G, B, with each color having 2 bytes.
 * 
 * 
 * @author yuhua
 *
 */
public class RGBPixel extends BaseConfig {	
	/**
	 * The number of bytes for one pixel
	 */
	private int LENGTH;
	
	/**
	 * The pixel data from image stream
	 */
	private byte[] data;

	public RGBPixel() {
		// read from a properties file to defined the "pixel" definition
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
	
	/*
	 * In our specific requirement, we use 2 bytes for each color depth. 
	 * TODO: make following method generic based on actual definition of pixel
	 */
	
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
