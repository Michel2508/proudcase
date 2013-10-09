package com.proudcase.managedbean;

import com.proudcase.constants.ENavigation;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mail.SendMail;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.UserBean;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.bson.types.ObjectId;

/**
 * Copyright © 21.08.2013 Michel Vocks This file is part of proudcase.
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
 * @Date: 21.08.2013
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@SessionScoped
public class ForgotPasswordBean implements Serializable {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    private String forgotPasswordUsername;
    private boolean showInformation;
    private boolean foundUser = false;
    private String passwordKey;
    private String newPassword;
    private String newPasswordAgain;
    private boolean accessToEditPassword = false;
    private boolean passwordsAreNotEqual;
    private UserBean changePasswordUser;
    transient private UserManager userManager =
            ManagerFactory.createUserManager();
    private static final String FORGOTPASSWORD_PARAM = "/forgotpassword.xhtml?pwkey=";

    public ForgotPasswordBean() {
    }

    public void init() {
        FacesContext fCtx = FacesContext.getCurrentInstance();

        // we got a key as parameter?
        if (passwordKey != null && !passwordKey.equals("")) {
            // check if the key is a valid objectid
            if (ObjectId.isValid(passwordKey)) {
                // convert the key
                ObjectId restoreKey = new ObjectId(passwordKey);

                // Get the user via the key
                changePasswordUser = userManager.getUserByRegistrationId(restoreKey);

                // found an user?
                if (changePasswordUser != null) {
                    // give him access to change the password
                    accessToEditPassword = true;
                    showInformation = false;

                    // just go out
                    return;
                }
            }
        }

        // the user typed a correct username
        if (foundUser) {
            // generate a faces message
            String message = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "restorepwmailwassend", null, sessionBean.getUserLocale());

            // add the message
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, message));
        } else {
            // user typed a wrong username
            // get the error message
            String message = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "error_usernotfound", null, sessionBean.getUserLocale());

            // add message
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, message));
        }

        // reset some stuff to be sure
        changePasswordUser = null;
        foundUser = false;
        showInformation = true;
    }

    public String resetPassword() throws ExceptionLogger {
        FacesContext fCtx = FacesContext.getCurrentInstance();

        // first of all, check if the user exists
        UserBean user = userManager.getUserByUsername(forgotPasswordUsername);

        // exists?
        if (user == null) {
            // save that we couldn't found the user
            foundUser = false;

            // send him to the forgotpassword view
            return ENavigation.FORGOTPASSWORD.toString();
        }

        // set a new registration key
        user.setRegistrationcode(new ObjectId());

        // save it in the database 
        userManager.save(user);

        // Get the domain 
        String domain = PropertyReader.getMessageResourceString(
                fCtx.getApplication().getMessageBundle(), "domain", null, sessionBean.getUserLocale());

        // generate the link for the activasion
        Object activateLink = domain + FORGOTPASSWORD_PARAM + user.getRegistrationcode().toString();

        // get the subject & text for the email
        String subject = PropertyReader.getMessageResourceString(
                fCtx.getApplication().getMessageBundle(), "forgotpassword_subject", null, sessionBean.getUserLocale());
        String text = PropertyReader.getMessageResourceString(
                fCtx.getApplication().getMessageBundle(), "forgotpassword_email_message", new Object[]{activateLink}, sessionBean.getUserLocale());

        // send email
        SendMail.sendMail(user.getUsername(), subject, text);

        // we found the user
        foundUser = true;

        // send him to the forgot password view
        return ENavigation.FORGOTPASSWORD.toString();
    }

    public void confirmNewPassword() throws ExceptionLogger {
        FacesContext fCtx = FacesContext.getCurrentInstance();

        // remove old stuff
        passwordsAreNotEqual = false;

        // user has access to change password?
        if (accessToEditPassword && changePasswordUser != null) {
            // compare both passwords
            if (!newPassword.equals(newPasswordAgain)) {
                // generate output
                String message = PropertyReader.getMessageResourceString(
                        fCtx.getApplication().getMessageBundle(), "error_pwnotequal", null, sessionBean.getUserLocale());

                // add
                fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, message));

                // remember that both are not equal 
                passwordsAreNotEqual = true;

                // out 
                return;
            }

            // set the new password
            changePasswordUser.setPassword(newPassword);
            
            // also remove the key. One key = one time password change
            changePasswordUser.setRegistrationcode(null);

            // store the new password (encrypted)
            userManager.saveUserWithPasswordEncr(changePasswordUser);

            // generate output
            String message = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "successnewpassword", null, sessionBean.getUserLocale());

            // add
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, message));
        }
    }

    public String toIndexOrStay() {
        // passwords are not equal so stay on the page
        if (passwordsAreNotEqual) {
            return null;
        } else {
            // back to index page
            return ENavigation.INDEX.toString();
        }
    }

    public String getForgotPasswordUsername() {
        return forgotPasswordUsername;
    }

    public void setForgotPasswordUsername(String forgotPasswordUsername) {
        this.forgotPasswordUsername = forgotPasswordUsername;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public boolean isShowInformation() {
        return showInformation;
    }

    public void setShowInformation(boolean showInformation) {
        this.showInformation = showInformation;
    }

    public String getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordAgain() {
        return newPasswordAgain;
    }

    public void setNewPasswordAgain(String newPasswordAgain) {
        this.newPasswordAgain = newPasswordAgain;
    }

    public boolean isAccessToEditPassword() {
        return accessToEditPassword;
    }

    public void setAccessToEditPassword(boolean accessToEditPassword) {
        this.accessToEditPassword = accessToEditPassword;
    }
}
