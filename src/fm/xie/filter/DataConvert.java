package fm.xie.filter;

/**
 * Define the data content format to be processed.
 * Provide basic operation definition.
 * 
 * @author yuhua
 *
 */

public abstract class DataConvert {
	int width = 0;
	int height = 0;

	/**
	 * fetch data from source into buffer
	 */
	abstract public void feedInBuffer();
	
	/**
	 * process one row of image
	 * @return
	 */
	abstract public void processLine();
	
	/**
	 * apply filter to transform data
	 */
	abstract public void apply(RGBFilter filter);
	
}
