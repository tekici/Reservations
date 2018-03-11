package com.reservation.pojo;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

@ManagedBean(name="showreservation", eager = true)
@SessionScoped
public class ShowReservation implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isReservationAlreadyCreated;
	private boolean isReservationFetched;
	private String queryWith;
	private String databaseQueryFunction;
	private static Logger logger;
	
	public ShowReservation() {

		initialize();
	}
	
	public void initialize() {

		//logger.info("Entered to initialize");
		System.out.println("Initializing the ShowReservation Class");
		queryWith = new String("resnum");
		isReservationAlreadyCreated = false;
		isReservationFetched = false;
	}
	
	public boolean verifyQueryType(String str) {
		
		if (queryWith.equals(str)) {
			return true;
		}
		return false;
		
	}
	//////////////////////////////////////////////////////////////////////////////////////
	// Setter and Getter Methods /////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	
	public boolean isReservationAlreadyCreated() {
		return isReservationAlreadyCreated;
	}

	public void setReservationAlreadyCreated(boolean isReservationAlreadyCreated) {
		this.isReservationAlreadyCreated = isReservationAlreadyCreated;
	}
		
	public boolean getIsReservationFetched() {
		return isReservationFetched;
	}

	public void setReservationFetched(boolean isReservationFetched) {
		this.isReservationFetched = isReservationFetched;
	}

	public String getQueryWith() {
		return this.queryWith;
	}

	public void setQueryWith(String queryWith) {
		this.queryWith = queryWith;
	}

	public String getDatabaseQueryFunction() {
		return databaseQueryFunction;
	}

	public void setDatabaseQueryFunction(String databaseQueryFunction) {
		this.databaseQueryFunction = databaseQueryFunction;
	}

}