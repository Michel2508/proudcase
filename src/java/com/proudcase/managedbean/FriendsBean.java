package com.proudcase.managedbean;

import com.google.code.morphia.Key;
import com.proudcase.constants.Constants;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.FriendInvitationManager;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.FriendInvitationBean;
import com.proudcase.persistence.UserBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
  * Copyright © 03.07.2013 Michel Vocks
  * This file is part of proudcase.

  * proudcase is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.

  * proudcase is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with proudcase.  If not, see <http://www.gnu.org/licenses/>.

/**
 * @Author: Michel Vocks
 *
 * @Date: 03.07.2013
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@ViewScoped
public class FriendsBean implements Serializable {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    private UserBean userToSearch;
    transient private UserManager userManager =
            ManagerFactory.createUserManager();
    transient private FriendInvitationManager friendInvitationManager =
            ManagerFactory.createFriendInvitationManager();
    private List<UserBean> friendsList =
            new ArrayList<>();
    private String searchCriteria;
    private String friendInvitationText;

    @PostConstruct
    public void init() {
        // retrieve the current logged user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // something wrong here
        if (loggedUser == null || loggedUser.getId() == null) {
            return;
        }

        // get all friends (but from the db)
        friendsList = userManager.getFriends(loggedUser);

        // no friends? empty list 
        if (friendsList == null) {
            friendsList = new ArrayList<>();
        }
    }

    public boolean checkCapableToBeFriend(UserBean friend) {
        // initial check
        if (friend == null) {
            return false;
        }

        // already a friend?
        // this is called on initital load, so we take the saved list
        // it should now be valid
        for (UserBean singleUser : friendsList) {
            if (singleUser.getId().equals(friend.getId())) {
                return false;
            }
        }

        // not a friend. So display the add friend button
        return true;
    }

    public void addAsFriend(UserBean friend) {
        // retrieve the current logged user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // initial check
        if (friend == null || loggedUser == null) {
            return;
        }

        // initialize private variables
        String outPutText;
        String outPutHeader;

        // already a friend? (extra check here)
        if (userManager.isFriend(loggedUser, friend)) {
            outPutText = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "friend_invitation_fail_text", null, sessionBean.getUserLocale());
            outPutHeader = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "friend_invitation_fail_head", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, outPutHeader, outPutText));

            return;
        }

        // ask, if there is already an invitation
        FriendInvitationBean friendInvitation = friendInvitationManager.getFriendInvitation(loggedUser, friend);

        if (friendInvitation != null && friendInvitation.getId() != null) {
            // throw up a message that already an invitation was send
            outPutText = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "invitation_already_send_text", null, sessionBean.getUserLocale());
            outPutHeader = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "invitation_already_send_head", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, outPutHeader, outPutText));

            // out
            return;
        }

        // maybe the friend has already send an invitation?
        friendInvitation = friendInvitationManager.getFriendInvitation(friend, loggedUser);

        if (friendInvitation != null && friendInvitation.getId() != null) {
            // throw up a message that your friend has already send an invitation
            outPutText = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "friend_invitation_send_text", null, sessionBean.getUserLocale());
            outPutHeader = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "friend_invitation_send_head", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, outPutHeader, outPutText));

            // out
            return;
        }

        // if we are here, set up an invitation
        FriendInvitationBean friendInvitationBean = new FriendInvitationBean();
        friendInvitationBean.setInvitationText(friendInvitationText);
        friendInvitationBean.setUserGotInvitation(friend);
        friendInvitationBean.setUserSendInvitation(loggedUser);

        // save in the database
        Key<FriendInvitationBean> save = friendInvitationManager.save(friendInvitationBean);

        // success?
        if (save != null && save.getId() != null) {
            outPutText = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "friend_invitation_success_text", null, sessionBean.getUserLocale());
            outPutHeader = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "friend_invitation_success_head", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, outPutHeader, outPutText));
        } else {
            // no success
            outPutText = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "friend_invitation_fail_text", null, sessionBean.getUserLocale());
            outPutHeader = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "friend_invitation_fail_head", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, outPutHeader, outPutText));
        }

    }

    public void removeFriend(UserBean friend) {
        // retrieve the current logged user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        if (friend == null || friend.getId() == null) {
            return;
        }
        if (loggedUser == null || loggedUser.getId() == null) {
            return;
        }

        // remove the friend from our list
        friendsList.remove(friend);

        // remove the relationship on both sides
        boolean firstSuccess = userManager.removeFriendRelationship(loggedUser, friend);
        boolean secondSuccess = userManager.removeFriendRelationship(friend, loggedUser);

        if (!firstSuccess && !secondSuccess) {
            String label = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "error_db_connection", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "", label));
        } else {
            String label = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "message_success_friend_removed", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "", label));
        }
    }

    public List<UserBean> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<UserBean> friendsList) {
        this.friendsList = friendsList;
    }

    public UserBean getUserToSearch() {
        return userToSearch;
    }

    public void setUserToSearch(UserBean userToSearch) {
        this.userToSearch = userToSearch;
    }

    public String getFriendInvitationText() {
        return friendInvitationText;
    }

    public void setFriendInvitationText(String friendInvitationText) {
        this.friendInvitationText = friendInvitationText;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(String searchCriteria) {
        this.searchCriteria = searchCriteria;
    }
}
