package com.railboard.demo.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.railboard.demo.components.BoardInfo;
import com.railboard.demo.constants.StringConstants;

@Component
@Scope(value="prototype", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class MassDotServiceImpl implements MassDotService {

	protected final Map<String, BoardInfo> boardInfoMap = new HashMap<>();
	protected final static int carrierIndex = 0;
	protected final static int departureIndex = 1;
	protected final static int destinationIndex = 2;
	protected final static int trainIndex = 3;
	protected final static int trackIndex = 4;
	protected final static int statusIndex = 5;
	private static CloseableHttpClient httpClient = HttpClients.createDefault();
	/** 
	 * Creates the information to be displayed on the web page
	 */
	@Override
	public void getBoardInfo(ModelAndView mav, Map<String, String> filter) {
		
		if (isStation(filter)) {
			boardInfoMap.clear();
			mav.addObject(StringConstants.STATION, mapStation(filter.get(StringConstants.STATION)));
			mav.addObject(StringConstants.BOARD_TABLE_HEADER, getTableHeader(filter));
			mav.addObject(StringConstants.BOARD_TABLE_INFO, retrieveBoardInfo(filter));
		}
	}
	
	/**
	 * wrapper to retrieve the information from apiv3 as json
	 * @param Map<String, String> filter info to be added to the aviv3 url
	 * @return
	 */
	protected String retrieveBordInfoJSON(Map<String, String> filter) {
		String apiOutput = null;
		try {
			apiOutput = callAVIv3(mapAPIv3URI(filter));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

		return apiOutput;
	}

	/**
	 * parses the predictions json to get some values for view table,
	 * status and assumed track#,
	 * and sets them into the BoardInfo object
	 * @param jsonString
	 * @param limit
	 * @param String idContainFilter -- assumed CR as a good filter to get only the commuter rail objects
	 */
	@SuppressWarnings("unchecked")
	protected void jsonToBoardInfoMapByPredictions(String jsonString, int limit, String idContainFilter) {
		/* assumed prediction is unique per schedule */
		
		if (jsonString != null) {
			int count = 0;
			
			Map<String, Object> javaRootMap = new Gson().fromJson(jsonString, Map.class);
			if (javaRootMap != null) {
				List<Object> dataList = (List<Object>) javaRootMap.get(StringConstants.JSON_DATA);
				 
				for (int i = 0; i < dataList.size(); ++i) {
					Map<String, Object> dataMap = (Map<String, Object>) dataList.get(i);
					
					if (dataMap != null) {
						Map<String, Object> relationships = (Map<String, Object>) dataMap.get(StringConstants.JSON_RELATIONSHIPS);
						
						String scheduleId = getRelationId(relationships, StringConstants.JSON_SCHEDULE_TYPE);
						
						if (scheduleId != null && scheduleId.contains(idContainFilter)) {
							Map<String, Object> attributes = (Map<String, Object>) dataMap.get(StringConstants.JSON_ATTRIBUTES);
							String predictionId = (String) dataMap.get(StringConstants.JSON_ID);
							if (attributes != null) {
								BoardInfo boardInfo = new BoardInfo();
								setPredictionAttributes(boardInfo, attributes, scheduleId, predictionId);
								boardInfoMap.put(scheduleId, boardInfo);
								count++;
							}
						}
					
						if (count >= limit && limit > 0) {
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * save prediction values in BoardInfo object -- refactored 
	 * @param boardInfo
	 * @param attributes
	 * @param scheduleId
	 * @param predictionId
	 */
	protected void setPredictionAttributes (BoardInfo boardInfo, Map<String, Object> attributes, String scheduleId, String predictionId) {
		if (attributes != null) {
			boardInfo.setScheduledId(scheduleId);
			boardInfo.setPredictionId(predictionId);
			boardInfo.setStatus((String) attributes.get(StringConstants.JSON_STATUS));
			boardInfo.setPredictedArrivalTime((String) attributes.get(StringConstants.JSON_ARRIVAL_TIME));
			boardInfo.setPredictedDepartureTime((String) attributes.get(StringConstants.JSON_DEPARTURE_TIME));
		}
	}
	
	/**
	 * helper -- parses json obtained maps in order to get the relationships Ids 
	 * @param relationships
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String getRelationId(Map<String, Object> relationships, String type) {
		String id = null; 
		if (relationships != null) {
			Map<String, Object> relation = (Map<String, Object>) relationships.get(type);
			if (relation != null) {
				Map<String, Object> data = (Map<String, Object>) relation.get(StringConstants.JSON_DATA);
				if (data != null) {
					id = (String) data.get("id");
				}
			}
		}
	
		return id;
	}
	
	/**
	 * parses trip json to get assumed information like destination and train name
	 * and sets it to BoardInfo object
	 * @param jsonString
	 * @param boardInfo
	 */
	@SuppressWarnings("unchecked")
	protected void jsonToBoardInfoByTrip(String jsonString, BoardInfo boardInfo) {
		if (jsonString != null) {
			Map<String, Object> javaRootMap = new Gson().fromJson(jsonString, Map.class);
			if (javaRootMap != null) {
				Object data = javaRootMap.get(StringConstants.JSON_DATA);
				Map<String, Object> dataMap = (Map<String, Object>) data;
				if (dataMap != null) {
					Map<String, Object> attributes = (Map<String, Object>) dataMap.get(StringConstants.JSON_ATTRIBUTES);
					if (attributes != null) {
						Double direction = (Double) attributes.get(StringConstants.JSON_DIRECTION);
						boardInfo.setTimepoint(direction > 0 ? false : true);
						String destination = (String) attributes.get(StringConstants.JSON_HEADSIGN);
						boardInfo.setDestination(destination != null ? destination : StringConstants.IKR);
						String train = (String) attributes.get(StringConstants.JSON_NAME);
						boardInfo.setTrain(train != null ? train : StringConstants.IKR);
					}
				}
			}
		}
	}
	
	/**
	 * wrapper to get and parse individual trip info
	 * @param tripId
	 * @param boardInfo
	 */
	protected void getInfoFromTrip(String tripId, BoardInfo boardInfo) {
		if (tripId != null) {
			Map<String, String> filter = new HashMap<>();
			filter.put(StringConstants.RESOURCE_TYPE_KEY, StringConstants.TRIPS);
			filter.put(StringConstants.RESOURCE_ID_KEY, tripId);
			jsonToBoardInfoByTrip(retrieveBordInfoJSON(filter), boardInfo);
		}
		
		if (boardInfo.getTrain() == null) {
			boardInfo.setTrain(StringConstants.IKR);
		}
		
		if (boardInfo.getDestination() == null) {
			boardInfo.setDestination(StringConstants.IKR);
		}
	}
	
	protected Integer getIntergerFromString(String s) {
		Integer i = null;
		if (NumberUtils.isDigits(s)) {
			i = Integer.parseInt(s);
		}
		
		return i;
	}
	
	/**
	 * parses schedule information mapped from json and uses prediction Id, if any, to assume the track#
	 * @param boardInfo
	 * @param attributes
	 * @param relationships
	 * @param scheduleId
	 * @param departureTime
	 */
	protected void setScheduleAttributesAndRelationships (BoardInfo boardInfo, Map<String, Object> attributes, 
														Map<String, Object> relationships, String scheduleId, String departureTime) {
		if (attributes != null) {
			boardInfo.setScheduledId(scheduleId);
			boardInfo.setTimepoint(true);
			boardInfo.setArrivalTime((String) attributes.get(StringConstants.JSON_ARRIVAL_TIME));
			boardInfo.setDepartureTime(departureTime);
			boardInfo.setTrack(StringConstants.IKR_ALT);
			if (boardInfo.getPredictionId() != null) {
				String[] pred = boardInfo.getPredictionId().split("-");
				if (pred.length > 1 && pred[pred.length - 2] != null) {
					String assumedTrackId = pred[pred.length - 2];
					Integer track = getIntergerFromString(assumedTrackId);
					/*
					if ( track == null || track == 0) {
						assumedTrackId = pred[pred.length - 1];
						track = getIntergerFromString(assumedTrackId);
					}
					*/
					if ( track != null && track != 0) {
						boardInfo.setTrack(String.valueOf(track));
					}
				}
			}
		}
		
		boardInfo.setRouteId(getRelationId(relationships, StringConstants.JSON_ROUTE_TYPE));
		boardInfo.setTripId(getRelationId(relationships, StringConstants.JSON_TRIP_TYPE));
		boardInfo.setStopId(getRelationId(relationships, StringConstants.JSON_STOP_TYPE));
		getInfoFromTrip(boardInfo.getTripId(), boardInfo);
		boardInfo.setCarrier(boardInfo.getRouteId() != null ? boardInfo.getRouteId() : StringConstants.IKR);
	}
	
	/**
	 * parses schedules json and sets information in BoardInfo;
	 * if the object was already created by prediction, uses that one, if not creates a new one
	 * @param jsonString
	 * @param limit
	 * @param idContainFilter
	 */
	@SuppressWarnings("unchecked")
	protected void jsonToBoardInfoMapBySchedules(String jsonString, int limit, String idContainFilter) {
	
		if (jsonString != null) {
			int count = 0;
		
			Map<String, Object> javaRootMap = new Gson().fromJson(jsonString, Map.class);
			if (javaRootMap != null) {
				List<Object> dataList = (List<Object>) javaRootMap.get(StringConstants.JSON_DATA);
				 
				for (int i = 0; i < dataList.size(); ++i) {
					Map<String, Object> dataMap = (Map<String, Object>) dataList.get(i);
					
					if (dataMap != null) {
						Map<String, Object> relationships = (Map<String, Object>) dataMap.get(StringConstants.JSON_RELATIONSHIPS);
						Map<String, Object> attributes = (Map<String, Object>) dataMap.get(StringConstants.JSON_ATTRIBUTES);
						String scheduleId = (String) dataMap.get(StringConstants.JSON_ID);
						Boolean timepoint = (Boolean) attributes.get(StringConstants.JSON_TIMEPOINT);  /* qualifies for method argument filter use/not */
						String departureTime = (String) attributes.get(StringConstants.JSON_DEPARTURE_TIME);
						if (scheduleId != null && departureTime != null && (idContainFilter == null || scheduleId.contains(idContainFilter)) && timepoint) {
							BoardInfo boardInfo = boardInfoMap.get(scheduleId);
							if (boardInfo == null) {
								boardInfo = new BoardInfo();
							}
							
							setScheduleAttributesAndRelationships(boardInfo, attributes, relationships, scheduleId, departureTime);
							
							if (boardInfo.isTimepoint()) {
								boardInfoMap.put(scheduleId, boardInfo);
								System.out.println(boardInfo.getTrain() + " :in");
								count++;
							}
							
							System.out.println(boardInfo.getTrain() + " :out");
						}	
					}
					
					if (count >= limit && limit > 0) {
						break;
					}
				}
			}
		}
	}
	
	/**
	 * builds a 6 string array to be sent to view
	 * @return
	 */
	protected String[] buildViewDataAsSixColumns() {
		return new String[6];
	}
	
	/**
	 * ignores orphaned records generated in prediction phase
	 * @return
	 */
	protected int getValidSchedulesCount() {
		int count = 0;
		for (BoardInfo value : boardInfoMap.values()) {
			if (value.isTimepoint()) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * converts the map of BoardInfo objects to an array of strings, to be sent to view;
	 * probably not necessary!!!
	 * resort by departure time and convert the time from full date to HH:MM
	 * @return
	 */
	protected List<String[]> getSchedulesForViewing() {
		List<String[]> viewList = new ArrayList<>();
		for (BoardInfo value : boardInfoMap.values()) {
			if (value.isTimepoint()) /* remove orphans */ {
				String[] viewData = buildViewDataAsSixColumns();
				viewData[carrierIndex] = value.getCarrier();
				viewData[departureIndex] = value.getDepartureTime();
				viewData[destinationIndex] = value.getDestination();
				viewData[trainIndex] = value.getTrain();
				viewData[trackIndex] = value.getTrack();
				viewData[statusIndex] = value.getStatus();
				
				viewList.add(viewData);
			}
		}
		
		Collections.sort(viewList, new SortByDepartureAsc(departureIndex));
		formatDepartureTime(viewList, departureIndex);
		
		return viewList;
	}
	
	/**
	 * convert the time sent in json to HH:MM A
	 * @param viewList
	 * @param index -- keeps trak of the string that contains the departure time
	 */
	protected void formatDepartureTime(List<String[]> viewList, int index) {
		if (viewList != null) {
			for (int i = 0; i < viewList.size(); ++i) {
				String[] value = viewList.get(i);
				value[index] = DateUtils.covertToTime(value[index]);
			}
		}
	}
	
	/**
	 * implements the business rule;
	 * 1. get predictions and save info in BoardInfo
	 * 2. get schedules for today starting with current time
	 * 3. if not enough trains (default up to 18 trains on the board) get from tomorrow
	 * @param filter
	 * @return
	 */
	protected List<String[]> retrieveBoardInfo(Map<String, String> filter) {
		String jsonInfo = null;
		int maxItemsCount = 18;
		
		//get predictions
		filter.put(StringConstants.RESOURCE_TYPE_KEY, StringConstants.PREDICTIONS);
		jsonInfo = retrieveBordInfoJSON(filter);
		jsonToBoardInfoMapByPredictions(jsonInfo, 0,  StringConstants.COMMUTER_RAIL);
		
		//get schedules for today
		filter.put(StringConstants.MIN_TIME, DateUtils.getMinTimeFilter());
		filter.put(StringConstants.RESOURCE_TYPE_KEY, StringConstants.SCHEDULES);
		jsonInfo = retrieveBordInfoJSON(filter);
		jsonToBoardInfoMapBySchedules(jsonInfo, maxItemsCount, StringConstants.COMMUTER_RAIL);
		
		return getSchedulesForViewing();
	}
	
	/**
	 * creates the table header info dynamically
	 * @param filter
	 * @return
	 */
	public List<String> getTableHeader(Map<String, String> filter) {
		ArrayList<String> header = new ArrayList<>();
		
		if (isDepartures(filter)) {
			header.add(StringConstants.CARRIER);
			header.add(StringConstants.TIME);
			header.add(StringConstants.DESTINATION);
			header.add(StringConstants.TRAIN);
			header.add(StringConstants.TRACK);
			header.add(StringConstants.STATUS);
		}
		
		return header;
	}
	
	/**
	* stations are to be retrieved from api-v3 , for now only map them
	*/
	protected String mapStation(String station) {
		StringBuilder ret = new StringBuilder();
		ret.append(station).append(" ").append(StringConstants.STATION_INFORMATION);
		
		return ret.toString();
	}
	
	/**
	 * connects and retrieves the data from api-v3 -- no caching, no compression, no api-key for now;
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	protected String callAVIv3(URI url) throws ClientProtocolException, IOException {
		String apiOutput = null;
		HttpUriRequest getRequest = new HttpGet(url);
        CloseableHttpResponse massResponse = httpClient.execute(getRequest);
        int statusCode = massResponse.getStatusLine().getStatusCode();
        if (statusCode != 200)
        {
            throw new RuntimeException("Failed with HTTP error code : " + statusCode);
        }
            
        try {
        	//Lets see what we got from API 
	        HttpEntity httpEntity = massResponse.getEntity();
	        apiOutput = EntityUtils.toString(httpEntity);
        } finally {
        	massResponse.close();
        }
		return apiOutput;
	}
	
	/**
	 * maps some arguments into the api-v3 url
	 * @param filter
	 * @return
	 * @throws URISyntaxException
	 */
	protected URI mapAPIv3URI(Map<String, String> filter) throws URISyntaxException {
		StringBuilder url = new StringBuilder();
		String type = filter.get(StringConstants.RESOURCE_TYPE_KEY);
		String id = filter.get(StringConstants.RESOURCE_ID_KEY);
		String stop = getStop(filter);
		
		if (type != null) {
			if (StringConstants.PREDICTIONS.equals(type)) {
				url.append(StringConstants.APIV3_PREDICTIONS); 
			} else if (StringConstants.SCHEDULES.equals(type)) {
				url.append(StringConstants.APIV3_SCHEDULES);
			} else if (StringConstants.TRIPS.equals(type)) {
				url.append(StringConstants.APIV3_TRIPS);
			} else if (StringConstants.STOPS.equals(type)) {
				url.append(StringConstants.APIV3_STOPS);
			} 

			if (id != null) {
				url.append("/").append(id).append(StringConstants.API_KEY);
			} else if (stop != null) {
				if (StringConstants.PREDICTIONS.equals(type)) {
					url.append(String.format(StringConstants.QUERY_PREDICTIONS, stop));
				} else if (StringConstants.SCHEDULES.equals(type)) {
					String minTime = filter.get(StringConstants.MIN_TIME);
					String date = filter.get(StringConstants.DATE);
					
					url.append(String.format(StringConstants.QUERY_SCHEDULES_FILTER, date != null ? date : DateUtils.getDateFilter(0), minTime != null ? minTime : "00:00", stop));
				}
			}
		}
		
		return new URI(url.toString());
	}
	
	/**
	 * station mapping or stop 
	 * @param filter
	 * @return
	 */
	protected String getStop(Map<String, String> filter) {
		String stop = StringConstants.NORTH_STATION_PLACE;
		
		if (filter.containsValue(StringConstants.SOUTH)) {
			stop = StringConstants.SOUTH_STATION_PLACE;
		}
		
		return stop;
	}
	
	protected boolean isStation(Map<String, String> filter) {
		return (filter != null && filter.containsKey(StringConstants.STATION));
	}
	
	protected boolean isDepartures(Map<String, String> filter) {
		return (filter != null && filter.containsValue(StringConstants.DEPARTURES));
	}
	
	/**
	 * comparator to sort the trains by departure time, view table with column sort 
	 *
	 */
	protected class SortByDepartureAsc implements Comparator<String[]> 
	{ 
		/* not null friendly */
		int index = departureIndex;
		
		public SortByDepartureAsc(int index) {
			this.index = index;
		}
		
	    @Override
		public int compare(String[] arg0, String[] arg1) {
			return arg0[index].compareTo(arg1[index]);
		} 
	}
}
