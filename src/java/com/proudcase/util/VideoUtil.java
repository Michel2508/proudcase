package com.proudcase.util;

import com.proudcase.constants.Constants;
import com.proudcase.constants.EVideoTyp;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.persistence.UserBean;
import com.proudcase.persistence.VideoLinkBean;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
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

    // File type of the encoding log files
    private static final String VIDEOENCODINGLOGTYPE = ".log";
    
    // Final video file typ
    private static final String FINALVIDEOTYP = ".mp4";

    // bytes to read in the memory
    private static final int BYTESTOREAD = 10240;
    
    // instance of the video encoding thread pool
    public static ExecutorService videoEncodingService;
    
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

    public static VideoLinkBean saveVideoInTemp(UploadedFile video, UserBean userObj)
            throws ExceptionLogger {
        // create a new video obj
        VideoLinkBean tempVideo = new VideoLinkBean();

        // is the temp folder available?
        String tempFolderStr = Constants.BASEPATH + "/" + Constants.VIDEOTEMPFOLDER
                + "/" + userObj.getId().toString();
        File tempFolder = new File(tempFolderStr);

        // not exists?
        if (!tempFolder.isDirectory()) {
            // create
            tempFolder.mkdirs();
        }

        // prepare the file
        String videoFileStr = (new ObjectId()).toString() + FINALVIDEOTYP;
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
        videoFileStr = userObj.getId().toString()
                + "/"
                + videoFileStr;

        // complete the object
        tempVideo.setVideoTyp(EVideoTyp.SELFHOSTEDVIDEO);
        tempVideo.setVideolink(videoFileStr);
        
        // also add the link to the thumbnail (which will be created during encoding)
        tempVideo.setThumbnailink(videoFileStr + Constants.JPEG_SUFFIX);

        return tempVideo;
    }

    public static void moveVideoToUserDir(String relativePath, UserBean userObj, String messageBundle)
            throws ExceptionLogger {
        
        // save the user id temp
        String userID = userObj.getId().toString();
        
        // create the new destination folder
        String basePath = Constants.BASEPATH + "/" + Constants.VIDEOFOLDER + "/";
        String destinationPath = basePath + userID.toString();

        // not exists?
        File destinationDir = new File(destinationPath);
        if (!destinationDir.isDirectory()) {
            // create all dir
            destinationDir.mkdirs();
        }
        
        // Get the absolute input video path
        String videoInputPath = Constants.BASEPATH + "/" + Constants.VIDEOTEMPFOLDER
                + "/" + relativePath;
        
        // Get the absolute output video path
        String videoOutputPath = basePath + relativePath;
        
        // Create the logging file for the encoding process
        File encodingLogFile = null;
        try {
            // Create a new log file for encoding of the video
            encodingLogFile = new File(videoOutputPath + VIDEOENCODINGLOGTYPE);
            encodingLogFile.createNewFile();
        } catch (IOException ex) {
            throw new ExceptionLogger(ex, "Cannot create log file for video encoding!");
        }
        
        // First of all, create a new instance of the new encoding thread
        VideoEncoderUtil videoEncodingThread = new VideoEncoderUtil(videoInputPath,
                videoOutputPath, encodingLogFile, userObj, messageBundle);
        
        // execute the encoding via the executor service
        videoEncodingService.execute(videoEncodingThread);
    }

    public static boolean isVideoInTempDir(String relativePath) {
        String videoFullPath = Constants.BASEPATH + "/"
                + Constants.VIDEOTEMPFOLDER + "/" + relativePath;

        return new File(videoFullPath).isFile();
    }
}
