package fm.xie.filter;

/**
 * The driver program to coordinate and carry out the filter process
 * 
 * Usage:
 * java fm.xie.filter.Processor <input-image> <output-image>
 * 
 * @author yuhua
 *
 */
public class Processor {
	public static void main(String[] args) {
		// invocation format
		if( args.length < 2 ) {
			System.err.println("Usage: java fm.xie.filter.Processor <input-image> <output-image>");
			System.exit(-1);
		}
		else {
			System.out.println("Processing image file: " + args[0] + ". Filtered image saved to: " + args[1]);
		}
		
		// filter definition
		RGBFilter filter = new RGBFilter();
		
		// actual image content and transformation
		RGBFileTransform content = new RGBFileTransform(args[0], args[1]);
		
		Processor p = new Processor();
		p.process(content, filter);
		
		System.out.println("Done processing. Check output file: " + args[1]);
	}
	
	public void process(RGBFileTransform content, RGBFilter filter) {
		System.out.println("shall we start to process?");
		content.apply(filter);
	}
}
