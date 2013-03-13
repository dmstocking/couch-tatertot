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
package org.couchpotato.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class DefaultTrustManager implements X509TrustManager {
    
    private boolean trustAll;
    private String trustMe;
    private List<X509TrustManager> trustManagers = new ArrayList<X509TrustManager>();
    
    public DefaultTrustManager(boolean trustAll, String trustMe) throws NoSuchAlgorithmException, KeyStoreException {
        this.trustAll = trustAll;
        this.trustMe = trustMe;
        
        // Add defaults trust-managers
        TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmFactory.init((KeyStore)null);
        for (TrustManager tm : tmFactory.getTrustManagers()) {
            if (tm instanceof X509TrustManager)
                trustManagers.add((X509TrustManager)tm);
        }
    }
    
	@Override
	public void checkClientTrusted(X509Certificate[] chain, String arg1)
			throws CertificateException {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String arg1)
			throws CertificateException {
	    // Return if we should trust all certificates
	    if(trustAll)
	        return;
	    // Check if certificate is equals to the manually inserted one
	    X509Certificate ss_cert = chain[0];
        try {
            String thumbprint = DefaultTrustManager.getThumbPrint(ss_cert);
            String trustMe = this.trustMe.replaceAll("[^a-fA-F0-9]+", "");
            if(thumbprint.equalsIgnoreCase(trustMe))
                return;
        } catch (NoSuchAlgorithmException e1) {
            /* Certificate is not accepted, continue */
        }
        
        // Check if certificate is valid anyway
	    for (X509TrustManager tm : this.trustManagers) {
	        try {
	            tm.checkServerTrusted(chain, arg1);
	            return;
            } catch (Exception e) {
                /* Certificate is not accepted, continue */
            }
        }
	    throw new CertificateException("Invalid SSL certificate");
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
	    ArrayList<X509Certificate> issuers = new ArrayList<X509Certificate>();
	    for (X509TrustManager tm : trustManagers ) {
	        issuers.addAll(Arrays.asList(tm.getAcceptedIssuers()));
	    }
		return (X509Certificate[]) issuers.toArray();
	}
	
	/// From Transdroid
	/// http://stackoverflow.com/questions/1270703/how-to-retrieve-compute-an-x509-certificates-thumbprint-in-java
	private static String getThumbPrint(X509Certificate cert) throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();
        return hexify(digest);
    }

    private static String hexify (byte bytes[]) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7',
                        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        StringBuffer buf = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; ++i) {
                buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
            buf.append(hexDigits[bytes[i] & 0x0f]);
        }

        return buf.toString();
    }

}