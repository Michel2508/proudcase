package com.proudcase.util;

import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.persistence.UserBean;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
  
  * @Author: Michel Vocks
  *
  * @Date: 17.10.2013
  *
  * @Encoding: UTF-8
*/
public class VideoEncoderUtil {
    
    // FFMPEG arguments
    private static final List<String> FFMPEG_ARGS = new ArrayList<>();
    
    // windows start command
    private static final List<String> WINSTARTCOMMAND = new ArrayList<>();
    
     // input placeholder
    private static final String INPUTPH = "#&INPUT";
    
    // output placeholder
    private static final String OUTPUTPH = "#$OUTPUT";
    
    static {
        // FFMPEG args
        FFMPEG_ARGS.add("ffmpeg");
        FFMPEG_ARGS.add("-i");
        FFMPEG_ARGS.add(INPUTPH);
        FFMPEG_ARGS.add("-c:v");
        FFMPEG_ARGS.add("libx264"); // h.265 format conversion
        FFMPEG_ARGS.add(OUTPUTPH);
        
        // win start command
        WINSTARTCOMMAND.add("cmd");
        WINSTARTCOMMAND.add("/C");
    };
    
    // This property returns the name of the os
    private static final String OSPROP = "os.name";
    
    // operating system abbreveations
    private static final String WINABB = "win";
    
    /**
     * Starts external program "FFMPEG" which converts a video to other formats
     * @param videoInputPath - Path to the initial video
     * @param videoOutputPath - Path to the output video
     * @param logOutput - Log output file
     * @param userObj - User object of the owner of this video
     * @throws com.proudcase.exclogger.ExceptionLogger
     */
    public static void convertVideo(String videoInputPath, String videoOutputPath, File logOutput, UserBean userObj) throws ExceptionLogger {
        // Get the os
        String operatingSystem = System.getProperty(OSPROP).toLowerCase();
        List<String> executeCommand = new ArrayList<>();
        
        // is the current operating system windows?
        if (operatingSystem.indexOf(WINABB) >= 0) {
            // format the run command
            executeCommand.addAll(WINSTARTCOMMAND);
            executeCommand.addAll(FFMPEG_ARGS);
        } else {
            // just add args
            executeCommand.addAll(FFMPEG_ARGS);
        }
        
        // replace the input- and output placeholders with the real path
        int index = executeCommand.indexOf(INPUTPH);
        executeCommand.remove(index);
        executeCommand.add(index, videoInputPath);
        index = executeCommand.indexOf(OUTPUTPH);
        executeCommand.remove(index);
        executeCommand.add(index, videoOutputPath);
        
        try {
            // create a processbuilder which executes our command
            ProcessBuilder processBuilder = new ProcessBuilder(executeCommand);

            // Redirect all output to our log file
            // *IMPORTANT* remove this and the thread will get a deadlock!
            processBuilder.redirectInput(logOutput);
            processBuilder.redirectError(logOutput);
            processBuilder.redirectOutput(logOutput);
            
            // let's execute the encoding process
            Process ffmpegProcess = processBuilder.start();
            
            // start a thread that watches the encoding and finally sends a message to the user
            new Thread(new VideoEncodingCommandReader(ffmpegProcess, userObj)).start();
            
        } catch (IOException ex) {
            throw new ExceptionLogger(ex, "Video encoding via ffmpeg not possible! (check ffmpeg in path environment)");
        }
        
    }

}
