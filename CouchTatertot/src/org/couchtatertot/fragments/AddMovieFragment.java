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
package org.couchtatertot.fragments;

import org.couchtatertot.R;
import org.couchtatertot.task.GetExternalPosterTask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class AddMovieFragment extends SherlockFragment {

	String imdb;
	String title;
	String poster;
	
	ImageView posterImageView;
	
	TextView titleTextView;
	TextView yearTextView;
	TextView plotTextView;
	
	TextView editTextView;
	
	Button addMovie;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.movie_fragment, container, false);
		posterImageView = (ImageView) root.findViewById(R.id.posterImageView);
		titleTextView = (TextView) root.findViewById(R.id.titleTextView);
		yearTextView = (TextView) root.findViewById(R.id.yearTextView);
		plotTextView = (TextView) root.findViewById(R.id.plotTextView);
		editTextView = (TextView) root.findViewById(R.id.editTextView);
		editTextView.setVisibility(View.VISIBLE);
		addMovie = (Button) root.findViewById(R.id.addMovieButton);
		addMovie.setVisibility(View.VISIBLE);
		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Intent intent = this.getSherlockActivity().getIntent();
		imdb = intent.getStringExtra("imdb");
		title = intent.getStringExtra("title");
		poster = intent.getStringExtra("poster");
		titleTextView.setText(title);
		yearTextView.setText(intent.getStringExtra("year"));
		plotTextView.setText(intent.getStringExtra("plot"));
		if ( poster != null )
			new GetExternalPosterTask( poster, posterImageView.getWidth(), posterImageView.getHeight() ){
				@Override
				protected void onPostExecute(Bitmap result) {
					super.onPostExecute(result);
					if ( result != null )
						posterImageView.setImageBitmap(result);
				}
			}.execute();
	}
	
}
