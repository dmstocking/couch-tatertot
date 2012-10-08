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

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.couchtatertot.R;
import org.couchtatertot.app.LoadingListFragment;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.widget.SafeArrayAdapter;

import java.util.List;

public class LogFragment extends LoadingListFragment<Void, Void, List<String>> {
	
	private SafeArrayAdapter<String> logAdapter;
	
	@Override
	protected boolean isRetainInstance() {
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logAdapter = new SafeArrayAdapter<String>(this.getActivity(), R.layout.log_banner_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( convertView == null ) {
					row = layoutInflater.inflate(R.layout.log_banner_item, null);
				}
				String item = getItem(position);
				TextView log = (TextView) row.findViewById(R.id.logTextView);
				log.setText(item);
				return row;
			}
		};
		// DO NOT SET ADAPTER UNTIL THE DATA HAS BEEN AQUIRED!!!!
		// this makes the list have a progress spinner for us
	}

	@Override
	protected String getEmptyText() {
		return "Empty Log";
	}

	@Override
	protected Void[] getRefreshParams() {
		return null;
	}

	@Override
	protected List<String> doInBackground(Void... arg0) throws Exception {
		return Preferences.getSingleton(getSherlockActivity()).getCouchPotato().loggingPartial(50,null);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(List<String> result) {
		if ( result != null && logAdapter != null ) {
			LogFragment.this.setListAdapter(logAdapter);
			logAdapter.clear();
			for ( String n : result ) {
				logAdapter.add(n);
			}
			logAdapter.notifyDataSetChanged();
			if ( logAdapter.getCount() == 0 ) {
				this.setListStatus(ListStatus.EMPTY);
			}
		}
	}
}
