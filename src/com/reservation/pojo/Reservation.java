package com.reservation.pojo;

import java.lang.reflect.Field;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.reservation.dao.*;
import com.reservation.util.Util;
import com.sun.faces.context.SessionMap;

@ManagedBean(name = "reservation", eager = true)
@SessionScoped
public class Reservation {

	
	private String reservationNumber;
	private String name;
	private String surname;
	private String reservationDate;
	private String issueDate;
	private String documentId;
	
	private String warningText;
	public static DatabaseOperations dbObj;
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value="#{showreservation}")
	public ShowReservation showReservation;

	public void setShowReservation(ShowReservation showRes) {
		this.showReservation = showRes;
	}
	

	public Reservation(){
		System.out.println("Initializing the Reservation Class");
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
		dbObj = new DatabaseOperations();		
		dbObj.getReservationtByResNum(this);		
	}
	/*
	 * This function is an example of calling PL SQL Function via Hibernate.
	 * See the DatabaseOperations.getReservationByResNum function to see the method in Model class.
	 */
	public void getReservationByDocID() {
		System.out.println("Entered getReservationByDocID() Method");
		dbObj = new DatabaseOperations();		
		dbObj.getReservationtByDocID(this);
	}
	/*
	 * Hibernate utility is used here to insert the new record to database 
	 * 
	 */
	public void saveReservation() {
		
		reservationNumber = Util.reservationNumberGenerator();
		reservationDate = "18-02-2018 12:00";		
		//documentId = "138";
		System.out.println(this.toString());
		
		System.out.println("Entered saveReservation() Method");
		dbObj = new DatabaseOperations();
		dbObj.insertReservation(this);
	}

	public void updateReservation() {
		System.out.println("Entered removeReservation() Method");
		dbObj = new DatabaseOperations();
		dbObj.updateReservation(this); 
	}
	
	public void removeReservation() {
		System.out.println("Entered removeReservation() Method");
		dbObj = new DatabaseOperations();
		dbObj.deleteReservation(this);
	}	


	//////////////////////////////////////////////////////////////////////////////////////
	// Navigation Methods ////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////

	public String navigateToShowReservation() {
		System.out.println("Entered navigateToShowReservation() Method");
		warningText="";
		//initialize();
		//showReservation.setSayWelcome("asd");
		if(showReservation.isReservationAlreadyCreated())//Since the query returned with existing reservation, 
									   //Adopting the showreservation page by getting reservation with current info on page
		{
			System.out.println("Existing reservation detected, information will be fetched from database before redirecting");
			getReservationByDocID();
			showReservation.setReservationAlreadyCreated(false); 
			return "ShowReservation";
		}else
		{
			initialize();
			return "ShowReservation";
		}
	}
	
	
	public String navigateToNewReservation() {
		
		System.out.println("Entered to navigateToNewReservation function");			
		initialize();
		return "NewReservation";
	}
	
	public void initialize() {
		System.out.println("Initializing the Reservation class members.");
		
		reservationNumber = "";
		documentId = "";
		name = "";
		surname = "";		
		reservationDate = "";
		issueDate = "";
		warningText = "";
		showReservation.initialize(); 
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

	public String getReservationDate() {
		return reservationDate;
	}

	public void setReservationDate(String reservationDate) {

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
