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
package org.couchpotato.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LibraryJson {
	public InfoJson info;
	public String plot;
	@SerializedName("status_id")
	public int statusId;
	public String tagline;
	public List<TitleJson> titles;
	public int year;
	public String identifier;
	public List<FilesJson> files;

	public String getCroppedPoster()
	{
		if ( info.images.poster.size() > 0 ) {
			return info.images.poster.get(0);
		} /*else {
			for ( FilesJson file : files ) {
				if ( file.type_id == 2 ) {
					return file.path;
				}
			}
			return null;
		}*/
		// not using this just because of how bad it works
		// the type_id can basically be anything the system wants
		// granted this happens most on upgrade, but just go get it from the internet
		return null;
	}
}
