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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockDialogFragment;
import org.couchtatertot.helper.Preferences;
import org.couchtatertot.task.ReleaseIgnoreTask;


public class ToggleIgnoreReleaseDialog extends SherlockDialogFragment {

	private int id;

	public ToggleIgnoreReleaseDialog(int id)
	{
		this.id = id;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final ProgressDialog dialog = ProgressDialog.show(getSherlockActivity(), "","Please wait...", true);
		dialog.setCancelable(true);
		Preferences pref = Preferences.getSingleton(getSherlockActivity());
		ReleaseIgnoreTask task = new ReleaseIgnoreTask(pref, id){
			@Override
			protected void onPostExecute(Void result) {
				if ( dialog != null && dialog.isShowing() )
					dialog.dismiss();
				ToggleIgnoreReleaseDialog.this.onPostExecute();
			}};
		task.execute();
		return dialog;
	}

	protected void onPostExecute()
	{
		return;
	}
	
}
