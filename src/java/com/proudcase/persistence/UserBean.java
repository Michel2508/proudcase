package com.proudcase.persistence;

import com.google.code.morphia.annotations.*;
import com.proudcase.constants.EPrivileges;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
public class UserBean implements Serializable {

    @Id
    private ObjectId id;
    
    @Reference
    private ImageBean avatar;

    @Indexed
    private List<ObjectId> friendRelations;
    
    @Indexed
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private EPrivileges userPrivs;
    
    private ObjectId rememberLogin;
    private Locale preferredLanguage;
    private String displayname;
    private Date birthdate;
    private String homepageurl;
    private String icqnumber;
    private String aimname;
    private String msnname;
    private String skypename;
    private String jobname;
    private String interestedin;
    private String livingplace;
    private String companyname;
    private Date lastlogin;
    private long personalrating;
    private boolean locked;
    private ObjectId registrationcode;
    private boolean activated;
    
    public UserBean() {
        this.birthdate = new Date();
        this.lastlogin = new Date();
        this.preferredLanguage = Locale.ENGLISH;
        this.locked = false;
        this.activated = false;
        this.registrationcode = new ObjectId();
        this.friendRelations = new ArrayList<>();
        userPrivs = EPrivileges.user;
    }
    
    @PrePersist
    void prePersist() {
        // update last login
        lastlogin = new Date();
    }

    @Override
    public String toString() {
        String nameToDisplay = null;
        
        // firstname and lastname available?
        if (firstname != null && firstname.length() > 0 && 
                lastname != null && lastname.length() > 0) {
            nameToDisplay = firstname + " " + lastname;
        } 
        
        // display name has second highest priority
        if (displayname != null && displayname.length() > 0 && nameToDisplay == null) {
            nameToDisplay = displayname;
        } 
        
        // company has last priority
        if (companyname != null && companyname.length() > 0 && nameToDisplay == null) {
            nameToDisplay = companyname;
        }
        
        // something wrong here :0
        if (nameToDisplay == null) {
            nameToDisplay = "Anonymus";
        }
        
        // return the name
        return nameToDisplay;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserBean other = (UserBean) obj;
        return Objects.equals(this.id, other.id);
    }
    
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ObjectId getRememberLogin() {
        return rememberLogin;
    }

    public void setRememberLogin(ObjectId rememberLogin) {
        this.rememberLogin = rememberLogin;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAimname() {
        return aimname;
    }

    public void setAimname(String aimname) {
        this.aimname = aimname;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getHomepageurl() {
        return homepageurl;
    }

    public void setHomepageurl(String homepageurl) {
        this.homepageurl = homepageurl;
    }

    public String getIcqnumber() {
        return icqnumber;
    }

    public void setIcqnumber(String icqnumber) {
        this.icqnumber = icqnumber;
    }

    public ImageBean getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageBean avatar) {
        this.avatar = avatar;
    }

    public String getInterestedin() {
        return interestedin;
    }

    public void setInterestedin(String interestedin) {
        this.interestedin = interestedin;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public String getLivingplace() {
        return livingplace;
    }

    public void setLivingplace(String livingplace) {
        this.livingplace = livingplace;
    }

    public String getMsnname() {
        return msnname;
    }

    public void setMsnname(String msnname) {
        this.msnname = msnname;
    }

    public String getSkypename() {
        return skypename;
    }

    public void setSkypename(String skypename) {
        this.skypename = skypename;
    }

    public Date getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(Date lastlogin) {
        this.lastlogin = lastlogin;
    }

    public List<ObjectId> getFriendRelations() {
        return friendRelations;
    }

    public void setFriendRelations(List<ObjectId> friendRelations) {
        this.friendRelations = friendRelations;
    }

    public Locale getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(Locale preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public long getPersonalrating() {
        return personalrating;
    }

    public void setPersonalrating(long personalrating) {
        this.personalrating = personalrating;
    }
    
    public void increasePersonalRating(int pointsToIncrease) {
        this.personalrating += pointsToIncrease;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public ObjectId getRegistrationcode() {
        return registrationcode;
    }

    public void setRegistrationcode(ObjectId registrationcode) {
        this.registrationcode = registrationcode;
    }

    public EPrivileges getUserPrivs() {
        return userPrivs;
    }

    public void setUserPrivs(EPrivileges userPrivs) {
        this.userPrivs = userPrivs;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
