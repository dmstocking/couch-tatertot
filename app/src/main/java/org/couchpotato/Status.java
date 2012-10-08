package org.couchpotato;

import org.couchpotato.json.StatusJson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Status {

	private static Map<Integer,StatusJson> statuses = new HashMap<Integer,StatusJson>();
	
	public static void populateStatuses( List<StatusJson> json )
	{
		if ( json != null && json.size() > 0 ) {
			statuses.clear();
			for ( StatusJson j : json ) {
				statuses.put(j.id, j );
			}
		}
	}
	
	public static StatusJson getStatus(int id)
	{
		return statuses.get(id);
	}
	
	/**
	 * @param id	The status ID. These start at 1!
	 * @return		The identifier
	 */
	static public String getIdentifier(int id)
	{
		if ( id <= statuses.size() ) {
			return statuses.get(id).identifier;
		} else {
			return "Unknown";
		}
	}
	
	/**
	 * @param id	The status ID. These start at 1!
	 * @return		The label
	 */
	static public String getLabel(int id)
	{
		if ( id <= statuses.size() ) {
			return statuses.get(id).label;
		} else {
			return "Unknown";
		}
	}
}
