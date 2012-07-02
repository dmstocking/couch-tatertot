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
package org.couchtatertot;

import org.couchtatertot.fragments.ManageFragment;
import org.couchtatertot.fragments.WantedFragment;
import org.couchtatertot.helper.PosterCache;
import org.couchtatertot.helper.Preferences;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class HomeActivity extends SherlockFragmentActivity {
	
	private ViewPager viewpager;
	private SlideAdapter pageAdapter;
	private TitlePageIndicator pageIndicator;
	
	private WantedFragment wantedFrag;
	private ManageFragment manageFrag;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PosterCache.setUpSingleton(this);
        Preferences.setUpSingleton(this);
        setContentView(R.layout.main_activity);
        
        wantedFrag = new WantedFragment();
        manageFrag = new ManageFragment();
        
        viewpager = ((ViewPager)findViewById(R.id.viewpager));
        pageIndicator = ((TitlePageIndicator)findViewById(R.id.viewPagerIndicator));
        pageAdapter =  new SlideAdapter( this.getSupportFragmentManager() );
        viewpager.setAdapter( pageAdapter );
        pageIndicator.setViewPager( viewpager );
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		this.getSupportMenuInflater().inflate(R.menu.home_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() )
		{
		case R.id.logMenuItem:
			return true;
		case R.id.cacheMenuItem:
			return true;
		case R.id.settingsMenuItem:
			return true;
		case R.id.aboutMenuItem:
			PosterCache.singleton.clear();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class SlideAdapter extends FragmentPagerAdapter {

		public SlideAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int arg0) {
			switch( arg0 ) {
			case 0:
				return wantedFrag;
			case 1:
				return manageFrag;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public String getPageTitle(int position) {
			switch( position ) {
			case 0:
				return "Wanted";
			case 1:
				return "Manage";
			}
			return null;
		}
    }
}