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

import java.util.ArrayList;

/**
 * Created by Laia on 11/08/2015.
 */
public class PollResultsAdapter extends ArrayAdapter<BVCandidate> {
    public PollResultsAdapter(Context context, ArrayList<BVCandidate> candidates) {
        super(context, 0, candidates);
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
        votesTV.setText(String.valueOf (candidate.getTotalVotes()));
        // Return the completed view to render on screen
        return convertView;
    }
}