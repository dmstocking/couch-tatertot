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

import java.net.URL;

import org.couchtatertot.helper.PosterCache;
import org.couchtatertot.helper.Preferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GetPosterTask extends CouchTask<Void,Void,Bitmap>
{
	
	protected String filename;
	protected int width;
	protected int height;
	
	public GetPosterTask(String filename, int width, int height)
	{
		this.filename = filename;
		this.width = width;
		this.height = height;
	}

	@Override
	public String getTaskLogName() {
		return "GetPosterTask";
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		try {
			Bitmap ret = null;
			if ( PosterCache.singleton.in(filename) ) {
				ret = PosterCache.singleton.getFromMemory(filename);
				if ( ret == null ) {
					ret = PosterCache.singleton.getFromDisk(filename);
				}
			} else {
				URL url = Preferences.singleton.getCouchPotato().fileCache(filename);
				ret = BitmapFactory.decodeStream(url.openStream());
				PosterCache.singleton.put(filename, ret);
				
			}
			// i don't like huge if statements
			if ( ret != null ) {
				if ( width > 0 && height > 0 ) {
					ret = Bitmap.createScaledBitmap(ret, width, height, true);
				}
			}
			return ret;
		} catch (Exception e) {
			this.error = e;
		}
		return null;
	}
}
