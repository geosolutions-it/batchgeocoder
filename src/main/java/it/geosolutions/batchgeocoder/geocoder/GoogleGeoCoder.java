package it.geosolutions.batchgeocoder.geocoder;

import fr.dudie.nominatim.model.Address;
import it.geosolutions.batchgeocoder.model.Location;
import it.geosolutions.batchgeocoder.model.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

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
 * @author DamianoG
 * Implement coding operation using Nominatim API, see http://wiki.openstreetmap.org/wiki/Nominatim
 */
public class GoogleGeoCoder extends GeoCoder {

	private Geocoder client;
	private static Logger LOG = Logger.getLogger(GoogleGeoCoder.class
			.getCanonicalName());

	private static String STATE_SUFFIX = ", Italia";
	
	public GoogleGeoCoder(String email){
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
			builder.setAddress(el);
			final GeocoderRequest geocoderRequest=builder.getGeocoderRequest();
			result = client.geocode(geocoderRequest);
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
