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

import org.couchpotato.CouchPotato;
import org.couchpotato.CouchPotato.PageEnum;
import org.couchpotato.json.MovieJson;
import org.couchtatertot.EditMovieActivity;
import org.couchtatertot.R;
import org.couchtatertot.app.LoadingListFragment;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.widget.LoadingPosterView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;

public class WantedFragment extends LoadingListFragment<Void, Void, List<MovieJson>> {
	
	private ArrayAdapter<MovieJson> movieAdapter;
	
//	private TitlePageIndicator pageIndicator = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    movieAdapter = new ArrayAdapter<MovieJson>(this.getActivity(), R.layout.wanted_banner_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( row == null ) {
					row = getActivity().getLayoutInflater().inflate(R.layout.wanted_banner_item, null);
				}
				MovieJson item = getItem(position);
				TextView title = (TextView) row.findViewById(R.id.titleTextView);
				TextView year = (TextView) row.findViewById(R.id.yearTextView);
				TextView plot = (TextView) row.findViewById(R.id.plotTextView);
				TextView quality = (TextView) row.findViewById(R.id.qualityTextView1);
				LoadingPosterView poster = (LoadingPosterView) row.findViewById(R.id.posterLoadingPosterView);
				title.setText(item.library.titles.get(0).title);
				if ( item.library.year > 0 )
					year.setText(item.library.year + ""); // setText(int) is for resources, dont ever pass an int that you want as a string
				else
					year.setText("");
				plot.setText(item.library.plot);
				if ( item.profile != null)
					quality.setText(item.profile.label);
				if ( item.library.files.size() > 1 )
					poster.setPoster(item.library.files.get(1).path);
				else
					poster.setImageBitmap(null);
				return row;
			}
		};
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.getListView().setBackgroundResource(R.color.couchpotato_background);
		this.getListView().setCacheColorHint(R.color.couchpotato_background);
	}

//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		try {
//			pageIndicator = (TitlePageIndicator)this.getActivity().findViewById(R.id.viewPagerIndicator);
//			pageIndicator.setOnPageChangeListener(this);
//		} catch (Exception e) {
//			; // there is no viewPagerIndicator
//			// tried to do this with a check but it always failed
//		}
//	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent( this.getSherlockActivity(), EditMovieActivity.class );
		MovieJson item = movieAdapter.getItem(position);
		intent.putExtra("id", item.id);
		intent.putExtra("page", PageEnum.MANAGE.name());
//		intent.putExtra("imdb", item.library.info.imdb);
//		intent.putExtra("title", item.library.info.titles.get(0));
//		intent.putExtra("year", item.library.info.year+"");
//		intent.putExtra("plot", item.library.info.plot);
//		if ( item.library.info.images.poster.size() > 0 )
//			intent.putExtra("poster", item.library.info.images.poster.get(0));
		startActivity(intent);
	}

//	@Override
//	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		if ( actionMode == null ) {
//			actionMode = getSherlockActivity().startActionMode( new ActionMode.Callback() {
//				
//				@Override
//				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//					return false;
//				}
//				
//				@Override
//				public void onDestroyActionMode(ActionMode mode) {
//					movieAdapter.notifyDataSetChanged();
//					selected.clear();
//					actionMode = null;
//				}
//				
//				@Override
//				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//					MenuInflater inflate = getSherlockActivity().getSupportMenuInflater();
//					inflate.inflate(R.menu.shows_cab_menu, menu);
//					return true;
//				}
//				
//				@Override
//				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//					switch ( item.getItemId() ) {
//					case R.id.pauseMenuItem:
//						final PauseDialog pDialog = new PauseDialog();
//						pDialog.setTitle("Set Pause");
//						pDialog.setOnOkClick( new OnClickListener(){
//							@Override
//							public void onClick(DialogInterface arg0, int arg1) {
//								final ProgressDialog dialog = ProgressDialog.show(WantedFragment.this.getSherlockActivity(), "","Pausing Shows. Please wait...", true);
//								dialog.setCancelable(true);
//								dialog.show();
//								String[] tvdbids = new String[selected.size()];
//								for ( int i=0; i < selected.size(); i++ ) {
//									tvdbids[i] = movieAdapter.getItem(selected.get(i)).id;
//								}
//								PauseTask pause = new PauseTask(tvdbids, pDialog.getPause()){
//									@Override
//									protected void onPostExecute(Boolean result) {
//										if ( dialog != null && dialog.isShowing() )
//											dialog.dismiss();
//									}};
//								pause.execute();
//							}} );
//						pDialog.show(getFragmentManager(), "update");
//						return true;
//					case R.id.refreshMenuItem:
//						{
//							final ProgressDialog dialog = ProgressDialog.show(WantedFragment.this.getSherlockActivity(), "","Refreshing Shows. Please wait...", true);
//							dialog.setCancelable(true);
//							dialog.show();
//							String[] tvdbids = new String[selected.size()];
//							for ( int i=0; i < selected.size(); i++ ) {
//								tvdbids[i] = movieAdapter.getItem(selected.get(i)).id;
//							}
//							RefreshTask refresh = new RefreshTask(tvdbids){
//								@Override
//								protected void onPostExecute(Boolean result) {
//									if ( dialog != null && dialog.isShowing() )
//										dialog.dismiss();
//								}};
//								refresh.execute();
//						}
//						return true;
//					case R.id.updateMenuItem:
//						{
//							final ProgressDialog dialog = ProgressDialog.show(WantedFragment.this.getSherlockActivity(), "","Updating Shows. Please wait...", true);
//							dialog.setCancelable(true);
//							dialog.show();
//							String[] tvdbids = new String[selected.size()];
//							for ( int i=0; i < selected.size(); i++ ) {
//								tvdbids[i] = movieAdapter.getItem(selected.get(i)).id;
//							}
//							UpdateTask update = new UpdateTask(tvdbids){
//								@Override
//								protected void onPostExecute(Boolean result) {
//									if ( dialog != null && dialog.isShowing() )
//										dialog.dismiss();
//								}};
//							update.execute();
//						}
//						return true;
////					case R.id.editMenuItem:
////						// get all selected items and create the edit show activity passing all of them
////						actionMode.finish();
////						return true;
//					}
//					return false;
//				}
//			});
//		}
//		ImageView overlay = (ImageView)arg1.findViewById(R.id.showSelectedOverlay);
//		int i = selected.indexOf(arg2);
//		if ( i >= 0 ) {
//			selected.remove(i);
//			overlay.setVisibility(View.INVISIBLE);
//		} else {
//			selected.add(arg2);
//			overlay.setVisibility(View.VISIBLE);
//		}
//		actionMode.setTitle(selected.size() + " Items Selected");
//		if ( selected.size() == 0 ) {
//			actionMode.finish();
//		}
//		return true;
//	}
//	
//	@Override
//	public void onPageScrollStateChanged(int arg0) {
//		// do nothing
//	}
//
//	@Override
//	public void onPageScrolled(int arg0, float arg1, int arg2) {
//		// do nothing
//	}
//
//	@Override
//	public void onPageSelected(int arg0) {
//		if ( arg0 != 0 && actionMode != null ) {
//			actionMode.finish();
//		}
//	}

	@Override
	protected String getEmptyText() {
		return "No Movies Available";
	}
	
	@Override
	protected Void[] getRefreshParams() {
		return null;
	}
	
	@Override
	protected List<MovieJson> doInBackground(Void... arg0) throws Exception {
		return Preferences.singleton.getCouchPotato().movieList(null, null, null, null);
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}
	
	@Override
	protected void onPostExecute(List<MovieJson> result) {
		setListAdapter(movieAdapter);
		movieAdapter.clear();
		for ( MovieJson s : result ) {
			movieAdapter.add(s);
		}
//		movieAdapter.sort( new ShowNameComparator() );
		if ( movieAdapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		} else {
			this.setListStatus(ListStatus.NORMAL);
		}
		movieAdapter.notifyDataSetChanged();
	}
}
