package it.geosolutions.batchgeocoder.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.dudie.nominatim.model.Address;

/**
 * Implementation of the data structure for the geographical data representing a location 
 * @author DamianoG
 *
 */
public class Position {

	
	private Double latitude;
	private Double longitude;
	
	private Double boundingNorth;
	private Double boundingSouth;
	private Double boundingWest;
	private Double boundingEast;
	
	public Position(){
		 latitude=0d;
		 longitude=0d;
		 boundingEast=0d;
		 boundingNorth=0d;
		 boundingSouth=0d;
		 boundingWest=0d;
	}
	
	
	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @return the bounding box point in order
	 * boundingEast;boundingNorth;boundingSouth;boundingWest;
	 */
	public Map<String, Double> getBoundingBoxPoints(){
		Map<String, Double> points = new HashMap<String, Double>();
		points.put("north", boundingNorth);
		points.put("south", boundingSouth);
		points.put("west", boundingEast);
		points.put("east", boundingEast);
		return points;
	}
	
	/**
	 * 
	 * @return a list of double in this order
	 *  latitude;Longitude;boundingEast;boundingNorth;boundingSouth;boundingWest;
	 */
	public List<String> getPositionAsList(){
		List<String> list = new ArrayList<String>();
		list.add(String.valueOf(latitude));
		list.add(String.valueOf(longitude));
		
		list.add(String.valueOf(boundingNorth));
		list.add(String.valueOf(boundingSouth));
		list.add(String.valueOf(boundingWest));
		list.add(String.valueOf(boundingEast));
		return list;
	}
	
	public void setPosition(Address address){
		latitude=address.getLatitude();
		longitude=address.getLongitude();
		boundingEast=address.getBoundingBox().getEast();
		boundingNorth=address.getBoundingBox().getNorth();
		boundingSouth=address.getBoundingBox().getSouth();
		boundingWest=address.getBoundingBox().getWest();
	}
	
	public void setPoint(double lon, double lat){
		this.latitude=lat;
		this.longitude=lon;
	}
	
	public void setBBOX(double easting, double northing, double southing, double westing){
		this.boundingEast=easting;
		this.boundingNorth=northing;
		this.boundingSouth=southing;
		this.boundingWest=westing;
	}
}
