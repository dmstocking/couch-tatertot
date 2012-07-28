/*
 * 	CouchTatertot is a android app for managing couchpotato
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
package org.couchtatertot.task;

import java.util.List;

import org.couchpotato.CouchPotato.PageEnum;
import org.couchtatertot.helper.Preferences;

public class MovieAddTask extends CouchTask<Void,Void,Void>
{
	
	protected String imdbId;
	protected Integer profileId = null;
	protected String defaultTitle;
	
	public MovieAddTask(String imdbId, String defaultTitle)
	{
		this.imdbId = imdbId;
		this.defaultTitle = defaultTitle;
	}
	
	public MovieAddTask(String imdbId, Integer profileId, String defaultTitle)
	{
		this.imdbId = imdbId;
		this.profileId = profileId;
		this.defaultTitle = defaultTitle;
	}

	@Override
	public String getTaskLogName() {
		return "MovieAddTask";
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			Preferences.singleton.getCouchPotato().movieAdd(profileId,imdbId,defaultTitle);
		} catch (Exception e) {
			this.error = e;
		}
		return null;
	}
}
