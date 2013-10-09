package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.constants.ENavigation;
import com.proudcase.constants.EPrivileges;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.ImageBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.security.PasswordEncryption;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
@ManagedBean
@SessionScoped
public class AuthenticationBean implements Serializable {

    // save the input from the fields
    private UserBean account = new UserBean();
    private boolean saveLogin = false;
    
    // DB
    transient private UserManager userManager =
            ManagerFactory.createUserManager();
    
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    
    private boolean showErrorDialog;
    private boolean showPermissionDeniedDialog;
    private static final String COOKIE_SEPARATOR = "#";
    private static final int COOKIE_MAXAGE = 31536000; // 365 days

    public AuthenticationBean() {
    }

    public void init() {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        
        // we have to show an error?
        if (showPermissionDeniedDialog) {
            // generate output
            String outputMessage = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "error_loginrequired", null, sessionBean.getUserLocale());

            // add message
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, outputMessage));
        }
    }

    public boolean isLoggedIn() {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);
        if (loggedUser == null) {
            return false;
        }

        return true;
    }

    public String login() throws ExceptionLogger {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        String outputMessage = "";
        boolean errorOccured = false;

        // validation before
        if (account.getUsername() == null || account.getPassword() == null) {
            errorOccured = true;
            outputMessage = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "error_loginfailed", null, sessionBean.getUserLocale());
        }

        // no error yet
        if (!errorOccured) {
            // get the user from the db
            UserBean retrievedUser = userManager.getUserByUsername(account.getUsername().toLowerCase());

            // couldn't find the user
            if (retrievedUser == null) {
                outputMessage = PropertyReader.getMessageResourceString(
                        fCtx.getApplication().getMessageBundle(), "error_usernotfound", null, sessionBean.getUserLocale());
                errorOccured = true;
            }

            // no error yet
            if (!errorOccured) {
                // encrypt password
                String encryptedPassword = PasswordEncryption.generateEncryptedString(account.getPassword());

                // both equal?
                if (!retrievedUser.getPassword().equals(encryptedPassword)) {
                    // no so error
                    errorOccured = true;

                    outputMessage = PropertyReader.getMessageResourceString(
                            fCtx.getApplication().getMessageBundle(), "error_loginfailed", null, sessionBean.getUserLocale());
                }

                // no error yet
                if (!errorOccured) {
                    // Password is correct. Check if this account is activated
                    if (retrievedUser.isLocked()) {
                        errorOccured = true;
                        outputMessage = PropertyReader.getMessageResourceString(
                                fCtx.getApplication().getMessageBundle(), "error_userislocked", null, sessionBean.getUserLocale());
                    }

                    // no error so log in
                    if (!errorOccured) {
                        // login the user
                        loginUser(retrievedUser, fCtx);

                        // empty the input from the user
                        account = new UserBean();
                    }
                }
            }
        }

        // error occured
        if (errorOccured) {
            // add the message
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, outputMessage, null));

            // show the error dialog
            showErrorDialog = true;
        }

        // stay on the current page
        return "";
    }

    private void loginUser(UserBean retrievedUser, FacesContext fCtx) {
        // put the user in our session map
        fCtx.getExternalContext().getSessionMap().put(Constants.AUTH_KEY, retrievedUser);

        // update last login
        userManager.updateUserLastLogin(retrievedUser, new Date());

        // check for save login
        checkSaveLogin(retrievedUser);

        // set the language to the preferred language
        sessionBean.setUserLocale(retrievedUser.getPreferredLanguage());
    }

    private void checkSaveLogin(UserBean user) {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        // the user wanted to save the login
        if (saveLogin) {
            // generate a new identification key
            user.setRememberLogin(new ObjectId());

            // update the user in the db
            userManager.save(user);

            // has the user a cookie?
            Cookie proudcaseCookie = (Cookie) fCtx.getExternalContext().getRequestCookieMap().get(Constants.SAVELOGIN_COOKIE_NAME);

            // set the value of the cookie
            String valueOfCookie = user.getUsername() + COOKIE_SEPARATOR
                    + user.getRememberLogin();

            // found one?
            if (proudcaseCookie != null) {
                // set the new value
                proudcaseCookie.setValue(valueOfCookie);
            } else {
                // create a new cookie
                proudcaseCookie = new Cookie(Constants.SAVELOGIN_COOKIE_NAME, valueOfCookie);
            }

            // set the max age
            proudcaseCookie.setMaxAge(COOKIE_MAXAGE);

            // get the response
            HttpServletResponse response = (HttpServletResponse) fCtx.getExternalContext().getResponse();

            // add the cookie
            response.addCookie(proudcaseCookie);
        }
    }

    public void proudcaseCookieCheck(PhaseEvent event) {
        // user is not logged in
        if (!isLoggedIn()) {
            // try to get the cookie
            Cookie proudcaseCookie = (Cookie) event.getFacesContext().getExternalContext().getRequestCookieMap().get(Constants.SAVELOGIN_COOKIE_NAME);

            // we found something?
            if (proudcaseCookie != null) {
                // get the value
                String cookieValue = proudcaseCookie.getValue();

                // disconnect the username from the key
                String[] params = cookieValue.split(COOKIE_SEPARATOR);
                if (params.length < 2) {
                    return;
                }

                // get the username and key
                String userName = params[0];
                String loginKeyStr = params[1];

                // is this a valid key?
                ObjectId loginKey = null;
                if (!ObjectId.isValid(loginKeyStr)) {
                    return;
                }
                loginKey = new ObjectId(loginKeyStr);

                // get the user via the username
                UserBean user = userManager.getUserByUsername(userName);

                // found the user?
                if (user == null) {
                    return;
                }

                // let us check if this cookie is still valid
                if (user.getRememberLogin().equals(loginKey)) {
                    // valid cookie - login
                    loginUser(user, event.getFacesContext());
                }
            }
        }
    }

    public void hideErrorDialog() {
        // set the visibility of the dialog to false
        showErrorDialog = false;
    }
    
    public void hidePermissionDeniedDialog() {
        // set the visibility of the dialog to false
        showPermissionDeniedDialog = false;
    }

    public String logout() {
        FacesContext fCtx = FacesContext.getCurrentInstance();

        // get the cookie 
        Cookie proudcaseCookie = (Cookie) fCtx.getExternalContext().getRequestCookieMap().get(Constants.SAVELOGIN_COOKIE_NAME);

        // the user has a cookie
        if (proudcaseCookie != null) {
            // set the cookie's maxage to zero (delete instantly)
            proudcaseCookie.setMaxAge(0);

            // get the response
            HttpServletResponse response = (HttpServletResponse) fCtx.getExternalContext().getResponse();

            // add the cookie
            response.addCookie(proudcaseCookie);
        }

        // remove the user from the session map 
        // actually this makes no sense because the map will be destroyed 
        // when I set session.invalidate() but well, probably saver ;-)
        fCtx.getExternalContext().getSessionMap().remove(Constants.AUTH_KEY);

        // destroy the session
        HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
        session.invalidate();

        // navigate back to index page
        return ENavigation.INDEX.toString();
    }
    
    public boolean isJuryMember() {
        // get the user 
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);
        
        // is null or not a jury member
        if (loggedUser == null || !loggedUser.getUserPrivs().equals(EPrivileges.jury)) {
            return false;
        }
        
        // is a jury member
        return true;
    }
    
    public boolean isNormalUser() {
        // get the user 
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);
        
        // is null or a normal user
        if (loggedUser == null || loggedUser.getUserPrivs().equals(EPrivileges.user)) {
            return true;
        }
        
        // is not a user
        return false;
    }

    public void securityRedirect() throws ExceptionLogger {
        if (!isLoggedIn()) {
            try {
                FacesContext fCtx = FacesContext.getCurrentInstance();

                // show permission denied dialog
                showPermissionDeniedDialog = true;

                fCtx.getExternalContext().
                        redirect(ENavigation.CONTEXT.toString() + ENavigation.INDEX.toString());
            } catch (IOException ex1) {
                throw new ExceptionLogger(ex1, "Redirect failed!");
            }
        }
    }

    public String returnName() {
        // retrieve the current user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        if (loggedUser == null) {
            return "";
        }

        return loggedUser.toString();
    }

    public ImageBean getAvatar() {
        // retrieve the current user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        if (loggedUser == null) {
            return null;
        }

        return loggedUser.getAvatar();
    }

    public UserBean getAccount() {
        return account;
    }

    public void setAccount(UserBean account) {
        this.account = account;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public boolean isShowErrorDialog() {
        return showErrorDialog;
    }

    public boolean isSaveLogin() {
        return saveLogin;
    }

    public void setSaveLogin(boolean saveLogin) {
        this.saveLogin = saveLogin;
    }

    public boolean isShowPermissionDeniedDialog() {
        return showPermissionDeniedDialog;
    }

    public void setShowPermissionDeniedDialog(boolean showPermissionDeniedDialog) {
        this.showPermissionDeniedDialog = showPermissionDeniedDialog;
    }
}
