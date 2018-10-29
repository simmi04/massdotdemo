package com.railboard.demo.constants;

public final class StringConstants {
	/* the mother of all solutions */
	public final static String IKR = "N/A";
	public final static String IKR_ALT = "TBD";
	
	/* column names */
	public final static String CARRIER = "CARRIER";
	public final static String TIME = "TIME";
	public final static String DESTINATION = "DESTINATION";
	public final static String TRAIN = "TRAIN#";
	public final static String TRACK = "TRACK#";
	public final static String STATUS = "STATUS";
	
	/* view variables */
	public final static String STATION = "station";
	public final static String BOARD_TABLE_COL_NR = "boardtablecolnr";
	public final static String BOARD_TABLE_HEADER = "boardtableheader";
	public final static String BOARD_TABLE_INFO = "boardtableinfo";
	
	/* view data */
	public final static String NORTH = "NORTH"; //to be retrieved from api-v3
	public final static String SOUTH = "SOUTH"; //to be retrieved from api-v3
	public final static String TEMP = "TEMPORARY";
	public final static String STATION_INFORMATION = "STATION DEPARTURES";
	
	/* filters values */
	public final static String ALL = "all";
	public final static String TRIPS = "trips";
	public final static String PREDICTIONS = "predictions";
	public final static String SCHEDULES = "schedules";
	public final static String STOPS = "stops";
	public final static String DEPARTURES = "departures";
	
	/* json mapping types */
	public final static String JSON_DATA = "data";
	public final static String JSON_ATTRIBUTES = "attributes";
	public final static String JSON_TIMEPOINT = "timepoint";
	public final static String JSON_ID = "id";
	public final static String JSON_STATUS = "status";
	public final static String JSON_HEADSIGN = "headsign";
	public final static String JSON_NAME = "name";
	public final static String JSON_ARRIVAL_TIME = "arrival_time";
	public final static String JSON_DEPARTURE_TIME = "departure_time";
	public final static String JSON_DIRECTION = "direction_id";
	public final static String JSON_RELATIONSHIPS = "relationships";
	public final static String JSON_TRIP_TYPE = "trip";
	public final static String JSON_STOP_TYPE = "stop";
	public final static String JSON_ROUTE_TYPE = "route";
	public final static String JSON_SCHEDULE_TYPE = "schedule";
	
	
	/* filter keys and their values */
	public final static String RESOURCE_TYPE_KEY = "resourcetype";
	public final static String IN_OUT_KEY = "inout";
	public final static String RESOURCE_ID_KEY = "resourceid";
	public final static String STATION_KEY = "station";
	public final static String PAGE_OFFSET = "pageoffset";
	public final static String PAGE_LIMIT = "pagelimit";
	public final static String MIN_TIME = "mintime";
	public final static String MAX_TIME = "maxtime";
	public final static String DATE = "date";
	public final static String COMMUTER_RAIL = "CR";

	/* api-v3 URLs */
	public final static String NORTH_STATION = "North%20Station";
	public final static String SOUTH_STATION = "South%20Station";
	public final static String NORTH_STATION_PLACE = "place-north";
	public final static String SOUTH_STATION_PLACE = "place-sstat";
	public final static String API_KEY = "?api_key=701c3b5fb31d43c8b4c4721ecec3ac0a";
	public final static String QUERY_PREDICTIONS = "?api_key=701c3b5fb31d43c8b4c4721ecec3ac0a&filter[stop]=%s&include=schedule";
	public final static String QUERY_SCHEDULES_FILTER = "?api_key=701c3b5fb31d43c8b4c4721ecec3ac0a&sort=departure_time&filter[date]=%s&filter[min_time]=%s&filter[max_time]=26:59&filter[stop]=%s";
	//public final static String QUERY_FILTER_FULL = "?sort=departure_time&filter[date]=%s&filter[min_time]=%s&filter[max_time]=26:59&filter[stop]=%s&page[limit]=%s&include=schedule,trip,route,vehicle,alerts,stop";
	//Public final static String QUERY_FILTER_FULL_NO_DATE = "?sort=departure_time&filter[min_time]=%s&filter[max_time]=26:59&filter[stop]=%s&page[limit]=%s&include=schedule,trip,route,vehicle,alerts,stop";
	public final static String APIV3_SCHEDULES = "https://api-v3.mbta.com/schedules";
	public final static String APIV3_PREDICTIONS = "https://api-v3.mbta.com/predictions";
	public final static String APIV3_TRIPS = "https://api-v3.mbta.com/trips";
	public final static String APIV3_STOPS = "https://api-v3.mbta.com/stops";
	
}
