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

import org.couchpotato.json.NotificationJson;
import org.couchtatertot.R;
import org.couchtatertot.app.LoadingListFragment;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.widget.SafeArrayAdapter;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotificationsFragment extends LoadingListFragment<Void, Void, List<NotificationJson>> {
	
	private SafeArrayAdapter<NotificationJson> notificationsAdapter;
	
	@Override
	protected boolean isRetainInstance() {
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		notificationsAdapter = new SafeArrayAdapter<NotificationJson>(this.getActivity(), R.layout.notifications_banner_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( convertView == null ) {
					row = layoutInflater.inflate(R.layout.notifications_banner_item, null);
				}
				NotificationJson item = getItem(position);
				TextView notification = (TextView) row.findViewById(R.id.notificationTextView);
				notification.setText(item.message);
				return row;
			}
		};
		// DO NOT SET ADAPTER UNTIL THE DATA HAS BEEN AQUIRED!!!!
		// this makes the list have a progress spinner for us
	}

	@Override
	protected String getEmptyText() {
		return "No Notifications";
	}

	@Override
	protected Void[] getRefreshParams() {
		return null;
	}

	@Override
	protected List<NotificationJson> doInBackground(Void... arg0) throws Exception {
		return Preferences.getSingleton().getCouchPotato().notificationList(null);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(List<NotificationJson> result) {
		if ( result != null && notificationsAdapter != null ) {
			NotificationsFragment.this.setListAdapter(notificationsAdapter);
			notificationsAdapter.clear();
			for ( NotificationJson n : result ) {
				notificationsAdapter.add(n);
			}
			// make a sorter that sorts by year
			notificationsAdapter.notifyDataSetChanged();
			if ( notificationsAdapter.getCount() == 0 ) {
				this.setListStatus(ListStatus.EMPTY);
			}
		}
	}
}
