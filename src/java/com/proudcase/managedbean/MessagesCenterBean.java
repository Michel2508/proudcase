package com.proudcase.managedbean;

import com.google.code.morphia.Key;
import com.proudcase.constants.Constants;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.FriendInvitationManager;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.MessagesManager;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.FriendInvitationBean;
import com.proudcase.persistence.MessagesBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.view.MessagesCenterViewBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.bson.types.ObjectId;

/**
 * Copyright © 03.07.2013 Michel Vocks This file is part of proudcase.
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
 * @Date: 03.07.2013
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@SessionScoped
public class MessagesCenterBean {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    transient private MessagesManager messagesManager =
            ManagerFactory.createMessagesManager();
    transient private FriendInvitationManager friendInvitationManager =
            ManagerFactory.createFriendInvitationManager();
    transient private UserManager userManager =
            ManagerFactory.createUserManager();
    private List<MessagesCenterViewBean> messagesList =
            new ArrayList<>();
    private int newMessages = 0;
    private UserBean receiver;
    private MessagesBean newMessage =
            new MessagesBean();

    public void init() {
        // Get the informations from the user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // wrong user? leave
        if (loggedUser == null || loggedUser.getId() == null) {
            return;
        }

        // retrieve all messages
        List<MessagesBean> messagesListDB = messagesManager.getAllMessagesForUser(loggedUser);

        // retrieve all invitations
        List<FriendInvitationBean> friendInvitationsList = friendInvitationManager.getAllFriendInvitationsForUser(loggedUser);

        // empty lists
        if (messagesListDB.isEmpty() && friendInvitationsList.isEmpty()) {
            return;
        }

        // iterate through all messages
        for (MessagesBean singleMessage : messagesListDB) {
            // check for update
            boolean skipThisMessage = false;
            for (MessagesCenterViewBean singleViewMessage : messagesList) {
                if (singleViewMessage.getMessageid().equals(singleMessage.getId())) {
                    // check for already read
                    if (!singleViewMessage.isReached() && singleMessage.isReached()) {
                        singleViewMessage.setReached(true);

                        // decrease our new messages counter
                        if (newMessages > 0) {
                            newMessages--;
                        }
                    }

                    // skip this
                    skipThisMessage = true;
                    break;
                }
            }

            // we have to skip
            if (skipThisMessage) {
                continue;
            }

            // create a new view object for our list and fill it
            MessagesCenterViewBean tempView = new MessagesCenterViewBean();
            tempView.setMessageid(singleMessage.getId());
            tempView.setMessage(singleMessage.getMessage());
            tempView.setSenddate(singleMessage.getSenddate());
            tempView.setReached(singleMessage.isReached());
            tempView.setSendDateToString(getConvertedTime(singleMessage.getSenddate()));
            tempView.setInvitationMessage(false);

            // is this message a new one?
            if (!singleMessage.isReached()) {
                // add one to our counter
                newMessages++;
            }

            // set the avatarhash
            tempView.setAvatar(singleMessage.getSender().getAvatar());

            // now get the nickname for the user
            tempView.setNickname(singleMessage.getSender().toString());

            // finally, add that view obj to our list
            messagesList.add(tempView);
        }

        // iterate through all invitations
        for (FriendInvitationBean singleInvitation : friendInvitationsList) {
            // check for update
            boolean skipThisInvitation = false;
            for (MessagesCenterViewBean singleViewMessage : messagesList) {
                if (singleViewMessage.getMessageid().equals(singleInvitation.getId())) {
                    // skip this
                    skipThisInvitation = true;
                    break;
                }
            }

            // we have to skip
            if (skipThisInvitation) {
                continue;
            }

            // create a new view object for our list and fill it
            MessagesCenterViewBean tempView = new MessagesCenterViewBean();
            tempView.setMessageid(singleInvitation.getId());
            tempView.setMessage(singleInvitation.getInvitationText());
            tempView.setSenddate(singleInvitation.getInvitationDate());
            tempView.setReached(false);
            tempView.setSendDateToString(getConvertedTime(singleInvitation.getInvitationDate()));
            tempView.setInvitationMessage(true);

            // add one to our counter
            newMessages++;

            // set the avatarhash
            tempView.setAvatar(singleInvitation.getUserSendInvitation().getAvatar());

            // now get the nickname for the user
            tempView.setNickname(singleInvitation.getUserSendInvitation().toString());

            // finally, add that view obj to our list
            messagesList.add(tempView);
        }

        // sort our list (new messages up)
        Collections.sort(messagesList);
    }

    public boolean isResetReachedMessages() {
        // Get the informations from the user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // wrong user? leave
        if (loggedUser == null || loggedUser.getId() == null) {
            return false;
        }

        // reset our counter
        newMessages = 0;

        // retrieve all messages
        List<MessagesBean> messagesListDB = messagesManager.getAllMessagesForUser(loggedUser);

        // empty list
        if (messagesListDB == null || messagesListDB.isEmpty()) {
            return true;
        }

        // iterate through all messages
        for (MessagesBean singleMessage : messagesListDB) {
            // mark every message now as reached (read)
            setMessageAsReached(singleMessage);
        }
        return true;
    }

    public void sendMessage() {
        // Get the informations from the user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // wrong user? leave
        if (loggedUser == null || loggedUser.getId() == null) {
            return;
        }

        // no receiver was choosen
        String label;
        if (receiver == null) {
            label = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "noreceiverselected", null, sessionBean.getUserLocale());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, label, label);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        // set the sender
        newMessage.setSender(loggedUser);

        // set the receiver
        newMessage.setReceiver(receiver);

        // set the senddate
        newMessage.setSenddate(new Date());

        // insert the message in the db
        Key<MessagesBean> save = messagesManager.save(newMessage);

        // reset the old stuff
        newMessage = new MessagesBean();
        receiver = null;

        if (save != null && save.getId() != null) {
            // add the success message to the context
            label = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "msg_was_sended", null, sessionBean.getUserLocale());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, label, label);
            FacesContext.getCurrentInstance().addMessage(null, message);
        } else {
            // add error message
            label = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "error_db_connection", null, sessionBean.getUserLocale());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, label, label);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    // set this message as read
    private void setMessageAsReached(MessagesBean singleMessage) {
        // is this message already marked as reached?
        if (!singleMessage.isReached()) {
            // set as reached (read)
            singleMessage.setReached(true);

            // update the message
            boolean result = messagesManager.updateMessageReachedFlag(singleMessage);

            // db error
            if (!result) {
                // TODO: log that shit!
            }
        }
    }

    public void acceptInvitation(ObjectId invitationID) {
        // Get the informations from the user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // wrong user? leave
        if (loggedUser == null || loggedUser.getId() == null) {
            return;
        }

        // retrieve the invitation
        FriendInvitationBean invitation = friendInvitationManager.get(invitationID);

        // something wrong here
        if (invitation == null) {
            return;
        }

        // add the relations on both sides
        boolean firstSuccess = userManager.addFriend(loggedUser, invitation.getUserSendInvitation());
        boolean secondSuccess = userManager.addFriend(invitation.getUserSendInvitation(), loggedUser);

        // both true - nice!
        String label;
        if (firstSuccess && secondSuccess) {
            // add success message
            label = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "invitation_accept_done", null, sessionBean.getUserLocale());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, label, label);
            FacesContext.getCurrentInstance().addMessage(null, message);

            // also delete the invitation from the db
            friendInvitationManager.delete(invitation);

            // and from our message list
            MessagesCenterViewBean temp = null;
            for (MessagesCenterViewBean messagesCenterViewBean : messagesList) {
                if (invitation.getId().equals(messagesCenterViewBean.getMessageid())) {
                    temp = messagesCenterViewBean;
                    break;
                }
            }

            // delete it please :)
            if (temp != null) {
                messagesList.remove(temp);
            }
        } else {
            // add error message
            label = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "invitation_canceled_done", null, sessionBean.getUserLocale());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, label, label);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void canceledInvitation(ObjectId invitationID) {
        // just delete the invitation from our database
        friendInvitationManager.deleteById(invitationID);

        // delete also from our list
        MessagesCenterViewBean temp = null;
        for (MessagesCenterViewBean messagesCenterViewBean : messagesList) {
            if (invitationID.equals(messagesCenterViewBean.getMessageid())) {
                temp = messagesCenterViewBean;
                break;
            }
        }

        // delete it please :)
        if (temp != null) {
            messagesList.remove(temp);
        }

        // add a message
        FacesContext fCtx = FacesContext.getCurrentInstance();
        String label = PropertyReader.getMessageResourceString(
                fCtx.getApplication().getMessageBundle(), "invitation_canceled_done", null, sessionBean.getUserLocale());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, label, label);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void deleteMessage(ObjectId messageID) {
        // delete the message from our database
        messagesManager.deleteById(messageID);

        // delete also from our list
        MessagesCenterViewBean temp = null;
        for (MessagesCenterViewBean messagesCenterViewBean : messagesList) {
            if (messageID.equals(messagesCenterViewBean.getMessageid())) {
                temp = messagesCenterViewBean;
                break;
            }
        }

        // delete it please :)
        if (temp != null) {
            messagesList.remove(temp);
        }

        // add a message
        FacesContext fCtx = FacesContext.getCurrentInstance();
        String label = PropertyReader.getMessageResourceString(
                fCtx.getApplication().getMessageBundle(), "message_delete_done", null, sessionBean.getUserLocale());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, label, label);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public String messageStatusText(MessagesCenterViewBean message) {
        // not null
        if (message == null) {
            return "ERROR";
        }

        // get the text from the right status
        String statusText;
        FacesContext fCtx = FacesContext.getCurrentInstance();
        if (message.isReached()) {
            statusText = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "old_msg", null, sessionBean.getUserLocale());
        } else {
            statusText = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "new_msg", null, sessionBean.getUserLocale());
        }

        // return the answer
        return statusText;
    }

    private String getConvertedTime(Date time) {
        SimpleDateFormat newFormat = new SimpleDateFormat(
                "EEEE, d. MMMM yyyy H:m", sessionBean.getUserLocale());
        return newFormat.format(time);
    }

    public String returnNewMessagesCounted() {
        // call the init method to fetch our new messages
        init();

        // return the number of new messages
        return String.valueOf(newMessages);
    }

    public MessagesBean getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(MessagesBean newMessage) {
        this.newMessage = newMessage;
    }

    public List<MessagesCenterViewBean> getMessagesList() {
        return messagesList;
    }

    public void setMessagesList(List<MessagesCenterViewBean> messagesList) {
        this.messagesList = messagesList;
    }

    public UserBean getReceiver() {
        return receiver;
    }

    public void setReceiver(UserBean receiver) {
        this.receiver = receiver;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}
