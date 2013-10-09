package com.proudcase.util;

import com.proudcase.constants.Constants;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.persistence.ImageBean;
import com.proudcase.visibility.EVisibility;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.bson.types.ObjectId;
import org.primefaces.model.UploadedFile;

/**
 * Copyright © 30.09.2012 Michel Vocks This file is part of proudcase.
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
 * @Date: 30.09.2012
 *
 * @Encoding: UTF-8
 */
public class ImageUtil {

    private static final String DEFAULT_CONTENTTYP = "image/";
    private static final String CONTENTTYP_JPG = "JPG";
    private static final String CONTENTTYP_JPEG = "JPEG";
    private static final String CONTENTTYP_PNG = "PNG";

    public static ImageBean saveImageAsFile(UploadedFile image, ObjectId userID, EVisibility securityRule, boolean secure)
            throws ExceptionLogger {
        // let us check if the user has a "home"-dir
        String homeDirStr = Constants.BASEPATH + "/"
                + Constants.IMAGEFOLDER + "/" + userID.toString();

        // the user wants to secure the image
        if (secure) {
            homeDirStr += "/" + Constants.IMAGESECUREFOLDER;
        }

        File homeDir = new File(homeDirStr);
        if (!homeDir.isDirectory()) {
            // create the home-dir
            homeDir.mkdirs();
        }

        // create a new image obj 
        ImageBean newImage = new ImageBean();

        // create an unique image name
        String imageName = (new ObjectId()).toString();

        // Create the imagepath. 
        // BASEPATH + unique user id + (maybe secureimage) + imagehash (new generated)
        String imagePath = homeDirStr + "/" + imageName;

        // let us check if this file exists
        // should never happen so throw an exception if it exists
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            throw new ExceptionLogger(
                    new RuntimeException("Image:" + imagePath + ":already exists"));
        }

        // write image to harddisk
        Dimension dimension = writeImageToHardDrive(image, imageFile);

        // create the relative image path
        String relativeImagePath = userID.toString()
                + "/"
                + (secure ? Constants.IMAGESECUREFOLDER + "/" : "")
                + imageName;

        // complete the object
        newImage.setRelativeimagepath(relativeImagePath);
        newImage.setImageName(image.getFileName());
        newImage.setOrderNumber(new Integer(0));
        newImage.setHeight(dimension.height);
        newImage.setWidth(dimension.width);
        newImage.setOwnerOfImage(userID);

        // return the image
        return newImage;
    }

    public static void deleteImage(String imagePath) {
        File oldImageFromUser = new File(Constants.BASEPATH + "/"
                + Constants.IMAGEFOLDER + "/" + imagePath);

        // check if it exists and is a file
        if (oldImageFromUser.isFile()) {
            // delete it
            oldImageFromUser.delete();
        }

        // probably in the temp folder
        oldImageFromUser = new File(Constants.BASEPATH + "/"
                + Constants.IMAGETEMPFOLDER + "/" + imagePath);

        // check if it exists and is a file
        if (oldImageFromUser.isFile()) {
            // delete it
            oldImageFromUser.delete();
        }
    }

    public static ImageBean saveImageInTemp(UploadedFile image, ObjectId id, EVisibility securityRule)
            throws ExceptionLogger {
        // create a new image obj
        ImageBean savedImage = new ImageBean();

        // is the temp folder available?
        String tempFolderStr = Constants.BASEPATH + "/" + Constants.IMAGETEMPFOLDER
                + "/" + id.toString();
        File tempFolder = new File(tempFolderStr);

        // not exists?
        if (!tempFolder.isDirectory()) {
            // create
            tempFolder.mkdirs();
        }

        // prepare the file
        String imageFileStr = (new ObjectId()).toString();
        File imageFile = new File(tempFolderStr + "/" + imageFileStr);

        // let us check if this file exists
        // should never happen so throw an exception if it exists
        if (imageFile.exists()) {
            throw new ExceptionLogger(
                    new RuntimeException("Image:" + imageFile.getAbsolutePath()
                    + ":already exists"));
        }

        // write image to harddisk
        Dimension dimension = writeImageToHardDrive(image, imageFile);

        // create the relative path
        imageFileStr = id.toString()
                + "/"
                + imageFileStr;

        // complete the object
        savedImage.setRelativeimagepath(imageFileStr);
        savedImage.setImageName(image.getFileName());
        savedImage.setOrderNumber(new Integer(0));
        savedImage.setHeight(dimension.height);
        savedImage.setWidth(dimension.width);
        savedImage.setOwnerOfImage(id);
        savedImage.setSecurityRule(securityRule);

        return savedImage;
    }
    
    private static Dimension writeImageToHardDrive(UploadedFile image, File imageFile) throws ExceptionLogger {
        // alright, let's start saving our file to filesystem
        String contentType = null;
        RenderedImage uploadedImage;
        try {
            // format the image to a nicer form
            uploadedImage = ImageIO.read(image.getInputstream());

            // get the right content type
            contentType = image.getContentType();
            contentType = contentType.substring(DEFAULT_CONTENTTYP.length(), contentType.length());

            // save the image
            ImageIO.write(uploadedImage, contentType, imageFile);
        } catch (IOException ex2) {
            throw new ExceptionLogger(ex2);
        }

        // determine the contentType
        int contentTypNum = ImageSave.IMAGE_UNKNOWN;
        if (contentType.equalsIgnoreCase(CONTENTTYP_JPEG)) {
            contentTypNum = ImageSave.IMAGE_JPEG;
        } else if (contentType.equalsIgnoreCase(CONTENTTYP_JPG)) {
            contentTypNum = ImageSave.IMAGE_JPEG;
        } else if (contentType.equalsIgnoreCase(CONTENTTYP_PNG)) {
            contentTypNum = ImageSave.IMAGE_PNG;
        }

        // Dimension
        Dimension dimension = new Dimension(uploadedImage.getWidth(), uploadedImage.getHeight());

        // so resize the image and compress it
        if (contentTypNum != ImageSave.IMAGE_UNKNOWN) {
            // resize
            BufferedImage resizedImage = ImageSave.resizeImage(imageFile.getAbsolutePath()
                    , contentTypNum, Constants.MAX_IMAGE_SIZE, Constants.MAX_IMAGE_SIZE);
            
            // save the new width and height
            dimension.setSize(resizedImage.getWidth(), resizedImage.getHeight());
            
            if (contentTypNum == ImageSave.IMAGE_JPEG) {
                // compression allowed
                ImageSave.saveCompressedImage(resizedImage, imageFile.getAbsolutePath(), contentTypNum);
            } else {
                // no compression allowed for png images
                ImageSave.saveImage(resizedImage, imageFile.getAbsolutePath(), contentTypNum);
            }
        }
        
        // return dimension 
        return dimension;
    }

    public static String moveImageToUserDir(String relativePath, ObjectId userId, boolean secure)
            throws ExceptionLogger {
        // is the image available?
        File imageFile = new File(Constants.BASEPATH + "/"
                + Constants.IMAGETEMPFOLDER
                + "/" + relativePath);

        // not exists?
        if (!imageFile.isFile()) {
            // it could be possible that the image is already in the user dir
            // but the user wants it secured (in the secure folder) or the other way around
            imageFile = new File(Constants.BASEPATH + "/"
                    + Constants.IMAGEFOLDER
                    + "/" + relativePath);

            // let us check if it exists
            if (!imageFile.isFile()) {
                return null;
            }

            // if we are here then the image is already in the user dir
            // but the user want this image secured or the other way around
            // so just continue normaly and move the image to the right directory
        }

        // get the image name
        String imageName = imageFile.getName();

        // create the new destination folder
        String destinationPath = Constants.BASEPATH + "/" + Constants.IMAGEFOLDER
                + "/" + userId.toString()
                + (secure ? "/" + Constants.IMAGESECUREFOLDER : "");

        // not exists?
        File destinationDir = new File(destinationPath);
        if (!destinationDir.isDirectory()) {
            // create all dir
            destinationDir.mkdirs();
        }

        // relative image path
        String relativeImagePath = userId.toString() + "/"
                + (secure ? Constants.IMAGESECUREFOLDER + "/" : "")
                + imageName;

        // let us check if this file exists
        // should never happen so throw an exception if it exists
        File newImageDestination = new File(destinationPath + "/" + imageName);
        if (newImageDestination.exists()) {
            throw new ExceptionLogger(
                    new RuntimeException("Image:" + newImageDestination.getAbsolutePath()
                    + ":already exists (can't move it)"));
        }

        // well, move the file
        if (imageFile.renameTo(newImageDestination)) {
            // success
            return relativeImagePath;
        } else {
            return null;
        }
    }

    public static boolean isAlreadyInRightDir(String relativePath, boolean secure) {
        String imageFullPath = Constants.BASEPATH + "/"
                + Constants.IMAGEFOLDER + "/" + relativePath;

        // we check if the user in the secure folder and should be secured
        if ((relativePath.contains(Constants.IMAGESECUREFOLDER) && !secure)
                || (!relativePath.contains(Constants.IMAGESECUREFOLDER) && secure)) {
            return false;
        }

        // let us check if the image exists
        if (new File(imageFullPath).isFile()) {
            return true;
        }

        // does not exist
        return false;
    }

    public static boolean isImageInTempDir(String relativePath) {
        String imageFullPath = Constants.BASEPATH + "/"
                + Constants.IMAGETEMPFOLDER + "/" + relativePath;

        if (new File(imageFullPath).isFile()) {
            return true;
        }

        return false;
    }
}
