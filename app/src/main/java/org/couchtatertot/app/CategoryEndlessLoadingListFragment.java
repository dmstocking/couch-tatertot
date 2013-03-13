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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.couchtatertot.R;
import org.couchtatertot.widget.CategoryWheel;

// these names are starting to get long
public abstract class CategoryEndlessLoadingListFragment<Params, Progress, Result> extends EndlessLoadingListFragment<Params, Progress, Result> implements OnScrollListener {

	private CategoryWheel categoryWheel;

	public abstract void OnCategorySelected( String value );

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.category_loadable_list_fragment, container, false);
		categoryWheel = (CategoryWheel)root.findViewById(R.id.headerCategoryWheel);
		categoryWheel.addOnSelectListener( new CategoryWheel.OnSelectListener() {
			@Override
			public void OnSelect(String value) {
				CategoryEndlessLoadingListFragment.this.OnCategorySelected(value);
			}
		});
		spinner = (ProgressBar)root.findViewById(R.id.workingProgressBar);
		error = (TextView)root.findViewById(R.id.errorTextView);
		empty = (TextView)root.findViewById(R.id.emptyTextView);
		empty.setText(getEmptyText());

//		refreshMenuActionView = new ProgressBar(this.getActivity());
//	    refreshMenuActionView.setIndeterminateDrawable(this.getActivity().getResources().getDrawable(R.drawable.refresh_spinner));

		return root;
	}
}
