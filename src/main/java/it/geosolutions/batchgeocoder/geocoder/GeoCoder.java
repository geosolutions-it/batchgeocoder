package it.geosolutions.batchgeocoder.geocoder;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import it.geosolutions.batchgeocoder.model.Location;

/**
 * 
 * @author Damiano Giampaoli
 * Abstarct coding operation from specific vendor API  
 * 
 */
public abstract class GeoCoder {

	/**
	 * retrive and store geographical information for the provided location.
	 * The implementation must try geocoding with all alternative names provided for a location,
	 * If all names aren't geocoded return false. 
	 * @param conf
	 * @param locations
	 * @return the list of discarded locations
	 */
	public abstract void geocode(Configuration conf, List<Location> locations, List<Location> geocodedList, List<Location> discardedList);
	
	/**
	 * retrive and store metadata information for the provided location.
	 * @param location
	 * @return the outcome of the operation
	 */
	public abstract boolean reverseGeocode(Location location);

	
}
