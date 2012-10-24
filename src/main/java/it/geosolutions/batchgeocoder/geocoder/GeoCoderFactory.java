package it.geosolutions.batchgeocoder.geocoder;

import org.apache.commons.configuration.Configuration;

/**
 * Simple Factory for creating geocoders.
 * 
 * @author Simone Giannecchini, GeoSolutions SAS
 * TODO introduce SPI mechanism
 * TODO introduce configuration object to hand over to factories for deciding what they can do.
 */
public class GeoCoderFactory {
	
	public static GeoCoder createGeoCoder(Configuration configuration){

		// get the type
		final String type=configuration.getString("type");
		if(type==null){
			return null;
		}
		if(type.equalsIgnoreCase("google")){
			return new GoogleGeoCoder();
		} else 
			if(type.equalsIgnoreCase("nominatim")){
				return new NominatimGeoCoder(configuration.getString("email"));
			}
		
		return null;
		
	}

}
