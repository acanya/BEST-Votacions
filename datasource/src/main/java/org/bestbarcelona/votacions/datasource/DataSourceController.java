package org.bestbarcelona.votacions.datasource;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pau on 16/5/15.
 */
public class DataSourceController {
    private static DataSourceController instance = null;

    protected DataSourceController() {
        // Exists only to defeat instantiation.
    }
    public static DataSourceController getInstance() {
        if (instance == null) {
            instance = new DataSourceController();
        }
        return instance;
    }


    public void createPoll(final String name, final String information, final Date startDate, final Date endDate,
                           final Number membership, final Boolean secret, final Boolean multipleSelection,
                           final ArrayList<String> candidates, final DataSourceCallBack callback){
        new Thread(new Runnable() {
            public void run() {
                BVPoll poll = new BVPoll();

                updatePoll(poll, name, information, startDate, endDate, membership, secret, multipleSelection, candidates, callback);

            }
        }).start();
    }

    public void deletePoll(final BVPoll poll, final DataSourceCallBack callback) {
        new Thread(new Runnable() {
            public void run() {

                //Delete the poll candidates synchronously
                ParseRelation candidatesRelation = poll.getCandidates();
                ParseQuery candidatesQuery = candidatesRelation.getQuery();
                List<BVCandidate> candidates = null;
                try {
                    candidates = candidatesQuery.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for (BVCandidate c :
                        candidates) {
                    try {
                        c.delete();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                //Delete the poll votes synchronously
                ParseRelation votesHistory = poll.getVotesHistory();
                ParseQuery votesQuery = votesHistory.getQuery();

                List<BVVote> votes = null;
                try {
                    votes = votesQuery.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for (BVVote v :
                        votes) {
                    try {
                        v.delete();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                //Delete the poll object

                try {
                    poll.delete();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Call completion
                callback.handleDatasourceCallBack(null, true);
            }
        }).start();
    }

    // FYI: Returns the query used in fetchPollsForUser().
    // (Useful for our ParseQueryAdapter implementation)
    public ParseQuery<BVPoll> getQueryFetchPollsFromUser(BVUser user) {
        ParseQuery<BVPoll> query = ParseQuery.getQuery(BVPoll.class);
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getDateFromServer());
        cal.add(Calendar.MONTH, -1);
        Date lastMonthDate = cal.getTime();
        query.whereGreaterThan("startDate", lastMonthDate);
        if (user.getMembership().intValue() == 0) {
            //If the user has no membership (user not from best) cannot see polls
            query.whereLessThanOrEqualTo("membership", user.getMembership().intValue());
        }
        return query;
    }

    public BVPoll getPollWithId(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Poll");
        //query.whereEqualTo("objectId", id);
        BVPoll poll = null;
        try {
            poll = (BVPoll)query.get(id);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return poll;
    }

    public void updatePoll(final BVPoll poll, final String name, final String information, final Date startDate, final Date endDate,
                           final Number membership, final Boolean secret, final Boolean multipleSelection,
                           final ArrayList<String> candidates, final DataSourceCallBack callback){
        new Thread(new Runnable() {
            public void run() {
                Boolean success = true;
                poll.setName(name);
                poll.setInformation(information);
                poll.setStartDate(startDate);
                poll.setEndDate(endDate);
                poll.setMembership(membership);
                poll.setSecret(secret);
                poll.setMultipleSelection(multipleSelection);
                poll.setPhase(0);
                poll.setCreatedBy(BVUser.getMyUser());

                ParseRelation candidatesRelation = poll.getCandidates();

                if (candidates != null) {
                    for (String name :
                            candidates) {
                        BVCandidate candidate = new BVCandidate();
                        candidate.setName(name);
                        candidatesRelation.add(candidate);
                        try {
                            candidate.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                            success = false;
                        }
                    }
                }


                try {
                    poll.save();
                } catch (ParseException e) {
                    success = false;
                    e.printStackTrace();
                }
                ArrayList list = new ArrayList();
                list.add(poll);
                //Call completion
                callback.handleDatasourceCallBack(list, success);
            }
        }).start();
    }


    public ParseQuery<BVCandidate> getQueryFetchCandidatesFromPoll(BVPoll poll) {
        ParseRelation<BVCandidate> candidatesRelation = poll.getCandidates();
        ParseQuery<BVCandidate> candidatesQuery = candidatesRelation.getQuery();
        return candidatesQuery;
    }

    public ParseQuery<BVVote> getQueryFetchVotesFromPoll(BVPoll poll) {
        ParseRelation<BVVote> votesRelation = poll.getVotesHistory();
        ParseQuery<BVVote> votesQuery = votesRelation.getQuery();
        return votesQuery;
    }


    public void checkCanVote(final BVPoll poll, final DataSourceCallBack callback) {
        new Thread(new Runnable() {
            public void run() {
                ParseQuery votesQuery = getQueryFetchVotesFromPoll(poll);
                List<BVVote> votes = null;
                Boolean canVote = true;
                try {
                    votes = votesQuery.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for (BVVote v :
                        votes) {
                    if (v.getVoterName().equals(BVUser.getMyUser().getFullName())) {
                        canVote = false;
                    }
                }

                //Call completion
                callback.handleDatasourceCallBack(null, canVote);
            }
        }).start();
    }

    public void sendPoll(final BVPoll poll, final List<BVCandidate> candidates, final DataSourceCallBack callback) {
        new Thread(new Runnable() {
            public void run() {
                Boolean someoneSelected = false;
                ParseRelation<BVVote> votesRelation = poll.getVotesHistory();

                for (BVCandidate c :
                        candidates) {
                    if (c.getSelected()) {
                        c.setSelected(false);
                        someoneSelected = true;
                        BVVote vote = new BVVote();

                        if (poll.getSecret()) {
                            // Encrypt candidate name
                            String password = PasswordsContainer.getPassword();
                            String message = c.getName();
                            String encryptedMsg = "";
                            try {
                                encryptedMsg = AESCrypt.encrypt(password, message);
                            }catch (GeneralSecurityException e){
                                //handle error
                                e.printStackTrace();
                            }
                            vote.setSecretCandidate(encryptedMsg);
                        } else {
                            vote.setCandidate(c);
                        }

                        vote.setVoterName(BVUser.getMyUser().getFullName());
                        votesRelation.add(vote);
                        try {
                            vote.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                            callback.handleDatasourceCallBack(null, false);
                            return;
                        }
                    }
                }

                try {
                    poll.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                    callback.handleDatasourceCallBack(null, false);
                    return;
                }

                if (someoneSelected) {
                    callback.handleDatasourceCallBack(null, true);
                } else {
                    callback.handleDatasourceCallBack(null, false);
                }
            }
        }).start();
    }

    public void refreshPoll(final BVPoll poll, final DataSourceCallBack callback) {
        try {
            poll.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
            //Shit happens
            callback.handleDatasourceCallBack(null, false);
        }

        //Call completion
        callback.handleDatasourceCallBack(null, true);
    }

    public void openPollIfNeeded(final BVPoll poll, final DataSourceCallBack callback) {
        if (poll.getStartDate().compareTo(getDateFromServer()) < 0) {
            poll.setPhase(1);
            try {
                poll.save();
            } catch (ParseException e) {
                e.printStackTrace();
                callback.handleDatasourceCallBack(null, false);
            }
            callback.handleDatasourceCallBack(null, true);
        } else {
            callback.handleDatasourceCallBack(null, false);
        }
    }

    public void closePollIfNeeded(final BVPoll poll, final DataSourceCallBack callback) {
        if (poll.getEndDate().compareTo(getDateFromServer()) < 0) {
            poll.setPhase(2);
            try {
                poll.save();
            } catch (ParseException e) {
                e.printStackTrace();
                callback.handleDatasourceCallBack(null, false);
            }
            callback.handleDatasourceCallBack(null, true);
        } else {
            callback.handleDatasourceCallBack(null, false);
        }
    }

    public void closePoll(final BVPoll poll, final DataSourceCallBack callback) {
        new Thread(new Runnable() {
            public void run() {
                    poll.setPhase(2);
                    try {
                        poll.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        callback.handleDatasourceCallBack(null, false);
                    }

                //Call completion
                callback.handleDatasourceCallBack(null, true);
            }
        }).start();
    }

    public void countVotes(final BVPoll poll, final DataSourceCallBackLong callback) {
        new Thread(new Runnable() {
            public void run() {
                List<BVCandidate> candidates = null;
                List<BVVote> votesHistory = null;
                try {
                    candidates = poll.getCandidates().getQuery().find();
                    votesHistory = poll.getVotesHistory().getQuery().find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for (BVCandidate c :
                        candidates) {
                    c.setTotalVotes(0);
                    for (BVVote v :
                            votesHistory) {
                        if (poll.getSecret()) {
                            // Decrypt name
                            String password = PasswordsContainer.getPassword();
                            String encryptedMsg = v.getSecretCandidate();
                            String messageAfterDecrypt = "";
                            try {
                                messageAfterDecrypt = AESCrypt.decrypt(password, encryptedMsg);
                            }catch (GeneralSecurityException e){
                                //handle error - could be due to incorrect password or tampered encryptedMsg
                                e.printStackTrace();
                            }

                            if (messageAfterDecrypt.equals(c.getName())) {
                                c.setTotalVotes(c.getTotalVotes().intValue()+1);
                            }
                        } else {
                            if (v.getCandidate().getObjectId().equals(c.getObjectId())) {
                                c.setTotalVotes(c.getTotalVotes().intValue()+1);
                            }
                        }
                    }
                    try {
                        c.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                ArrayList<BVVote> votesNotDuplicated = new ArrayList<BVVote>();
                if (poll.getMultipleSelection()) {
                    for (BVVote v :
                            votesHistory) {
                        if (votesNotDuplicated.size() == 0) {
                            votesNotDuplicated.add(v);
                        }
                        Boolean repeated = false;
                        for (BVVote vND :
                                votesNotDuplicated) {
                            if (vND.getVoterName().equals(v.getVoterName())) {
                                repeated = true;
                            }
                        }

                        if (!repeated) {
                            votesNotDuplicated.add(v);
                        }
                    }
                }

                if (poll.getMultipleSelection()) {
                    callback.handleDatasourceCallBack(candidates, votesNotDuplicated, votesHistory.size(), true);
                } else {
                    callback.handleDatasourceCallBack(candidates, votesHistory, votesHistory.size(), true);
                }
            }
        }).start();
    }
    
    public Date getDateFromServer() {
        Date serverDate = null;
        Map<String, String> map = new HashMap<String, String>();
        map.put("user", BVUser.getMyUser().getUsername());
        try {
            serverDate = ParseCloud.callFunction("getTime", map);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return serverDate;
    }
}
