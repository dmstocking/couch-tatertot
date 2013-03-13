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

import org.couchtatertot.helper.Preferences;

public class ReleaseIgnoreTask extends CouchTask<Void,Void,Void>
{
	
	protected Preferences pref;
	protected int[] ids;

	public ReleaseIgnoreTask(Preferences pref, int id)
	{
		this(pref, new int[]{id});
	}
	
	public ReleaseIgnoreTask(Preferences pref, int[] ids)
	{
		this.pref = pref;
		this.ids = ids;
	}

	@Override
	public String getTaskLogName() {
		return "ReleaseIgnoreTask";
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			for ( int id : ids ) {
				pref.getCouchPotato().releaseIgnore(id);
			}
		} catch (Exception e) {
			this.error = e;
		}
		return null;
	}
}
