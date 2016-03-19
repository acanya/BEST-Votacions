package org.bestbarcelona.votacions;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.bestbarcelona.votacions.datasource.BVPoll;
import org.bestbarcelona.votacions.datasource.DataSourceCallBack;
import org.bestbarcelona.votacions.datasource.DataSourceController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;


public class CreatePollActivity extends ActionBarActivity implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener {
    public Button but1;
    public Button but3;
    public TextView tv1;
    public TextView tv2;
    public boolean isStart;
    public long startTime=0;
    public long endTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        but1 = (Button)findViewById(R.id.button1);
        tv1 = (TextView) findViewById(R.id.startTime);
        but3 = (Button)findViewById(R.id.button3);
        tv2 = (TextView) findViewById(R.id.endTime);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        but1.setText(date);
        but3.setText(date);

        Calendar cal = Calendar.getInstance();
        String time1 = String.valueOf(cal.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(cal.get(Calendar.MINUTE));
        tv1.setText (time1);
        String time2 = String.valueOf(cal.get(Calendar.HOUR_OF_DAY)+1)+":"+String.valueOf(cal.get(Calendar.MINUTE));
        tv2.setText (time2);
        startTime = System.currentTimeMillis();
        endTime = startTime + 3600000;  //(1 hour later)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_poll, menu);
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
            onClickNext(v);
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id ==R.id.action_copy){
            Context context = getApplicationContext();
            CharSequence textToast = "This action is not implemented yet";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, textToast, duration);
            toast.show();
        }


        return super.onOptionsItemSelected(item);
    }
    public void onClickNext (View v ){


        //Obtaining all elements from view
        EditText name = (EditText) findViewById(R.id.editName);
        EditText info = (EditText) findViewById(R.id.editInfo);


        boolean isSecret = ((CheckBox) findViewById(R.id.checkBoxSecret)).isChecked();
        boolean isMultiple = ((CheckBox) findViewById(R.id.checkBoxMultiple)).isChecked();
        //boolean notifyMembers = ((CheckBox) findViewById(R.id.checkBoxNotify)).isChecked();
        boolean allMembers =((Switch)findViewById(R.id.switch1)).isChecked();
        Number membership = 0;
        if (allMembers) {
            membership = 1;
        } else {
            membership = 2;
        }
        //checking if elements are correct and not empty
        if (endTime<=startTime ){
            Context context = getApplicationContext();
            CharSequence textToast = "One of the dates is incorrect";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, textToast, duration);
            toast.show();
        }
        else {
            if (name.getText().toString().length()==0 || info.getText().toString().length()==0){
                Context context = getApplicationContext();
                CharSequence textToast = "One of the fields is empty";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, textToast, duration);
                toast.show();
            }
            else {

                DataSourceController.getInstance().createPoll(name.getText().toString(), info.getText().toString(), new Date(startTime), new Date(endTime), membership, isSecret, isMultiple, null, new DataSourceCallBack() {
                    @Override
                    public void handleDatasourceCallBack(List objects, Boolean success) {
                        if (success) {
                            Log.v("poll created", "poll created successfully");
                            Intent newIntent = new Intent (CreatePollActivity.this, PollCandidatesActivity.class);
                            BVPoll poll = (BVPoll)objects.get(0);
                            newIntent.putExtra("id", poll.getObjectId());
                            CreatePollActivity.this.startActivity(newIntent);
                        }
                    }
                });
            }
        }
    }
    public void setStartDate(View v) {
        isStart=true;
        startTime = 0;
        TimePickerFragment tf = new TimePickerFragment();
        tf.show(getSupportFragmentManager(), "timePicker");
        DialogFragment df = new DatePickerFragment();
        df.show(getSupportFragmentManager(), "datePicker");
    }
    public void setEndDate(View v) {
        endTime=0;
        isStart=false;
        DialogFragment newFragment1 = new TimePickerFragment();
        newFragment1.show(getSupportFragmentManager(), "timePicker");
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public static class DatePickerFragment extends DialogFragment{

        private Activity mActivity;
        private DatePickerDialog.OnDateSetListener mListener;
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;

            // This error will remind you to implement an OnTimeSetListener
            //   in your Activity if you forget
            try {
                mListener = (DatePickerDialog.OnDateSetListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnTimeSetListener");
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(mActivity, mListener, year, month, day);
        }

    }

    public static class TimePickerFragment extends DialogFragment {
        private Activity mActivity;
        private TimePickerDialog.OnTimeSetListener mListener;
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;

            // This error will remind you to implement an OnTimeSetListener
            //   in your Activity if you forget
            try {
                mListener = (TimePickerDialog.OnTimeSetListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnTimeSetListener");
            }
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(mActivity, mListener, hour, minute,
                    DateFormat.is24HourFormat(mActivity));
        }
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Date d = new Date();

        d.setTime((hourOfDay - 1) * 60 * 60 * 1000 + minute * 60 * 1000);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(d);
        if (isStart){
            tv1.setText (date.substring(11));
            startTime = startTime + TimeUnit.MINUTES.toMillis((hourOfDay * 60) + minute);
            Log.v ("start",(String.valueOf(hourOfDay)+":"+String.valueOf(minute)));
        }
        else{
            tv2.setText (date.substring(11));
            endTime = endTime + TimeUnit.MINUTES.toMillis((hourOfDay * 60) + minute);

        }
    }
    @Override

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Date d  = new Date (year-1900,month,day);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(d);
        Log.v ("date",date);
        if (isStart) {
            but1.setText(date);
            startTime = startTime + d.getTime();
        }
        else {
            but3.setText(date);
            endTime=endTime+d.getTime();

        }
    }

}
