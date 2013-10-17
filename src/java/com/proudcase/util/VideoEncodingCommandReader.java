package com.proudcase.util;

import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.MessagesManager;
import com.proudcase.persistence.MessagesBean;
import com.proudcase.persistence.UserBean;
import java.util.Date;
import javax.faces.context.FacesContext;

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
public class VideoEncodingCommandReader implements Runnable {

    // Process instance of the video encoding (ffmpeg)
    private final Process ffmpegProcess;
    
    // User object of the owner of this video
    private final UserBean ownerObj;

    public VideoEncodingCommandReader(Process ffmpegProcess, UserBean ownerObj) {
        this.ffmpegProcess = ffmpegProcess;
        this.ownerObj = ownerObj;
    }

    @Override
    public void run() {
        int exitResult = -100;
        try {
            exitResult = ffmpegProcess.waitFor();
        } catch (InterruptedException ex) {
            try {
                throw new ExceptionLogger(ex, "Wait for ffmpeg encoding exception!");
            } catch (ExceptionLogger ex2) {
                // do nothing
            }
        }

        // if we are here, then the encoding is hopefully done
        if (exitResult == 0) {
            // create a new message for the user
            MessagesBean finishMessage = new MessagesBean();
            
            // set some attributes
            finishMessage.setReceiver(ownerObj);
            finishMessage.setSenddate(new Date());
            
            // Get the message for the user
            FacesContext fCtx = FacesContext.getCurrentInstance();
            String message = PropertyReader.getMessageResourceString(
                    fCtx.getApplication().getMessageBundle(), "mediainfo", null, ownerObj.getPreferredLanguage());
            
            // add the message
            finishMessage.setMessage(message);
            
            // finally, save our message obj in the db
            MessagesManager messagesManager = ManagerFactory.createMessagesManager();
            messagesManager.save(finishMessage);
        } else {
            try {
                throw new ExceptionLogger(new InterruptedException(), "FFMPEG exit with error!");
            } catch (ExceptionLogger ex2) {
                // do nothing
            }
        } 
    }

}
