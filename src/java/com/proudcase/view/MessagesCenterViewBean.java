package com.proudcase.view;

import com.proudcase.persistence.ImageBean;
import java.io.Serializable;
import java.util.Date;
import org.bson.types.ObjectId;

/**
 *
 * @author Michel Vocks (michelvocks@gmail.com)
 * @since 14.12.2011
 */
public class MessagesCenterViewBean implements Serializable, Comparable<MessagesCenterViewBean> {

    private ObjectId messageid;
    private String nickname;
    private ImageBean avatar;
    private String message;
    private Date senddate;
    private String senddateString;
    private boolean reached;
    private boolean invitationMessage;

    public MessagesCenterViewBean() {
    }

    public MessagesCenterViewBean(ObjectId messageid, String nickname, ImageBean avatar, String message, Date senddate, String senddateString, boolean reached, boolean invitationMessage) {
        this.messageid = messageid;
        this.nickname = nickname;
        this.avatar = avatar;
        this.message = message;
        this.senddate = senddate;
        this.senddateString = senddateString;
        this.reached = reached;
        this.invitationMessage = invitationMessage;
    }

    public ImageBean getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageBean avatar) {
        this.avatar = avatar;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ObjectId getMessageid() {
        return messageid;
    }

    public void setMessageid(ObjectId messageid) {
        this.messageid = messageid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getSenddate() {
        return senddate;
    }

    public void setSenddate(Date senddate) {
        this.senddate = senddate;
    }

    public boolean isReached() {
        return reached;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }
    
    public String getSendDateToString() {
        return senddateString;
    }
    
    public void setSendDateToString(String sendDateToString) {
        this.senddateString = sendDateToString;
    }

    public boolean isInvitationMessage() {
        return invitationMessage;
    }

    public void setInvitationMessage(boolean invitationMessage) {
        this.invitationMessage = invitationMessage;
    }
    
    @Override
    public int compareTo(MessagesCenterViewBean o) {
        if (o == null) {
            return 0;
        }
        if (this.reached && !o.reached) {
            return -1;
        }
        if (!this.reached && o.reached) {
            return 1;
        }
        if (this.senddate.before(o.senddate)) {
            return -1;
        }
        if (this.senddate.after(o.senddate)) {
            return 1;
        }
        return 0;
    }
}
