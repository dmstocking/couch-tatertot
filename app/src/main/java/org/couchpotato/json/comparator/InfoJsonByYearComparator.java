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


public class InfoJsonByYearComparator implements Comparator<InfoJson> {

	@Override
	public int compare(InfoJson lhs, InfoJson rhs) {
		String lhsYear = lhs.year + ""; // wtb easy cast int to string
		String rhsYear = rhs.year + "";
		int cmp = lhsYear.compareTo(rhsYear);
		cmp = -cmp; // higher years are better
		if ( cmp == 0 ) {
			InfoJsonByTitleComparator compare = new InfoJsonByTitleComparator();
			return compare.compare(lhs, rhs); // lolololololololololol
		} else {
			return cmp;
		}
	}
	
}
