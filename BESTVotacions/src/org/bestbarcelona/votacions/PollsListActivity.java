package org.bestbarcelona.votacions;

import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.bestbarcelona.votacions.datasource.BVPoll;
import org.bestbarcelona.votacions.datasource.BVUser;
import org.bestbarcelona.votacions.datasource.DataSourceCallBack;
import org.bestbarcelona.votacions.datasource.DataSourceController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PollsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listViewPolls;
    private PollsListAdapter adapter;

    private SwipeRefreshLayout mListViewContainer;
    private SwipeRefreshLayout mEmptyViewContainer;

    private TextView textUserName;
    private TextView textUserStatus;


    @Override
    protected void onStart() {
        super.onStart();
        registerForContextMenu(listViewPolls);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterForContextMenu(listViewPolls);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Loads the XML view layout
        setContentView(R.layout.activity_polls_list);

        // set Action Bar icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action_best_logomark);

        // SwipeRefreshLayout
        mListViewContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_listView);
        mEmptyViewContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_emptyView);

        // Set TextViews
        textUserName = (TextView) findViewById(R.id.textUserName);
        textUserStatus = (TextView) findViewById(R.id.textUserStatus);
        refreshUserInfo();

        // Configure SwipeRefreshLayout
        onCreateSwipeToRefresh(mListViewContainer);
        onCreateSwipeToRefresh(mEmptyViewContainer);

        adapter = new PollsListAdapter(this, BVUser.getMyUser());
        this.listViewPolls = (ListView) findViewById(R.id.listViewPolls);
        listViewPolls.setAdapter(adapter);
        listViewPolls.setEmptyView(mEmptyViewContainer);

        BVUser.getMyUser().fetchInBackground();


        if ((int) BVUser.getMyUser().getMembership() == 0) {
            AlertDialog.Builder adb = new AlertDialog.Builder(PollsListActivity.this);
            adb.setTitle("Welcome");
            adb.setMessage("Your account is not activated. Contact with your LBG to activate it.");
            adb.setNegativeButton("Ok", null);
            adb.show();
        }


        final ListView listView = (ListView) findViewById(R.id.listViewPolls);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object o = listView.getItemAtPosition(position);
                BVPoll pollSelected = (BVPoll) o;
                Intent myIntent = new Intent(PollsListActivity.this, PollInfoActivity.class);
                myIntent.putExtra("id", pollSelected.getObjectId());
                PollsListActivity.this.startActivity(myIntent);
            }
        });
    }

    private void onCreateSwipeToRefresh(SwipeRefreshLayout refreshLayout) {

        refreshLayout.setOnRefreshListener(this);

        refreshLayout.setColorScheme(
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (adapter != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            refreshPollsList();
                        }
                    }).start();
                }
                mListViewContainer.setRefreshing(false);
                mEmptyViewContainer.setRefreshing(false);
            }
        }, 4000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_polls_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ParseUser.logOut();
                            setResult(RESULT_OK);
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // Nothing happens
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.are_you_sure_logout)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener).show();
        } else if (id == R.id.action_create_poll) {
            if (BVUser.getMyUser().getCanCreate()) {
                Intent intent = new Intent(this, CreatePollActivity.class);
                startActivity(intent);
            } else {
                showToastWithText("You don't have privileges to create a poll");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshPollsList() {
        BVUser.getMyUser().fetchInBackground();
        refreshUserInfo();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.loadObjects();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void showToastWithText(String text) {
        Context context = getApplicationContext();
        CharSequence textToast = text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, textToast, duration);
        toast.show();
    }

    public void refreshUserInfo() {
        textUserName.setText(BVUser.getMyUser().getFullName());

        String membershipText = "";
        int membership = (int) BVUser.getMyUser().getMembership();
        if (membership == 0) {
            membershipText = "Account not active";
        }
        else if (membership == 1) {
            membershipText = "Baby Member";
        }
        else if (membership == 2) {
            membershipText = "Full Member";
        }
        if (BVUser.getMyUser().getCanCreate()) {
            membershipText = membershipText.concat(" // Admin");
        }

        textUserStatus.setText(membershipText);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final BVPoll currentPoll = adapter.getItem(info.position);

        if  (currentPoll.getCreatedBy().getObjectId() == BVUser.getMyUser().getObjectId()) {

            if (item.getItemId() == R.id.remove_category) {

                AlertDialog.Builder adb = new AlertDialog.Builder(PollsListActivity.this);
                adb.setTitle("Delete");
                adb.setMessage("Are you sure you want to delete " + currentPoll.getName() + "?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DataSourceController.getInstance().deletePoll(currentPoll, new DataSourceCallBack() {
                            @Override
                            public void handleDatasourceCallBack(List objects, Boolean success) {
                                listViewPolls.post(new Runnable() {
                                    public void run() {
                                        refreshPollsList();
                                    }
                                });
                            }
                        });
                    }
                });
                adb.show();
            }
        } else {
            showToastWithText("You don't have privileges to delete this poll.");
        }
        return true;
    }

}
