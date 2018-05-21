package fm.xie.filter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Generic configuration from properties files, load the definition.
 * Actual interpretation of definition is left to actual child class.
 * 
 * @author yuhua
 *
 */
public class BaseConfig {
	protected Properties properties = null;
	private String propertyFile = null;
	
	/**
	 * Load definition from properties file
	 * 
	 * @param propFile: the file containing filter or pixel definition,
	 */
	public BaseConfig(String propFile) 
	{
		try {
			if( propFile != null )
				propertyFile = propFile;
			
			properties = new Properties();
	
			// InputStream inStream = getClass().getClassLoader().getResourceAsStream(propertyFile);
			FileInputStream inStream  = new FileInputStream(propertyFile);
			
			
			System.out.println("loading property: " + propertyFile );
			properties.load(inStream);
			
		}
		catch(IOException io) {
			io.printStackTrace();
		}
	}
}
