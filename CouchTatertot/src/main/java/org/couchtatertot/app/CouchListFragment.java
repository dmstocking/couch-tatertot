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

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListFragment;

public class CouchListFragment extends SherlockListFragment {
	
	private boolean retainedLifecycle = false;
	
	protected boolean isInRetainLifecycle()
	{
		return retainedLifecycle;
	}
	
	protected boolean isRetainInstance()
	{
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(isRetainInstance());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.retainedLifecycle = true; // past this point if this is retained then this value will stay true
		// if it goes back to false then we recreated the fragment
	}
	
}
