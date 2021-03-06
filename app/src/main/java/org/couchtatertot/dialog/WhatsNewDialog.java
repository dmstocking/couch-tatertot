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
package org.couchtatertot.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockDialogFragment;

public class WhatsNewDialog extends SherlockDialogFragment {

	String title = null;
	DialogInterface.OnClickListener okListener = null;
	
	private static String whatsNew =
			"- Added Help.\n" +
			"- Moved from couchpotato cache to downloaded image files.\n" +
			"- Added categories wheel for movie lists.\n" +
			"- Added a new release view.\n" +
			"- Updated the releases view.\n" +
			"- Automatic actions on update. (to save you the pain)\n" +
			"- HTTP authentication.\n" +
			"- True HTTPS support. (Thank hardwarefault)\n" +
			"\n" +
			"Please show your support by rating/reviewing this app on Google Play!";
	
	public WhatsNewDialog()
	{
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getSherlockActivity());
		builder.setTitle(title);
		builder.setMessage(whatsNew);
		builder.setPositiveButton("Dismiss", okListener);
		builder.setCancelable(false);
		return builder.create();
	}
	
	public void setTitle( String title )
	{
		this.title = title;
	}
	
	public String getTitle( String title )
	{
		return title;
	}
	
	public void setOnOkClick( OnClickListener listener )
	{
		okListener = listener;
	}

}
