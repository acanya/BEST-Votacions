package org.bestbarcelona.votacions;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import org.bestbarcelona.votacions.datasource.BVCandidate;
import org.bestbarcelona.votacions.datasource.BVPoll;
import org.bestbarcelona.votacions.datasource.BVUser;
import org.bestbarcelona.votacions.datasource.DataSourceCallBack;
import org.bestbarcelona.votacions.datasource.DataSourceController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PollInfoActivity extends ActionBarActivity {

    private ArrayList<BVCandidate> candidates = new ArrayList<BVCandidate>();
    private String poll_id;
    private BVPoll poll;
    private ProgressDialog progressDialog;
    private ListView listView;
    private ArrayList<BVCandidate> selectedCandidates;
    private Button sendBtn;
    private Button closeBtn;
    private Button resultsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_info);

        progressDialog = new ProgressDialog(PollInfoActivity.this);
        progressDialog.setTitle("Loading");

        selectedCandidates = new ArrayList<BVCandidate>();
        Intent PollIntent = getIntent();
        poll_id = PollIntent.getStringExtra("id");
        poll = DataSourceController.getInstance().getPollWithId(poll_id);

        closeBtn = (Button)findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(PollInfoActivity.this);
                adb.setTitle("Close poll");
                adb.setMessage("Are you sure that you want to close this poll?");
                adb.setNegativeButton("No", null);
                adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.show();
                        DataSourceController.getInstance().closePoll(poll, new DataSourceCallBack() {
                            @Override
                            public void handleDatasourceCallBack(List objects, Boolean success) {
                                if (success) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToastWithText("Poll closed");
                                        }
                                    });
                                    checkPollStatus();
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToastWithText("Error closing poll");
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                adb.show();
            }
        });

        if (!poll.getCreatedBy().getObjectId().equals(BVUser.getMyUser().getObjectId())) {
            closeBtn.setEnabled(false);
        }

        sendBtn = (Button)findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SparseBooleanArray selecteds = listView.getCheckedItemPositions();

                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                    if (selecteds.get(i)) {
                        // Do something
                        BVCandidate c = (BVCandidate)listView.getAdapter().getItem(i);
                        c.setSelected(true);
                        selectedCandidates.add(c);
                    }
                }

                if (selectedCandidates.isEmpty()) {
                    showToastWithText("Please select at least one candidate");
                } else {
                    AlertDialog.Builder adb = new AlertDialog.Builder(PollInfoActivity.this);
                    adb.setTitle("Send vote");
                    adb.setMessage("Are you sure of your election?");
                    adb.setNegativeButton("No", null);
                    adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.show();
                            DataSourceController.getInstance().checkCanVote(poll, new DataSourceCallBack() {
                                @Override
                                public void handleDatasourceCallBack(List objects, Boolean success) {
                                    if (success) {
                                        //You can vote
                                        DataSourceController.getInstance().sendPoll(poll, selectedCandidates, new DataSourceCallBack() {
                                            @Override
                                            public void handleDatasourceCallBack(List objects, Boolean success) {
                                                if (success) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            showToastWithText("Vote sent successfully");
                                                        }
                                                    });

                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            showToastWithText("Error sending vote");
                                                        }
                                                    });
                                                }
                                                progressDialog.dismiss();
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showToastWithText("Sorry, you already voted");
                                            }
                                        });
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    adb.show();

                }
            }
        });

        resultsBtn = (Button)findViewById(R.id.resultsBtn);
        resultsBtn.setEnabled(false);
        resultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(PollInfoActivity.this, PollResultsActivity.class);
                myIntent.putExtra("id", poll.getObjectId());
                PollInfoActivity.this.startActivity(myIntent);
            }
        });


        TextView tvInfo = (TextView)findViewById(R.id.textViewInfo);
        TextView startDate = (TextView)findViewById(R.id.textViewStartDate);
        TextView endDate = (TextView)findViewById(R.id.textViewEndDate);
        TextView multiple = (TextView) findViewById(R.id.textViewMS);
        TextView secret = (TextView) findViewById(R.id.textViewSecret);

        java.text.DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(this.getBaseContext());
        String startTimeString = dateFormat.format(poll.getStartDate());
        String endTimeString = dateFormat.format(poll.getEndDate());
        dateFormat = android.text.format.DateFormat.getDateFormat(this.getBaseContext());
        String startDateString = dateFormat.format(poll.getStartDate());
        String endDateString = dateFormat.format(poll.getEndDate());

        setTitle(poll.getName());
        startDate.setText(startDateString + " - " + startTimeString);
        endDate.setText(endDateString + " - " + endTimeString);
        tvInfo.setText(poll.getInformation());

        //Configure ListView of Candidates
        listView = (ListView) findViewById(R.id.pollCandidateListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final PollCandidatesAdapter adapter = new PollCandidatesAdapter(this, poll);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,int position,long id) {
                View v = listView.getChildAt(position);
                CheckedTextView ctv = (CheckedTextView)view;
                toggle(ctv);
                adapter.notifyDataSetChanged();
            }
        });


        if (poll.getMultipleSelection()){
            multiple.setText("Multiple Selection");
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
        else {
            multiple.setText("Single Selection");
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        if (poll.getSecret()){
            secret.setText("Secret Poll");
        }
        else{
            secret.setText("Public Poll");
        }

        checkPollStatus();
    }

    public void checkPollStatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });

        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                DataSourceController.getInstance().refreshPoll(poll, new DataSourceCallBack() {
                    @Override
                    public void handleDatasourceCallBack(List objects, final Boolean success) {
                        if (success) {
                            if ((int) poll.getPhase() == 0) {
                                DataSourceController.getInstance().openPollIfNeeded(poll, new DataSourceCallBack() {
                                    @Override
                                    public void handleDatasourceCallBack(List objects, Boolean success) {

                                        progressDialog.dismiss();
                                        if (success) {
                                            progressDialog.dismiss();
                                            checkPollStatus();
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showToastWithText("The poll is still not open");
                                                    sendBtn.setEnabled(false);
                                                }
                                            });
                                        }
                                    }
                                });
                            } else if ((int) poll.getPhase() == 1) {
                                DataSourceController.getInstance().closePollIfNeeded(poll, new DataSourceCallBack() {
                                    @Override
                                    public void handleDatasourceCallBack(List objects, Boolean success) {
                                        progressDialog.dismiss();
                                        if (success) {
                                            checkPollStatus();
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if ((int) BVUser.getMyUser().getMembership() >= (int) poll.getMembership()) {
                                                        sendBtn.setEnabled(true);
                                                    } else {
                                                        sendBtn.setEnabled(false);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToastWithText("Poll finished");
                                        sendBtn.setEnabled(false);
                                        closeBtn.setEnabled(false);
                                        resultsBtn.setEnabled(true);
                                    }
                                });
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToastWithText("An error ocurred");
                                }
                            });
                        }
                        Looper.loop();
                    }
                });
            }
        }).start();


    }


    public void toggle(CheckedTextView v)
    {
        if (v.isChecked())
        {
            v.setChecked(false);
        }
        else
        {
            v.setChecked(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
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
        return true;
    }
}
