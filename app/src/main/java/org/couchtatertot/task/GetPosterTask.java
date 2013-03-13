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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.couchtatertot.helper.PosterCache;
import org.couchtatertot.helper.Preferences;

import java.net.URL;

public class GetPosterTask extends CouchTask<Void,Void,Bitmap>
{
	
	protected Preferences pref;
	protected PosterCache cache;
	protected String location;
	protected int width;
	protected int height;
	
	public GetPosterTask(Preferences pref, PosterCache cache, String location, int width, int height)
	{
		this.pref = pref;
		this.cache = cache;
		this.location = location;
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
			if ( cache.in(location) ) {
				ret = cache.getFromMemory(location);
				if ( ret == null ) {
					ret = cache.getFromDisk(location);
				}
			} else {
//				URL url = pref.getCouchPotato().fileCache(location);
				URL url = new URL(location);
				ret = BitmapFactory.decodeStream(url.openStream());
				cache.put(location, ret);
			}
			// I have removed this ONLY because 
			// 1) the bitmaps are small
			// 2) it causes a lot of Garbage Collection calls ... A LOT!
//			if ( ret != null ) {
//				if ( width > 0 && height > 0 ) {
//					ret = Bitmap.createScaledBitmap(ret, width, height, true);
//				}
//			}
			return ret;
		} catch (Exception e) {
			this.error = e;
		}
		return null;
	}
}
