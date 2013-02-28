/*
 * 	SickStache is a android application for managing SickBeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	SickStache is free software: you can redistribute it and/or modify
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
package org.couchtatertot.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import org.couchpotato.CouchPotato;

public class Preferences implements OnSharedPreferenceChangeListener {
	
	private static Preferences singleton;
	
	public boolean isUpdated = false;
	
	private SharedPreferences pref;
	private CouchPotato potato;
	
	private OnSharedPreferenceChangeListener listener;
	
	public static void newSingleton( Context c )
	{
		c = c.getApplicationContext();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
	    singleton = new Preferences( pref, c );
	}
	
	public static Preferences getSingleton( Context c ) {
		if ( singleton == null )
			newSingleton( c );
		return singleton;
	}

	public Preferences( SharedPreferences pref, Context c )
	{
		this.pref = pref;
		pref.registerOnSharedPreferenceChangeListener( this );
		try {
			c = c.getApplicationContext();
			int versionCurrent = c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionCode;
			int versionSave = pref.getInt("version", -1);
			if ( versionCurrent != versionSave ) {
				Editor edit = pref.edit();
				edit.putInt("version", versionCurrent);
				edit.commit();
				isUpdated = true;
			}
		} catch (Exception e) {
			Log.e("Preferences", "ERROR: " + e.getMessage(), e);
		}
		updateCouchPotato();
	}
	
	public String getHost()
	{
		return pref.getString("host", "192.168.0.1");
	}
	
	public int getPort()
	{
		return Integer.parseInt(pref.getString("port", "5050"));
	}
	
	// TODO I don't know if I want this considering I should just go auto get it
	public String getAPI()
	{
		return pref.getString("api", "");
	}
	
	public boolean getHTTPS()
	{
		return pref.getBoolean("https", false);
	}
	
	public boolean getTrustAll()
	{
		return pref.getBoolean("trustAll", true);
	}
	
	public String getTrustMe()
	{
	    return pref.getString("trustMe", "");
	}
	
	public String getPath()
	{
		return pref.getString("path","");
	}
	
	public String getUsername()
	{
		return pref.getString("username", "");
	}
	
	public String getPassword()
	{
		return pref.getString("password", "");
	}
	
	public SortEnum getNotificationSort()
	{
		try {
			return SortEnum.valueOf(pref.getString("notification_sort", "DESCENDING"));
		} catch (Exception e) {
			setNotificationSort( SortEnum.DESCENDING );
			return SortEnum.DESCENDING;
		}
	}
	
	public void setNotificationSort( SortEnum sort )
	{
		SharedPreferences.Editor edit = pref.edit();
		edit.putString("notification_sort", sort.toString());
		edit.commit();
	}
	
	public CouchPotato getCouchPotato()
	{
		// this should work because we are passing a reference not the object
		// so when we assign a new value we are not changing any of the old objects
		return potato;
//		return new CouchPotato(false, "10.30.0.159", 5050, null, "0e8b7a6885f947d0aa547246deb95caa", "", "");
	}
	
	public void setSickBeard( String host, String port, String api, String path, String username, String password )
	{
		SharedPreferences.Editor edit = pref.edit();
		edit.putString("host", host);
		edit.putString("port", port);
		edit.putString("api", api);
		edit.putString("path", path);
		edit.putString("username", username);
		edit.putString("password", password);
		edit.commit();
		updateCouchPotato();
	}
	
	private void updateCouchPotato()
	{
		potato = new CouchPotato( getHTTPS(), getHost(), getPort(), getPath(), getAPI(), getUsername(), getPassword(), getTrustAll(), getTrustMe() );
	}

	public void registerSharedPreferencesChangedListener( OnSharedPreferenceChangeListener listener )
	{
		this.listener = listener;
	}

	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		this.updateCouchPotato();
		if ( listener != null ) {
			listener.onSharedPreferenceChanged(arg0, arg1);
		}
	}
}
