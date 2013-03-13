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
package org.couchtatertot.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.couchtatertot.R;
import org.couchtatertot.app.CouchFragment;
import org.couchtatertot.dialog.DownloadReleaseDialog;
import org.couchtatertot.dialog.ToggleIgnoreReleaseDialog;
import org.couchtatertot.widget.WorkingTextView;

public class ReleaseFragment extends CouchFragment {

	int id;
	
	String title;
	String provider;
	String age;
	String size;
	String score;
	String quality;
	String status;

	String detailUrl;
	
	TextView titleTextView;
	TextView providerTextView;
	TextView ageTextView;
	TextView sizeTextView;
	TextView scoreTextView;
	TextView qualityTextView;
	TextView statusTextView;

	WorkingTextView downloadWorkingTextView;
	WorkingTextView ignoreWorkingTextView;
	WorkingTextView detailsWorkingTextView;
	
	@Override
	protected boolean isRetainInstance() {
		return true;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Intent intent = this.getActivity().getIntent();
		id = intent.getIntExtra("id", -1);
		title = intent.getStringExtra("title");
		provider = intent.getStringExtra("provider");
		age = intent.getStringExtra("age");
		size = intent.getStringExtra("size");
		score = intent.getStringExtra("score");
		quality = intent.getStringExtra("quality");
		status = intent.getStringExtra("status");

		detailUrl = intent.getStringExtra("detailUrl");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.release_fragment, container, false);
		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		titleTextView = (TextView) view.findViewById(R.id.titleTextView);
		titleTextView.setText(title);
		providerTextView = (TextView) view.findViewById(R.id.providerTextView);
		providerTextView.setText(provider);
		ageTextView = (TextView) view.findViewById(R.id.ageTextView);
		ageTextView.setText(age);
		sizeTextView = (TextView) view.findViewById(R.id.sizeTextView);
		sizeTextView.setText(size);
		scoreTextView = (TextView) view.findViewById(R.id.scoreTextView);
		scoreTextView.setText(score);
		qualityTextView = (TextView) view.findViewById(R.id.qualityTextView);
		qualityTextView.setText(quality);
		statusTextView = (TextView) view.findViewById(R.id.statusTextView);
		statusTextView.setText(status);
		downloadWorkingTextView = (WorkingTextView) view.findViewById(R.id.downloadWorkingTextView);
		downloadWorkingTextView.setVisibility(View.VISIBLE);
		downloadWorkingTextView.text.setText("Download");
		downloadWorkingTextView.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final DownloadReleaseDialog diag = new DownloadReleaseDialog(id);
				diag.show(getFragmentManager(), "download");
			}
		});
		ignoreWorkingTextView = (WorkingTextView) view.findViewById(R.id.ignoreWorkingTextView);
		ignoreWorkingTextView.setVisibility(View.VISIBLE);
		ignoreWorkingTextView.text.setText("Toggle Ignore");
		ignoreWorkingTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final ToggleIgnoreReleaseDialog diag = new ToggleIgnoreReleaseDialog(id);
				diag.show(getFragmentManager(), "toggleignore");
			}
		});
		detailsWorkingTextView = (WorkingTextView) view.findViewById(R.id.detailsWorkingTextView);
		detailsWorkingTextView.setVisibility(View.VISIBLE);
		detailsWorkingTextView.text.setText("Details");
		detailsWorkingTextView.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(detailUrl));
				startActivity(intent);
			}
		});
	}
	
}
