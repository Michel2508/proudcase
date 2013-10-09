package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.constants.ENavigation;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mail.SendMail;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.UserBean;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
@ViewScoped
public class RegistrationBean implements Serializable {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    private UserBean newUser =
            new UserBean();
    transient private UserManager userManager =
            ManagerFactory.createUserManager();
    private static final String REGISTERPARAM = "/register.xhtml?registerid=";
    private static final CharSequence ATCHAR = "@";
    private static final CharSequence DOT = ".";
    private String passwordAgain;
    private String registerId;
    private int redirectCount = 3;
    private boolean showDialog;

    public RegistrationBean() {
    }

    public void init() {
        // we got a param?
        if (registerId == null || !ObjectId.isValid(registerId)) {
            return;
        }

        // Convert the param to an object id
        ObjectId registerObjId = new ObjectId(registerId);

        // get the user from the db via registrationid
        UserBean user = userManager.getUserByRegistrationId(registerObjId);

        String outputMessage;
        FacesMessage fMessage;
        FacesContext fCtx = FacesContext.getCurrentInstance();

        // if null then something wrong here
        if (user == null) {
            outputMessage = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "error_activateaccount", null, sessionBean.getUserLocale());
            fMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, outputMessage);
        } else {
            // Success! Activate the account
            user.setActivated(true);

            // we log in the user automatically - so update the last login
            user.setLastlogin(new Date());

            // We don't need the registrationid again so just set it to null
            user.setRegistrationcode(null);

            // save it in the db
            userManager.save(user);

            // directly log in the user
            fCtx.getExternalContext().getSessionMap().put(Constants.AUTH_KEY, user);

            outputMessage = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "activate_success", null, sessionBean.getUserLocale());
            fMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, null, outputMessage);
        }

        // add the message
        fCtx.addMessage(null, fMessage);
    }

    public String register() throws ExceptionLogger {
        FacesContext fCtx = FacesContext.getCurrentInstance();

        // get the current user locale and save it
        newUser.setPreferredLanguage(sessionBean.getUserLocale());

        boolean somethingMissing = false;
        String locationMessage = "";
        showDialog = true;
        String errorMsg = PropertyReader.getMessageResourceString(
                fCtx.getApplication().getMessageBundle(), "registration_error", null, sessionBean.getUserLocale());

        // all fields are filled?
        if (newUser.getUsername() == null
                || !newUser.getUsername().contains(ATCHAR) || !newUser.getUsername().contains(DOT)) {
            somethingMissing = true;
            locationMessage = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "username_notspecified", null, sessionBean.getUserLocale());
        } else if (newUser.getDisplayname() == null || newUser.getDisplayname().equals("")) {
            somethingMissing = true;
            locationMessage = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "nickname_notspecified", null, sessionBean.getUserLocale());
        } else if (newUser.getPassword() == null || passwordAgain == null
                || !newUser.getPassword().equals(passwordAgain)) {
            somethingMissing = true;
            locationMessage = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "password_incorrect", null, sessionBean.getUserLocale());
        }

        // something is missing here, print that the user
        if (somethingMissing) {
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, locationMessage));
            return "";
        }
        
        // remove whitespaces from the username
        newUser.setUsername(newUser.getUsername().trim());
        
        // lower case username
        newUser.setUsername(newUser.getUsername().toLowerCase());

        // try to find the user, maybe he is already registered
        UserBean foundUser = userManager.getUserByUsername(newUser.getUsername());

        // User already exists
        if (foundUser != null) {
            locationMessage = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "username_inuse", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, locationMessage));
            return "";
        }
        
        // We like to save the firstname, lastname and the nickname in a specific format
        char[] formatBuilder;
        if (newUser.getFirstname() != null && newUser.getFirstname().length() > 0) {
            newUser.setFirstname(newUser.getFirstname().replaceAll("\\s+",""));
            formatBuilder = newUser.getFirstname().toLowerCase().toCharArray();
            formatBuilder[0] = Character.toUpperCase(formatBuilder[0]);
            newUser.setFirstname(new String(formatBuilder));
        }

        if (newUser.getLastname() != null && newUser.getLastname().length() > 0) {
            formatBuilder = newUser.getLastname().toLowerCase().toCharArray();
            formatBuilder[0] = Character.toUpperCase(formatBuilder[0]);
            newUser.setLastname(new String(formatBuilder));
        }

         // remove all spaces etc
        newUser.setDisplayname(newUser.getDisplayname().replaceAll("\\s+",""));
        
        formatBuilder = newUser.getDisplayname().toLowerCase().toCharArray();
        formatBuilder[0] = Character.toUpperCase(formatBuilder[0]);
        newUser.setDisplayname(new String(formatBuilder));
        
        // nickname already in use?
        foundUser = userManager.getUserByNickname(newUser.getDisplayname());
        
        // if we found a user with the same nickname, return a message
        if (foundUser != null) {
            locationMessage = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "nickname_inuse", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, locationMessage));
            return "";
        }

        // email is the username and we also like to save this email in lower case
        newUser.setUsername(newUser.getUsername().toLowerCase());

        // save the user in the database
        UserBean savedUser = userManager.saveUserWithPasswordEncr(newUser);
        
        // log in the user automaticly by adding the user object to the session map
        fCtx.getExternalContext().getSessionMap().put(Constants.AUTH_KEY, savedUser);
        
        // Generate and send an email to the user for verification
        // First get the subject
        String subject = PropertyReader.getMessageResourceString(
                fCtx.getApplication().getMessageBundle(), "registration_subject", null, sessionBean.getUserLocale());

        // Get the domain 
        String domain = PropertyReader.getMessageResourceString(
                fCtx.getApplication().getMessageBundle(), "domain", null, sessionBean.getUserLocale());

        // Add to the domain the destination, parameter and the value
        domain += REGISTERPARAM + newUser.getRegistrationcode().toString();

        // Convert this to our params array
        Object[] params = {
            domain
        };

        // Get the message for the email
        String outputMessage = PropertyReader.getMessageResourceString(
                fCtx.getApplication().getMessageBundle(), "registration_email_message", params, sessionBean.getUserLocale());

        // Finally, send the message to the user
        SendMail.sendMail(newUser.getUsername(), subject, outputMessage);

        // output for the user
        String register = PropertyReader.getMessageResourceString(
                fCtx.getApplication().getMessageBundle(), "registration_success", null, sessionBean.getUserLocale());
        outputMessage = PropertyReader.getMessageResourceString(
                fCtx.getApplication().getMessageBundle(), "check_email", null, sessionBean.getUserLocale());
        fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, register, outputMessage));

        // remove the old crap
        newUser = new UserBean();

        return "";
    }

    public void redirectCounter() throws ExceptionLogger {
        // we are at zero?
        if (redirectCount <= 0) {
            // redirect
            try {
                FacesContext fCtx = FacesContext.getCurrentInstance();
                fCtx.getExternalContext().
                        redirect(ENavigation.CONTEXT.toString() + ENavigation.INDEX.toString());
            } catch (IOException ex1) {
                throw new ExceptionLogger(ex1, "Redirect failed!");
            }
        } else {
            // decrement
            redirectCount--;
        }
    }

    public String getRedirectCountAsString() {
        return String.valueOf(redirectCount);
    }

    // Switch show dialog
    public void switchShowDialog() {
        showDialog = false;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public UserBean getNewUser() {
        return newUser;
    }

    public void setNewUser(UserBean newUser) {
        this.newUser = newUser;
    }

    public String getPasswordAgain() {
        return passwordAgain;
    }

    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }
}
