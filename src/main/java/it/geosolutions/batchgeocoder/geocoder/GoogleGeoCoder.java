package it.geosolutions.batchgeocoder.geocoder;

import it.geosolutions.batchgeocoder.model.Description;
import it.geosolutions.batchgeocoder.model.Location;
import it.geosolutions.batchgeocoder.model.Location.TYPE;
import it.geosolutions.batchgeocoder.model.Position;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;

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
	public void geocode(Configuration conf, List<Location> locations, List<Location> geocodedList, List<Location> discardedList) {
		
		int interval = conf.getInt("request.intervall");
		
		// request builder
		final GeocoderRequestBuilder builder = new GeocoderRequestBuilder();
		builder.setLanguage("it"); // TODO customizable
		builder.setRegion(""); // TODO customizable
		
		GeocodeResponse result = null;
		for(Location location : locations){
			for(String el : location.getAlternativeNames()){
				if (el != null && el.length() > 0)
				{
					//builder.setAddress((location.getType()==TYPE.comune ? "Comune di ":"") + el + ", IT");  // TODO customizable
					builder.setAddress((location.getType()==TYPE.comune ? "":"") + el + ", IT");  // TODO customizable
					final GeocoderRequest geocoderRequest=builder.getGeocoderRequest();
					result = client.geocode(geocoderRequest);
					if (result != null)
					{
						switch(result.getStatus()){
						case OK:
							// parse the results
							final List<GeocoderResult> results = result.getResults();
							if(results!=null&&!results.isEmpty()){
								int l = 0;
								for (GeocoderResult address : results)
								{
									if (!address.isPartialMatch() && 
											((address.getAddressComponents().get(0).getTypes().get(0).equals("administrative_area_level_2") || address.getAddressComponents().get(0).getTypes().get(0).equals("administrative_area_level_1")) ||
											address.getAddressComponents().get(1).getTypes().get(0).equals("administrative_area_level_3") && address.getAddressComponents().get(0).getLongName().equals(address.getAddressComponents().get(1).getLongName())))
									{
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
										Description parent = new Description();
										if (
												address.getAddressComponents().get(0).getTypes().get(0).equals("administrative_area_level_2") ||
												address.getAddressComponents().get(0).getTypes().get(0).equals("administrative_area_level_1")
										)
										{
											parent.setDescription(new String[] {"0", address.getAddressComponents().get(1).getLongName()});
										}
										else if (address.getAddressComponents().get(1).getTypes().get(0).equals("administrative_area_level_3"))
										{
											parent.setDescription(new String[] {"0", address.getAddressComponents().get(2).getLongName()});
										}
										if (l == 0)
										{
											location.setPosition(pos);
											location.setParent(parent);
											geocodedList.add(location);
											LOG.info("Geocoded "+location.getName()+" with location:"+location.getLocationAsList());
										}
										else{
											// New location found ...
											Location newLocation = new Location();
											Description newDescription = new Description();
											newDescription.setDescription(new String[] {"0", location.getName()});
											newLocation.setDescription(newDescription);
											newLocation.setParent(parent);
											newLocation.setPosition(pos);
											newLocation.setType(location.getType());
											geocodedList.add(newLocation);
											LOG.info("Geocoded *new* "+newLocation.getName()+" with location:"+newLocation.getLocationAsList());
										}
										l++;
									}
								}
							}
							break;
						default:
							discardedList.add(location);
							Position pos = new Position();
							location.setPosition(pos);
							Description parent = new Description();
							parent.setDescription(new String[] {"0", location.getName()});
							location.setParent(parent);
							LOG.info("Discarded " + location.getName());
							break;
						}
					}
					
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						new RuntimeException(e);
					}
				}
			}
		}
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
