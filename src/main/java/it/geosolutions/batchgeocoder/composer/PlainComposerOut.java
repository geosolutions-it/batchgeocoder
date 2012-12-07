package it.geosolutions.batchgeocoder.composer;

import it.geosolutions.batchgeocoder.model.Location;

import java.util.ArrayList;
import java.util.List;


/**
 * Basic composer
 * @author DamianoG
 *
 */
public class PlainComposerOut implements OutputComposer{

	public List<String[]> composeAll(List<Location> locations) {
		List<String[]> strList = new ArrayList<String[]>();
		strList.add(composeHeader());
		for(Location el : locations){
			String[] arr = new String[el.getLocationAsList().size()];
			strList.add(el.getLocationAsList().toArray(arr));
		}
		return strList;
	}
	
	public String[] composeHeader(){
		String [] header = new String[8];
		header[0] = "NAME";
		header[1] = "LATITUDE";
		header[2] = "LONGITUDE";
		header[3] = "NORTH";
		header[4] = "SOUTH";
		header[5] = "EAST";
		header[6] = "WEST";
		header[7] = "TYPE";
		return header;
	}

}
