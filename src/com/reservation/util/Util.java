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
	
	public static Date stringToDateFormat(String sDate) {
		
		Date returnDate = new Date();
		
		DateFormat df = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.ENGLISH);
		try {
			returnDate = df.parse(sDate);
		} catch (ParseException e) {
			// TODO Add logger exception
			e.printStackTrace();
		}
		logger.info("[Util] Converted date is : " + returnDate);
		return returnDate;
		
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
