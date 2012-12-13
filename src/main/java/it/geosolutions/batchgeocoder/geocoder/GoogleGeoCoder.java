package it.geosolutions.batchgeocoder.geocoder;

import it.geosolutions.batchgeocoder.model.Location;
import it.geosolutions.batchgeocoder.model.Location.TYPE;
import it.geosolutions.batchgeocoder.model.Position;

import java.util.List;
import java.util.logging.Logger;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;

/**
 * 
 * @author Simone Giannecchini, GeoSolutions SAS
 * Implement coding operation using Google API, see https://developers.google.com/maps/documentation/geocoding
 */
public class GoogleGeoCoder extends GeoCoder {

	private Geocoder client;
	
	private static Logger LOG = Logger.getLogger(GoogleGeoCoder.class
			.getCanonicalName());
	
	public GoogleGeoCoder(){
		client = new Geocoder();
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.geosolutions.geobatchcoder.search.Search#geocode(it.geosolutions.geobatchcoder.model.Location)
	 */
	@Override
	public boolean geocode(Location location) {

		// request builder
		final GeocoderRequestBuilder builder = new GeocoderRequestBuilder();
		builder.setLanguage("it"); // TODO customizable
		builder.setRegion(""); // TODO customizable
		
		GeocodeResponse result = null;
		for(String el : location.getAlternativeNames()){
			builder.setAddress((location.getType()==TYPE.comune ? "Comune di ":"") + el + ", IT");
			final GeocoderRequest geocoderRequest=builder.getGeocoderRequest();
			result = client.geocode(geocoderRequest);
			if (result != null)
			{
				switch(result.getStatus()){
				case OK:
					// parse the results
					final List<GeocoderResult> results = result.getResults();
					if(results!=null&&!results.isEmpty()){
						final GeocoderResult address = results.get(0);
						Position pos = new Position();
						final GeocoderGeometry geometry = address.getGeometry();
						final LatLng latlon = geometry.getLocation();
						final LatLngBounds bbox = geometry.getViewport();
						pos.setPoint(latlon.getLng().doubleValue(), latlon.getLat().doubleValue());
						pos.setBBOX(
								bbox.getNortheast().getLng().doubleValue(), 
								bbox.getNortheast().getLat().doubleValue(), 
								bbox.getSouthwest().getLat().doubleValue(), 
								bbox.getSouthwest().getLng().doubleValue());
						
						location.setPosition(pos);
						return true;							
					}
				break;
				default:
					break;
				
				}
			}
		}
		Position pos = new Position();
		location.setPosition(pos);
		return false;
		
	}

	/*
	 * (non-Javadoc)
	 * @see it.geosolutions.geobatchcoder.search.Search#reverseGeocode(it.geosolutions.geobatchcoder.model.Location)
	 */
	@Override
	public boolean reverseGeocode(Location location) {
		// TODO Auto-generated method stub
		return false;
	}

}
