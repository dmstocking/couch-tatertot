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

import java.util.List;

import org.couchpotato.Status;
import org.couchpotato.json.ReleaseJson;
import org.couchtatertot.R;
import org.couchtatertot.app.LoadingListFragment;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.task.ReleaseDownloadTask;
import org.couchtatertot.task.ReleaseIgnoreTask;
import org.couchtatertot.widget.SafeArrayAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ReleasesFragment extends LoadingListFragment<Void, Void, List<ReleaseJson>> {
	
	private SafeArrayAdapter<ReleaseJson> releaseAdapter;
	
	private int id;

	@Override
	protected boolean isRetainInstance() {
		return true;
	}

	@Override
	protected int getChoiceMode() {
		return ListView.CHOICE_MODE_SINGLE;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Intent intent = this.getActivity().getIntent();
		id = intent.getIntExtra("id", -1);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    releaseAdapter = new SafeArrayAdapter<ReleaseJson>(this.getActivity(), R.layout.releases_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( row == null ) {
					row = layoutInflater.inflate(R.layout.releases_item, null);
				}
				ReleaseJson item = getItem(position);
				View overlay = row.findViewById(R.id.selectedOverlay);
				if ( position == getListView().getCheckedItemPosition() ) {
					overlay.setVisibility(View.VISIBLE);
				} else {
					overlay.setVisibility(View.INVISIBLE);
				}
				TextView release = (TextView) row.findViewById(R.id.nameTextView);
				TextView provider = (TextView) row.findViewById(R.id.providerTextView);
				TextView age = (TextView) row.findViewById(R.id.ageTextView);
				TextView score = (TextView) row.findViewById(R.id.scoreTextView);
				TextView size = (TextView) row.findViewById(R.id.sizeTextView);
				TextView quality = (TextView) row.findViewById(R.id.qualityTextView);
				TextView status = (TextView) row.findViewById(R.id.statusTextView);
				release.setText(item.getValueFromInfo("name"));
				release.setSelected(true);
				provider.setText(item.getValueFromInfo("provider"));
				age.setText("Age: " + item.getValueFromInfo("age"));
				score.setText("Score: " + item.getValueFromInfo("score"));
				size.setText("Size: " + item.getValueFromInfo("size"));
				quality.setText(item.getValueFromInfo("quality"));
				status.setText(Status.getIdentifier(item.statusId));
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
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if ( actionMode == null ) {
			actionMode = getSherlockActivity().startActionMode( new ActionMode.Callback() {
				
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode) {
					int checkedPos = getListView().getCheckedItemPosition();
					getListView().setItemChecked(checkedPos, false);
					actionMode = null;
				}
				
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflate = getSherlockActivity().getSupportMenuInflater();
					inflate.inflate(R.menu.releases_cab_menu, menu);
					return true;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					ListView l = getListView();
					switch ( item.getItemId() ) {
					case R.id.downloadMenuItem:
						{
							final ProgressDialog dialog = ProgressDialog.show(getSherlockActivity(), "","Please wait...", true);
							dialog.setCancelable(true);
							dialog.show();
							ReleaseJson listItem = (ReleaseJson) l.getItemAtPosition(l.getCheckedItemPosition());
							ReleaseDownloadTask task = new ReleaseDownloadTask(listItem.id){
								@Override
								protected void onPostExecute(Void result) {
									if ( dialog != null && dialog.isShowing() )
										dialog.dismiss();
									if ( ReleasesFragment.this != null && ReleasesFragment.this.getActivity() != null )
										ReleasesFragment.this.refresh();
									if ( actionMode != null )
										actionMode.finish();
								}};
							task.execute();
							return true;
						}
					case R.id.ignoreMenuItem:
						{
							final ProgressDialog dialog = ProgressDialog.show(getSherlockActivity(), "","Please wait...", true);
							dialog.setCancelable(true);
							dialog.show();
							ReleaseJson listItem = (ReleaseJson) l.getItemAtPosition(l.getCheckedItemPosition());
							ReleaseIgnoreTask task = new ReleaseIgnoreTask(listItem.id){
								@Override
								protected void onPostExecute(Void result) {
									if ( dialog != null && dialog.isShowing() )
										dialog.dismiss();
									if ( ReleasesFragment.this != null && ReleasesFragment.this.getActivity() != null )
										ReleasesFragment.this.refresh();
									if ( actionMode != null )
										actionMode.finish();
								}};
							task.execute();
							return true;
						}
					case R.id.detailsMenuItem:
						{
							ReleaseJson listItem = (ReleaseJson) l.getItemAtPosition(l.getCheckedItemPosition());
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(listItem.getValueFromInfo("detail_url")));
							startActivity(intent);
						}
						return true;
					}
					return false;
				}
			});
			actionMode.setTitle("1 Item Selected");
		}
		if ( l.getCheckedItemPosition() == ListView.INVALID_POSITION ) {
			actionMode.finish();
		}
	}

	@Override
	protected String getEmptyText() {
		return "No Releases Available";
	}
	
	@Override
	protected Void[] getRefreshParams() {
		return null;
	}
	
	@Override
	protected List<ReleaseJson> doInBackground(Void... arg0) throws Exception {
		return Preferences.singleton.getCouchPotato().movieGet(id).releases;
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}
	
	@Override
	protected void onPostExecute(List<ReleaseJson> result) {
		setListAdapter(releaseAdapter);
		releaseAdapter.clear();
		for ( ReleaseJson s : result ) {
			releaseAdapter.add(s);
		}
//		movieAdapter.sort( new ShowNameComparator() );
		if ( releaseAdapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		} else {
			this.setListStatus(ListStatus.NORMAL);
		}
		releaseAdapter.notifyDataSetChanged();
	}
}
