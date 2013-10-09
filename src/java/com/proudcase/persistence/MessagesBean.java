package com.proudcase.persistence;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import java.io.Serializable;
import java.util.Date;
import org.bson.types.ObjectId;

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
@Entity
public class MessagesBean implements Serializable {

    @Id
    private ObjectId id;
    
    @Reference
    private UserBean sender;
    
    @Reference
    @Indexed
    private UserBean receiver;
    
    private String message;
    private boolean reached;
    private Date senddate;

    public MessagesBean() {
    }

    public MessagesBean(ObjectId id, UserBean sender, UserBean receiver, String message, boolean reached, Date senddate) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.reached = reached;
        this.senddate = senddate;
    }

    public ObjectId getId() {
        return id;
    }

    public UserBean getReceiver() {
        return receiver;
    }

    public void setReceiver(UserBean receiver) {
        this.receiver = receiver;
    }

    public Date getSenddate() {
        return senddate;
    }

    public void setSenddate(Date senddate) {
        this.senddate = senddate;
    }

    public UserBean getSender() {
        return sender;
    }

    public void setSender(UserBean sender) {
        this.sender = sender;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isReached() {
        return reached;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }
}