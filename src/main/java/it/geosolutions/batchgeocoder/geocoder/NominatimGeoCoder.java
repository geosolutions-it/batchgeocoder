package it.geosolutions.batchgeocoder.geocoder;

import it.geosolutions.batchgeocoder.model.Description;
import it.geosolutions.batchgeocoder.model.Location;
import it.geosolutions.batchgeocoder.model.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.client.NominatimClient;
import fr.dudie.nominatim.model.Address;

/**
 * 
 * @author DamianoG
 * Implement coding operation using Nominatim API, see http://wiki.openstreetmap.org/wiki/Nominatim
 */
public class NominatimGeoCoder extends GeoCoder {

	private NominatimClient client;
	private static Logger LOG = Logger.getLogger(NominatimGeoCoder.class
			.getCanonicalName());

	private static String STATE_SUFFIX = ", Italia";
	
	public NominatimGeoCoder(String email){
		HttpClient httpClient = new DefaultHttpClient();
		client = new JsonNominatimClient(httpClient, email);
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.geosolutions.geobatchcoder.search.Search#geocode(it.geosolutions.geobatchcoder.model.Location)
	 */
	@Override
	public void geocode(Configuration conf, List<Location> locations, List<Location> geocodedList, List<Location> discardedList) {
		
		int interval = conf.getInt("request.intervall");
		
		List<Address> results = null;
		for(Location location : locations){
			for(String el : location.getAlternativeNames()){
				results = new ArrayList<Address>();
				try {
					results = client.search(el + STATE_SUFFIX);
					if (results.size() > 0) {
						int l = 0;
						for (Address address : results)
						{
							if (address.getElementClass().equals("boundary") && 
									address.getElementType().equals("administrative") && 
										address.getAddressElements().length <= 4)
							{
								Position pos = new Position();
								pos.setPosition(address);
								Description parent = new Description();
								parent.setDescription(new String[]{"0", address.getAddressElements()[0].getValue()});
								if (l==0)
								{
									location.setPosition(pos);
									location.setParent(parent);
									geocodedList.add(location);
									LOG.info("Geocoded "+location.getName()+" with location:"+location.getLocationAsList());
								}
								else
								{
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
						
						if (l==0) throw new IOException("No valid address found.");
					}
				} catch (IOException e) {
					//LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
					//JOptionPane.showConfirmDialog(null, "Nominatim ti ha rifiutato a " + el);
					discardedList.add(location);
					Position pos = new Position();
					location.setPosition(pos);
					LOG.info("Discarded " + location.getName());
				}
			}
			
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				new RuntimeException(e);
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
