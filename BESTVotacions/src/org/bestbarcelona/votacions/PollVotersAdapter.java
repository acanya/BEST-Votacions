package org.bestbarcelona.votacions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import org.bestbarcelona.votacions.datasource.BVCandidate;
import org.bestbarcelona.votacions.datasource.BVPoll;
import org.bestbarcelona.votacions.datasource.BVVote;
import org.bestbarcelona.votacions.datasource.DataSourceController;

import java.util.ArrayList;

/**
 * Created by Laia on 11/08/2015.
 */


public class PollVotersAdapter extends ArrayAdapter<BVVote> {
    public PollVotersAdapter(Context context, ArrayList<BVVote> votes) {
        super(context, 0, votes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BVVote vote = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poll_add_candidates_adapter, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.tvName);
        name.setText(vote.getVoterName());
        // Return the completed view to render on screen
        return convertView;
    }
}
