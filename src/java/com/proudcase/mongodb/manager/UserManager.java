package com.proudcase.mongodb.manager;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.proudcase.constants.Constants;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.persistence.UserBean;
import com.proudcase.security.PasswordEncryption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;

/**
 * Copyright © 24.09.2012 Michel Vocks This file is part of proudcase.
 *
 * proudcase is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * proudcase is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * proudcase. If not, see <http://www.gnu.org/licenses/>.
 *
 * /
 *
 **
 * @Author: Michel Vocks
 *
 * @Date: 24.09.2012
 *
 * @Encoding: UTF-8
 */
public class UserManager extends BasicDAO<UserBean, ObjectId> {

    public UserManager(Datastore datastore) {
        super(UserBean.class, datastore);
    }

    public UserBean saveUserWithPasswordEncr(UserBean user) throws ExceptionLogger {
        // we also save the password encrypted with md5
        user.setPassword(PasswordEncryption.generateEncryptedString(user.getPassword()));

        // just save the user
        Key<UserBean> save = save(user);

        // return the user
        if (save != null) {
            return ds.getByKey(UserBean.class, save);
        } else {
            return null;
        }
    }

    public UserBean getUserByUsername(String username) {
        // query
        Query<UserBean> query = ds.createQuery(UserBean.class).field("username").equal(username);

        // get the user
        UserBean singleUser = query.get();

        // return 
        return singleUser;
    }

    public UserBean getUserByNickname(String nickname) {
        // query
        Query<UserBean> query = ds.createQuery(UserBean.class).field("displayname")
                .equal(nickname);

        // return result
        return query.get();

    }

    public UserBean getUserByRegistrationId(ObjectId registerId) {
        // Query
        Query<UserBean> query = ds.createQuery(UserBean.class).
                field("registrationcode").equal(registerId);

        // return the user
        return query.get();
    }

    public UserBean updateImagePath(UserBean user) {
        // class type definition and set
        UpdateOperations<UserBean> ops = ds.createUpdateOperations(UserBean.class).set("avatar", user.getAvatar());

        // query
        Query<UserBean> query = ds.createQuery(UserBean.class).field(Mapper.ID_KEY).equal(user.getId());

        // update
        UpdateResults<UserBean> updateFirst = ds.updateFirst(query, ops);

        // return the user fresh from the database
        return ds.get(user);
    }

    /*
     * Here we load all the friends. 
     * The user just holds a list of keys (from all friends)
     * and with this list we retrieve the friends objects.
     */
    public List<UserBean> getFriends(UserBean user) {
        // get the actual friends list 
        List<ObjectId> friendList = get(user.getId()).getFriendRelations();

        // initial check if the user has friends
        if (friendList == null || friendList.isEmpty()) {
            return new ArrayList<>();
        }

        // friend relations contains keys (which morphia does not accept for id fields)
        List<ObjectId> friendIDs = new ArrayList<>();
        for (ObjectId userKey : friendList) {
            friendIDs.add(userKey);
        }

        // query
        Query<UserBean> query = ds.createQuery(UserBean.class)
                .field(Mapper.ID_KEY).in(friendIDs);

        // return the result
        return query.asList();
    }

    public List<UserBean> getSuggestionList(String searchQuery) {
        // remove the first and last space
        searchQuery = searchQuery.trim();

        // try to split with spaces as pattern
        String[] splittedSearchQuery = searchQuery.split(" ");

        // inititate the query
        Query<UserBean> query = ds.createQuery(UserBean.class);

        // iterate all query's
        for (int i = 0; i < splittedSearchQuery.length; i++) {
            searchQuery = splittedSearchQuery[i];
            
            // only the last argument gets the star expression
            if (i == (splittedSearchQuery.length + 1)) {
                // add the star operator (like search)
                searchQuery += ".*";
            }

            // create the pattern
            Pattern regex = Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE);

            // query for specific criteria
            query.or(
                    query.criteria("firstname").equal(regex),
                    query.criteria("lastname").equal(regex),
                    query.criteria("displayname").equal(regex),
                    query.criteria("companyname").equal(regex));
        }

        // max results
        query.limit(Constants.MAXSUGGESTIONS);

        // return the result
        return query.asList();
    }

    public boolean removeFriendRelationship(UserBean user, UserBean friendToRemove) {
        // query
        Query<UserBean> query = ds.createQuery(UserBean.class).field(Mapper.ID_KEY)
                .equal(user.getId());
        UpdateResults<UserBean> result;

        // create the update command 
        UpdateOperations<UserBean> ops = ds.createUpdateOperations(UserBean.class)
                .removeAll("friendRelations", friendToRemove.getId());

        // yes, do it please
        result = ds.updateFirst(query, ops);

        // let us check what happened
        if (result == null || result.getUpdatedCount() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isFriend(UserBean user, UserBean friend) {
        // query
        Query<UserBean> query = ds.createQuery(UserBean.class).field(Mapper.ID_KEY).equal(user.getId());

        // query if there a relation
        query.field("friendRelations").equal(friend.getId());

        // return the result
        return query.countAll() > 0;
    }

    public boolean isFriendOfFriend(UserBean user, UserBean friendOfFriend) {
        // Get all friends from the user 
        List<UserBean> userFriends = getFriends(user);

        // is empty so no!
        if (userFriends == null || userFriends.isEmpty()) {
            return false;
        }

        // Iterate through all friends
        for (UserBean singleFriend : userFriends) {
            // Check for every friend if the given user is a friend (friend of friend)
            if (isFriend(singleFriend, friendOfFriend)) {
                // we have a hit - return true
                return true;
            }
        }

        // if we are here then he is not a friend of friend!
        return false;
    }

    public boolean addFriend(UserBean user, UserBean friend) {
        // query
        Query<UserBean> query = ds.createQuery(UserBean.class).field(Mapper.ID_KEY).equal(user.getId());

        // update query
        UpdateOperations<UserBean> ops = ds.createUpdateOperations(UserBean.class).
                add("friendRelations", friend.getId());

        // execute the query
        UpdateResults<UserBean> update = ds.update(query, ops);

        return update.getUpdatedExisting();
    }

    public List<UserBean> getTopUsersByLimit(int limit) {
        // query
        Query<UserBean> query = ds.createQuery(UserBean.class)
                .order("-personalrating").limit(limit);

        // retrieve the users
        List<UserBean> topTenUsers = query.asList();

        // we got something?
        if (topTenUsers == null || topTenUsers.isEmpty()) {
            return null;
        }

        // return the list
        return topTenUsers;
    }

    public boolean updateUserLastLogin(UserBean user, Date lastlogin) {
        // Query
        Query<UserBean> query = ds.createQuery(UserBean.class).
                field(Mapper.ID_KEY).equal(user.getId());

        // Update operation
        UpdateOperations<UserBean> ops = ds.createUpdateOperations(UserBean.class)
                .set("lastlogin", lastlogin);

        // execute update
        UpdateResults<UserBean> update = ds.update(query, ops);

        return update.getUpdatedExisting();
    }

    public boolean updateUserLocale(UserBean user, Locale locale) {
        // Query
        Query<UserBean> query = ds.createQuery(UserBean.class)
                .field(Mapper.ID_KEY).equal(user.getId());

        // Update operation
        UpdateOperations<UserBean> ops = ds.createUpdateOperations(UserBean.class)
                .set("preferredLanguage", locale);

        // execute update
        UpdateResults<UserBean> update = ds.update(query, ops);

        return update.getUpdatedExisting();
    }
}
