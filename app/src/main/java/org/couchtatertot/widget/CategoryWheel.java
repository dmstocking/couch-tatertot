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
package org.couchtatertot.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.couchtatertot.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryWheel extends ScrollView {

	public interface OnSelectListener {

		public void OnSelect(String value);

	}

	private TextView lastClicked;
	private List<OnSelectListener> onSelectListeners = new ArrayList<OnSelectListener>();

	public CategoryWheel(Context context) {
		super(context);
		constructor(context);
	}

	public CategoryWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		constructor(context);
	}

	public CategoryWheel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		constructor(context);
	}

	private void constructor(Context context)
	{
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View root = inflater.inflate(R.layout.category_wheel,this,true);
		LinearLayout layout = (LinearLayout)root.findViewById(R.id.categoryLinearLayout);
		String[] items = context.getResources().getStringArray(R.array.category_wheel_items);
		for ( int i=0; i < items.length; i++ ) {
			TextView txt = (TextView)inflater.inflate(R.layout.category_wheel_item,null,false);
			txt.setText(items[i]);
			txt.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
					if ( lastClicked != view ) {
						TextView me = (TextView) view;
						me.setTextAppearance(CategoryWheel.this.getContext(), R.style.CategoryLabelSelected);
						CategoryWheel.this.lastClicked.setTextAppearance(CategoryWheel.this.getContext(), R.style.CategoryLabel);
						CategoryWheel.this.lastClicked = me;
						CategoryWheel.this.onSelectListener(((TextView) view).getText().toString());
					}
				}
			});
			if ( i == 0 ) {
				txt.setTextAppearance(this.getContext(), R.style.CategoryLabelSelected);
				lastClicked = txt;
			}
			layout.addView(txt);
		}
	}

	public void addOnSelectListener( OnSelectListener l) {
		this.onSelectListeners.add( l );
	}

	public void removeOnSelectListener( OnSelectListener l ) {
		this.onSelectListeners.remove( l );
	}

	private void onSelectListener( String value )
	{
		for ( OnSelectListener listener : this.onSelectListeners ) {
			if ( listener != null )
				listener.OnSelect( value );
		}
	}
}
