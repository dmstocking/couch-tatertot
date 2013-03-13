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

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;
import org.couchtatertot.dialog.WhatsNewDialog;
import org.couchtatertot.fragment.ManageFragment;
import org.couchtatertot.fragment.WantedFragment;
import org.couchtatertot.helper.PosterCache;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.task.QualityListTask;
import org.couchtatertot.task.StatusListTask;

public class HomeActivity extends SherlockFragmentActivity implements OnSharedPreferenceChangeListener {
	
	private static int PREFERENCES_ACTIVITY_REQUEST_CODE = 1;

	private boolean preferencesChanged = false;
	
	private ViewPager viewpager;
	private SlideAdapter pageAdapter;
	private TitlePageIndicator pageIndicator;
	
	private WantedFragment wantedFrag;
	private ManageFragment manageFrag;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.getSingleton(this).registerSharedPreferencesChangedListener(this);
        PosterCache.getSingleton(this);
        setContentView(R.layout.main_activity);
        
        wantedFrag = new WantedFragment();
        manageFrag = new ManageFragment();
        new QualityListTask(Preferences.getSingleton(this)).execute();
        new StatusListTask(Preferences.getSingleton(this)).execute();
        
        viewpager = ((ViewPager)findViewById(R.id.viewpager));
        pageIndicator = ((TitlePageIndicator)findViewById(R.id.viewPagerIndicator));
        pageAdapter =  new SlideAdapter( this.getSupportFragmentManager() );
        viewpager.setAdapter( pageAdapter );
        pageIndicator.setViewPager( viewpager );
        
        if ( Preferences.getSingleton(this).isUpdated ) {
        	showWhatsNewDiag();
        }
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
			{
				Intent intent = new Intent(this,LogActivity.class);
				startActivity(intent);
				return true;
			}
		case R.id.notificationMenuItem:
			{
				Intent intent = new Intent(this,NotificationsActivity.class);
				startActivityForResult(intent, PREFERENCES_ACTIVITY_REQUEST_CODE);
				return true;
			}
		case R.id.settingsMenuItem:
			{
				Intent intent = new Intent(this,PreferencesActivity.class);
				startActivityForResult(intent, PREFERENCES_ACTIVITY_REQUEST_CODE);
				return true;
			}
		case R.id.whatsNewMenuItem:
			{
	        	showWhatsNewDiag();
	        	return true;
			}
		case R.id.aboutMenuItem:
			{
				Intent intent = new Intent(this,AboutActivity.class);
				startActivity(intent);
				return true;
			}
		case R.id.helpMenuItem:
			{
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Buttink/couch-tatertot/wiki/FAQ"));
				startActivity(i);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void showWhatsNewDiag() {
		// make sure the dialog box isnt already up
		Fragment f = getSupportFragmentManager().findFragmentByTag("whatsnew");
		if ( f == null ) {
			// since it isnt lets make it
		    WhatsNewDialog diag = new WhatsNewDialog();
		    diag.setOnOkClick( new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Preferences.getSingleton(HomeActivity.this).isUpdated = false;
				}
			});
		    diag.show(getSupportFragmentManager(), "whatsnew");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if we came back from the PreferencesActivity
		if ( requestCode == PREFERENCES_ACTIVITY_REQUEST_CODE ) {
			if ( preferencesChanged ) {
				wantedFrag.refresh();
				manageFrag.refresh();
				preferencesChanged = false;
		        new QualityListTask(Preferences.getSingleton(this)).execute();
		        new StatusListTask(Preferences.getSingleton(this)).execute();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		preferencesChanged = true;
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