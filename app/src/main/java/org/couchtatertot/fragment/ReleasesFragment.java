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
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.couchpotato.Quality;
import org.couchpotato.Status;
import org.couchpotato.json.ReleaseJson;
import org.couchtatertot.R;
import org.couchtatertot.ReleaseActivity;
import org.couchtatertot.app.LoadingListFragment;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.task.ReleaseDownloadTask;
import org.couchtatertot.task.ReleaseIgnoreTask;
import org.couchtatertot.widget.MarqueeView;
import org.couchtatertot.widget.SafeArrayAdapter;

import java.util.List;

public class ReleasesFragment extends LoadingListFragment<Void, Void, List<ReleaseJson>> {
	
	private SafeArrayAdapter<ReleaseJson> releaseAdapter;
	
	private int id;

	@Override
	protected boolean isRetainInstance() {
		return true;
	}

	@Override
	protected int getChoiceMode() {
		return ListView.CHOICE_MODE_MULTIPLE;
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
				// if even
				if ( position % 2 == 0 )
					row.setBackgroundResource(R.color.couchpotato_background_dark);
				else
					row.setBackgroundResource(R.color.couchpotato_background);
				ReleaseJson item = getItem(position);
				CheckBox releaseCheckBox = (CheckBox)row.findViewById(R.id.releaseCheckBox);
				if ( ReleasesFragment.this.getListView().getCheckedItemPositions().get(position) == true ) {
					releaseCheckBox.setActivated(true);
				} else {
					releaseCheckBox.setActivated(false);
				}
				releaseCheckBox.setTag(position);
				releaseCheckBox.setOnClickListener( new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						onItemChecked(view);
					}
				});
				MarqueeView titleTextView = (MarqueeView) row.findViewById(R.id.titleTextView);
				titleTextView.setText(item.info.name);
				TextView quality = (TextView) row.findViewById(R.id.qualityTextView);
				quality.setText(Quality.getQuality(item.qualityId));
				TextView status = (TextView) row.findViewById(R.id.statusTextView);
				status.setText(Status.getLabel(item.statusId));
				/*View overlay = row.findViewById(R.id.selectedOverlay);
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
				release.setText(item.info.name);
				release.setSelected(true);
				provider.setText(item.info.provider);
				age.setText("Age: " + item.info.age);
				score.setText("Score: " + item.info.score);
				size.setText("Size: " + item.info.size);
				quality.setText(Quality.getQuality(item.qualityId));
				status.setText(Status.getIdentifier(item.statusId));*/
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

	public void onItemChecked(View v)
	{
		CheckBox checkBox = (CheckBox)v.findViewById(R.id.releaseCheckBox);
		int position = (Integer)checkBox.getTag();
		onItemChecked(checkBox,position);
		return;
	}

	public void onItemChecked(CheckBox checkBox, int position)
	{
		ListView list = ReleasesFragment.this.getListView();
		// if we are checked
		if ( list.getCheckedItemPositions().get(position) == true ) {
			// then uncheck us
			ReleasesFragment.this.getListView().setItemChecked(position,false);
			checkBox.setChecked(false);
		} else {
			// otherwise check us
			ReleasesFragment.this.getListView().setItemChecked(position,true);
			checkBox.setChecked(true);
		}
		if ( actionMode == null ) {
			actionMode = getSherlockActivity().startActionMode( new ActionMode.Callback() {

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					// clear the checked items
					SparseBooleanArray array = getListView().getCheckedItemPositions();
					for ( int i=0; i < array.size(); i++ ) {
						if ( array.valueAt(i) ) {
							getListView().setItemChecked(i,false);
						}
					}
					// uncheck all the checked checkboxes
					for ( int i=0; i < getListView().getChildCount(); i++ )
						((CheckBox)getListView().getChildAt(i).findViewById(R.id.releaseCheckBox)).setChecked(false);
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
							int[] ids = new int[l.getCheckedItemCount()];
							SparseBooleanArray array = l.getCheckedItemPositions();
							for ( int i=0; i < ids.length; i++ ) {
								ids[i] = ((ReleaseJson) l.getItemAtPosition(array.keyAt(i))).id;
							}
							Preferences pref = Preferences.getSingleton(getSherlockActivity());
							ReleaseDownloadTask task = new ReleaseDownloadTask(pref, ids){
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
							int[] ids = new int[l.getCheckedItemCount()];
							SparseBooleanArray array = l.getCheckedItemPositions();
							for ( int i=0; i < ids.length; i++ ) {
								ids[i] = ((ReleaseJson) l.getItemAtPosition(array.keyAt(i))).id;
							}
							Preferences pref = Preferences.getSingleton(getSherlockActivity());
							ReleaseIgnoreTask task = new ReleaseIgnoreTask(pref, ids){
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
						/*case R.id.detailsMenuItem:
						{
							ReleaseJson listItem = (ReleaseJson) l.getItemAtPosition(l.getCheckedItemPosition());
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(listItem.info.detailUrl));
							startActivity(intent);
							return true;
						}*/
					}
					return false;
				}
			});
			actionMode.setTitle(this.getListView().getCheckedItemCount() + " Item Selected");
		} else if ( actionMode != null ) {
			actionMode.setTitle(this.getListView().getCheckedItemCount() + " Item Selected");
			if ( this.getListView().getCheckedItemCount() <= 0 ) {
				actionMode.finish();
				actionMode = null;
			}
		}
		return;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if ( actionMode != null ) {
			actionMode.finish();
			actionMode = null;
		}
		Intent intent = new Intent( this.getSherlockActivity(), ReleaseActivity.class );
		ReleaseJson item = releaseAdapter.getItem(position);
		intent.putExtra("id", item.id);
		intent.putExtra("title", item.info.name);
		intent.putExtra("provider", item.info.provider);
		intent.putExtra("age", item.info.age);
		intent.putExtra("size", item.info.size);
		intent.putExtra("score", item.info.score);
		intent.putExtra("quality", Quality.getQuality(item.qualityId));
		intent.putExtra("status", Status.getLabel(item.statusId));
		intent.putExtra("detailUrl", item.info.detailUrl);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
		onItemChecked(view);
		return true;
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
		return Preferences.getSingleton(getSherlockActivity()).getCouchPotato().movieGet(id).releases;
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
