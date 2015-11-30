package org.bestbarcelona.votacions.datasource;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Pau on 16/5/15.
 */
@ParseClassName("Vote")
public class BVVote extends ParseObject {

    public BVVote() {

    }

    public String getVoterName() {
        return getString("voter_name");
    }
    public void setVoterName(String value) {
        put("voter_name", value);
    }
    public String getSecretCandidate() {
        return getString("secretCandidate");
    }
    public void setSecretCandidate(String value) {
        put("secretCandidate", value);
    }
    public BVCandidate getCandidate() {
        return (BVCandidate) getParseObject("candidate");
    }
    public void setCandidate(BVCandidate value) {
        put("candidate", value);
    }
}
