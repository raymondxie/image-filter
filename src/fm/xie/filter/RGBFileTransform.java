package fm.xie.filter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The engine of filter processing.
 * 
 * The class holds image data streams (source and target), and takes a filter to apply transformation on 
 * source image pixels and produce target image pixels.
 * 
 * @author yuhua
 *
 */
public class RGBFileTransform extends DataConvert {
	// temp buffer of picking out 2 bytes from data stream
	private byte twoBytes[] = new byte[2];
	
	/**
	 * the source data stream for processing 
	 */
	private BufferedInputStream inStream = null;

	/**
	 * the target data stream with filtered result after the processing 
	 */
	private BufferedOutputStream outStream = null;
	
	/**
	 * Holds the data pixel lines when we apply the filter to them.
	 * In our implementation, we hold 3 lines, with each line: width x 6 bytes.
	 * The lines rotate to re-use the space, such as (2,0,1) -> (0,1,2) -> (1,2,0) -> (2,0,1) -> ...
	 */
	private byte lines[][];
	
	/**
	 * holds the target pixel after filter
	 */
	private byte result[];
	
	private int LINEBYTES;		 // total bytes in one line
	private int BUFFERLINES = 3; // TODO: should be set by RGBFilter.height
	private int PIXELBYTES = 6;  // TODO: should be match with or set by RGBPixel.length
	

	// current index of image row being read, absolute value (0 - height)
	private int pReadLine;

	// current index of image row being processed by filter, usually one line behind "pReadLine"
	private int pProcessLine;
	
	// the filter definition, we will apply the filter to the data content
	private RGBFilter filter;
	
	/**
	 * The image data comes from a file, and will write to a file after filter is applied.
	 * 
	 * @param inputFile	 input file name
	 * @param outputFile  output file name
	 */
	public RGBFileTransform(String inputFile, String outputFile) {
		try {
			
			inStream = new BufferedInputStream(new FileInputStream(inputFile));
			outStream = new BufferedOutputStream(new FileOutputStream(outputFile));
						
			// the image size: W x H... 
			//TODO:  check it out - seems most of image does not observe WxH definition at this offset or size, in the input stream; so data could be wrong!!		
			inStream.read(twoBytes);
			width = (int)(twoBytes[0] & 0x00FF << 8 | twoBytes[1] & 0x00FF);
			
			inStream.read(twoBytes);		
			height = (int)(twoBytes[0] & 0x00FF << 8 | twoBytes[1] & 0x00FF);
			
			//TODO: for testing, commented it out
			// width = 100;
			// height = 30;
			
			// the image lines for processing
			LINEBYTES = width * PIXELBYTES;
			
			lines = new byte[BUFFERLINES][LINEBYTES];
			result = new byte[LINEBYTES];
			pReadLine = 0;
			
			System.out.printf("Image size: (%d, %d) \n", width, height );
		}
		catch(IOException io) {
			io.printStackTrace();
		}
	}
	
	/**
	 * We can process image data from any streaming data, such as over Internet,
	 * in additional to file.
	 * 
	 * @param in
	 * @param out
	 */
	public RGBFileTransform(InputStream in, OutputStream out) {
		// TODO: implement later
	}
	
	/**
	 * Reads in one line of image data, with byte count at: byte_count_per_pixel x pixel_per_row
	 */
	@Override
	public void feedInBuffer() {
		try {
			// note: we only keep  BUFFERLINES=3 lines in memory;
			int slot = pReadLine % BUFFERLINES;

			if(pReadLine >= height) {
				System.out.println("Reached to end of image, no more data.");
				
				//corner case #2: clear out next line in the rotation slot, so we can do processing on last line.
				lines[slot] = new byte[LINEBYTES];
				
				return;
			}
			
			
			int count = inStream.read( lines[slot] );			
			System.out.println("pReadLine: " + pReadLine);
			pReadLine++;

			if( count != LINEBYTES ) {
				System.err.println("Image line input seems to be truncated");
			}
		}
		catch(IOException io) {
			System.err.println("Failed to read data content");
		}
	}
	
	/**
	 * Apply the filter to the content to produce target pixel result
	 * 
	 * @param rgbFilter	 the filter definition used to apply on source image data.
	 */
	@Override
	public void apply(RGBFilter rgbFilter) {
		
		filter = rgbFilter;
		
		if( pProcessLine == 0 ) {
			// corner case #1:  when we at first line "pProcessLine=0", we need to read in second line too 
			feedInBuffer();
			feedInBuffer();			
		}
		
		while(pProcessLine < height) {
			processLine();
		}
		
		// flush data and close data stream
		doneProcessing();
		
		System.out.println("Completed to apply filter to all pixels");
	}
	
	/**
	 * process current line of image row by filter
	 */
	@Override
	public void processLine() {
		
		// the cache index for lines we are processing
		//
		int prev = (pProcessLine-1) % BUFFERLINES;
		if( pProcessLine < 1) {
			// corner case #3: if processing first line, the prev buffer is at index = 2;
			// think the 3 lines of cache as rotating lines
			prev = 2;
		}
		int curr = pProcessLine % BUFFERLINES;
		int next = (pProcessLine+1) % BUFFERLINES;
		
		// By now, we have 3 proper lines:  lines[prev], lines[curr], lines[next]
		// We just need to walk through the line - pCol: (0, width-1)
		int pCol = 0;
		while( pCol < width ) {
			processPixel(pCol, prev, curr, next);
			pCol++;
		}
		
		// We got the result line now!
		try {
			outStream.write(result);
		}
		catch(IOException io) {
			System.err.println("Failed to write out result");
		}
		
		System.out.println("pProcessLine: " + pProcessLine);
		pProcessLine++;
		
		// after we processed one row of image, read in next row
		feedInBuffer();
	}


	/**
	 * Apply the filter to pixel at position: (curr, col) 
	 *  
	 * @param col	horizontal poistion on the line
	 * @param prev	index to previous line in cache
	 * @param curr	index to current line in cache
	 * @param next	index to next line in cache
	 */
	private void processPixel(int col, int prev, int curr, int next) {
		// filter contains 3 configurations, one for each color: R, G, B
		float[][] fred = filter.filters.get("filter.r");
		float[][] fgreen = filter.filters.get("filter.g");
		float[][] fblue = filter.filters.get("filter.b");
		
		int row = 0;
		int red = 0;
		int green = 0;
		int blue = 0;

		// original data is centered on (0,0), so i:(-1,0,1) and j:(-1,0,1)
		for(int i=-1; i<=1; i++) {
			for(int j=-1; j<=1; j++) {

				// Use current pixel position as (0,0), we are going to look through (i,j) which is relative to center (0,0)
				switch(i) {
					case -1: row = prev; break;
					case 0:	row = curr; break;
					case 1: row = next; break;
				}

				byte[] cc = new byte[6];
				
				// corner case #4: need to safeguard column
				//
				if( col+j < 0 || col+j >= width ) {
					// out of bound on left, or right side, just set 6 bytes of 0 for it
					; // cc = new byte[6];
				}
				else {
					for(int k=0; k<6; k++) {
						cc[k] = lines[row][(col+j)*6+k];
					}
				}
				
				// note: filter coordinate is centered on (1,1), while original data is centered on (0,0)
				// do a math round, and accumulates data from result of each neighboring pixels for each color channel
				red += Math.round( (cc[0] & 0x00FF << 8 + cc[1] & 0x00FF) * fred[1+i][1+j] ); 
				green += Math.round( (cc[2] & 0x00FF << 8 + cc[3] & 0x00FF) * fgreen[i+1][j+1] );
				blue += Math.round( (cc[4] & 0x00FF << 8 + cc[5] & 0x00FF) * fblue[i+1][j+1] );
			}
		}
		
		// corner case #5: the sum of filtering data could exceed the range, shall we do a cap on it?
		// TODO: check the corner case later 
		// put the 6 bytes to target line
		int c = col * 6;	// index at the target pixel data 
		result[c]   = (byte) (red & 0x0000FF00 >> 8);
		result[c+1] = (byte) (red & 0x000000FF);
		result[c+2] = (byte) (green & 0x0000FF00 >> 8);
		result[c+3] = (byte) (green & 0x000000FF);
		result[c+4] = (byte) (blue & 0x0000FF00 >> 8);
		result[c+5] = (byte) (blue & 0x000000FF);
	}


	/**
	 * Called after we finish process to close the streams for cleanup.
	 */
	private void doneProcessing() {
		try {
			if(inStream != null) {
				inStream.close();
			}
			if(outStream != null) {
				outStream.flush();
				outStream.close();
			}
		}
		catch(IOException io) {
			System.err.println("Unable to close data stream after reaching to its end");
		}
	}

}
