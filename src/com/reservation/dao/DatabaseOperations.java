package com.reservation.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.jdbc.Work;

import com.reservation.pojo.Reservation;
import com.reservation.util.HibernateUtil;
import com.reservation.util.SQLExceptionType;
import com.reservation.util.Util;

import oracle.jdbc.internal.OracleTypes;

public class DatabaseOperations {
	private static Transaction transObj;
	private static Session sessionObj = HibernateUtil.getSessionFactory().openSession();
	private Logger logger;	
	public Reservation reservation;
	
	public DatabaseOperations(Reservation reservation) {
		this.reservation = reservation;
		logger = Logger.getLogger("DBO");
	}	
	
	public void insertReservation() {
		
		System.out.println("Saving Reservation With Reservation Number: " + reservation.getReservationNumber());
		
		sessionObj.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {

				CallableStatement call = connection.prepareCall("{? = call TURGAY.FUNRES_INSERTRESERVATION(?,?,?,?,?)}");
				try {
					FacesContext context = FacesContext.getCurrentInstance();
					call.registerOutParameter(1,OracleTypes.INTEGER);
					call.setString(2, reservation.getReservationNumber());
					call.setString(3, reservation.getName());
					call.setString(4, reservation.getSurname());
					call.setString(5, Util.dateToStringFormat(reservation.getReservationDate(),"dd-MM-yyyy HH:mm"));
					call.setString(6, reservation.getDocumentId());
					call.execute();
					int returnResult = (int)call.getObject(1);
					
					reservation.showReservation.setReservationAlreadyCreated(false);
					
					switch (returnResult)
					{	
						case 0://Insert Success
						{
							logger.info("[SQL] Successfully inserted the record.");
							context.addMessage(null, new FacesMessage("Info",  "Your Reservation is Created Successfully.") );
							reservation.newReservation.setReservationSuccess(true);
							break;
						}
						case 1://Date is Occupied. Can occur when another user allocated that date&hour 
							   //while current user was in process to create a new reservation.
						{
							logger.warning("[SQL] Unique constraint violation [ReservationDate]");
							context.addMessage(null, new FacesMessage("Warning",  "Sorry, the selected date&time is occupied.") );
							break;
				
						}
						case 2://There is already a reservation for the given DocumentID. 
							   //Can occur when the user already have a reservation.
						{
							logger.warning("[SQL] Unique constraint violation [DocumentID]");
							context.addMessage(null, new FacesMessage("Warning",  "You already have a reservation. Please click \"Show Reservation\" button to see.") );
							reservation.showReservation.setReservationAlreadyCreated(true);
							break;
						}
						default :
						{
							logger.warning("[SQL] The return response could not be resolved: " + returnResult);
							context.addMessage(null, new FacesMessage("Warning",  "An Error Occurred while creating your reservation. Please contact with Administrator.") );							
						}
						
					}

					}catch (Exception ex) 
					{
						logger.severe("[SQL] Exception is thrown while executing [TURGAY.FUNRES_GETRESERVATIONBYRESNUM] for ResNum: " 
						+ reservation.getReservationNumber());
						FacesContext context = FacesContext.getCurrentInstance();
						context.addMessage(null, new FacesMessage("Warning",  "An Error Occurred while creating your reservation. Please contact with Administrator.") );
						
						ex.printStackTrace();
					}finally
					{
						connection.commit();	
					}
				
				reservation.updateResultsForm();//Updating the common results form to show the results
				
			}
			
		});
		
		
	}
	
	/*
	 * Add reservation via hibernate
	 * 
	 * Params : 
	 * (input) reservation : The reservation managed bean
	 * (output)sqlResult   : The result of insert that returned from database, and parsed with Util.parseException
	 * 						 in SQLExceptionType .
	 */
	public SQLExceptionType addReservation() {	
		
		System.out.println("Saving Reservation With Reservation Number: " + reservation.getReservationNumber());

		SQLExceptionType sqlResult = SQLExceptionType.Success;
		try {
			transObj = sessionObj.beginTransaction();
			sessionObj.save(reservation);
			
			// XHTML Response Text
			//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("createdStudentId",  studentObj.getId());						
		} catch (ConstraintViolationException exceptionObj) {
			System.out.println("catched exception");
			sqlResult = Util.parseException(exceptionObj.getMessage());
			//exceptionObj.printStackTrace();
		} finally {
			try {
			transObj.commit();
			}catch (ConstraintViolationException exceptionObj) {
				System.out.println("catched exception");
				sqlResult = Util.parseException(exceptionObj.getMessage());
				//exceptionObj.printStackTrace();
			}
		}
		
		return sqlResult;
	}
	
	/*
	 * Using the PL SQL Function FUNRES_GETRESERVATIONBYRESNUM 
	 * 
	 */
	public void getReservationtByResNum() {	
		
		String resNum = reservation.getReservationNumber();
		System.out.println("Getting reservation by ReservationNumber : " + resNum);
		
		sessionObj.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {

			    CallableStatement call = connection.prepareCall("{? = call TURGAY.FUNRES_GETRESERVATIONBYRESNUM(?)}");
				try {
					FacesContext context = FacesContext.getCurrentInstance();
				    reservation.showReservation.setReservationFetched(false);//Parameter for enabling/disabling the show view of the page
					
					call.registerOutParameter(1,OracleTypes.CURSOR);
					call.setString(2, resNum);
					call.execute();
					ResultSet cursor = (ResultSet)call.getObject(1);
					if (cursor.next())
					{
						logger.info("[SQL Results] \n" + 
						"ReservationNum: " + cursor.getString(2) + "\n" + 
						"Name: " + cursor.getString(3) + "\n" + 
						"Surname: " + cursor.getString(4) + "\n" + 
						"ReservationDate: " + cursor.getString(5) + "\n" + 
						"IssueDate: " + cursor.getString(6) + "\n" + 
						"DocumentID: " + cursor.getString(7) + "\n" );
					    
					    reservation.setReservationNumber(cursor.getString(2));
					    reservation.setName(cursor.getString(3));
					    reservation.setSurname(cursor.getString(4));
					    reservation.setReservationDate(Util.stringToDateFormat(cursor.getString(5),"yyyy-MM-dd HH:mm:ss"));
					    reservation.setIssueDate(cursor.getString(6));
					    reservation.setDocumentId(cursor.getString(7));			
					    
					    context.addMessage(null, new FacesMessage("Successful",  "Your Reservation Has Found") );
					    reservation.showReservation.setReservationFetched(true);//Parameter for enabling/disabling the show view of the page
					}
					else {
						logger.info("[SQL Query Result is empty]");
						context.addMessage(null, new FacesMessage("Warning",  "Could not find the reservation") );
					}
					//while(cursor.next()) {}
				}catch (Exception ex) 
				{
					logger.severe("[SQL] Exception is thrown while executing [TURGAY.FUNRES_GETRESERVATIONBYRESNUM] for Reservation Num: "
					+ resNum);
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage("Failure",  "An Error Occurred while getting the reservation. Please contact with Administrator.") );					
					ex.printStackTrace();
				}finally
				{
					connection.commit();					
				}
				reservation.updateResultsForm();//Updating the common results form to show the results
			}
			
		});
		
	}
	/*
	 * Using the PL SQL Function FUNRES_GETRESERVATIONBYDOCID 
	 * 
	 */
	public void getReservationtByDocID() {	
		
		String docID = reservation.getDocumentId();
		System.out.println("Getting reservation by DocumentID : " + docID);
		
		sessionObj.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				
				CallableStatement call = connection.prepareCall("{? = call TURGAY.FUNRES_GETRESERVATIONBYDOCID(?)}");
				try {
					FacesContext context = FacesContext.getCurrentInstance();
					reservation.showReservation.setReservationFetched(false);//Parameter for enabling/disabling the show view of the page	
					call.registerOutParameter(1,OracleTypes.CURSOR);
					call.setString(2, docID);
					call.execute();
					ResultSet cursor = (ResultSet)call.getObject(1);
					if (cursor.next())
					{
						logger.info("[SQL Results] \n" + 
						"ReservationNum: " + cursor.getString(2) + "\n" + 
						"Name: " + cursor.getString(3) + "\n" + 
						"Surname: " + cursor.getString(4) + "\n" + 
						"ReservationDate: " + cursor.getString(5) + "\n" + 
						"IssueDate: " + cursor.getString(6) + "\n" + 
						"DocumentID: " + cursor.getString(7) + "\n" );
					    
					    reservation.setReservationNumber(cursor.getString(2));
					    reservation.setName(cursor.getString(3));
					    reservation.setSurname(cursor.getString(4));
					    reservation.setReservationDate(Util.stringToDateFormat(cursor.getString(5),"yyyy-MM-dd HH:mm:ss"));
					    reservation.setIssueDate(cursor.getString(6));
					    reservation.setDocumentId(cursor.getString(7));			
					    
					    context.addMessage(null, new FacesMessage("Successful",  "Your Reservation Has Found") );
					    reservation.showReservation.setReservationFetched(true);//Parameter for enabling/disabling the show view of the page						
					    
					}
					else {
						logger.info("[SQL Query Result is empty]");
						context.addMessage(null, new FacesMessage("Warning",  "Could not find the reservation") );
					}
				}catch (Exception ex) 
				{
					logger.severe("[SQL] Exception is thrown while executing [TURGAY.FUNRES_GETRESERVATIONBYDOCID] for Document ID: "
					+ docID);
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage("Failure",  "An Error Occurred while getting the reservation. Please contact with Administrator.") );
					ex.printStackTrace();
				}finally
				{
					connection.commit();			
				}

				reservation.updateResultsForm();//Updating the common results form to show the results
			}
			
		});
		
	}
	public void updateReservation() {

		System.out.println("Reservation With Reservation Number: " + reservation.getReservationNumber() + " is being updated");
		
		try {
			transObj = sessionObj.beginTransaction();
			sessionObj.update(reservation);

		} catch (Exception exceptionObj) {
			//Here need to parse the exception to determine if it is a duplicate record, and return a value to the 
			//Reservation.java class for determining what to update on View jsp page.
			exceptionObj.printStackTrace();
		} finally {
			transObj.commit();
		}
		
	}
	public void deleteReservation() {

		System.out.println("Reservation With Reservation Number: " + reservation.getReservationNumber() + " is being updated");
		
		try {
			transObj = sessionObj.beginTransaction();
			sessionObj.delete(reservation);

		} catch (Exception exceptionObj) {
			//Here need to parse the exception to determine if it is a duplicate record, and return a value to the 
			//Reservation.java class for determining what to update on View jsp page.
			exceptionObj.printStackTrace();
		} finally {
			transObj.commit();
		}
		
	}
	/*
	 * Get an arraylist of hours that available for the specified date, by using SQL Function
	 * 
	 * Input  
	 * String Day : Have to be DD.MM.YYYY format
	 * Output
	 * ArrayList<String> : The array , each record represents an available hour to make reservation , for demanded day.
	 * 
	 */
	public ArrayList<String> getFreeHoursOfDay(String day) {
				
		ArrayList<String> resultList = new ArrayList<String>();
		
		logger.info("Getting the free hours for day : " + day);
		sessionObj.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
							
				CallableStatement call = connection.prepareCall("{? = call TURGAY.FUNRES_GETFREEHOURSOFDAY(?)}");
				try {
					reservation.newReservation.setHoursFetched(false);//Parameter for enabling/disabling the view of the page	
					call.registerOutParameter(1,OracleTypes.CURSOR);
					call.setString(2, day);
					call.execute();
					ResultSet cursor = (ResultSet)call.getObject(1);
					
					while(cursor.next())
					{
						String freeHour = cursor.getString(1);
					    resultList.add(freeHour);												
					}

					logger.info( "[SQL] Fetched Free Hours from DB : " + resultList);
					reservation.newReservation.setHoursFetched(true);
					
				}catch (Exception ex) 
				{
					logger.severe("[SQL] Exception is thrown while executing [TURGAY.FUNRES_GETFREEHOURSOFDAY] for day: "
					+ day);
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage("Failure",  "An Error Occurred while getting the reservation. Please contact with Administrator.") );
					ex.printStackTrace();
				}finally
				{
					connection.commit();			
				}
				
				reservation.updateHoursPanel();//Updating the common results form to show the results
			}
			
		});
		
		return resultList;
	}
}
