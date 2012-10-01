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

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MovieJson {
	public ProfileJson profile;
	@SerializedName("library_id")
	public int libraryId;
	public List<ReleaseJson> releases;
	@SerializedName("status_id")
	public int statusId;
	@SerializedName("profile_id")
	public int profileId;
	public LibraryJson library;
	public StatusJson status;
	@SerializedName("last_edit")
	public int lastEdit;
	public int id;
	public List<String> files;
}
