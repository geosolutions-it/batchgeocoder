package it.geosolutions.batchgeocoder.launcher;

import it.geosolutions.batchgeocoder.geocoder.GeoCoder;
import it.geosolutions.batchgeocoder.geocoder.GoogleGeoCoder;
import it.geosolutions.batchgeocoder.io.CSVRepositoryReader;
import it.geosolutions.batchgeocoder.io.CSVRepositoryWriter;
import it.geosolutions.batchgeocoder.io.Input;
import it.geosolutions.batchgeocoder.io.Output;
import it.geosolutions.batchgeocoder.io.OutputFileType;
import it.geosolutions.batchgeocoder.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Launch a bulk translation for the data retrieved from a source and produce 2 files: 
 * One for discarded data (data without any coding) and one for geocodedcoded (or reverse geocoded) data
 * @author DamianoG
 *
 */
public class SearchEngine {

	private static Logger LOG = Logger.getLogger(SearchEngine.class
			.getCanonicalName());
	
	private Input repo;
	private GeoCoder searcher;
	private Output listGeocoded;
	private Output outDiscarded;
	private Configuration conf;
	
	public SearchEngine(){
		try {
			conf = new PropertiesConfiguration("configuration.properties");
		} catch (ConfigurationException e) {
			LOG.log(Level.SEVERE, "failed to load configurations");
		}
		repo = new CSVRepositoryReader();
		searcher = new GoogleGeoCoder("");// NominatimGeoCoder(conf.getString("email"));
		listGeocoded = new CSVRepositoryWriter(OutputFileType.GEOCODED);
		outDiscarded = new CSVRepositoryWriter(OutputFileType.DISCARDED);
		
	}
	
	/*
	 * TODO GENERALIZE IT FOR GEOCODING AND REVERSEGEOCODING
	 */
	public void runSearch(){
		
		int intervall = conf.getInt("request.intervall");
		List<Location> geocodedList = new ArrayList<Location>();
		repo.loadLocations();
		boolean outcome = false;
		List<Location> discardedList = new ArrayList<Location>();
		
		for(Location el : repo.getLocations()){
			outcome = searcher.geocode(el);
			if(!outcome){
				discardedList.add(el);
				LOG.info("Discarded " + el.getName());
			}
			else{
				geocodedList.add(el);
				LOG.info("Geocoded " + el.getName());
			}
			
			try {
				Thread.sleep(intervall);
			} catch (InterruptedException e) {
				new RuntimeException(e);
			}
		}
		
		listGeocoded.storeLocations(geocodedList);
		outDiscarded.storeLocations(discardedList);
		LOG.info("Discarded Number " + discardedList.size());
		for(Location el : discardedList){
			LOG.info("Discarded " + el.getName());
		}
	}
}
