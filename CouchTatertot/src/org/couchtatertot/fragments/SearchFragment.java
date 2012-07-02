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

import java.util.Comparator;

import org.couchpotato.json.InfoJson;
import org.couchpotato.json.SearchResultsJson;
import org.couchpotato.json.comparator.InfoJsonByTitleComparator;
import org.couchpotato.json.comparator.InfoJsonByYearComparator;
import org.couchtatertot.fragments.SearchFragment.SearchParams;
import org.couchtatertot.AddMovieActivity;
import org.couchtatertot.R;
import org.couchtatertot.app.LoadingListFragment;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.widget.LoadingPosterView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SearchFragment extends LoadingListFragment<SearchParams, Void, SearchResultsJson> {
	
	public class SearchParams {
		public String query;
	}
	
	private String query;
	
	private ArrayAdapter<InfoJson> searchAdapter;
	private Comparator<InfoJson> sorter;
	
	private SearchResultsJson lastResults = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		sorter = new Comparator<InfoJson>(){
			@Override
			public int compare(InfoJson lhs, InfoJson rhs) {
				return 0;
			}
		};
		searchAdapter = new ArrayAdapter<InfoJson>(this.getActivity(), R.layout.wanted_banner_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( convertView == null ) {
					row = getActivity().getLayoutInflater().inflate(R.layout.wanted_banner_item, null);
				}
				InfoJson item = getItem(position);
				TextView title = (TextView) row.findViewById(R.id.titleTextView);
				TextView year = (TextView) row.findViewById(R.id.yearTextView);
				TextView plot = (TextView) row.findViewById(R.id.plotTextView);
				TextView quality = (TextView) row.findViewById(R.id.qualityTextView1);
				LoadingPosterView poster = (LoadingPosterView) row.findViewById(R.id.posterLoadingPosterView);
				title.setText(item.titles.get(0));
				if ( item.year == 0 )
					year.setText("");
				else
					year.setText(item.year + ""); // setText(int) is for resources, don't ever pass an int that you want as a string
				plot.setText(item.plot); // i set this but it isn't visible because of sizing I don't know if i want to use so im leaving here
				quality.setVisibility(View.GONE);
				poster.setVisibility(View.GONE);
				return row;
			}
		};
		super.onCreate(savedInstanceState);
		// DO NOT SET ADAPTER UNTIL THE DATA HAS BEEN AQUIRED!!!!
		// this makes the list have a progress spinner for us
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.removeItem(R.id.searchMenuItem);
		inflater.inflate(R.menu.search_menu, menu);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Intent intent = this.getActivity().getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	query = intent.getStringExtra(SearchManager.QUERY);
	    }
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if ( position >= searchAdapter.getCount() ) {
			return;
		}
		Intent intent = new Intent( this.getSherlockActivity(), AddMovieActivity.class );
		intent.putExtra("imdb", searchAdapter.getItem(position).imdb);
		intent.putExtra("title", searchAdapter.getItem(position).originalTitle);
		intent.putExtra("year", searchAdapter.getItem(position).year+"");
		intent.putExtra("plot", searchAdapter.getItem(position).plot);
		if ( searchAdapter.getItem(position).images.poster.size() > 0 )
			intent.putExtra("poster", searchAdapter.getItem(position).images.poster.get(0));
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() ) {
		case R.id.sortByRelevanceMenuItem:
			sorter = new Comparator<InfoJson>(){
				@Override
				public int compare(InfoJson lhs, InfoJson rhs) {
					return 0;
				}
			};
			onPostExecute(lastResults);
			return true;
		case R.id.sortByTitleMenuItem:
			sorter = new InfoJsonByTitleComparator();
			searchAdapter.sort(sorter);
			return true;
		case R.id.sortByYearMenuItem:
			sorter = new InfoJsonByYearComparator();
			searchAdapter.sort(sorter);
			return true;
//		case R.id.searchLanguageMenuItem:
//			final LanguageDialog lDialog = new LanguageDialog();
//			lDialog.setTitle("Search Language");
//			lDialog.setOnListClick( new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					language = lDialog.getLang();
//					SearchFragment.this.refresh();
//				}
//			});
//			lDialog.show(getFragmentManager(), "language");
//			break;
		case R.id.searchMenuItem:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected String getEmptyText() {
		return "No Results Found";
	}

	@Override
	protected SearchParams[] getRefreshParams() {
		SearchParams params = new SearchParams();
		params.query = query;
//		params.language = language;
		return new SearchParams[] { params };
	}

	@Override
	protected SearchResultsJson doInBackground(SearchParams... arg0) throws Exception {
		return Preferences.singleton.getCouchPotato().movieSearch(arg0[0].query);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(SearchResultsJson result) {
		if ( result != null && searchAdapter != null ) {
			SearchFragment.this.setListAdapter(searchAdapter);
			lastResults = result;
			searchAdapter.clear();
			for ( InfoJson s : result.movies ) {
				searchAdapter.add(s);
			}
			// make a sorter that sorts by year
			searchAdapter.sort( sorter );
			searchAdapter.notifyDataSetChanged();
			if ( searchAdapter.getCount() == 0 ) {
				this.setListStatus(ListStatus.EMPTY);
			}
		}
	}
}
