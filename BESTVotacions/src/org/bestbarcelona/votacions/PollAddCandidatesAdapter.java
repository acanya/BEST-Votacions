package org.bestbarcelona.votacions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Laia on 01/08/2015.
 */
public class PollAddCandidatesAdapter extends ArrayAdapter<String> {
    public PollAddCandidatesAdapter(Context context, ArrayList<String> candidates) {
        super(context, 0, candidates);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String candidate = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poll_add_candidates_adapter, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        // Populate the data into the template view using the data object
        tvName.setText(candidate);
        // Return the completed view to render on screen
        return convertView;
    }

}

