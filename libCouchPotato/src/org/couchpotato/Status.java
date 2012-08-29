package org.couchpotato;

import java.util.Arrays;
import java.util.List;

// this classes ONLY job is to abstract interface for statuses from couchpotato
// this was made to not use the internet every 5 seconds to get a list of statuses
public class Status {

	static public List<String> identifiers;
	static public List<String> labels;
	
	static {
		 identifiers = Arrays.asList(
				"needs_update",
				"ignored",
				"done",
				"snatched",
				"downloaded",
				"active",
				"wanted",
				"deleted",
				"available"
			);
		 labels = Arrays.asList(
				"Needs update",
				"Ignored",
				"Done",
				"Snatched",
				"Downloaded",
				"Active",
				"Wanted",
				"Deleted",
				"Available"
			);
	}
	
	/**
	 * @return	Number of status enums. IDs start at 1!
	 */
	static public int size()
	{
		return 8;
	}
	
	/**
	 * @param id	The status ID. These start at 1!
	 * @return		The identifier
	 */
	static public String getIdentifier(int id)
	{
		return identifiers.get(id-1);
	}
	
	/**
	 * @param id	The status ID. These start at 1!
	 * @return		The label
	 */
	static public String getLabel(int id)
	{
		return identifiers.get(id-1);
	}
}
