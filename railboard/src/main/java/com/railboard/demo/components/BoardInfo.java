package com.railboard.demo.components;

import org.springframework.stereotype.Component;

@Component
public class BoardInfo {
	private boolean timepoint;
	private String carrier;
	private String arrivalTime;
	private String departureTime;
	private String predictedArrivalTime;
	private String predictedDepartureTime;
	private String destination;
	private String train;
	private String track;
	private String status;
	private String scheduledId;
	private String predictionId;
	private String routeId;
	private String tripId;
	private String stopId;
	/* TODO add direction id and sequence id */
	
	/**
	 * @return the timepoint
	 */
	public boolean isTimepoint() {
		return timepoint;
	}

	/**
	 * @param timepoint the timepoint to set
	 */
	public void setTimepoint(boolean timepoint) {
		this.timepoint = timepoint;
	}
	
	/**
	 * @return the carrier
	 */
	public String getCarrier() {
		return carrier;
	}
	
	/**
	 * @param carrier the carrier to set
	 */
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	/**
	 * @return the arrivalTime
	 */
	public String getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * @param arrivalTime the arrivalTime to set
	 */
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * @return the departureTime
	 */
	public String getDepartureTime() {
		return departureTime;
	}

	/**
	 * @param departureTime the departureTime to set
	 */
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	/**
	 * @return the predictedArrivalTime
	 */
	public String getPredictedArrivalTime() {
		return predictedArrivalTime;
	}

	/**
	 * @param predictedArrivalTime the predictedArrivalTime to set
	 */
	public void setPredictedArrivalTime(String predictedArrivalTime) {
		this.predictedArrivalTime = predictedArrivalTime;
	}

	/**
	 * @return the predictedDepartureTime
	 */
	public String getPredictedDepartureTime() {
		return predictedDepartureTime;
	}

	/**
	 * @param predictedDepartureTime the predictedDepartureTime to set
	 */
	public void setPredictedDepartureTime(String predictedDepartureTime) {
		this.predictedDepartureTime = predictedDepartureTime;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the train
	 */
	public String getTrain() {
		return train;
	}

	/**
	 * @param train the train to set
	 */
	public void setTrain(String train) {
		this.train = train;
	}

	/**
	 * @return the track
	 */
	public String getTrack() {
		return track;
	}

	/**
	 * @param track the track to set
	 */
	public void setTrack(String track) {
		this.track = track;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the scheduledId
	 */
	public String getScheduledId() {
		return scheduledId;
	}

	/**
	 * @param scheduledId the scheduledId to set
	 */
	public void setScheduledId(String scheduledId) {
		this.scheduledId = scheduledId;
	}

	/**
	 * @return the predictionId
	 */
	public String getPredictionId() {
		return predictionId;
	}

	/**
	 * @param predictionId the predictionId to set
	 */
	public void setPredictionId(String predictionId) {
		this.predictionId = predictionId;
	}

	/**
	 * @return the routeId
	 */
	public String getRouteId() {
		return routeId;
	}

	/**
	 * @param routeId the routeId to set
	 */
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	
	/**
	 * @return the tripId
	 */
	public String getTripId() {
		return tripId;
	}

	/**
	 * @param tripId the tripId to set
	 */
	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	/**
	 * @return the stopId
	 */
	public String getStopId() {
		return stopId;
	}

	/**
	 * @param stopId the stopId to set
	 */
	public void setStopId(String stopId) {
		this.stopId = stopId;
	}
}
