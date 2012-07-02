/*
 * 	libCouchPotato is a java library for communication with couchpotato
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/couch-tatertot/
 * 	
 * 	libCouchPotato is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.couchpotato.json.comparator;

import java.util.Comparator;

import org.couchpotato.json.InfoJson;


public class InfoJsonByTitleComparator implements Comparator<InfoJson> {

	@Override
	public int compare(InfoJson lhs, InfoJson rhs) {
		String s1 = lhs.originalTitle;
		String s2 = rhs.originalTitle;
		if ( s1 == null )
			s1 = "";
		if ( s2 == null )
			s2 = "";
		
		// have to do this before the "the " part otherwise caps mess it up
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		
		if ( s1.startsWith("the ") )
			s1 = s1.substring(4);
		if ( s2.startsWith("the ") )
			s2 = s2.substring(4);

		return s1.compareTo(s2);
	}
	
}
