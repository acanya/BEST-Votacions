package org.bestbarcelona.votacions.datasource;

import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * Created by Pau on 16/5/15.
 */
@ParseClassName("_User")
public class BVUser extends ParseUser {

    public BVUser() {

    }

    public static BVUser getMyUser() {
        return (BVUser) ParseUser.getCurrentUser();
    }

    public Boolean getCanCreate() {
        return getBoolean("canCreate");
    }

    public Number getMembership() {
        if (getNumber("membership") == null) {
            return 0;
        }
        return getNumber("membership");
    }

    public String getFullName() {
        return getString("name");
    }

}