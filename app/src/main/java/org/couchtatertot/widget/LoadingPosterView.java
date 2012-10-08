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
package org.couchtatertot.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import org.couchtatertot.helper.PosterCache;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.task.GetPosterTask;

public class LoadingPosterView extends ImageView {
	
	private GetPosterTask task = null;
	
	public LoadingPosterView(Context context) {
		super(context);
	}
	
	public LoadingPosterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public LoadingPosterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setPoster(String filename) {
		if ( filename == null ) {
			this.setImageBitmap(null);
			return;
		}

		if ( task != null ) {
			task.cancel(true);
		}
		// Clear current image
		this.setImageBitmap(null);
		Preferences pref = Preferences.getSingleton(this.getContext());
		PosterCache cache = PosterCache.getSingleton(this.getContext());
		task = new GetPosterTask(pref, cache, filename,this.getWidth(),this.getHeight()){
			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				if ( result != null && task == this )
					LoadingPosterView.this.setImageBitmap(result);
			}};
		task.execute();
	}
}
