package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.constants.EPrivileges;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.JuryFeedbackManager;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.*;
import com.proudcase.visibility.EVisibility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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
@ViewScoped
public class DisplayShowcaseBean implements Serializable {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    
    transient private ShowcaseManager showcaseManager =
            ManagerFactory.createShowcaseManager();
    transient private UserManager userManager =
            ManagerFactory.createUserManager();
    transient private JuryFeedbackManager juryFeedbackManager =
            ManagerFactory.createJuryFeedbackManager();
    
    private ShowcaseBean displayShowcase;
    private String showcaseID;
    private boolean hasPermission = true;
    
    private List<VideoLinkBean> videoLinkList;
    private List<ShowcaseTextBean> showcaseTextList;
    private List<ImageBean> imagesList;
    private List<JuryFeedbackBean> juryFeedbackList;

    private JuryFeedbackBean juryFeedback;
    private boolean showFeedbackDialog = false;
    private int showcaseRanking = 0;
    private int userRating = 0;
    private int numberOfRatings = 0;

    public void loadShowcase() {
        // parameter is shit
        if (showcaseID == null || showcaseID.equals("")) {
            return;
        }

        // load the showcase (with the given id)
        displayShowcase = showcaseManager.get(new ObjectId(showcaseID));

        // showcase found?
        if (displayShowcase == null) {
            return;
        }

        // Get the informations from the user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // small extra check for security
        if (displayShowcase.getVisibility() != EVisibility.all && loggedUser == null) {
            // the showcase is not public and the user is not logged in - no access
            hasPermission = false;
            return;
        }

        // Check if everyone can see this showcase && the user is not the owner
        if (displayShowcase.getVisibility() != EVisibility.all
                && !displayShowcase.getUserAccount().getId().equals(loggedUser.getId())) {
            // not logged in? no permission!
            if (loggedUser == null) {
                hasPermission = false;
            }

            // Check if the user is a friend
            if (hasPermission && !userManager.isFriend(displayShowcase.getUserAccount(), loggedUser)) {
                // friend of friend enabled?
                if (displayShowcase.getVisibility() == EVisibility.friendsfriends) {
                    // is a friend of a friend?
                    if (!userManager.isFriendOfFriend(displayShowcase.getUserAccount(), loggedUser)) {
                        // permission denied!
                        hasPermission = false;
                    }
                } else {
                    // user is not a friend and visibility is set to friends only. No Permission!
                    hasPermission = false;
                }
            }

            // check if only the owner can see this showcase
            if (hasPermission && displayShowcase.getVisibility() == EVisibility.onlyme) {
                hasPermission = false;
            }
        }

        // No permission? No need to load the data from db
        if (!hasPermission) {
            return;
        }

        // set the showcase text
        showcaseTextList = displayShowcase.getShowcaseTexts();
        if (showcaseTextList == null) {
            showcaseTextList = new ArrayList<>();
        }

        // VideoLink
        videoLinkList = displayShowcase.getVideoLinks();
        if (videoLinkList == null) {
            videoLinkList = new ArrayList<>();
        }

        // images
        imagesList = displayShowcase.getImageList();
        if (imagesList == null) {
            imagesList = new ArrayList<>();
        }

        // jury feedback
        juryFeedbackList = displayShowcase.getJuryFeedbackList();
        if (juryFeedbackList == null) {
            juryFeedbackList = new ArrayList<>();
        }

        // let us check if the user (who is watching this showcase) is a jury member
        if (loggedUser != null && loggedUser.getUserPrivs().equals(EPrivileges.jury)) {
            // check if the jury member already gave a feedback for this showcase
            for (JuryFeedbackBean juryFb : juryFeedbackList) {
                if (juryFb.getFeedbackOwner().getId().equals(loggedUser.getId())) {
                    // set the feedback 
                    juryFeedback = juryFb;
                }
            }

            // didn't give a feedback
            if (juryFeedback == null) {
                juryFeedback = new JuryFeedbackBean();
                juryFeedback.setFeedbackOwner(loggedUser);
                juryFeedback.setShowcaseID(displayShowcase.getId());
            }
        }

        // Let us sort the showcase text for the user prefered language
        sortShowcaseTextObj();

        // sort the images in the right order
        Collections.sort(imagesList);

        // calculate the average rating of this showcase
        calculateRating();
    }

    public String getShowcaseTitle() {
        // No permission?
        if (!hasPermission) {
            FacesContext fCtx = FacesContext.getCurrentInstance();
            return PropertyReader.getMessageResourceString(fCtx.getApplication()
                    .getMessageBundle(), "error_permission_denied", null, sessionBean
                    .getUserLocale());
        }

        // Get the specified text for the preferred language
        ShowcaseTextBean langShowcase = getSpecifiedText();

        // return the showcase title only if available
        if (langShowcase != null) {
            return langShowcase.getTitle();
        }
        return null;
    }

    public String getShowcaseText() {
        ShowcaseTextBean langShowcase = getSpecifiedText();

        // return the showcase text only if available
        if (langShowcase != null) {
            return langShowcase.getExplaintext();
        }
        return null;
    }

    // this method returns the ShowcaseText object
    // for the client language
    private ShowcaseTextBean getSpecifiedText() {
        // get the language
        Locale clientLanguage = sessionBean.getUserLocale();

        // invalid showcase
        if (showcaseID == null || showcaseTextList == null) {
            return null;
        }

        // now we have to check if this showcase has this language supported
        for (ShowcaseTextBean singleShowcaseText : showcaseTextList) {
            if (singleShowcaseText.getLang().equals(clientLanguage)) {
                // we found the right text. Return the object
                return singleShowcaseText;
            }
        }

        // if we are here then the default language isn't supported
        // so let us check if the owner of the showcase has english support
        for (ShowcaseTextBean singleShowcaseText : showcaseTextList) {
            if (singleShowcaseText.getLang().equals(Locale.ENGLISH)) {
                return singleShowcaseText;
            }
        }

        // Well. If we are here then english isn't supported nor the default
        // language from the client.
        if (!showcaseTextList.isEmpty()) {
            return showcaseTextList.get(0);
        }
        return null;
    }

    // here we sort the list of showcasetext objs
    // we need this because on display we want 
    // that the prefered language is on the first tab
    private void sortShowcaseTextObj() {
        ShowcaseTextBean preferedShowcaseText = getSpecifiedText();

        if (preferedShowcaseText != null) {
            for (int i = 0; i < showcaseTextList.size(); i++) {
                if (showcaseTextList.get(i).getLang().equals(preferedShowcaseText.getLang())) {
                    // okay we found the place where the prefered text stays
                    // now we just remove it and add it to the first place
                    showcaseTextList.remove(i);
                    showcaseTextList.add(0, preferedShowcaseText);

                    // out of the loop (function)
                    return;
                }
            }
        }
    }

    public void saveFeedback() {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        
        // persist the feedback
        juryFeedbackManager.save(juryFeedback);
        
        // is our feedback already in our feedbacklist?
        boolean isAlreadyInList = false;
        for (JuryFeedbackBean juryFb : juryFeedbackList) {
            if (juryFb.getId().equals(juryFeedback.getId())) {
                isAlreadyInList = true;
                break;
            }
        }
        
        // is not in list?
        if (!isAlreadyInList) {
            // add to the list
            showcaseManager.addFeedbackToShowcase(displayShowcase.getId(), juryFeedback);
            juryFeedbackList.add(juryFeedback);
        }
        
        // add the output message
        String output = PropertyReader.getMessageResourceString(fCtx.getApplication()
                    .getMessageBundle(), "feedbackadded", null, sessionBean
                    .getUserLocale());
        fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, output, null));
    }

    public void toggleFeedbackDialogOff() {
        showFeedbackDialog = false;
    }

    // image available?
    public boolean isAvailablePic() {
        return imagesList.size() > 0;
    }

    // video available?
    public boolean isAvailableVideo() {
        return videoLinkList.size() > 0;
    }

    // A video or/and pic available?
    public boolean isAvailableMedia() {
        return (isAvailablePic() == true || isAvailableVideo() == true);
    }

    // calculates the current rating for this showcase
    private void calculateRating() {
        List<ShowcaseRankingBean> rankingList = displayShowcase.getShowcaseRankings();

        // is it empty?
        if (rankingList == null) {
            setShowcaseRanking(0);
            return;
        }

        // iterate through the list
        double sumOfRatings = 0.0;
        for (ShowcaseRankingBean currentRanking : rankingList) {
            sumOfRatings += currentRanking.getRanking();
        }

        // save the number of ratings
        setNumberOfRatings(rankingList.size());

        // Now we have all ratings. Calculate the average
        int tempRating = (int) Math.ceil(sumOfRatings / getNumberOfRatings());
        setShowcaseRanking(tempRating);
    }

    // saves the rating from the user in the database
    public void saveRating() {
        // first we have to check if there are already a rating
        // so we need the account id
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // well not logged in? something terrible happens here
        if (loggedUser == null) {
            return;
        }

        // try to find an existing rating
        ShowcaseRankingBean showcaseRankingBean = showcaseManager
                .getShowcaseRankingByUser(displayShowcase.getId(), loggedUser);

        // found nothing, so create a new one
        if (showcaseRankingBean == null) {
            showcaseRankingBean = new ShowcaseRankingBean(loggedUser.getId(), userRating);
        } else {
            // update
            showcaseRankingBean.setRanking(userRating);

            // add the rating to our list
            displayShowcase.getShowcaseRankings().add(showcaseRankingBean);
        }

        // database insert / update
        showcaseManager.updateOrInsertRanking(displayShowcase.getId(),
                showcaseRankingBean);

        // increase the personal rating from the owner of the showcase
        UserBean showcaseOwner = userManager.get(displayShowcase.getUserAccount().getId());
        showcaseOwner.increasePersonalRating(userRating);
        userManager.save(showcaseOwner);

        // calculate again the ratings 
        calculateRating();
    }

    public ShowcaseBean getDisplayShowcase() {
        return displayShowcase;
    }

    public void setDisplayShowcase(ShowcaseBean displayShowcase) {
        this.displayShowcase = displayShowcase;
    }

    public String getShowcaseID() {
        return showcaseID;
    }

    public void setShowcaseID(String showcaseID) {
        this.showcaseID = showcaseID;
    }

    public int getShowcaseRanking() {
        return showcaseRanking;
    }

    public void setShowcaseRanking(int showcaseRanking) {
        this.showcaseRanking = showcaseRanking;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public List<VideoLinkBean> getVideoLinkList() {
        return videoLinkList;
    }

    public void setVideoLinkList(List<VideoLinkBean> videoLinkList) {
        this.videoLinkList = videoLinkList;
    }

    public List<ShowcaseTextBean> getShowcaseTextList() {
        return showcaseTextList;
    }

    public void setShowcaseTextList(List<ShowcaseTextBean> showcaseTextList) {
        this.showcaseTextList = showcaseTextList;
    }

    public boolean isShowcaseAvailable() {
        return (displayShowcase != null && displayShowcase.getId() != null);
    }

    public List<ImageBean> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<ImageBean> imagesList) {
        this.imagesList = imagesList;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public boolean isHasPermission() {
        return hasPermission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public List<JuryFeedbackBean> getJuryFeedbackList() {
        return juryFeedbackList;
    }

    public void setJuryFeedbackList(List<JuryFeedbackBean> juryFeedbackList) {
        this.juryFeedbackList = juryFeedbackList;
    }

    public JuryFeedbackBean getJuryFeedback() {
        return juryFeedback;
    }

    public void setJuryFeedback(JuryFeedbackBean juryFeedback) {
        this.juryFeedback = juryFeedback;
    }

    public boolean isShowFeedbackDialog() {
        return showFeedbackDialog;
    }

    public void setShowFeedbackDialog(boolean showFeedbackDialog) {
        this.showFeedbackDialog = showFeedbackDialog;
    }
}
