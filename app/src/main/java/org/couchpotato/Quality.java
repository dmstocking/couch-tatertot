package org.couchpotato;

import org.couchpotato.json.QualityJson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quality {

	private static Map<Integer,String> qualities = new HashMap<Integer,String>();
	
	public static void populateQualities( List<QualityJson> json )
	{
		if ( json != null && json.size() > 0 ) {
			qualities.clear();
			for ( QualityJson j : json ) {
				qualities.put(j.id, j.label);
			}
		}
	}
	
	public static String getQuality(int id)
	{
		String label = qualities.get(id);
		if ( label == null ) {
			return "Unknown";
		} else {
			return label;
		}
	}
}
