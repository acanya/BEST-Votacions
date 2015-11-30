package org.bestbarcelona.votacions.datasource;

import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Pau on 16/5/15.
 */
@ParseClassName("Poll")
public class BVPoll extends ParseObject {

    public BVPoll() {

    }

    public String getName() {
        return getString("name");
    }
    public void setName(String value) {
        put("name", value);
    }
    public String getInformation() {
        return getString("information");
    }
    public void setInformation(String value) {
        put("information", value);
    }
    public Date getStartDate() {
        return getDate("startDate");
    }
    public void setStartDate(Date value) {
        put("startDate", value);
    }
    public Date getEndDate() {
        return getDate("endDate");
    }
    public void setEndDate(Date value) {
        put("endDate", value);
    }
    public Number getMembership() {
        return getNumber("membership");
    }
    public void setMembership(Number value) {
        put("membership", value);
    }
    public Boolean getSecret() {
        return getBoolean("secret");
    }
    public void setSecret(Boolean value) {
        put("secret", value);
    }
    public Boolean getMultipleSelection() {
        return getBoolean("multipleSelection");
    }
    public void setMultipleSelection(Boolean value) {
        put("multipleSelection", value);
    }
    public Number getPhase() {
        return getNumber("phase");
    }
    public void setPhase(Number value) {
        put("phase", value);
    }
    public BVUser getCreatedBy() {
        return (BVUser) getParseUser("createdBy");
    }
    public void setCreatedBy(BVUser value) {
        put("createdBy", value);
    }
    public ParseRelation getCandidates() {
        return getRelation("candidates");
    }
    public void setCandidates(ParseRelation value) {
        put("candidates", value);
    }
    public ParseRelation getVotesHistory() {
        return getRelation("votes_history");
    }
    public void setVotesHistory(ParseRelation value) {
        put("votes_history", value);
    }



}
