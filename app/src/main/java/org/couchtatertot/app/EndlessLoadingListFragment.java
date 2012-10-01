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
package org.couchtatertot.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class EndlessLoadingListFragment<Params, Progress, Result> extends LoadingListFragment<Params, Progress, Result> implements OnScrollListener {
	
	protected abstract boolean getAtEnd();
	protected abstract int getUpdatePadding();
	protected abstract int getStep(); // not actually used BUT I WILL STOP YOU FROM MESSING YOURSELF UP!
	protected abstract void clearAdapter();
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.getListView().setOnScrollListener(this);
	}
	
	@Override
	public void refresh() {
		clearAdapter();
		super.refresh();
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if ( visibleItemCount == 0 || getAtEnd() )
			return;
		if ( firstVisibleItem + visibleItemCount + getUpdatePadding() >= totalItemCount ) {
			if ( downloader.getStatus() == AsyncTask.Status.FINISHED ) {
				downloader = new Downloader();
				downloader.execute(getRefreshParams());
			}
		}
	}
	
	// I dont need this function
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) { }
}
