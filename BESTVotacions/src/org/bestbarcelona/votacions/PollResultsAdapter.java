package org.bestbarcelona.votacions;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import org.bestbarcelona.votacions.datasource.BVCandidate;
import org.bestbarcelona.votacions.datasource.BVPoll;
import org.bestbarcelona.votacions.datasource.DataSourceController;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Laia on 11/08/2015.
 */
public class PollResultsAdapter extends ArrayAdapter<BVCandidate> {
    boolean showNumber;
    Number votes;
    public PollResultsAdapter(Context context, ArrayList<BVCandidate> candidates,Number totalVotes,boolean number) {
        super(context, 0, candidates);
        votes=totalVotes;
        showNumber=number;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BVCandidate candidate = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poll_results_adapter, parent, false);
        }
        final TextView candTV = (TextView) convertView.findViewById(R.id.candidateName);
        final TextView votesTV = (TextView) convertView.findViewById(R.id.candidateVotes);
        candTV.setText(candidate.getName());

        double percentvotes = (candidate.getTotalVotes().intValue() / votes.doubleValue())*100;

        DecimalFormat myFormat = new DecimalFormat("0.00");
        String votesTVtext = String.valueOf(candidate.getTotalVotes());
        String percentTVtext = myFormat.format(percentvotes)+"%";
        if (showNumber) {
            votesTV.setText(votesTVtext);
        }
        else {
            votesTV.setText(percentTVtext);

        }
        // Return the completed view to render on screen
        return convertView;
    }
}