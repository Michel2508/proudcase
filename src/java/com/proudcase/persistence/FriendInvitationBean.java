package com.proudcase.persistence;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import java.io.Serializable;
import java.util.Date;
import org.bson.types.ObjectId;

/**
  * Copyright © 03.10.2012 Michel Vocks
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
 * @Date: 03.10.2012
 *
 * @Encoding: UTF-8
 */
@Entity
public class FriendInvitationBean implements Serializable{
    
    @Id
    private ObjectId id;
    
    @Reference
    UserBean userSendInvitation;
    
    @Reference
    @Indexed
    UserBean userGotInvitation;
    
    private String invitationText;
    private Date invitationDate;

    public FriendInvitationBean() {
        invitationDate = new Date();
    }

    public FriendInvitationBean(ObjectId id, UserBean userSendInvitation, UserBean userGotInvitation, String invitationText, Date invitationDate) {
        this.id = id;
        this.userSendInvitation = userSendInvitation;
        this.userGotInvitation = userGotInvitation;
        this.invitationText = invitationText;
        this.invitationDate = invitationDate;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Date getInvitationDate() {
        return invitationDate;
    }

    public void setInvitationDate(Date invitationDate) {
        this.invitationDate = invitationDate;
    }

    public String getInvitationText() {
        return invitationText;
    }

    public void setInvitationText(String invitationText) {
        this.invitationText = invitationText;
    }

    public UserBean getUserGotInvitation() {
        return userGotInvitation;
    }

    public void setUserGotInvitation(UserBean userGotInvitation) {
        this.userGotInvitation = userGotInvitation;
    }

    public UserBean getUserSendInvitation() {
        return userSendInvitation;
    }

    public void setUserSendInvitation(UserBean userSendInvitation) {
        this.userSendInvitation = userSendInvitation;
    }
}
