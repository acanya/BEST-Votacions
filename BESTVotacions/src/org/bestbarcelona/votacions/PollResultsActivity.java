package org.bestbarcelona.votacions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.bestbarcelona.votacions.datasource.BVCandidate;
import org.bestbarcelona.votacions.datasource.BVPoll;
import org.bestbarcelona.votacions.datasource.BVVote;
import org.bestbarcelona.votacions.datasource.DataSourceCallBack;
import org.bestbarcelona.votacions.datasource.DataSourceCallBackLong;
import org.bestbarcelona.votacions.datasource.DataSourceController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PollResultsActivity extends ActionBarActivity {
    public Number totalNumVotes;
    private BVPoll actualPoll;
    private PollResultsAdapter resultsAdapter;
    private PollVotersAdapter votersAdapter;
    private ListView cand;
    private ListView voters;
    private String PollID;
    private ArrayList<BVCandidate> candidatesArray;
    private ArrayList<BVVote> votesArray;
    public boolean numbers; //if true- show numbers, if false, show percentage

    private PollResultsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_results);

        final ProgressDialog progressDialog = new ProgressDialog(PollResultsActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();

        Intent newIntent = getIntent();
        PollID= newIntent.getStringExtra("id");
        actualPoll = DataSourceController.getInstance().getPollWithId(PollID);

        final TextView numVotesTextView = (TextView)findViewById(R.id.tvVotes);
        cand = (ListView) findViewById(R.id.CandidatesListView1);
        voters = (ListView) findViewById(R.id.listViewVoters);
        numbers=true;
        DataSourceController.getInstance().countVotes(actualPoll, new DataSourceCallBackLong() {
            @Override
            public void handleDatasourceCallBack(List candidates, List votes, Number totalVotes, Boolean success) {
                totalNumVotes = totalVotes;
                candidatesArray = (ArrayList)candidates;
                votesArray = (ArrayList)votes;
                Collections.sort(votesArray, new Comparator<BVVote>() {
                    @Override
                    public int compare(BVVote v1, BVVote v2) {
                        return v1.getVoterName().compareTo(v2.getVoterName());
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        votersAdapter = new PollVotersAdapter(PollResultsActivity.this, votesArray);
                        resultsAdapter = new PollResultsAdapter(PollResultsActivity.this, candidatesArray, totalNumVotes,numbers);
                        voters.setAdapter(votersAdapter);
                        cand.setAdapter(resultsAdapter);
                        numVotesTextView.setText(String.valueOf(totalNumVotes));
                        progressDialog.dismiss();
                    }
                });
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poll_results, menu);
        return true;
    }
    public void showToastWithText(String text) {
        Context context = getApplicationContext();
        CharSequence textToast = text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, textToast, duration);
        toast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        if (id ==R.id.change_view){
            numbers= !numbers;
            String text;
            if (numbers){
                text = "view as numbers";
            }
            else {
                text = "view as percentage";
            }
            showToastWithText(text);
            setAdapter();

        }
        return true;
    }
    public void setAdapter (){

            final ListView listView = (ListView) findViewById(R.id.CandidatesListView1);
            adapter = new PollResultsAdapter(PollResultsActivity.this, candidatesArray, totalNumVotes,numbers);
            listView.setAdapter(adapter);
            registerForContextMenu(listView);

    }
}
