package transfer;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class loggerclient {
	 private final static Logger LOGGER =  
             Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); 

	public static void log(Level level, String content) {
		boolean append = true;
		try {
			//Logger logger = Logger.getLogger("Controller");
			FileHandler handler = new FileHandler("client.log", append);
			LOGGER.addHandler(handler);
			
			LOGGER.log(level, content);		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
