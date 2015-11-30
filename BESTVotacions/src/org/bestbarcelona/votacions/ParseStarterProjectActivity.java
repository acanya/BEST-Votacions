package org.bestbarcelona.votacions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ui.ParseLoginBuilder;

import org.bestbarcelona.votacions.datasource.BVUser;

public class ParseStarterProjectActivity extends Activity {
    // Called activities request codes
    public final int LOGIN_ACTIVITY_INTENT_REQUEST_CODE = 0;
    public final int POLLS_LIST_ACTIVITY_INTENT_REQUEST_CODE = 1;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checks if the user is logged in. Else it shows the login screen activity
        checkLogin();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the user exited the login activity without performing a valid login
        // the activity is finished. If the login succeeded, the Polls List Activity
        // is executed.
        if (requestCode == LOGIN_ACTIVITY_INTENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("BESTVotacions", "Estic loguejat"); //
                runPollsList();
            }
        }
        else if (requestCode == POLLS_LIST_ACTIVITY_INTENT_REQUEST_CODE) {
            // If a RESULT_OK is get (means user logged out), the login screen is shown again
            if (resultCode == RESULT_OK) {
                checkLogin();
            }
        }
        else {
            finish();
        }
    }

    // Checks if the user is logged in. Else it shows the login screen activity
    protected void checkLogin() {
        if (BVUser.getMyUser() == null) {
            Log.d("BESTVotacions", "No estic loguejat"); //

            // Login activity call
            ParseLoginBuilder builder = new ParseLoginBuilder(ParseStarterProjectActivity.this);
            builder.setAppLogo(R.drawable.best_barcelona_color);
            startActivityForResult(builder.build(), LOGIN_ACTIVITY_INTENT_REQUEST_CODE);
        }
        else {
            runPollsList();
        }
    }

    protected void runPollsList() {
        // Initialize Parse Analytics
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        Intent intent = new Intent(this, PollsListActivity.class);
        startActivityForResult(intent, POLLS_LIST_ACTIVITY_INTENT_REQUEST_CODE);
    }
}
