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
package org.couchtatertot.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import org.couchtatertot.HomeActivity;
import org.couchtatertot.R;
import org.couchtatertot.dialog.EditDialog;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.task.GetExternalPosterTask;
import org.couchtatertot.task.MovieAddTask;
import org.couchtatertot.widget.WorkingTextView;

public class AddMovieFragment extends SherlockFragment {

	String imdb;
	String title;
	String poster;
	String[] titles;
	String plot;
	String year;
	
	
	String selectedTitle;
	Integer selectedProfileId = null;
	
	ImageView posterImageView;
	
	TextView titleTextView;
	TextView yearTextView;
	TextView plotTextView;
	
	WorkingTextView editTextView;
	
	Button addMovie;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Intent intent = this.getActivity().getIntent();
		imdb = intent.getStringExtra("imdb");
		title = intent.getStringExtra("title");
		selectedTitle = title;
		poster = intent.getStringExtra("poster");
		titles = intent.getStringArrayExtra("titles");
		plot = intent.getStringExtra("plot");
		year = intent.getStringExtra("year");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.movie_fragment, container, false);
		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		posterImageView = (ImageView) view.findViewById(R.id.posterLoadingPosterView);
		if ( poster != null ) {
			new GetExternalPosterTask( poster, posterImageView.getWidth(), posterImageView.getHeight() ){
				@Override
				protected void onPostExecute(Bitmap result) {
					super.onPostExecute(result);
					if ( result != null )
						posterImageView.setImageBitmap(result);
				}
			}.execute();
		}
		titleTextView = (TextView) view.findViewById(R.id.titleTextView);
		titleTextView.setText(title);
		yearTextView = (TextView) view.findViewById(R.id.yearTextView);
		yearTextView.setText(year);
		plotTextView = (TextView) view.findViewById(R.id.plotTextView);
		plotTextView.setText(plot);
		editTextView = (WorkingTextView) view.findViewById(R.id.editWorkingTextView);
		editTextView.setVisibility(View.VISIBLE);
		editTextView.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditDialog diag;
				if ( selectedProfileId == null )
					diag = new EditDialog(titles,selectedTitle,0);
				else
					diag = new EditDialog(titles,selectedTitle,selectedProfileId);
				diag.setOnOkClick( new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selectedTitle = diag.getSelectedTitle();
						titleTextView.setText(selectedTitle);
						selectedProfileId = diag.getSelectedProfileId();
					}
				});
				diag.show(getFragmentManager(), "edit");
			}
		});
		addMovie = (Button) view.findViewById(R.id.addMovieButton);
		addMovie.setVisibility(View.VISIBLE);
		addMovie.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final ProgressDialog prog = ProgressDialog.show(getSherlockActivity(), "Adding Movie", "Please wait. Adding movie ...");
				prog.setCancelable(true);
				Preferences pref = Preferences.getSingleton(v.getContext());
				MovieAddTask task = new MovieAddTask(pref, imdb, selectedProfileId, selectedTitle){
					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						prog.dismiss();
						Intent intent = new Intent(getSherlockActivity(),HomeActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				};
				task.execute();
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
}
