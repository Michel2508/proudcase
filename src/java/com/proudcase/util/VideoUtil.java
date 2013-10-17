package com.proudcase.util;

import com.proudcase.constants.Constants;
import com.proudcase.constants.EVideoTyp;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.persistence.VideoLinkBean;
import com.proudcase.visibility.EVisibility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bson.types.ObjectId;
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
 * @Author: Michel Vocks
 *
 * @Date: 12.10.2013
 *
 * @Encoding: UTF-8
 */
public class VideoUtil {

    // flash autostart tag
    private static final String AUTOSTART = "?autostart=true";

    // bytes to read in the memory
    private static final int BYTESTOREAD = 10240;

    public static VideoLinkBean saveVideoAsFile(UploadedFile video, ObjectId userID)
            throws ExceptionLogger {
        // let us check if the user has a "home"-dir
        String homeDirStr = Constants.BASEPATH + "/"
                + Constants.VIDEOFOLDER + "/" + userID.toString();

        File homeDir = new File(homeDirStr);
        if (!homeDir.isDirectory()) {
            // create the home-dir
            homeDir.mkdirs();
        }

        // create a new videolink obj 
        VideoLinkBean newVideo = new VideoLinkBean();

        // create an unique video name
        String videoName = (new ObjectId()).toString();

        // Create the videopath. 
        // BASEPATH + unique user id + (maybe securevideo) + videohash (new generated)
        String videoPath = homeDirStr + "/" + videoName;

        // let us check if this file exists
        // should never happen so throw an exception if it exists
        File videoFile = new File(videoPath);
        if (videoFile.exists()) {
            throw new ExceptionLogger(
                    new RuntimeException("Video:" + videoPath + ":already exists"));
        }

        // write video to harddisk
        writeVideoToFile(video, videoFile);
        
        // create the relative video path
        String relativeVideoPath = userID.toString()
                + "/" + videoName;

        // complete the object
        newVideo.setVideolink(relativeVideoPath);
        newVideo.setVideoTyp(EVideoTyp.SELFHOSTEDVIDEO);

        // return the video obj
        return newVideo;
    }

    private static void writeVideoToFile(UploadedFile video, File videoFile) throws ExceptionLogger {
        OutputStream outputStream = null;

        try {
            // write the inputStream to a FileOutputStream
            outputStream
                    = new FileOutputStream(videoFile);

            int read = 0;
            byte[] bytes = new byte[BYTESTOREAD];
            InputStream videoInputStream = video.getInputstream();

            while ((read = videoInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (IOException e) {
            throw new ExceptionLogger(e, "Error during saving a video to disk:" + videoFile.getAbsolutePath());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    throw new ExceptionLogger(e, "Error during saving a video to disk:" + videoFile.getAbsolutePath());
                }
            }
        }
    }

    public static void deleteVideo(String videoPath) {
        File oldVideoFromUser = new File(Constants.BASEPATH + "/"
                + Constants.VIDEOFOLDER + "/" + videoPath);

        // check if it exists and is a file
        if (oldVideoFromUser.isFile()) {
            // delete it
            oldVideoFromUser.delete();
        }

        // probably in the temp folder
        oldVideoFromUser = new File(Constants.BASEPATH + "/"
                + Constants.VIDEOTEMPFOLDER + "/" + videoPath);

        // check if it exists and is a file
        if (oldVideoFromUser.isFile()) {
            // delete it
            oldVideoFromUser.delete();
        }
    }

    public static VideoLinkBean saveVideoInTemp(UploadedFile video, ObjectId userID)
            throws ExceptionLogger {
        // create a new video obj
        VideoLinkBean tempVideo = new VideoLinkBean();

        // is the temp folder available?
        String tempFolderStr = Constants.BASEPATH + "/" + Constants.VIDEOTEMPFOLDER
                + "/" + userID.toString();
        File tempFolder = new File(tempFolderStr);

        // not exists?
        if (!tempFolder.isDirectory()) {
            // create
            tempFolder.mkdirs();
        }

        // prepare the file
        String videoFileStr = (new ObjectId()).toString() + video.getFileName().substring(video.getFileName().length() - 4);
        File videoFile = new File(tempFolderStr + "/" + videoFileStr);

        // let us check if this file exists
        // should never happen so throw an exception if it exists
        if (videoFile.exists()) {
            throw new ExceptionLogger(
                    new RuntimeException("Video:" + videoFile.getAbsolutePath()
                            + ":already exists"));
        }

        // write video to harddisk
        writeVideoToFile(video, videoFile);
        
        // create the relative path
        videoFileStr = userID.toString()
                + "/"
                + videoFileStr;

        // complete the object
        tempVideo.setVideoTyp(EVideoTyp.SELFHOSTEDVIDEO);
        tempVideo.setVideolink(videoFileStr);

        return tempVideo;
    }

    public static String moveVideoToUserDir(String relativePath, ObjectId userID)
            throws ExceptionLogger {
        // is the video available?
        File videoFile = new File(Constants.BASEPATH + "/"
                + Constants.VIDEOTEMPFOLDER
                + "/" + relativePath);

        // not exists?
        if (!videoFile.isFile()) {
            return null;
        }

        // get the video name
        String imageName = videoFile.getName();

        // create the new destination folder
        String destinationPath = Constants.BASEPATH + "/" + Constants.VIDEOFOLDER
                + "/" + userID.toString();

        // not exists?
        File destinationDir = new File(destinationPath);
        if (!destinationDir.isDirectory()) {
            // create all dir
            destinationDir.mkdirs();
        }

        // relative video path
        String relativeVideoPath = userID.toString() + "/" + imageName;

        // let us check if this file exists
        // should never happen so throw an exception if it exists
        File newVideoDestination = new File(destinationPath + "/" + imageName);
        if (newVideoDestination.exists()) {
            throw new ExceptionLogger(
                    new RuntimeException("Video:" + newVideoDestination.getAbsolutePath()
                            + ":already exists (can't move it)"));
        }

        // well, move the file
        if (videoFile.renameTo(newVideoDestination)) {
            // success
            return relativeVideoPath;
        } else {
            return null;
        }
    }

    public static boolean isVideoInTempDir(String relativePath) {
        String videoFullPath = Constants.BASEPATH + "/"
                + Constants.VIDEOTEMPFOLDER + "/" + relativePath;

        return new File(videoFullPath).isFile();
    }

    // returns the video url with autostart tag
    public static String getVideoURLWithAutostart(String url) {
        return url + AUTOSTART;
    }

}
