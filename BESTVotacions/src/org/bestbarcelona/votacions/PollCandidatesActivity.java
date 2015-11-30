package org.bestbarcelona.votacions;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.bestbarcelona.votacions.datasource.BVPoll;
import org.bestbarcelona.votacions.datasource.DataSourceCallBack;
import org.bestbarcelona.votacions.datasource.DataSourceController;

import java.util.ArrayList;
import java.util.List;


public class PollCandidatesActivity extends ActionBarActivity {

    private ArrayList<String> candidates = new ArrayList<String>();
    private String poll_id;
    private BVPoll poll;

    //adapter
    private PollAddCandidatesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_candidates);
        Intent PollIntent = getIntent();
        poll_id = PollIntent.getStringExtra("id");
        poll = DataSourceController.getInstance().getPollWithId(poll_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poll_candidates, menu);
        return true;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final String currentCandidate = adapter.getItem(info.position);


            if (item.getItemId() == R.id.remove_category) {

                AlertDialog.Builder adb = new AlertDialog.Builder(PollCandidatesActivity.this);
                adb.setTitle("Delete");
                adb.setMessage("Are you sure you want to delete " + currentCandidate + "?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        candidates.remove (candidates.indexOf(currentCandidate));
                        setAdapter();
                    }
                });
                adb.show();
            }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {

            View v = getWindow().getDecorView().findViewById(android.R.id.content);
            saveCandidates(v);
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void addCandidate (View v) {
        String newCandidate =( (EditText) findViewById(R.id.editNewCandidate)).getText().toString();
        if (newCandidate!=null && newCandidate.length()>0) {
            candidates.add(newCandidate);
            ((EditText) findViewById(R.id.editNewCandidate)).setText("");
            setAdapter();
        }

    }
    public void setAdapter (){
        if (candidates != null) {

            final ListView listView = (ListView) findViewById(R.id.candidateListView);
            adapter = new PollAddCandidatesAdapter(this, candidates);
            listView.setAdapter(adapter);
            registerForContextMenu(listView);
        }
    }
    public void saveCandidates (View v){

        if (candidates != null && candidates.size()>0) {
            DataSourceController.getInstance().updatePoll(poll, poll.getName(), poll.getInformation(), poll.getStartDate(), poll.getEndDate(), poll.getMembership(), poll.getSecret(), poll.getMultipleSelection(), candidates, new DataSourceCallBack() {
                @Override
                public void handleDatasourceCallBack(List objects, Boolean success) {
                    if (success) {
                        Log.v("poll updated", "poll updated successfully");
                    }
                }
            });
            Intent i = new Intent(this, PollsListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else {
            Context context = getApplicationContext();
            CharSequence textToast = "You need to add candidates to the poll!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, textToast, duration);
            toast.show();
        }
    }


}
