package it.geosolutions.batchgeocoder.geocoder;

import it.geosolutions.batchgeocoder.model.Location;
import it.geosolutions.batchgeocoder.model.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

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
	public boolean geocode(Location location) {
		
		List<Address> result = null;
		for(String el : location.getAlternativeNames()){
			result = new ArrayList<Address>();
			try {
				result = client.search(el + STATE_SUFFIX);
				if (result.size() > 0) {
					Position pos = new Position();
					pos.setPosition(result.get(0));
					location.setPosition(pos);
					return true;
				}
			} catch (IOException e) {
				LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
				JOptionPane.showConfirmDialog(null, "Nominatim ti ha rifiutato a " + el);
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
