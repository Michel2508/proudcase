package com.proudcase.util;

import com.proudcase.constants.Constants;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.MessagesManager;
import com.proudcase.mongodb.manager.VideoLinkManager;
import com.proudcase.persistence.MessagesBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.persistence.VideoLinkBean;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * @Date: 17.10.2013
 *
 * @Encoding: UTF-8
 */
public class VideoEncoderUtil implements Runnable {

    // FFMPEG arguments
    private static final List<String> FFMPEG_ARGS = new ArrayList<>();

    // windows start command
    private static final List<String> WINSTARTCOMMAND = new ArrayList<>();

    // Thumbnail commands for ffmpeg
    private static final List<String> THUMBNAIL_FFMPEG_ARGS = new ArrayList<>();

    // input placeholder
    private static final String INPUTPH = "#&INPUT";

    // output placeholder
    private static final String OUTPUTPH = "#$OUTPUT";

    static {
        // FFMPEG args
        FFMPEG_ARGS.add("ffmpeg");
        FFMPEG_ARGS.add("-i"); // next arg is path to input video
        FFMPEG_ARGS.add(INPUTPH); // path to input video
        FFMPEG_ARGS.add("-movflags"); // relocate MOOV atom in the video
        FFMPEG_ARGS.add("faststart"); // allow playback to begin before the file is completely downloaded
        FFMPEG_ARGS.add("-c:v"); // next arg is the output format
        FFMPEG_ARGS.add("libx264"); // h.265 format conversion
        FFMPEG_ARGS.add(OUTPUTPH); // output video path

        // win start command
        WINSTARTCOMMAND.add("cmd");
        WINSTARTCOMMAND.add("/C");

        // thumbnail args for ffmpeg
        THUMBNAIL_FFMPEG_ARGS.add("ffmpeg");
        THUMBNAIL_FFMPEG_ARGS.add("-i");        // next arg is path to input video
        THUMBNAIL_FFMPEG_ARGS.add(INPUTPH);     // path to input video
        THUMBNAIL_FFMPEG_ARGS.add("-r");        // force the frames per second
        THUMBNAIL_FFMPEG_ARGS.add("1");         // one frame per second
        THUMBNAIL_FFMPEG_ARGS.add("-s");        // force to capture in a specific quality
        THUMBNAIL_FFMPEG_ARGS.add("hd1080");    // hd1080 == 1920x1080 resolution
        THUMBNAIL_FFMPEG_ARGS.add("-t");        // force to capture only specified seconds
        THUMBNAIL_FFMPEG_ARGS.add("1");         // forse to capture only for one second
        THUMBNAIL_FFMPEG_ARGS.add("-ss");       // force to start at a specific point
        THUMBNAIL_FFMPEG_ARGS.add("00:00:10");  // capture start after 10 seconds
        THUMBNAIL_FFMPEG_ARGS.add(OUTPUTPH);    // output thumbnail path
    }
    ;
    
    // This property returns the name of the os
    private static final String OSPROP = "os.name";

    // operating system abbreveations
    private static final String WINABB = "win";

    // Some things we need for the encoding process
    private final String videoInputPath;
    private final String videoOutputPath;
    private final File logOutput;
    private final UserBean userObj;
    private final String messageBundle;

    public VideoEncoderUtil(String videoInputPath, String videoOutputPath, File logOutput, UserBean userObj, String messageBundle) {
        this.videoInputPath = videoInputPath;
        this.videoOutputPath = videoOutputPath;
        this.logOutput = logOutput;
        this.userObj = userObj;
        this.messageBundle = messageBundle;
    }

    /**
     * Starts external program "FFMPEG" which converts a video to other formats
     */
    @Override
    public void run() {
        // Get the os
        String operatingSystem = System.getProperty(OSPROP).toLowerCase();
        List<String> executeCommandEnc = new ArrayList<>();
        List<String> executeCommandThumb = new ArrayList<>();

        // is the current operating system windows?
        if (operatingSystem.indexOf(WINABB) >= 0) {
            // format the run command for capturing the thumbnail
            executeCommandThumb.addAll(WINSTARTCOMMAND);
            executeCommandThumb.addAll(THUMBNAIL_FFMPEG_ARGS);

            // format the run command for encoding
            executeCommandEnc.addAll(WINSTARTCOMMAND);
            executeCommandEnc.addAll(FFMPEG_ARGS);
        } else {
            // just add args
            executeCommandThumb.addAll(THUMBNAIL_FFMPEG_ARGS);
            executeCommandEnc.addAll(FFMPEG_ARGS);
        }

        // replace the input- and output placeholders with the real path for thumbnail capturing
        int index = executeCommandThumb.indexOf(INPUTPH);
        executeCommandThumb.remove(index);
        executeCommandThumb.add(index, videoInputPath);
        index = executeCommandThumb.indexOf(OUTPUTPH);
        executeCommandThumb.remove(index);
        // Just take the video path and add jpeg suffix        
        executeCommandThumb.add(index, videoOutputPath + Constants.JPEG_SUFFIX);

        // replace the input- and output placeholders with the real path for encoding
        index = executeCommandEnc.indexOf(INPUTPH);
        executeCommandEnc.remove(index);
        executeCommandEnc.add(index, videoInputPath);
        index = executeCommandEnc.indexOf(OUTPUTPH);
        executeCommandEnc.remove(index);
        executeCommandEnc.add(index, videoOutputPath);

        // Get the file from the input video
        File tempVideoFile = new File(videoInputPath);

        try {
            // create a processbuilder which executes the thumbnail command
            ProcessBuilder processBuilder = new ProcessBuilder(executeCommandThumb);

            // Redirect all output to our log file
            // *IMPORTANT* remove this and the thread will get a deadlock!
            processBuilder.redirectInput(logOutput);
            processBuilder.redirectError(logOutput);
            processBuilder.redirectOutput(logOutput);

            // let's execute the thumbnail process
            Process ffmpegProcess = processBuilder.start();

            // Wait until the capturing process is finished
            try {
                ffmpegProcess.waitFor();
            } catch (InterruptedException ex) {
                throw new ExceptionLogger(ex, "Wait for ffmpeg thumbnail process interrupt exception!");
            }

            // create a processbuilder which executes the encoding command
            processBuilder = new ProcessBuilder(executeCommandEnc);

            // Redirect all output to our log file
            // *IMPORTANT* remove this and the thread will get a deadlock!
            processBuilder.redirectInput(logOutput);
            processBuilder.redirectError(logOutput);
            processBuilder.redirectOutput(logOutput);

            // let's execute the encoding process
            ffmpegProcess = processBuilder.start();

            // Wait until the encoding process is finished
            int exitResult = -100;
            try {
                exitResult = ffmpegProcess.waitFor();
            } catch (InterruptedException ex) {
                throw new ExceptionLogger(ex, "Wait for ffmpeg encoding process interrupt exception!");
            }

            // create a file object from the output file
            File encodedVideoFile = new File(videoOutputPath);

            // if we are here, then the encoding is hopefully done
            if (exitResult == 0 && encodedVideoFile.isFile()) {
                // first of all, delete the temp video file!
                if (tempVideoFile.isFile()) {
                    tempVideoFile.delete();
                }

                // Get the videoLink object from the database
                VideoLinkManager videoLinkManager = ManagerFactory.createVideoLinkManager();
                VideoLinkBean videoLink = videoLinkManager.getVideoLinkByVideoName(encodedVideoFile.getName());

                // this should never happen, just to be sure
                if (videoLink != null) {
                    // mark that the encoding is done
                    videoLink.setEncodingDone(true);

                    // update the obj in the db
                    videoLinkManager.save(videoLink);
                }

                // create a new message for the user
                MessagesBean finishMessage = new MessagesBean();

                // set some attributes
                finishMessage.setReceiver(userObj);
                finishMessage.setSenddate(new Date());

                // Get the message for the user
                String message = PropertyReader.getMessageResourceString(
                        messageBundle, "videoencodingfinished", null, userObj.getPreferredLanguage());

                // add the message
                finishMessage.setMessage(message);

                // finally, save our message obj in the db
                MessagesManager messagesManager = ManagerFactory.createMessagesManager();
                messagesManager.save(finishMessage);
            } else {
                throw new ExceptionLogger(new InterruptedException(), "FFMPEG exit with error!");
            }

        } catch (IOException ex) {
            try {
                throw new ExceptionLogger(ex, "Video encoding via ffmpeg not possible! (check ffmpeg in path environment)");
            } catch (ExceptionLogger ex2) {
            }
        } catch (ExceptionLogger ex2) {
        }
    }

}
