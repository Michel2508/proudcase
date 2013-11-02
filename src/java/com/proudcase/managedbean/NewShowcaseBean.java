package com.proudcase.managedbean;

import com.proudcase.comparator.SelectItemComparator;
import com.proudcase.constants.Constants;
import com.proudcase.constants.ENavigation;
import com.proudcase.constants.EVideoTyp;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.FileManager;
import com.proudcase.mongodb.manager.ImageManager;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.mongodb.manager.VideoLinkManager;
import com.proudcase.persistence.*;
import com.proudcase.util.FileUtil;
import com.proudcase.util.ImageUtil;
import com.proudcase.util.VideoUtil;
import com.proudcase.util.YouTubeUtil;
import com.proudcase.visibility.EVisibility;
import java.io.Serializable;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.bson.types.ObjectId;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

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
public class NewShowcaseBean implements Serializable {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    private ShowcaseBean singleShowcase;
    private List<ShowcaseTextBean> manyShowcaseText
            = new ArrayList<>();

    private DualListModel<String> languages;
    private boolean sendMsg = true;
    private List<ImageBean> imageList
            = new ArrayList<>();
    private List<VideoLinkBean> videoLinks
            = new ArrayList<>();
    private List<SelectItem> categorieList
            = new ArrayList<>();
    private VideoLinkBean singleVideoLink
            = new VideoLinkBean();
    private List<FileBean> fileList
            = new ArrayList<>();

    private ImageBean deleteImageCache;
    private VideoLinkBean deleteVideoCache;
    private FileBean deleteFileCache;

    private final transient ShowcaseManager showcaseManager
            = ManagerFactory.createShowcaseManager();
    private final transient ImageManager imageManager
            = ManagerFactory.createImageManager();
    private final transient VideoLinkManager videoLinkManager
            = ManagerFactory.createVideoLinkManager();
    private final transient FileManager fileManager
            = ManagerFactory.createFileManager();

    private final Map<String, SupportedLanguagesBean> localeMap
            = new HashMap<>();

    private String categorieSelect;
    private String showcaseId;

    public NewShowcaseBean() {
    }

    @PostConstruct
    public void init() {
        // fill our select item list
        for (CategorieBean singleCategorie : applicationBean.getCategorieList()) {
            for (LangCategorieBean singleLangCategorie : singleCategorie.getLangCategorieList()) {
                // we only need the items in the language from the user
                if (singleLangCategorie.getLanguage().equals(sessionBean.getUserLocale())) {
                    categorieList.add(new SelectItem(singleCategorie.getId().toString(),
                            singleLangCategorie.getCategoriename()));
                }
            }
        }

        // Sort the categorielist in an aphabetic way
        Collections.sort(categorieList, new SelectItemComparator());

        List<String> sourceLangs = new ArrayList<>();
        List<String> langTarget = new ArrayList<>();
        String localeDisplayname;
        for (SupportedLanguagesBean singleLanguage : applicationBean.getSupportedLanguagesList()) {
            // get the display name
            localeDisplayname = sessionBean.localeAsString(singleLanguage.getLanguage());

            // fill the locale map
            localeMap.put(localeDisplayname, singleLanguage);

            // is this locale the same as from the user?
            if (singleLanguage.getLanguage().equals(sessionBean.getUserLocale())) {
                // set this directly as target language
                langTarget.add(localeDisplayname);
            } else {
                // fill the source array
                sourceLangs.add(localeDisplayname);
            }
        }

        languages = new DualListModel<>(sourceLangs, langTarget);
    }

    public void initShowcase() {
        // check if the showcaseid was set via get parameter
        if (showcaseId != null && ObjectId.isValid(showcaseId) && singleShowcase == null) {
            // convert the string to a valig objectid
            ObjectId showcaseObjId = new ObjectId(showcaseId);

            // retrieve the showcase from the database
            singleShowcase = showcaseManager.get(showcaseObjId);

            // we got something?
            if (singleShowcase == null) {
                singleShowcase = new ShowcaseBean();
            } else {
                // first we set the categorie
                if (singleShowcase.getCategorieid() != null) {
                    categorieSelect = singleShowcase.getCategorieid().toString();
                }

                // set the showcasetext if exists
                if (singleShowcase.getShowcaseTexts() != null && !singleShowcase.getShowcaseTexts().isEmpty()) {
                    manyShowcaseText = singleShowcase.getShowcaseTexts();

                    // iterate all showcasetext-objects
                    for (ShowcaseTextBean singleShowcasetext : manyShowcaseText) {
                        // iterate the source languages
                        for (String singleLanguage : languages.getSource()) {
                            SupportedLanguagesBean sourceLanguage = localeMap.get(singleLanguage);

                            if (sourceLanguage.getLanguage().equals(singleShowcasetext.getLang())) {
                                // got the language so remove it
                                languages.getSource().remove(singleLanguage);

                                // add it to the target array
                                languages.getTarget().add(singleLanguage);

                                // get out of this loop
                                break;
                            }
                        }
                    }
                }

                // set the images
                if (singleShowcase.getImageList() != null && !singleShowcase.getImageList().isEmpty()) {
                    imageList = singleShowcase.getImageList();

                    // sort the images
                    Collections.sort(imageList);
                }

                // videolinks
                if (singleShowcase.getVideoLinks() != null && !singleShowcase.getVideoLinks().isEmpty()) {
                    videoLinks = singleShowcase.getVideoLinks();
                }

                // files
                if (singleShowcase.getFileList() != null && !singleShowcase.getFileList().isEmpty()) {
                    fileList = singleShowcase.getFileList();
                }
            }
        } else if (singleShowcase == null) {
            singleShowcase = new ShowcaseBean();
        }
    }

    public String onFlowProcess(FlowEvent event) throws ExceptionLogger {
        if (event.getOldStep().equals("settings")) {
            // convert the selected categorie
            if (categorieSelect != null && ObjectId.isValid(categorieSelect)) {
                singleShowcase.setCategorieid(new ObjectId(categorieSelect));
            }

            // the user left the settings step so let us add the languages to an array
            boolean skipLanguage = false;
            for (String oneLang : languages.getTarget()) {
                // first get the locale from the string
                SupportedLanguagesBean targetLanguage = localeMap.get(oneLang);

                // nothing found? woot?
                if (targetLanguage == null) {
                    continue;
                }

                // iterate all our already created text objects
                for (ShowcaseTextBean oneShowcaseText : manyShowcaseText) {
                    // found the language already in the array - skip it
                    if (oneShowcaseText.getLang().equals(targetLanguage.getLanguage())) {
                        skipLanguage = true;
                    }
                }

                if (!skipLanguage) {
                    ShowcaseTextBean textShowcase = new ShowcaseTextBean(targetLanguage.getLanguage());
                    manyShowcaseText.add(textShowcase);
                }

                // reset the var
                skipLanguage = false;
            }
        }

        String label;
        FacesContext fCtx = FacesContext.getCurrentInstance();

        // add the info for addmedia
        switch (event.getNewStep()) {
            case "addmedia":
                label = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(), "mediainfo", null, sessionBean.getUserLocale());
                fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", label));
                break;
            case "sortmedia":
                label = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(), "sortinfo", null, sessionBean.getUserLocale());
                fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", label));
                break;
        }

        // we are at the last step
        if (event.getNewStep().equals("done")) {
            // some validation for sorting the images
            for (ImageBean singleImage : imageList) {
                if (singleImage.getOrderNumber() == null || singleImage.getOrderNumber() == 0) {
                    // One image has no id - error
                    label = PropertyReader.getMessageResourceString(fCtx.getApplication().
                            getMessageBundle(), "imagenotordered", null, sessionBean.getUserLocale());
                    fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", label));
                    return event.getOldStep();
                }

                // loop again through all images
                for (ImageBean againOneImage : imageList) {
                    // two images have the same id - error
                    if (againOneImage.getOrderNumber() == singleImage.getOrderNumber()
                            && !againOneImage.equals(singleImage)) {
                        label = PropertyReader.getMessageResourceString(fCtx.getApplication().
                                getMessageBundle(), "twoimagessameid", null, sessionBean.getUserLocale());
                        fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", label));
                        return event.getOldStep();
                    }
                }
            }

            label = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(), "showcasesave_success", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", label));

            // save the showcase in the database
            saveShowcase();
        }

        return event.getNewStep();
    }

    public void onTransfer(TransferEvent event) {
        // method called on language transfer
        // check if languages were removed
        if (event.isRemove()) {
            // iterate through all items
            for (Object languageObj : event.getItems()) {
                // cast the language to a string object
                if (languageObj instanceof String) {
                    String language = (String) languageObj;

                    // get the locale from our language map
                    SupportedLanguagesBean removedLanguage = localeMap.get(language);

                    // now iterate through all created showcasetext objects
                    for (ShowcaseTextBean singleShowcasetext : manyShowcaseText) {
                        // found the showcasetext
                        if (singleShowcasetext.getLang().equals(removedLanguage.getLanguage())) {
                            // remove from list
                            manyShowcaseText.remove(singleShowcasetext);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void saveShowcase() throws ExceptionLogger {
        // get the user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean currentUser = (UserBean) fCtx.getExternalContext().
                getSessionMap().get(Constants.AUTH_KEY);

        // only if we have here a real user
        if (currentUser == null || currentUser.getId() == null) {
            return;
        }

        // add the reference
        singleShowcase.setUserAccount(currentUser);

        // add to the showcase the current date
        singleShowcase.setCreatedate(new Date());

        // is the showcase restricted?
        boolean secure = false;
        if (!singleShowcase.getVisibility().equals(EVisibility.all)) {
            secure = true;
        }

        // move the images now to the userfolder
        for (ImageBean singleImage : imageList) {
            // probably the user pressed the edit button so let us check first 
            // if  the images are already in the right folder
            if (!ImageUtil.isAlreadyInRightDir(singleImage.getRelativeimagepath(), secure)) {
                // Move the image
                String newRelativeImagePath = ImageUtil.moveImageToUserDir(
                        singleImage.getRelativeimagepath(), currentUser.getId(), secure);

                // save the new relative path
                if (newRelativeImagePath != null) {
                    singleImage.setRelativeimagepath(newRelativeImagePath);
                } else {
                    // can't move it throw exception
                    throw new ExceptionLogger(new RuntimeException(),
                            "Can't move image:" + singleImage.getRelativeimagepath());
                }
            }

            // check if the visibility is still correct
            if (!singleImage.getSecurityRule().equals(singleShowcase.getVisibility())) {
                // set the new visibility
                singleImage.setSecurityRule(singleShowcase.getVisibility());
            }

            // save the image information in the database
            imageManager.save(singleImage);
        }

        // persist video links
        for (VideoLinkBean videoLink : videoLinks) {
            videoLinkManager.save(videoLink);

            // is the video self hosted?
            if (videoLink.getVideoTyp().equals(EVideoTyp.SELFHOSTEDVIDEO) && VideoUtil.isVideoInTempDir(videoLink.getVideolink())) {
                // move video from the temp folder to the real folder
                VideoUtil.moveVideoToUserDir(videoLink.getVideolink(), currentUser, fCtx.getApplication().getMessageBundle());
            }
        }

        // persist files
        for (FileBean file : fileList) {
            // persist
            fileManager.save(file);

            // move the file from the temp folder to the real folder
            if (FileUtil.isFileInTempDir(file.getRelativeFilePath())) {
                FileUtil.moveFileToUserDir(file, currentUser);
            }
        }

        // add images to the showcase
        singleShowcase.setImageList(imageList);

        // sort the images
        Collections.sort(imageList);

        // add videolinks
        singleShowcase.setVideoLinks(videoLinks);

        // add files
        singleShowcase.setFileList(fileList);

        // add text to the showcase
        singleShowcase.setShowcaseTexts(manyShowcaseText);

        // finally save the showcase in the database
        showcaseManager.save(singleShowcase);
    }

    public void handlePictureUpload(FileUploadEvent event) throws ExceptionLogger {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean currentUser = (UserBean) fCtx.getExternalContext().
                getSessionMap().get(Constants.AUTH_KEY);

        // only if we have here a real user
        if (currentUser == null || currentUser.getId() == null) {
            return;
        }

        // max images reached
        if (imageList.size() > Constants.MAXIMAGESFORSHOWCASE) {
            String outputMessage = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(),
                    "maximagesreached", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, outputMessage, outputMessage));
            return;
        }

        // get the source picture
        UploadedFile pictureFile = event.getFile();

        // we have this picture already in our list - not allowed
        for (ImageBean singleListImage : imageList) {
            if (pictureFile.getFileName().equals(singleListImage.getImageName())) {
                String outputMessage = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(),
                        "picturealreadyinlist", null, sessionBean.getUserLocale());
                fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, outputMessage, outputMessage));
                return;
            }
        }

        // okay, save this image to the temp folder till the showcase is saved and resize it.
        ImageBean savedImage = ImageUtil.saveImageInTemp(pictureFile, currentUser.getId(), singleShowcase.getVisibility(), true);

        // and add this image to our list
        imageList.add(savedImage);
    }

    public void handleVideoUpload(FileUploadEvent event) throws ExceptionLogger {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean currentUser = (UserBean) fCtx.getExternalContext().
                getSessionMap().get(Constants.AUTH_KEY);

        // only if we have here a real user
        if (currentUser == null || currentUser.getId() == null) {
            return;
        }

        // * TODO * check how much space the user has left
        // get the source
        UploadedFile videoFile = event.getFile();

        // okay, save this video to the temp folder till the showcase is saved
        VideoLinkBean tempVideo = VideoUtil.saveVideoInTemp(videoFile, currentUser);

        // add video object to our reference list
        videoLinks.add(tempVideo);

        // add a message that informs the user about the encoding process
        String outputMessage = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(),
                "videoprocessinginfo", null, sessionBean.getUserLocale());
        fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, outputMessage, outputMessage));
    }

    public void handleFileUpload(FileUploadEvent event) throws ExceptionLogger {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean currentUser = (UserBean) fCtx.getExternalContext().
                getSessionMap().get(Constants.AUTH_KEY);

        // only if we have here a real user
        if (currentUser == null || currentUser.getId() == null) {
            return;
        }

        // * TODO * check how much space the user has left
        // get the source
        UploadedFile uploadedFile = event.getFile();

        // okay, save this file to the temp folder till the showcase is saved
        FileBean tempFile = FileUtil.saveFileInTemp(uploadedFile, currentUser);

        // add the file to our reference list
        fileList.add(tempFile);
    }

    public void makeShowcasePublic() {
        // set the showcase to public
        singleShowcase.setShowcasepublic(true);

        // save the new state
        showcaseManager.save(singleShowcase);

        // print the message
        FacesContext fCtx = FacesContext.getCurrentInstance();
        String outputMessage = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(),
                "madepublic_success", null, sessionBean.getUserLocale());
        fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, outputMessage));
    }

    public void deleteShowcase() {
        // delete the pictures 
        for (ImageBean singleImage : singleShowcase.getImageList()) {
            ImageUtil.deleteImage(singleImage.getRelativeimagepath());
        }

        // delete videos
        for (VideoLinkBean videoLink : videoLinks) {
            // delete
            videoLinkManager.delete(videoLink);

            // is the video self hosted?
            if (videoLink.getVideoTyp().equals(EVideoTyp.SELFHOSTEDVIDEO)) {
                // remove it from the harddisc
                VideoUtil.deleteVideo(videoLink);
            }
        }

        // delete files
        for (FileBean file : fileList) {
            // delete
            fileManager.delete(file);

            // remove it from the harddisc
            FileUtil.deleteFile(file.getRelativeFilePath());
        }

        // delete the showcase from the database
        showcaseManager.delete(singleShowcase);

        // print the message
        FacesContext fCtx = FacesContext.getCurrentInstance();
        String outputMessage = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(),
                "showcasedelete_success", null, sessionBean.getUserLocale());
        fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, outputMessage));
    }

    public void onPictureDrop(DragDropEvent event) {
        // on a drop event this method is called.
        // we get the dropped image and save the position
        if (event.getData() instanceof ImageBean) {
            ImageBean draggedImage = (ImageBean) event.getData();

            for (ImageBean singleImage : imageList) {
                // find our image by comparing the relativepath (should be unique)
                if (singleImage.getRelativeimagepath().equals(draggedImage.getRelativeimagepath())) {
                    singleImage.setOrderNumber(Integer.parseInt(event.getDropId().replaceAll("content:slot", "")));
                }
            }
        }
    }

    public void addVideoLink() {
        // we just save the id from the video, so get it from the link
        singleVideoLink.setYoutubeID(YouTubeUtil.getVideoID(singleVideoLink.getVideolink()));

        // This is a youtube video!
        singleVideoLink.setVideoTyp(EVideoTyp.YOUTUBEVIDEO);

        // add it to our reference list
        videoLinks.add(singleVideoLink);

        // create a new object for further videolinks
        singleVideoLink = new VideoLinkBean();
    }

    public String deleteImageFromList() {
        if (deleteImageCache == null) {
            return null;
        }

        // remove the image from our list
        imageList.remove(deleteImageCache);

        // and also delete it from the harddrive
        ImageUtil.deleteImage(deleteImageCache.getRelativeimagepath());

        // reset the state
        deleteImageCache = null;

        return null;
    }

    public String deleteVideoFromList() {
        if (deleteVideoCache == null) {
            return null;
        }

        // remove from temporal list
        videoLinks.remove(deleteVideoCache);

        // check if this object already has an id
        if (deleteVideoCache.getId() != null) {
            // delete it from the database
            videoLinkManager.delete(deleteVideoCache);
        }

        // is this video self hosted?
        if (deleteVideoCache.getVideoTyp().equals(EVideoTyp.SELFHOSTEDVIDEO)) {
            // remove this video from the harddrive
            VideoUtil.deleteVideo(deleteVideoCache);
        }

        // reset the state
        deleteVideoCache = null;

        return null;
    }

    public String deleteFileFromList() {
        if (deleteFileCache == null) {
            return null;
        }

        // remove from temporal list
        fileList.remove(deleteFileCache);

        // has already an id?
        if (deleteFileCache.getId() != null) {
            // delete it from the database
            fileManager.delete(deleteFileCache);
        }

        // remove this file from harddrive
        FileUtil.deleteFile(deleteFileCache.getRelativeFilePath());

        // reset the state
        deleteFileCache = null;

        return null;
    }

    public String convertRelativeImagePath(ImageBean image) {
        String relativeImagePath;
        if (ImageUtil.isImageInTempDir(image.getRelativeimagepath())) {
            relativeImagePath = Constants.IMAGETEMPFOLDER + "/"
                    + image.getRelativeimagepath();
        } else {
            relativeImagePath = Constants.IMAGEFOLDER + "/"
                    + image.getRelativeimagepath();
        }
        return relativeImagePath;
    }

    public String convertRelativeVideoPath(VideoLinkBean video) {
        String videoLink;
        if (VideoUtil.isVideoInTempDir(video.getVideolink())) {
            videoLink = Constants.VIDEOTEMPFOLDER + "/"
                    + video.getVideolink();
        } else {
            videoLink = Constants.VIDEOFOLDER + "/"
                    + video.getVideolink();
        }
        return videoLink;
    }

    public String convertRelativeThumbnailPath(VideoLinkBean video) {
        String thumbnailLink;
        if (VideoUtil.isVideoInTempDir(video.getVideolink())) {
            thumbnailLink = Constants.VIDEOTEMPFOLDER + "/"
                    + video.getThumbnaillink();
        } else {
            thumbnailLink = Constants.VIDEOFOLDER + "/"
                    + video.getThumbnaillink();
        }
        return thumbnailLink;
    }

    public String convertRelativeFilePath(FileBean file) {
        String filePath;
        if (FileUtil.isFileInTempDir(file.getRelativeFilePath())) {
            filePath = Constants.FILETEMPFOLDER + "/"
                    + file.getRelativeFilePath();
        } else {
            filePath = Constants.FILEFOLDER + "/"
                    + file.getRelativeFilePath();
        }
        return filePath;
    }

    public String linkToPreview() {
        return ENavigation.DISPLAYSHOWCASE.toString() + singleShowcase.getId();
    }

    public ShowcaseBean getSingleShowcase() {
        return singleShowcase;
    }

    public void setSingleShowcase(ShowcaseBean singleShowcase) {
        this.singleShowcase = singleShowcase;
    }

    public VideoLinkBean getDeleteVideoCache() {
        return deleteVideoCache;
    }

    public void setDeleteVideoCache(VideoLinkBean deleteVideoCache) {
        this.deleteVideoCache = deleteVideoCache;
    }

    public void setDeleteImageCache(ImageBean deleteImageCache) {
        this.deleteImageCache = deleteImageCache;
    }

    public boolean isSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(boolean sendMsg) {
        this.sendMsg = sendMsg;
    }

    public DualListModel<String> getLanguages() {
        return languages;
    }

    public void setLanguages(DualListModel<String> languages) {
        this.languages = languages;
    }

    public List<ShowcaseTextBean> getManyShowcaseText() {
        return manyShowcaseText;
    }

    public void setManyShowcaseText(List<ShowcaseTextBean> manyShowcaseText) {
        this.manyShowcaseText = manyShowcaseText;
    }

    public VideoLinkBean getSingleVideoLink() {
        return singleVideoLink;
    }

    public void setSingleVideoLink(VideoLinkBean singleVideoLink) {
        this.singleVideoLink = singleVideoLink;
    }

    public List<VideoLinkBean> getVideoLinks() {
        return videoLinks;
    }

    public void setVideoLinks(List<VideoLinkBean> videoLinks) {
        this.videoLinks = videoLinks;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<SelectItem> getCategorieList() {
        return categorieList;
    }

    public void setCategorieList(List<SelectItem> categorieList) {
        this.categorieList = categorieList;
    }

    public List<ImageBean> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageBean> imageList) {
        this.imageList = imageList;
    }

    public ImageBean getDeleteImageCache() {
        return deleteImageCache;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public String getCategorieSelect() {
        return categorieSelect;
    }

    public void setCategorieSelect(String categorieSelect) {
        this.categorieSelect = categorieSelect;
    }

    public String getShowcaseId() {
        return showcaseId;
    }

    public void setShowcaseId(String showcaseId) {
        this.showcaseId = showcaseId;
    }

    public List<FileBean> getFileList() {
        return fileList;
    }

    public FileBean getDeleteFileCache() {
        return deleteFileCache;
    }

    public void setDeleteFileCache(FileBean deleteFileCache) {
        this.deleteFileCache = deleteFileCache;
    }
}
