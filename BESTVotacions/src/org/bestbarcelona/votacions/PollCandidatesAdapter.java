package org.bestbarcelona.votacions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import org.bestbarcelona.votacions.datasource.BVCandidate;
import org.bestbarcelona.votacions.datasource.BVPoll;
import org.bestbarcelona.votacions.datasource.DataSourceController;

/**
 * Created by Pau on 10/8/15.
 */
public class PollCandidatesAdapter extends ParseQueryAdapter<BVCandidate> {
    private BVPoll poll;
    public PollCandidatesAdapter(Context context, final BVPoll poll) {
        super(context, new ParseQueryAdapter.QueryFactory<BVCandidate>() {
            public ParseQuery<BVCandidate> create() {
                // Here we can edit the query where we fetch the BVPoll items

                return DataSourceController.getInstance().getQueryFetchCandidatesFromPoll(poll);
            }
        });
        this.poll = poll;
    }

    @Override
    public View getItemView(BVCandidate object, View v, final ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.poll_candidates_adapter, parent, false);
        }

        super.getItemView(object, v, parent);

        final BVCandidate c = object;

        final CheckedTextView ctv = (CheckedTextView) v.findViewById(R.id.checkList);

        // Populate the data into the template view using the data object
        ctv.setText(object.getName());
        // Return the completed view to render on screen
        return v;
    }

}
