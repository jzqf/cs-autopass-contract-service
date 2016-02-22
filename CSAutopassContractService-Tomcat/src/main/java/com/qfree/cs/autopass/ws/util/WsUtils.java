
package com.qfree.cs.autopass.ws.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.util.Calendar;

public class WsUtils {

	public static final int NOERRORCODEASSIGNED_ERRORCODE = -2;
	/* We do not initialize the error message to "" because if we do that, an 
	 * empty XML element will be returned to the webservice client if no error 
	 * occurs. Currently, the accepted behaviour is that *no* error message XML 
	 * element be returned at all for this case (just the error code = 0).
	 * 
	 *  2014.07.01: Kjetil asked for this to be changed so that an empty string
	 *  is returned instead.
	 */
	public static final String NOERRORCODEASSIGNED_ERRORMESSAGE = ""; // null;

	public static final int VALIDATION_ERRORCODE = 100;
	public static final String VALIDATION_ERRORMESSAGE = "Input parameter valideringsfeil";

	public static final int DBACCESSPROBLEM_ERRORCODE = 101;
	public static final String DBACCESSPROBLEM_ERRORMESSAGE = "Tjeneste er utilgjengelig. Prøv senere.";

	public static final int MAXCONNECTIONS_ERRORCODE = 102;
	public static final String MAXCONNECTIONS_ERRORMESSAGE = "Maksimalt antall samtidige tilkoblinger overskredet. Prøv senere.";

	public static final int CANNOT_ENCODE_CHARACTER_ERRORCODE = 103;
	public static final String CANNOT_ENCODE_CHARACTER_ERRORMESSAGE = "Forespørsel inneholder tegn som ikke er støttet.";

	//	private static final int DATABASE_ERROR_ERRORCODE = 104;
	//	private static final String DATABASE_ERROR_ERRORMESSAGE = "";

	// pattern for a Q-Free stored procedure name appearing at the start of a string,
	// e.g., "qp_WSC_CoreContractCreateTest : Brikke er allerede aktiv på en kontrakt (56)".
	// The regex backslash needs to be escaped here.  The effective regex is:
	//     ^qp_.+\s*:\s*
	private static final String QP_STORED_PROC_PREFIX_REGEX = "^qp_.+\\s*:\\s*";

	private static final Pattern qp_stored_proc_prefix_pattern = Pattern.compile(QP_STORED_PROC_PREFIX_REGEX);

	/**
	 * Returns True if the object is null or is an "empty" string.
	 * 
	 * This method should be use to test if an object that represents a a Sybase
	 * string is empty. Sybase replaces empty strings "" with strings of a 
	 * single character " ". I believe Sybase does this to avoid having truly
	 * empty strings "" interpreted as null strings.
	 * 
	 * @param object
	 * @return True if object is null or if it is an empty string or if it is a 
	 * string consisting of a single space. false otherwise.
	 */
	public static boolean sybaseStringIsEmpty(Object object) {
		return object == null
				|| object.toString().isEmpty()
				|| " ".equals(object.toString());
	}

	/**
	 * Returns a java.sql.Date object given a date string and format specifier.
	 * 
	 * @param dateString
	 * @param format
	 * @return java.sql.Date object corresponding to dateString and format
	 * @throws ParseException
	 */
	public static Date parseStringToSqlDate(String dateString, String formatString) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		// If we do not set "lenient" to false here, then the dateString "1958-05-06"
		// will be parsed without throwing an exception even if formatString is set
		// to "yyyyMMdd". However, in this case it will not be parsed to the correct
		// date!  This is terrible, because no error will be raised and execution will
		// continue as if a different date was specified.  The setLenient(false) call
		// here ensures that the date string strictly adheres to the specified format
		// string.
		format.setLenient(false);
		return new Date(format.parse(dateString).getTime());	// convert java.util.Date to java.sql.Date
	}

	/**
	 * Returns a java.sql.Timestamp object given a datetime string and format 
	 * specifier.
	 * 
	 * @param dateString
	 * @param format
	 * @return java.sql.Date object corresponding to dateString and format
	 * @throws ParseException
	 */
	public static Timestamp parseStringToSqlTimestamp(String dateString, String formatString) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		// See above for why we call setLenient(false) here.
		format.setLenient(false);
		return new Timestamp(format.parse(dateString).getTime());	// convert java.util.Date to java.sql.Date
	}

	/**
	 * Strips the Sybase procedure name if it appears at the start of the string. 
	 * 
	 * This is used to remove the procedure name from error messages returned from 
	 * database so that the customer does not see implementation details that they
	 * don't need to see
	 * 
	 * @param errorMessage
	 * @return
	 */
	public static Object stripSybaseProcName(Object errorMessage) {
		Object cleanedErrorMessage = errorMessage;
		if (cleanedErrorMessage != null && !cleanedErrorMessage.toString().isEmpty()) {
			//			Pattern p = Pattern.compile(QP_STORED_PROC_PREFIX_REGEX);
			Matcher m = qp_stored_proc_prefix_pattern.matcher(cleanedErrorMessage.toString());
			cleanedErrorMessage = m.replaceFirst("");
		}
		return cleanedErrorMessage;
	}

/*    public static int tryParseInt(String value) {
        int test = -1;
        try {
            test = Integer.parseInt(value);
            
        } catch(NumberFormatException e) {
            
        }
        return test;
    }*/
            
/*    protected static String timeFix(int entity){
        String value;
        if(entity < 10) {
            value = "0" + Integer.toString(entity);
        }
        else {
            value = Integer.toString(entity);
        }
        return value;        
    }*/
    
        // tall høyre justert, text venstre justert. 
/*    protected String paddingFix(int Length, String data, String Align) {
        
        int i;
        String fixed;
        fixed = data;        
        i = data.length();                
        if(Align.equals("right") && i < Length) {
            while(fixed.length() < Length) {
                fixed = " " + fixed;
            }
        }
        
        else if(Align.equals("left") && i < Length) {
            while(fixed.length() < Length) {
                fixed = fixed + " ";
            }
        }        
        return fixed;
    }*/

/*    public static void WriteLog(String Message) {
        Calendar Cal = Calendar.getInstance();
        int Time[] = new int[10]; 
        
        Time[0] = Cal.get(Calendar.DATE);
        Time[1] = Cal.get(Calendar.MONTH) + 1;
        Time[2] = Cal.get(Calendar.YEAR);
        Time[3] = Cal.get(Calendar.HOUR_OF_DAY);
        Time[4] = Cal.get(Calendar.MINUTE);
        Time[5] = Cal.get(Calendar.SECOND);
        String path = System.getenv("QFDIR");
        String LogPath = path + "/log";
        String temp = LogPath + "/PaymentMethodWS.log";
        String DateStamp = "";
        File LogFile = new File(temp);
        
        for(int i = 0; i < 6; i++) {
            DateStamp += timeFix(Time[i]);
        }
               
        try {
            // create directory if it doesn't exist.
            new File(LogPath).mkdir();
            if(!LogFile.exists()) {
                LogFile.createNewFile();
            }
            
            FileWriter fstream = new FileWriter(LogFile, true);
            BufferedWriter log = new BufferedWriter(fstream);
            log.write("[" + DateStamp + "]\t" + Message + "\r\n");
            
            log.close();
            
        }
        
        catch (Exception e) {
            
        }
    
    }*/
}
