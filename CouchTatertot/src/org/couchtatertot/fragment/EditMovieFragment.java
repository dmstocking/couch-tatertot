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

import java.util.ArrayList;
import java.util.List;

import org.couchpotato.CouchPotato.PageEnum;
import org.couchpotato.json.MovieJson;
import org.couchtatertot.R;
import org.couchtatertot.ReleasesActivity;
import org.couchtatertot.app.LoadingFragment;
import org.couchtatertot.dialog.EditDialog;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.task.MovieDeleteTask;
import org.couchtatertot.task.MovieEditTask;
import org.couchtatertot.task.RefreshTask;
import org.couchtatertot.widget.LoadingPosterView;
import org.couchtatertot.widget.WorkingTextView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EditMovieFragment extends LoadingFragment<Integer,Void,MovieJson> {

	int id;
	PageEnum page;
	
	String imdb;
	String title;
	String poster;
	
	LoadingPosterView posterImageView;
	
	TextView titleTextView;
	TextView yearTextView;
	TextView plotTextView;
	
	WorkingTextView releasesWorkingTextView;
	WorkingTextView editWorkingTextView;
	WorkingTextView refreshWorkingTextView;
	WorkingTextView deleteWorkingTextView;
	
	List<String> titles = null;
	String origTitle = null;
	int profileId = -1;
	
	private MovieJson savedMovieJson = null;
	
	@Override
	protected boolean isRetainInstance() {
		return true;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Intent intent = this.getActivity().getIntent();
		id = intent.getIntExtra("id", -1);
		page = PageEnum.valueOf(intent.getStringExtra("page"));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		posterImageView = (LoadingPosterView) view.findViewById(R.id.posterLoadingPosterView);
		titleTextView = (TextView) view.findViewById(R.id.titleTextView);
		yearTextView = (TextView) view.findViewById(R.id.yearTextView);
		plotTextView = (TextView) view.findViewById(R.id.plotTextView);
		editWorkingTextView = (WorkingTextView) view.findViewById(R.id.editWorkingTextView);
		editWorkingTextView.setVisibility(View.VISIBLE);
		refreshWorkingTextView = (WorkingTextView) view.findViewById(R.id.refreshWorkingTextView);
		refreshWorkingTextView.setVisibility(View.VISIBLE);
		deleteWorkingTextView = (WorkingTextView) view.findViewById(R.id.deleteWorkingTextView);
		deleteWorkingTextView.setVisibility(View.VISIBLE);
		// releases should never be null but it wont be shown to the user if its downloaded
		releasesWorkingTextView = (WorkingTextView) view.findViewById(R.id.releasesWorkingTextView);
		if ( page == PageEnum.WANTED || page == PageEnum.ALL ) {
			releasesWorkingTextView.setVisibility(View.VISIBLE);
			releasesWorkingTextView.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getSherlockActivity(),ReleasesActivity.class);
					intent.putExtra("id", id);
					startActivity(intent);
				}
			});
		}
		editWorkingTextView.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditDialog diag = new EditDialog(titles,origTitle,profileId);
				diag.setOnOkClick( new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						diag.dismiss();
						editWorkingTextView.setIsWorking(true);
						MovieEditTask task = new MovieEditTask(EditMovieFragment.this.id, diag.getSelectedProfileId(), diag.getSelectedTitle() ){
							@Override
							protected void onPostExecute(Void result) {
								super.onPostExecute(result);
								// there is no way to tell if this failed
								editWorkingTextView.setIsWorking(false);
								EditMovieFragment.this.refresh();
							}
						};
						task.execute();
					}
				});
				diag.show(getFragmentManager(), "edit");
			}
		});
		refreshWorkingTextView.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshWorkingTextView.setIsWorking(true);
				RefreshTask task = new RefreshTask(id){
					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						refreshWorkingTextView.setIsWorking(false);
					}
				};
				task.execute();
			}
		});
		deleteWorkingTextView.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteWorkingTextView.setIsWorking(true);
				List<Integer> ids = new ArrayList<Integer>();
				ids.add(id);
				// TODO add a DeleteTask constructor that doesnt need lists
				MovieDeleteTask task = new MovieDeleteTask(ids,page){
					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						deleteWorkingTextView.setIsWorking(false);
					}
				};
				task.execute();
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if ( isInRetainLifecycle() ) {
			if ( savedMovieJson != null ) {
				onPostExecute(savedMovieJson);
			} else {
				this.refresh();
				this.setStatus(Status.WORKING);
			}
		}
	}

	@Override
	protected String getEmptyText() {
		return "Show was empty? If you see this file a bug report.";
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.movie_fragment;
	}

	@Override
	protected Integer[] getRefreshParams() {
		return new Integer[]{id};
	}

	@Override
	protected MovieJson doInBackground(Integer... arg0) throws Exception {
		return Preferences.singleton.getCouchPotato().movieGet(arg0[0]);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
	}

	@Override
	protected void onPostExecute(MovieJson result) {
		savedMovieJson = result;
		titles = result.library.info.titles;
		origTitle = result.library.titles.get(0).title;
		profileId = result.profileId;
		titleTextView.setText(result.library.titles.get(0).title);
		yearTextView.setText(result.library.info.year+"");
		plotTextView.setText(result.library.info.plot);
		if ( result.releases.size() == 0 ) {
			releasesWorkingTextView.text.setEnabled(false);
		}
		releasesWorkingTextView.text.setText("Releases (" + result.releases.size() + ")");
		if ( result.library.files.size() > 1 )
			posterImageView.setPoster(result.library.files.get(1).path);
		this.setStatus(Status.NORMAL);
	}
	
}
