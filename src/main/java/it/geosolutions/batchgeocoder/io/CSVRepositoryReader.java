package it.geosolutions.batchgeocoder.io;

import it.geosolutions.batchgeocoder.model.Description;
import it.geosolutions.batchgeocoder.model.Location;
import it.geosolutions.batchgeocoder.model.Location.TYPE;
import it.geosolutions.batchgeocoder.model.Position;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;

import au.com.bytecode.opencsv.CSVReader;

public class CSVRepositoryReader implements Input {
	
	private static Logger LOG = Logger.getLogger(CSVRepositoryReader.class.getCanonicalName());
	private static String FILE_PATH = "src/main/resources/";
	private List<Location> locationList;
	private Configuration conf;
	
	public CSVRepositoryReader(Configuration conf){
		locationList = new ArrayList<Location>();
		this.conf = conf;
	}
	
	public List<Location> getLocations() {
		return locationList;
	}
	
	public void loadLocations() {
		int id = conf.getInt("index.id");
		int value = conf.getInt("index.value");
		Integer altValue = (!conf.getString("index.altValue").isEmpty())?conf.getInt("index.altValue"):null;
		String basePath = conf.getString("basePath");
		String fileName = conf.getString("fileNameIn");
		
		List<String[]> allData = new ArrayList<String[]>();
		allData.addAll(buildList(id,value, altValue, basePath + fileName));
		
		for(String[] el : allData){
			Location loc = new Location();
			loc.setPosition(new Position());
			Description tmpDesc = new Description();
			tmpDesc.setDescription(el);
			loc.setDescription(tmpDesc);
			loc.setType(TYPE.valueOf(conf.getString("location.type")));
			locationList.add(loc);
		}
		
	}

	public void loadLocationsZ() {
//		Map<String, String[]> comuni = buildMap(2, 8, 9, FILE_PATH + "comuni_italiani_2012.csv");
//		Map<String, String[]> province = buildMap(8, 10, null, FILE_PATH + "ripartizioni_regioni_province_2012.csv");
//		Map<String, String[]> regioni = buildMap(0, 2, null, FILE_PATH + "regioni_2012.csv");
//		Map<String, String[]> allData = new HashMap<String, String[]>();
		List<String[]> allData = new ArrayList<String[]>();
		allData.addAll(buildList(2, 8, 9, FILE_PATH + "comuni_italiani_2012.csv"));
		allData.addAll(buildList(8, 10, null, FILE_PATH + "ripartizioni_regioni_province_2012.csv"));
		allData.addAll(buildList(0, 2, null, FILE_PATH + "regioni_2012.csv"));
		//List<String[]> list = fromMapToList(allData);
		for(String[] el : allData){
			Location loc = new Location();
			loc.setPosition(new Position());
			Description tmpDesc = new Description();
			tmpDesc.setDescription(el);
			loc.setDescription(tmpDesc);
			locationList.add(loc);
		}
		
	}
	
	
//	private List<String[]> fromMapToList(Map<String, String[]> map){
//		List<String[]> list = new ArrayList<String[]>();
//		for(String el : map.keySet()){
//			String [] newEl = new String[3];
//			newEl[0] = el;
//			newEl[1] = map.get(el)[0];
//			newEl[2] = map.get(el)[1];
//			list.add(newEl);
//		}
//		return list;
//	}
	
	private List<String[]> buildList(Integer indexKey, Integer indexValue, Integer indexAlternativeValue, String filename) {
		List<String[]> myEntries = null;
		List<String[]> resultList = new ArrayList<String[]>(); 
		CSVReader reader = null;
		Reader fileReader = null;
		try {
			fileReader = new FileReader(filename);
			reader = new CSVReader(fileReader, ';');
			myEntries = reader.readAll();
			for(String[] el : myEntries){
				if (el != null && el.length > 1) {
					String[] tmpEl = new String[3];
					tmpEl[0] = el[indexKey];
					tmpEl[1] = el[indexValue];
					tmpEl[2] = (indexAlternativeValue == null) ? null
							: el[indexAlternativeValue];
					resultList.add(tmpEl);
				}
			}
		} catch (IOException e) {
			LOG.log(Level.SEVERE,e.getLocalizedMessage(),e);
		}
		finally{
			try {
				fileReader.close();
				reader.close();
			} catch (IOException e) {
				LOG.log(Level.FINE, e.getLocalizedMessage(), e);
			}
		}
		return resultList;
	}
	
//	private Map<String, String[]> buildMap(Integer indexKey,
//			Integer indexValue, Integer indexAlternativeValue, String filename) {
//
//		List<String[]> myEntries = null;
//		try {
//			CSVReader reader = new CSVReader(new FileReader(filename), ';');
//			myEntries = reader.readAll();
//		} catch (IOException e) {
//			LOG.log(Level.SEVERE,e.getLocalizedMessage(),e);
//		}
//		Map<String, String[]> map = new HashMap<String, String[]>();
//		for (String[] el : myEntries) {
//			if (el != null && el.length > 1) {
//				String[] values = new String[2];
//				values[0] = el[indexValue];
//				values[1] = (indexAlternativeValue == null) ? null
//						: el[indexAlternativeValue];
//				map.put(el[indexKey], values);
//			}
//		}
//		return map;
//	}

}
