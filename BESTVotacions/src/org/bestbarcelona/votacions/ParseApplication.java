package org.bestbarcelona.votacions;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import org.bestbarcelona.votacions.datasource.BVCandidate;
import org.bestbarcelona.votacions.datasource.BVPoll;
import org.bestbarcelona.votacions.datasource.BVUser;
import org.bestbarcelona.votacions.datasource.BVVote;

public class ParseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Initialize Crash Reporting.
    ParseCrashReporting.enable(this);

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

      // Register subclasses
      ParseObject.registerSubclass(BVPoll.class);
      ParseObject.registerSubclass(BVCandidate.class);
      ParseObject.registerSubclass(BVUser.class);
      ParseObject.registerSubclass(BVVote.class);

    // Add your initialization code here
      Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));

    // Register for the Push service
      ParsePush.subscribeInBackground("", new SaveCallback() {
          @Override
          public void done(ParseException e) {
              if (e == null) {
                  Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
              } else {
                  Log.e("com.parse.push", "failed to subscribe for push", e);
            }
        }
    });

    //ParseUser.enableAutomaticUser();
    //ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    // defaultACL.setPublicReadAccess(true);
    //ParseACL.setDefaultACL(defaultACL, true);
  }
}
