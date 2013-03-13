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

import com.google.gson.GsonBuilder;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.couchpotato.json.*;
import org.couchpotato.json.deserializer.JsonBooleanDeserializer;
import org.couchpotato.json.type.JsonBoolean;
import org.couchpotato.net.CouchAuthenticator;
import org.couchpotato.net.ssl.DefaultTrustManager;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

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
	private boolean trustAll = true;
	
	public CouchPotato( boolean ssl, String host, int port, String path, String api, String username, String password, boolean trustAll, String trustMe )
	{
		this( ssl ? "https" : "http", host, port, path, api, username, password, trustAll, trustMe );
	}
	
	private CouchPotato( String scheme, String hostname, int port, String path, String api, String username, String password, boolean trustAll, String trustMe )
	{
		this.scheme = scheme;
		this.hostName = hostname;
		this.port = port;
		this.path = path;
		this.api = api;
		this.username = username;
		this.password = password;
		this.trustAll = trustAll;
		
		if ( this.username == null )
			this.username = "";
		if ( this.password == null )
			this.password = "";
		
		// Configure SSL behavior based on user preferences
		Authenticator.setDefault(new CouchAuthenticator(username, password, hostname));
		HostnameVerifier verifier;
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager(trustAll, trustMe)}, new SecureRandom());
			if( trustAll ) {
				verifier = new AllowAllHostnameVerifier();
			} else {
				verifier = new StrictHostnameVerifier();
			}
			HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(verifier);
		} catch (NoSuchAlgorithmException e) {
			
		} catch (KeyManagementException e) {
			
		} catch (KeyStoreException e) {
			
		}
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
			filename = filename.replace("\\", "/");
			if ( filename.startsWith("/") == false ) {
				filename = "/" + filename;
			}
			return this.getUri("file.cache" + filename, null).toURL();
		} catch (URISyntaxException e) {
			throw new MalformedURLException(e.getMessage());
		}
	}
	
	public void loggingGet( int which ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		// TODO make me
	}
	
	public List<String> loggingPartial( Integer lines, LoggingTypeEnum type ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		if ( lines != null ) {
			builder.append("&lines=");
			builder.append(lines);
		}
		if ( type != null ) {
			builder.append("&type=");
			builder.append(type.toString().toLowerCase());
		}
		LoggingJson json = this.<LoggingJson>command("logging.partial/", builder.toString(), LoggingJson.class);
		// split will remove the time stamp ... and I want it
//		Pattern newLine = Pattern.compile("\\\\n\\d{2}\\-\\d{2} \\d{2}:\\d{2}:\\d{2}");
		// Lazy >.> figure this out better later
		Pattern newLine = Pattern.compile("\\\\n");
		return Arrays.asList(newLine.split(json.log));
	}
	
	public void movieAdd( Integer profileId, String imdbId, String title ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		if ( profileId != null ) {
			builder.append("&profile_id=");
			builder.append(profileId);
		}
		builder.append("&identifier=");
		builder.append(imdbId);
		if ( title != null ) {
			builder.append("&title=");
			builder.append(title);
		}
		// all you get back is success and currently it doens't actually mean success
		// if you give an id that doesn't exist it returns "success" : true
		this.<Object>command("movie.add/", builder.toString(), Object.class);
	}
	
	public void movieDelete( List<Integer> IDs, PageEnum page ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("&id=");
		Iterator<Integer> iter = IDs.iterator();
		if ( iter.hasNext() ) {
			builder.append(iter.next().toString().toLowerCase());
			while ( iter.hasNext() ) {
				builder.append(",");
				builder.append(iter.next().toString().toLowerCase());
			}
		}
		builder.append("&delete_from=");
		builder.append(page.toString().toLowerCase());
		// SEE movieAdd for why this is here
		this.<Object>command("movie.delete/", builder.toString(), Object.class);
	}
	
	public void movieEdit( int profileId, int id, String defaultTitle ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("&profile_id=");
		builder.append(profileId);
		builder.append("&id=");
		builder.append(id);
		builder.append("&default_title=");
		builder.append(defaultTitle);
		// SEE movieAdd for why this is here
		this.<Object>command("movie.edit/", builder.toString(), Object.class);
	}
	
	public MovieJson movieGet( int id ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("&id=");
		builder.append(id);
		// SEE movieAdd for why this is here
		return this.<MovieWrapperJson>command("movie.get/", builder.toString(), MovieWrapperJson.class).movie;
	}
	
	/**
	 * Gets a list of movies
	 * 
	 * @param status		Filter by status
	 * @param limit			Number of items to return
	 * @param offset		Offset to start return items at
	 * @param search		Filter by search
	 * @param startsWith	Filter by what the movie title starts with
	 */
	public List<MovieJson> movieList( String status, int limit, int offset, String search, String startsWith ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		if ( status != null ) {
			builder.append("&status=");
			builder.append(status);
		}
		if ( limit > 0 ) {
			builder.append("&limit_offset=");
			builder.append(limit);
			if ( offset > 0 ) {
				builder.append(",");
				builder.append(offset);
			}
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
		StringBuilder builder = new StringBuilder();
		builder.append("&id=");
		builder.append(id);
		// SEE movieAdd for why this is here
		this.<Object>command("movie.refresh/", builder.toString(), Object.class);
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
	public List<NotificationJson> notificationList( String limitOffset ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		if ( limitOffset != null ) {
			builder.append("&limit_offset=");
			builder.append(limitOffset);
		}
		return this.<NotificationListJson>command("notification.list/", builder.toString(), NotificationListJson.class).notifications;
	}
	
	public List<ProfileJson> profileList() throws MalformedURLException, IOException, SocketTimeoutException
	{
		return this.<ProfileListJson>command("profile.list/", null, ProfileListJson.class).list;
	}
	
	public List<QualityJson> qualityList() throws MalformedURLException, IOException, SocketTimeoutException
	{
		return this.<QualityListJson>command("quality.list/", null, QualityListJson.class).list;
	}
	
	public void releaseDelete( int id ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("&id=");
		builder.append(id);
		this.<Object>command("release.delete/", builder.toString(), Object.class);
		return;
	}
	
	public void releaseDownload( int id ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("&id=");
		builder.append(id);
		this.<Object>command("release.download/", builder.toString(), Object.class);
		return;
	}
	
	public void releaseIgnore( int id ) throws MalformedURLException, IOException, SocketTimeoutException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("&id=");
		builder.append(id);
		this.<Object>command("release.ignore/", builder.toString(), Object.class);
		return;
	}
	
	public void renamerScan() throws MalformedURLException, IOException, SocketTimeoutException
	{
		;
	}
	
	public List<StatusJson> statusList() throws MalformedURLException, IOException, SocketTimeoutException
	{
		return this.<StatusListJson>command("status.list/", null, StatusListJson.class).list;
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
		if ( path == null || path.length() == 0 )
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
