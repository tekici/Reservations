package com.reservation.pojo;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="showreservation")
@SessionScoped
public class ShowReservation implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isReservationAlreadyCreated;
	private String sayWelcome = "Welcome to JSF 2.0";

	public String getSayWelcome() {
		return sayWelcome;
	}

	public void setSayWelcome(String sayWelcome) {
		this.sayWelcome = sayWelcome;
	}
	
	public boolean isReservationAlreadyCreated() {
		return isReservationAlreadyCreated;
	}

	public void setReservationAlreadyCreated(boolean isReservationAlreadyCreated) {
		this.isReservationAlreadyCreated = isReservationAlreadyCreated;
	}
	
	public void initialize() {
		isReservationAlreadyCreated = false;
	}

}