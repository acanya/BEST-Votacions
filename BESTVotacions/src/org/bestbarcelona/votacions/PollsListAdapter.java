package org.bestbarcelona.votacions;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import org.bestbarcelona.votacions.datasource.BVPoll;
import org.bestbarcelona.votacions.datasource.BVUser;
import org.bestbarcelona.votacions.datasource.BVVote;
import org.bestbarcelona.votacions.datasource.DataSourceController;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Majandi <majandi@best.eu.org> on 20/05/2015.
 */
public class PollsListAdapter extends ParseQueryAdapter<BVPoll> {

    @Override
    public View getItemView(BVPoll poll, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_list_polls, null);
        }

        super.getItemView(poll, v, parent);

        TextView pollNameTextView = (TextView) v.findViewById(R.id.pollName);
        ImageView pollStatusIcon = (ImageView) v.findViewById(R.id.iconPollStatus);
        pollNameTextView.setText(poll.getName());
        //Date currentDate = DataSourceController.getInstance().getDateFromServer(); // TODO: NOT USED!
        // If the user has enough privileges to vote in this poll
        if ((int) BVUser.getMyUser().getMembership() >= (int) poll.getMembership()) {
            boolean canVote = true;
            ParseQuery votesHistoryQuery = poll.getVotesHistory().getQuery();
            try {
                List<BVVote> votersList = votesHistoryQuery.find();
                for (BVVote vote :
                        votersList) {
                    if (vote.getVoterName().equals(BVUser.getMyUser().getFullName())) {
                        canVote = false;
                        break;
                    }
                }
            } catch (ParseException e) {
                Log.d("BEST Votacions", "Parse exception: " + e);
                e.printStackTrace();
            }
            if (canVote) {
                if ((int) poll.getPhase() == 2) {
                    pollStatusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_cross));
                } else {
                    pollStatusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_blue));
                }
            }
            else {
                pollStatusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_green));
            }
        }
        else {
            pollStatusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_ban));
        }

        if ((int) poll.getPhase() == 1) {
            pollNameTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            pollNameTextView.setTypeface(Typeface.DEFAULT);
        }

        // Populate the appropriate TextView with the poll deadline
        TextView pollDeadlineTextView = (TextView) v.findViewById(R.id.pollDeadline);
        DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(getContext());
        String timeString = dateFormat.format(poll.getEndDate());
        dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        String dateString = dateFormat.format(poll.getEndDate());
        pollDeadlineTextView.setText("Deadline: " + dateString + " " + timeString);

        return v;
    }

    public PollsListAdapter(Context context, final BVUser user) {
        // We fetch the current server time from the function that we implemented
        // in the Parse Cloud service
        super(context, new ParseQueryAdapter.QueryFactory<BVPoll>() {
            public ParseQuery<BVPoll> create() {
                // Here we can edit the query where we fetch the BVPoll items
                return DataSourceController.getInstance().getQueryFetchPollsFromUser(user);
            }
        });

    }
}
