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
package org.couchtatertot.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.couchpotato.json.ProfileJson;
import org.couchtatertot.R;
import org.couchtatertot.task.GetProfilesTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class EditDialog extends SherlockDialogFragment {

	protected List<String> titles;
	protected int selectedTitle;
	protected int selectedProfileId;
	
	protected String title = "Edit Movie";
	protected DialogInterface.OnClickListener okListener = null;
	
	protected Spinner titleSpinner;
	protected Spinner qualitySpinner;
	
	protected ProgressBar workingProgressBar;
	
	public EditDialog(String[] titles, String currentTitle, int currentProfileId)
	{
		this(Arrays.asList(titles), currentTitle, currentProfileId );
	}
	
	public EditDialog(List<String> titles, String currentTitle, int currentProfileId)
	{
		super();
		this.titles = titles;
		for ( int i=0; i < titles.size(); i++ ) {
			if ( titles.get(i).compareTo(currentTitle) == 0 )
				this.selectedTitle = i; 
		}
		this.selectedProfileId = currentProfileId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = this.getSherlockActivity().getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getSherlockActivity());
		builder.setTitle(title);
		View view = inflater.inflate(R.layout.edit_dialog, null);
		titleSpinner = (Spinner)view.findViewById(R.id.titleSpinner);
		qualitySpinner = (Spinner)view.findViewById(R.id.qualitySpinner);
		workingProgressBar = (ProgressBar)view.findViewById(R.id.workingProgressBar);
		titleSpinner.setAdapter(new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_item,titles));
		titleSpinner.setSelection(selectedTitle);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, okListener);
		builder.setNegativeButton(R.string.cancel, null);
		builder.setCancelable(false);
		
		// start the task to get profiles
		GetProfilesTask task = new GetProfilesTask(){
			@Override
			protected void onPostExecute(List<ProfileJson> result) {
				super.onPostExecute(result);
				if ( result != null ) {
					qualitySpinner.setAdapter(new ArrayAdapter<ProfileJson>(getSherlockActivity(),android.R.layout.simple_spinner_item, result){
						@Override
						public View getDropDownView(int position, View convertView, ViewGroup parent) {
							View row = convertView;
							if ( row == null ) {
								row = getSherlockActivity().getLayoutInflater().inflate(android.R.layout.simple_spinner_dropdown_item, null);
							}
							ProfileJson item = getItem(position);
							TextView text = (TextView) row.findViewById(android.R.id.text1);
							text.setText(item.label);
							return row;
						}

						@Override
						public View getView(int position, View convertView, ViewGroup parent) {
							View row = convertView;
							if ( row == null ) {
								row = getSherlockActivity().getLayoutInflater().inflate(android.R.layout.simple_spinner_item, null);
							}
							ProfileJson item = getItem(position);
							TextView text = (TextView) row.findViewById(android.R.id.text1);
							text.setText(item.label);
							return row;
						}
					});
					for ( int i=0; i < result.size(); i++ ) {
						if ( result.get(i).id == selectedProfileId ) {
							qualitySpinner.setSelection(i);
							break;
						}
					}
					workingProgressBar.setVisibility(View.GONE);
					titleSpinner.setVisibility(View.VISIBLE);
					qualitySpinner.setVisibility(View.VISIBLE);
				}
			}};
		task.execute();
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
	
	public int getSelectedProfileId()
	{
		return ((ProfileJson)qualitySpinner.getSelectedItem()).id;
	}
	
	public String getSelectedTitle()
	{
		return (String)titleSpinner.getSelectedItem();
	}
	
}
