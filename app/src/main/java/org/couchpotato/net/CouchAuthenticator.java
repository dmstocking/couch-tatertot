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
package org.couchpotato.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class CouchAuthenticator extends Authenticator {

	private String user;
	private String pass;
	private String hostname;
	
	public CouchAuthenticator( String user, String pass, String hostname ) {
		this.user = user;
		this.pass = pass;
		this.hostname = hostname;
	}
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		if( getRequestingSite().getHostAddress().equals(this.hostname) ||
				getRequestingSite().getHostName().equals(this.hostname))
			return new PasswordAuthentication(user,pass.toCharArray());
		else
			return new PasswordAuthentication("", "".toCharArray());
	}

}
