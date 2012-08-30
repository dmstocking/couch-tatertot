package org.couchtatertot;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;

public class HistoryService extends IntentService {

	public HistoryService() {
		super("HistoryService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		long lastCheck = SystemClock.elapsedRealtime();
		while ( true ) {
			if ( SystemClock.elapsedRealtime() - lastCheck > 10*60*1000 ) {
				
				try {
					Thread.sleep(10*60*1000);
				} catch (Exception e) {;}
			}
		}
	}

}
