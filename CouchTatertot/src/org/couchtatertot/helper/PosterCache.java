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
package org.couchtatertot.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ConcurrentMap;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.google.common.collect.MapMaker;

public class PosterCache {
	
	private static final String cacheLogName = "PosterCache";
	private static final String cacheFolder = "posters";

	public static PosterCache singleton;
	
	private Context c;
	private File cacheDir;
	private LruCache<String,Bitmap> memCache;

	public static void setUpSingleton( Context c )
	{
		if ( singleton == null )
			singleton = new PosterCache(c);
	}
	
	public static void newSingleton( Context c )
	{
		singleton = new PosterCache(c);
	}
	
	private PosterCache( Context c )
	{
		this.c = c;
		this.cacheDir = new File(c.getExternalCacheDir(), cacheFolder);
		this.cacheDir.mkdirs();
		int memClass = ((ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		// use half of the memory unless we have less then 32MB
		int cacheSize = memClass * 1024 * 1024 / 2;
		if ( memClass < 32 )
			cacheSize = memClass * 1024 * 1024 / 4;
		this.memCache = new LruCache<String,Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
		        return value.getRowBytes() * value.getHeight();
			}
		};
	}
	
	public boolean in( String key )
	{
		return inMem(key) || inDisk(key);
	}
	
	public boolean inMem( String key )
	{
		String filename = sanatizeKey(key);
		Bitmap ret = memCache.get(key);
		return ret != null;
	}
	
	public boolean inDisk( String key )
	{
		String filename = sanatizeKey(key);
		try {
			if ( cacheDir.canRead() ) {
				File tmp = new File( cacheDir, filename );
				return tmp.exists();
			}
		} catch (Exception e) {
			Log.e(cacheLogName, "Error finding if \"" + filename + "\" exists on the disk. ERROR:" + e.getMessage(), e);
		}
		return false;
	}
	
	public void put( String key, Bitmap bitmap )
	{
		String filename = sanatizeKey(key);
		Bitmap existing = memCache.get(key);
		if ( existing == null ) {
			synchronized ( memCache ) {
				memCache.put(filename, bitmap);
			}
		}
		try {
			File tmp = new File( cacheDir, filename );
			if ( tmp.exists() == false ) {
				FileOutputStream out = new FileOutputStream( tmp );
				bitmap.compress(CompressFormat.PNG, 90, out);
				out.close();
				Log.e(cacheLogName, "Added poster \"" + filename + "\" to disk cache.");
			} else {
				Log.e(cacheLogName, "Poster already existed in cache.");
			}
		} catch (Exception e) {
			Log.e(cacheLogName, "Error adding poster. ERROR:" + e.getMessage(), e);
		}
	}
	
	
	/**
	 * @param key	the name of the poster to get from couchpotato cache ASSUMED UNIQUE NAMING
	 * @return		the bitmap for the poster from memory or null if it does not exist
	 */
	public Bitmap getFromMemory( String key )
	{
		// strip everything but the actual filename for the key
		key = sanatizeKey(key);
		synchronized ( memCache ) {
			return memCache.get(key);
		}
	}
	
	/**
	 * @param key	the name of the poster to get from couchpotato cache ASSUMED UNIQUE NAMING
	 * @return		the bitmap for the poster from the disk or null if it does not exist
	 */
	public Bitmap getFromDisk( String key )
	{
		key = sanatizeKey(key);
		try {
			File file = new File( cacheDir, key );
			if ( file.exists() ) {
				FileInputStream in = new FileInputStream( file );
				// get from disk
				Bitmap map = BitmapFactory.decodeStream(in);
				in.close();
				// re-add to the cache
				synchronized ( memCache ) {
					memCache.put(key, map);
				}
				return map;
			}
		} catch (Exception e) {
			Log.e(cacheLogName, "Error getting poster. ERROR: " + e.getMessage(), e);
		}
		return null;
	}
	
	public void clear()
	{
		clearMem();
		clearDisk();
	}
	
	public void clearMem()
	{

		synchronized ( memCache ) {
			memCache.evictAll();
		}
	}

	public void clearDisk()
	{
		try {
			File dir = cacheDir;
			for ( File f : dir.listFiles() ) {
				if ( f.isFile() ) {
					f.delete();
				}
			}
		} catch (Exception e) {
			Log.e(cacheLogName, "Error trying to clear poster cache. ERROR: " + e.getMessage(), e);
		}
	}
	
	private String sanatizeKey(String key) {
		return key.replaceAll("[.:/,%?&=]", "_").replaceAll("_+", "_");
	}
}
