package com.reservation.pojo;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="newreservation", eager = true)
@SessionScoped
public class NewReservation implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger;
	
	private ArrayList<String> hoursList;
	private String selectedHour;
	private boolean isHoursFetched;
	private boolean isReservationSuccess;
		
	public NewReservation(){

		logger = Logger.getLogger("NewReservation");
		
	}
	
	public void initialize() {
		System.out.println("Initializing the NewReservation Class");
		isReservationSuccess = false;		
	}
	
	public boolean isHoursFetched() {
		return isHoursFetched;
	}

	public void setHoursFetched(boolean isHoursFetched) {
		this.isHoursFetched = isHoursFetched;
	}

	public boolean isReservationSuccess() {
		return isReservationSuccess;
	}

	public void setReservationSuccess(boolean isReservationSuccess) {
		this.isReservationSuccess = isReservationSuccess;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Setter and Getter Methods /////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	public ArrayList<String> getHoursList() {
		return hoursList;
	}

	public void setHoursList(ArrayList<String> hoursList) {
		this.hoursList = hoursList;
	}

	public String getSelectedHour() {
		return selectedHour;
	}

	public void setSelectedHour(String selectedHour) {
		this.selectedHour = selectedHour;
	}


}
