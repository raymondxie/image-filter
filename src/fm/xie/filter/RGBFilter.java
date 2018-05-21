package fm.xie.filter;

import java.util.HashMap;

/**
 * 
 * Read in from properties file about the filter definition for RGB filter
 * 
 * @author yuhua
 *
 */
/*
 * RGB Filter properties file should use keys:  
 * filter, filter.r, filter,g, filter.b
 * for example:
	filter=3,3
	filter.r=0.1,0.1,0.1,0,0.3,0,0,0.2,0
	filter.g=0,0.1,0.1,0,0.3,0.1,0,0.1,0.1
	filter.b=0,0.1,0.1,0.1,0.3,0,0.1,0.1,0
 */

public class RGBFilter extends BaseConfig {
	// indicate filter pattern, size or shape, 
	// for RGB filter, we have 3x3 matrix
	public final int width;
	public final int height;

	// This holds filter for R, G, B colors
	HashMap<String, float[][]> filters = new HashMap<String, float[][]>();
	
	public RGBFilter() {
		super("rgb-filter.properties");
		
		String filter = properties.getProperty("filter");
		String[] numbers = filter.split(",");
		width = Integer.parseInt( numbers[0].trim() );
		height = Integer.parseInt( numbers[1].trim() );

		populateFilter("filter.r");
		populateFilter("filter.g");
		populateFilter("filter.b");
	}

	private void populateFilter(String color) {
		System.out.println( properties.getProperty( color ));
		
		float[][] temp = new float[width][height];

		String filter = properties.getProperty( color );
		String[] numbers = filter.split(",");
		
		int filter_len = width * height;
		
		// read in a string of filter data, and construct 3x3 matrix as a filter
		for(int i=0; i < filter_len; i++ ) {
			int m = i/3;
			int n = i%3;
			temp[m][n] = Float.parseFloat(numbers[i].trim());
		}
		
		filters.put(color, temp);
	}
}
