package com.reservation.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

public class Util {

	static Logger logger;
	
	Util (){}
	
	static {
		logger = Logger.getLogger("Util");		
	}
	
	public static Date stringToDateFormat(String sDate, String format) {//yyyy-MM-dd HH:mm:ss
		
		if (sDate == null || format == null)
		{
			logger.warning("date or format variable is null.");
			return null;
		}
		
		Date returnDate = new Date();
		logger.info("[Util] String Date to convert : " + sDate + " With format: " + format);
		DateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
		try {
			returnDate = df.parse(sDate);
		} catch (ParseException e) {
			// TODO Add logger exception
			e.printStackTrace();
		}
		logger.info("[Util] Converted string to date as : " + returnDate);
		return returnDate;
		
	}
	
	public static String dateToStringFormat(Date dDate, String format) {//dd-MM-YYYY HH:MM
		
		if (dDate == null || format == null)
		{
			logger.warning("date or format variable is null.");
			return null;
		}
		System.out.println("Converting the date : " + dDate + " With format: " + format);
		DateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
		String sDate = df.format(dDate);
		logger.info("[Util] Converted date to String as : " + sDate);
		return sDate;
	}
	
	public static String dateToSimpleStringFormat(Date dDate) {
		if (dDate == null)
		{
			logger.warning("date variable is null.");
			return null;
		}
		
		System.out.println("Converting the date : " + dDate);
		DateFormat df = new SimpleDateFormat("dd-MM-YYYY", Locale.ENGLISH);
		String sDate = df.format(dDate);
		logger.info("[Util] Converted date to String as : " + sDate);
		return sDate;
	}
	
	public static SQLExceptionType parseException(String exMessage) {
		
		logger.info("Parsing the exception for sqlexceptiontype: " + exMessage);
		
		SQLExceptionType exType = SQLExceptionType.ReservationExistingForDate;
		
		
		if (exMessage.contains("RESERVATIONS_UK_RESDATE"))
			exType = SQLExceptionType.ReservationExistingForDate;
		else if (exMessage.contains("RESERVATIONS_UK_DOCID"))
			exType = SQLExceptionType.ReservationExistingForDocID;
		else 
			exType = SQLExceptionType.ReservationExistingForDocID;
		
		
		logger.info("Parsed sqlexceptiontype is : " + exType.name());
		return exType;
		
	}
	
	public static String reservationNumberGenerator() {

		System.out.println("Entered to reservationNumberGenerator function");
		StringBuilder generated = new StringBuilder();
		String charSet = new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
		Random random = new Random();
		
		try {
			for (int i =0 ; i<7; ++i)
			{
				generated.append(charSet.charAt(random.nextInt(charSet.length())));
			}
			logger.info("Generated reservation number is : " + generated);
			
		}
		catch (Exception ex) {

			logger.severe("[Util] Exception thrown while generating reservation number");
			
		}

		return generated.toString();
		
	}
}
