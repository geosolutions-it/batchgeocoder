package it.geosolutions.batchgeocoder.io;

import it.geosolutions.batchgeocoder.model.Description;
import it.geosolutions.batchgeocoder.model.Location;
import it.geosolutions.batchgeocoder.model.Location.TYPE;
import it.geosolutions.batchgeocoder.model.LocationImpl;
import it.geosolutions.batchgeocoder.model.Position;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class SolrDictionaryRepositoryReader implements Input {
	
	private static Logger LOG = Logger.getLogger(SolrDictionaryRepositoryReader.class.getCanonicalName());
//	private static String STATE_SUFFIX = ", Italia";
	private static String FILE_PATH = "src/main/resources/";
	private List<Location> locationList;

	public SolrDictionaryRepositoryReader(){
		locationList = new ArrayList<Location>();
	}
	
	public List<Location> getLocations() {
		return locationList;
	}
	
	public void loadLocations() {
		Configuration conf = null;
		try {
			conf = new PropertiesConfiguration("configuration.properties");
		} catch (ConfigurationException e) {
			LOG.log(Level.SEVERE, "failed to load configurations");
		}
		String basePath = conf.getString("basePath");
		String fileName = conf.getString("fileNameIn");
		
		List<String[]> allData = new ArrayList<String[]>();
		allData.addAll(buildList(basePath + fileName));
		
		for(String[] el : allData){
			Location loc = new LocationImpl();
			loc.setPosition(new Position());
			Description tmpDesc = new Description();
			tmpDesc.setDescription(new String[]{el[0], el[1], el[2]});
			loc.setDescription(tmpDesc);
			loc.setType(TYPE.valueOf(el[3]));
			locationList.add(loc);
		}
		
	}
	
	private List<String[]> buildList(String filename) {
		List<String[]> resultList = new ArrayList<String[]>(); 
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filename), "UTF-8");
			
			long index = 0;
			while (scanner.hasNextLine())
			{
				String line = scanner.nextLine();
				
				if (line.indexOf("/") > 0) {
					String[] locations = line.split(" / ");

					String[] tmpEl = new String[4];
					tmpEl[0] = String.valueOf(index++);
					tmpEl[1] = locations[1];
					tmpEl[2] = null;
					tmpEl[3] = "regione";
					if (!contains(resultList, tmpEl))
						resultList.add(tmpEl.clone());

					tmpEl = new String[4];
					tmpEl[0] = String.valueOf(index++);
					tmpEl[1] = locations[2];
					tmpEl[2] = null;
					tmpEl[3] = "provincia";
					if (!contains(resultList, tmpEl))
						resultList.add(tmpEl.clone());

					tmpEl = new String[4];
					tmpEl[0] = String.valueOf(index++);
					tmpEl[1] = locations[3];
					tmpEl[2] = null;
					tmpEl[3] = "comune";
					if (!contains(resultList, tmpEl))
						resultList.add(tmpEl.clone());
				}
			}
		} catch (IOException e) {
			LOG.log(Level.SEVERE,e.getLocalizedMessage(),e);
		}
		finally{
			try {
				scanner.close();
			} catch (Exception e) {
				LOG.log(Level.FINE, e.getLocalizedMessage(), e);
			}
		}
		return resultList;
	}

	private boolean contains(List<String[]> resultList, String[] tmpEl) {
		boolean hit = false;
		for (String[] el : resultList)
		{
			if (el.length == tmpEl.length)
			{
				for (int i=0; i<el.length-1; i++)
				{
					if ((el[i] == null && tmpEl[i] != null) || (el[i] == null && tmpEl[i] != null))
					{
						continue;
					}
					
					if (el[i] != null && tmpEl[i] != null && el[i].equals(tmpEl[i]))
					{
						hit = true;
						break;
					}
				}
			}
			
			if (hit == true) break;
		}
		return hit;
	}

}
