package com.reservation.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

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
	
	public DatabaseOperations() {
		logger = Logger.getLogger("DBO");
	}	
	
	public void insertReservation(Reservation reservation) {
		
		System.out.println("Saving Reservation With Reservation Number: " + reservation.getReservationNumber());
		
		sessionObj.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {

				try {
					CallableStatement call = connection.prepareCall("{? = call TURGAY.FUNRES_INSERTRESERVATION(?,?,?,?,?)}");
					call.registerOutParameter(1,OracleTypes.INTEGER);
					call.setString(2, reservation.getReservationNumber());
					call.setString(3, reservation.getName());
					call.setString(4, reservation.getSurname());
					call.setString(5, reservation.getReservationDate());
					call.setString(6, reservation.getDocumentId());
					call.execute();
					int returnResult = (int)call.getObject(1);
					
					reservation.setAsReservationAlreadyCreated(false);
					switch (returnResult)
					{					
						case 1://Date is Occupied. Can occur when another user allocated that date&hour 
							   //while current user was in process to create a new reservation.
						{
							logger.warning("[SQL] Unique constraint violation [ReservationDate]");
							reservation.setWarningText("Sorry, the selected date&time is occupied.");
							//reservation.setShowReservationAction("#{reservation.navigateToShowReservationInit}");
							//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("showReservationAction",  "#{reservation.navigateToShowReservationInit}");							
						}
						case 2://There is already a reservation for the given DocumentID. 
							   //Can occur when the user already have a reservation.
						{
							logger.warning("[SQL] Unique constraint violation [DocumentID]");
							reservation.setWarningText("You already have a reservation. Please click \"Show Reservation\" button to see.");
							reservation.setAsReservationAlreadyCreated(true);
							//reservation.setShowReservationAction("#{reservation.navigateToShowReservationAfterQuery}");
							//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("showReservationAction",  "#{reservation.navigateToShowReservationAfterQuery}");
						}
					}
					
					
					}catch (Exception ex) 
					{
						logger.severe("[SQL] Exception is thrown while executing [TURGAY.FUNRES_GETRESERVATIONBYRESNUM] for ResNum: " 
						+ reservation.getReservationNumber());
						reservation.setWarningText("An Error Occurred while creating your reservation. Please contact with Administrator.");						
						ex.printStackTrace();
					}
				
			}
			
		});
		
		
	}
	
	public SQLExceptionType addReservation(Reservation reservation) {	
		
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
	public void getReservationtByResNum(Reservation reservation) {	
		
		String resNum = reservation.getReservationNumber();
		System.out.println("Getting reservation by ReservationNumber : " + resNum);
		
		sessionObj.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {

				try {
					CallableStatement call = connection.prepareCall("{? = call TURGAY.FUNRES_GETRESERVATIONBYRESNUM(?)}");
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
					    reservation.setReservationDate(cursor.getString(5));
					    reservation.setIssueDate(cursor.getString(6));
					    reservation.setDocumentId(cursor.getString(7));			
					    
					    reservation.setWarningText("");
					}
					else {
						logger.info("[SQL Query Result is empty]");
						reservation.setWarningText("Could not find the reservation");
					}
					//while(cursor.next()) {}
				}catch (Exception ex) 
				{
					logger.severe("[SQL] Exception is thrown while executing [TURGAY.FUNRES_GETRESERVATIONBYRESNUM] for Reservation Num: "
					+ resNum);
					reservation.setWarningText("An Error Occurred while getting the reservation. Please contact with Administrator.");
					ex.printStackTrace();
				}
			}
			
		});
		
	}
	/*
	 * Using the PL SQL Function FUNRES_GETRESERVATIONBYDOCID 
	 * 
	 */
	public void getReservationtByDocID(Reservation reservation) {	
		
		String docID = reservation.getDocumentId();
		System.out.println("Getting reservation by DocumentID : " + docID);
		
		sessionObj.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {

				try {
					CallableStatement call = connection.prepareCall("{? = call TURGAY.FUNRES_GETRESERVATIONBYDOCID(?)}");
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
					    reservation.setReservationDate(cursor.getString(5));
					    reservation.setIssueDate(cursor.getString(6));
					    reservation.setDocumentId(cursor.getString(7));			
					    
					    reservation.setWarningText("");
					}
					else {
						logger.info("[SQL Query Result is empty]");
						reservation.setWarningText("Could not find the reservation");
					}
				}catch (Exception ex) 
				{
					logger.severe("[SQL] Exception is thrown while executing [TURGAY.FUNRES_GETRESERVATIONBYDOCID] for Document ID: "
					+ docID);
					reservation.setWarningText("An Error Occurred while getting the reservation. Please contact with Administrator.");
					ex.printStackTrace();
				}
			}
			
		});
		
	}
	public void updateReservation(Reservation reservation) {

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
	
	public void deleteReservation(Reservation reservation) {

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

}