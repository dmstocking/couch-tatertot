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

import java.net.URI;

import org.couchtatertot.task.GetPosterTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class BoundedLoadingPosterView extends ImageView {
	
	private GetPosterTask task = null;
	
	public BoundedLoadingPosterView(Context context) {
		super(context);
	}
	
	public BoundedLoadingPosterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public BoundedLoadingPosterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		double width = View.MeasureSpec.getSize(widthMeasureSpec);
		double height = View.MeasureSpec.getSize(heightMeasureSpec);
		// standard aspect ratio for movie posters
		double aspect = 0.675;
		double actual = width / height;
		// find which way we are wonky because we only want to expand not contract
		// the minus is because i dont care if its slightly out of aspect ratio
		if ( actual-0.01 > aspect  ) {
			// rounding error doens't really matter to me
			widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)(aspect*height), View.MeasureSpec.EXACTLY);
		} else if ( actual+0.1 < aspect ) {
			// cast to a double just in case it couldn't implicitly cast it
			heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)(width/aspect), View.MeasureSpec.EXACTLY);
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void setPoster(String filename) {
		if ( task != null ) {
			task.cancel(true);
		}
		// Clear current image
		this.setImageBitmap(null);
		task = new GetPosterTask(filename,this.getWidth(),this.getHeight()){
			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				if ( result != null && task == this )
					BoundedLoadingPosterView.this.setImageBitmap(result);
			}};
		task.execute();
	}
}
