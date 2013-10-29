package com.proudcase.util;

import com.proudcase.constants.Constants;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.persistence.FileBean;
import com.proudcase.persistence.UserBean;
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
public class FileUtil {

    // bytes to read in the memory
    private static final int BYTESTOREAD = 10240;
    
    private static void writeFileStreamToFile(UploadedFile file, File outputFile) throws ExceptionLogger {
        OutputStream outputStream = null;

        try {
            // write the inputStream to a FileOutputStream
            outputStream
                    = new FileOutputStream(outputFile);

            int read = 0;
            byte[] bytes = new byte[BYTESTOREAD];
            InputStream fileInputStream = file.getInputstream();

            while ((read = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            throw new ExceptionLogger(e, "Error during saving a file to disk:" + outputFile.getAbsolutePath());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    throw new ExceptionLogger(e, "Error during saving a file to disk:" + outputFile.getAbsolutePath());
                }
            }
        }
    }

    public static void deleteFile(String filePath) {
        File oldFileFromUser = new File(Constants.BASEPATH + "/"
                + Constants.FILEFOLDER + "/" + filePath);

        // check if it exists and is a file
        if (oldFileFromUser.isFile()) {
            // delete it
            oldFileFromUser.delete();
        }

        // probably in the temp folder
        oldFileFromUser = new File(Constants.BASEPATH + "/"
                + Constants.FILETEMPFOLDER + "/" + filePath);

        // check if it exists and is a file
        if (oldFileFromUser.isFile()) {
            // delete it
            oldFileFromUser.delete();
        }
    }

    public static FileBean saveFileInTemp(UploadedFile file, UserBean userObj)
            throws ExceptionLogger {
        // create a new file obj
        FileBean fileTemp = new FileBean();
        
        // Every file get's a new folder, that we never have a problem with identical names
        String fileFolderID = new ObjectId().toString();
        
        // is the temp folder available?
        String tempFolderStr = Constants.BASEPATH + "/" + Constants.FILETEMPFOLDER
                + "/" + userObj.getId().toString() + "/" + fileFolderID;
        File tempFolder = new File(tempFolderStr);

        // not exists?
        if (!tempFolder.isDirectory()) {
            // create
            tempFolder.mkdirs();
        }
        
        // set the original name and size
        fileTemp.setFileName(file.getFileName());
        fileTemp.setFileSize(file.getSize());

        // prepare the file
        File fileDest = new File(tempFolderStr + "/" + fileTemp.getFileName());

        // let us check if this file exists
        // should never happen so throw an exception if it exists
        if (fileDest.exists()) {
            throw new ExceptionLogger(
                    new RuntimeException("File:" + fileDest.getAbsolutePath()
                            + ":already exists"));
        }

        // write file to harddisk
        writeFileStreamToFile(file, fileDest);

        // create the relative path
        String relativeFilePath = userObj.getId().toString()
                + "/" + fileFolderID + "/"
                + fileTemp.getFileName();

        // complete the object
        fileTemp.setRelativeFilePath(relativeFilePath);
        
        return fileTemp;
    }

    public static void moveFileToUserDir(FileBean fileBean, UserBean userObj)
            throws ExceptionLogger {
        
        // save the user id temp
        String userID = userObj.getId().toString();
        
        // create the new destination folder
        String basePath = Constants.BASEPATH + "/" + Constants.FILEFOLDER + "/";
        String destinationPath = basePath + userID.toString() + "/" + fileBean.getFileFolderID();

        // not exists?
        File destinationDir = new File(destinationPath);
        if (!destinationDir.isDirectory()) {
            // create all dir
            destinationDir.mkdirs();
        }
        
        // Get the absolute input file 
        File fileInput = new File(Constants.BASEPATH + "/" + Constants.FILETEMPFOLDER
                + "/" + fileBean.getRelativeFilePath());
        
        // Get the absolute output file
        File fileOutput = new File(basePath + fileBean.getRelativeFilePath());
        
        // move the file to our new location
        fileInput.renameTo(fileOutput);
    }

    public static boolean isFileInTempDir(String relativePath) {
        String fileFullPath = Constants.BASEPATH + "/"
                + Constants.FILETEMPFOLDER + "/" + relativePath;

        return new File(fileFullPath).isFile();
    }
}
