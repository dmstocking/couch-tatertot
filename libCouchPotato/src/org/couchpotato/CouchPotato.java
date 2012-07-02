/*
 * 	libCouchPotato is a java library for communication with couchpotato
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
package org.couchpotato;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.couchpotato.json.*;
import org.couchpotato.json.deserializer.JsonBooleanDeserializer;
import org.couchpotato.json.type.JsonBoolean;
import org.couchpotato.net.CouchAuthenticator;
import org.couchpotato.net.ssl.DefaultTrustManager;

import com.google.gson.GsonBuilder;

public class CouchPotato {
	
	private static final int SOCKET_TIMEOUT = 30000;
	
	public enum LoggingTypeEnum {
		ALL, ERROR, INFO, DEBUG;
		
		@Override
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}
	
	public enum PageEnum {
		ALL, WANTED, MANAGE;
		
		@Override
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}

	private String scheme;
	private String hostName;
	private int port;
	private String path;
	private String api;
	
	private String username;
	private String password;
	
	private String version;
	
	public CouchPotato( boolean ssl, String host, int port, String path, String api, String username, String password )
	{
		this( ssl ? "https" : "http", host, port, path, api, username, password );
	}
	
	private CouchPotato( String scheme, String hostName, int port, String path, String api, String username, String password )
	{
		this.scheme = scheme;
		this.hostName = hostName;
		this.port = port;
		this.path = path;
		this.api = api;
		this.username = username;
		this.password = password;
		
		if ( this.username == null )
			this.username = "";
		if ( this.password == null )
			this.password = "";
		
		try {
			Authenticator.setDefault(new CouchAuthenticator(username,password));
			if ( scheme.compareTo("https") == 0 ) {
				SSLContext ctx = SSLContext.getInstance("TLS");
		        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
		        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
		        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
					@Override
					public boolean verify(String arg0, SSLSession arg1) {
						return true;
					}
				});
			}
		} catch ( Exception e ) {;}
	}
	
	public String getVersion()
	{
		if ( version == null ) {
			; // TODO go get it
		}
		return version;
	}
	
	public boolean appAvailable() throws MalformedURLException, IOException, SocketTimeoutException
	{
		return this.commandSuccessful("app.available/", "", Object.class);
	}
	
	public boolean appRestart() throws MalformedURLException, IOException, SocketTimeoutException
	{
		return this.commandSuccessful("app.restart/", "", Object.class);
	}
	
	public boolean appShutdown() throws MalformedURLException, IOException, SocketTimeoutException
	{
		return this.commandSuccessful("app.shutdown/", "", Object.class);
	}
	
	public String appVersion() throws MalformedURLException, IOException, SocketTimeoutException
	{
		return this.<AppVersionJson>command("app.version/", "", AppVersionJson.class).version;
	}
	
	public URL fileCache( String filename ) throws MalformedURLException
	{
		// WOW this sucks i hope this works for both windows and linux
		try {
			return this.getUri("file.cache" + filename, null).toURL();
		} catch (URISyntaxException e) {
			throw new MalformedURLException(e.getMessage());
		}
	}
	
	public void loggingGet( int which ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		// TODO make me
	}
	
	public void loggingPartial( int lines, LoggingTypeEnum type ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public void movieAdd( String profileId, String imdbId, String title ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public void movieDelete( List<Integer> IDs, PageEnum page ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public void movieGet( int id ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	/**
	 * Gets a list of movies
	 * 
	 * @param status		Filter by status
	 * @param limitOffset	Filter by limit with offset
	 * @param search		Filter by search
	 * @param startsWith	Filter by what the movie title starts with
	 */
	public List<MovieJson> movieList( String status, String limitOffset, String search, String startsWith ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		if ( status != null ) {
			builder.append("&status=");
			builder.append(status);
		}
		if ( limitOffset != null ) {
			builder.append("&limit_offset=");
			builder.append(limitOffset);
		}
		if ( search != null ) {
			builder.append("&search=");
			builder.append(search);
		}
		if ( startsWith != null ) {
			builder.append("&starts_with=");
			builder.append(startsWith);
		}
		return this.<MovieListJson>command("movie.list/", builder.toString(), MovieListJson.class).movies;
	}
	
	public void movieRefresh( int id ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public SearchResultsJson movieSearch( String query ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("&q=");
		builder.append(query);
		return this.<SearchResultsJson>command("movie.search/", builder.toString(), SearchResultsJson.class);
	}
	
	/**
	 * Gets a list of notifications
	 * 
	 * @param limitOffset	Filter by limit with offset
	 */
	public void notificationList( String limitOffset ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public void profileList() throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public void qualityList() throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public void releaseDelete( int id ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public void releaseDownload( int id ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public void releaseIgnore( int id ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public void renamerScan() throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public void updaterCheck() throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	private URI getUri( String command, String arguments ) throws URISyntaxException
	{
		// TODO fix this because couch potato api doesn't use all query options
		if ( arguments != null && arguments.length() == 0 )
			arguments = null;
		if ( path == null )
			return new URI( scheme, null, hostName, port, "/api/" + api + "/" + command, arguments, null );
		else
			return new URI( scheme, null, hostName, port, "/" + path + "/api/" + api + "/" + command, arguments, null );
	}
	
	// Using JsonResponse will not work all the time now
	// Remove it and push all the changes to the classes
	private <T> T command( String command, String arguments, Type type ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		try {
			URI uri = this.getUri(command,arguments);
			// this WILL throw a EOFexception if it gets a HTTP 301 response because it wont follow it
			// so ALL URLS MUST BE PERFECT!!!!
			HttpURLConnection server = (HttpURLConnection)uri.toURL().openConnection();
			// TODO going to try and not use this and see if everything works out
//			if ( uri.getScheme().compareTo("https") == 0 ) {
//				server = (HttpsURLConnection)uri.toURL().openConnection();
//			} else {
//				server = (HttpURLConnection)uri.toURL().openConnection();
//			}
			server.setConnectTimeout(SOCKET_TIMEOUT);
			Reader reader = new BufferedReader( new InputStreamReader(server.getInputStream() ) );
			// TypeToken cannot figure out T so instead it must be supplied
			GsonBuilder build = new GsonBuilder();
			build.registerTypeAdapter(JsonBoolean.class, new JsonBooleanDeserializer() );
			T response = build.create().fromJson( reader, type );
			return response;
		} catch ( URISyntaxException e ) {
			throw new MalformedURLException(e.getMessage());
		}
	}
	
	private boolean commandSuccessful( String command, String arguments, Type type ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		return command(command,arguments,type) != null ? true : false;
	}
	
	// pointless because no JsonResponse
//	private <T> T commandData( String command, String arguments, Type type ) throws MalformedURLException, IOException, SocketTimeoutException
//	{
//		return this.<T>command(command, arguments, type).data;
//	}
}
