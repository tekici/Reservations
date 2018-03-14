package com.reservation.pojo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.context.RequestContext;

import com.reservation.dao.*;
import com.reservation.util.Util;
import com.sun.faces.context.SessionMap;

@ManagedBean(name = "reservation", eager = true)
@SessionScoped
public class Reservation {

	
	private String reservationNumber;
	private String name;
	private String surname;
	private Date reservationDate;
	private String issueDate;
	private String documentId;
	
	private String warningText;
	public static DatabaseOperations dbObj;
	private static Logger logger;
	
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value="#{newreservation}")
	public NewReservation newReservation;

	@ManagedProperty(value="#{showreservation}")
	public ShowReservation showReservation;

	public void setShowReservation(ShowReservation newRes) {
		this.showReservation = newRes;
	}

	public void setNewReservation(NewReservation newRes) {
		this.newReservation = newRes;
	}
	
	public Reservation(){

		System.out.println("Reservation constructor");
		dbObj = new DatabaseOperations(this);	
		logger = Logger.getLogger("NewReservation");
	}
	/*
	 * This function will initialize all members of reservation class
	 * showReservation and newReservation classes.
	 */
	public void initialize() {
		System.out.println("Initializing the Reservation class members.");
		
		reservationNumber = "";
		documentId = "";
		name = "";
		surname = "";		
		reservationDate = new Date();
		issueDate = "";
		warningText = "";
		
		//setHoursOnCalendarChange();//For the initial date
		showReservation.initialize(); 
		newReservation.initialize();

	}
	//////////////////////////////////////////////////////////////////////////////////////
	// Database Related Methods //////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////	
	/*
	 * This function is an example of calling PL SQL Function via Hibernate.
	 * See the DatabaseOperations.getReservationByResNum function to see the method in Model class.
	 */
	public void getReservationByResNum() {
		System.out.println("Entered getReservationByResNum() Method");
		dbObj.getReservationtByResNum();		
		System.out.println("showreserveation.getIsReservationFetched() : "+ showReservation.getIsReservationFetched());
	}
	/*
	 * This function is an example of calling PL SQL Function via Hibernate.
	 * See the DatabaseOperations.getReservationByResNum function to see the method in Model class.
	 */
	public void getReservationByDocID() {
		System.out.println("Entered getReservationByDocID() Method");
		dbObj.getReservationtByDocID();
		System.out.println("showreserveation.getIsReservationFetched() : "+ showReservation.getIsReservationFetched());

	}
	/*
	 * Hibernate utility is used here to insert the new record to database 
	 * 
	 */
	public void saveReservation() {

		System.out.println("Entered saveReservation() Method");
		reservationNumber = Util.reservationNumberGenerator();
		reservationDate = Util.stringToDateFormat(Util.dateToStringFormat(reservationDate,"dd-MM-yyyy")
				+" "
				+ newReservation.getSelectedHour().trim(),"dd-MM-yyyy HH:mm");
		logger.info("reservation date is : " + reservationDate);
		//reservationDate = "18-02-2018 12:00";		
		//documentId = "138";
		System.out.println(this.toString());
		
		dbObj.insertReservation();
	}

	public void updateReservation() {
		System.out.println("Entered updateReservation() Method");
		reservationDate = Util.stringToDateFormat(Util.dateToStringFormat(reservationDate,"dd-MM-yyyy")
				+" "
				+ newReservation.getSelectedHour().trim(),"dd-MM-yyyy HH:mm");
		logger.info("reservation date is : " + reservationDate);
		//reservationDate = "18-02-2018 12:00";		
		//documentId = "138";
		System.out.println(this.toString());
		dbObj.updateReservation(); 
	}
	
	public void removeReservation() {
		System.out.println("Entered removeReservation() Method");
		dbObj.deleteReservation();
	}	

	public void setHoursOnCalendarChange() {
		logger.info("Entered to the method getFreeHoursOfDay");
		ArrayList<String> returnList = new ArrayList<String>();
		returnList = dbObj.getFreeHoursOfDay(Util.dateToStringFormat(reservationDate,"dd-MM-yyyy"));
		newReservation.setHoursList(returnList);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Navigation and Update Form Methods ////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////

	public String navigateToShowReservation() {
		System.out.println("Entered navigateToShowReservation() Method");
		warningText="";
		
		if(showReservation.isReservationAlreadyCreated())//Since the query returned with existing reservation, 
		//Adopting the showreservation page by getting reservation with current info on page
		{
			System.out.println("Existing reservation detected, information will be fetched from database before redirecting");
			getReservationByDocID();
			showReservation.setReservationAlreadyCreated(false); 
			showReservation.setReservationFetched(true);
			return "ShowReservation?faces-redirect=true";
		}else
		{
			initialize();
			return "ShowReservation?faces-redirect=true";
		}
	}
	
	public String navigateToModifyReservation() {
		System.out.println("Entered navigateToModifyReservation() Method");
		return "ModifyReservation";
	}
	
	public String navigateToNewReservation() {
		
		System.out.println("Entered to navigateToNewReservation function");			
		initialize();
		return "NewReservation?faces-redirect=true";
	}
	/*
	 * This function will update the resultForm that is common for all .xhtml pages
	 * So the content will be uptated depending on the current instance with the same name 
	 * resultForm
	 */
	public void updateResultsForm() {
		System.out.println("Updating item resultForm");
		RequestContext.getCurrentInstance().update("resultForm");		
	}
	
	public void updateHoursPanel() {
		logger.info("Updating item NewReservation.hoursPanel");
		RequestContext.getCurrentInstance().update("hoursPanel");	
	}
	//////////////////////////////////////////////////////////////////////////////////////
	// Setter and Getter Methods /////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	
	public String getReservationNumber() {
		return reservationNumber;
	}

	public void setReservationNumber(String reservationNumber) {
		this.reservationNumber = reservationNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Date getReservationDate() {
		return reservationDate;
	}

	public void setReservationDate(Date reservationDate) {

		this.reservationDate = reservationDate;
		
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		
		this.issueDate = issueDate;
		
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	public String getWarningText() {
		return warningText;
	}

	public void setWarningText(String warningText) {
		this.warningText = warningText;
	}
		
	public ShowReservation getShowReservation() {
		return showReservation;
	}
	

	public String toString() {
		  StringBuilder result = new StringBuilder();
		  String newLine = System.getProperty("line.separator");
		
		  result.append( this.getClass().getName() );
		  result.append( " Object {" );
		  result.append(newLine);
		
		  //determine fields declared in this class only (no fields of superclass)
		  Field[] fields = this.getClass().getDeclaredFields();
		
		  //print field names paired with their values
		  for ( Field field : fields  ) {
		    result.append("  ");
		try {
		  result.append( field.getName() );
		  result.append(": ");
		  //requires access to private field:
		      result.append( field.get(this) );
		    } catch ( IllegalAccessException ex ) {
		      System.out.println(ex);
		    }
		    result.append(newLine);
		  }
		  result.append("}");
		
		  return result.toString();
	}


}
