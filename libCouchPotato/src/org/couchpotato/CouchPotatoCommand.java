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
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.HttpsURLConnection;

import org.couchpotato.json.JsonResponse;
import org.couchpotato.json.deserializer.JsonBooleanDeserializer;
import org.couchpotato.json.type.JsonBoolean;

import com.google.gson.GsonBuilder;

public class CouchPotatoCommand {

	private static final String SUCCESS = "success";

	private static final int TIMEOUT = 30000;
	
	URI command;
	Type type;
	
	public CouchPotatoCommand( URI command, Type type )
	{
		this.command = command;
		this.type = type;
	}
	
	private <T> JsonResponse<T> perform() throws MalformedURLException, IOException, SocketTimeoutException
	{
		HttpURLConnection server = null;
		if ( command.getScheme().compareTo("https") == 0 ) {
			server = (HttpsURLConnection)command.toURL().openConnection();
		} else {
			server = (HttpURLConnection)command.toURL().openConnection();
		}
		server.setConnectTimeout(TIMEOUT);
		Reader reader = new BufferedReader( new InputStreamReader(server.getInputStream() ) );
		// TypeToken cannot figure out T so instead it must be supplied
		//Type type = new TypeToken< JSONResponse<T> >() {}.getType();
		GsonBuilder build = new GsonBuilder();
		build.registerTypeAdapter(JsonBoolean.class, new JsonBooleanDeserializer() );
		JsonResponse<T> response = build.create().fromJson( reader, type );
		return response;
	}
	
	private <T> Boolean performSuccessful() throws MalformedURLException, IOException, SocketTimeoutException
	{
		return this.<T>perform().result.compareTo(SUCCESS) == 0;
	}
	
	private <T> T performData() throws MalformedURLException, IOException, SocketTimeoutException
	{
		return this.<T>perform().data;
	}
	
}
