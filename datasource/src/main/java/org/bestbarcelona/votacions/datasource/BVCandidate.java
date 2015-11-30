package org.bestbarcelona.votacions.datasource;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Pau on 16/5/15.
 */
@ParseClassName("Candidate")
public class BVCandidate extends ParseObject {

    public BVCandidate() {

    }

    public String getName() {
        return getString("name");
    }
    public void setName(String value) {
        put("name", value);
    }
    public Boolean getSelected() {
        return getBoolean("selected");
    }
    public void setSelected(Boolean value) {
        put("selected", value);
    }
    public Number getTotalVotes() {
        return getNumber("totalVotes");
    }
    public void setTotalVotes(Number value) {
        put("totalVotes", value);
    }
}