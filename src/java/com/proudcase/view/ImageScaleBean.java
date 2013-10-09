/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proudcase.view;

import com.proudcase.persistence.ImageBean;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Michel
 */
@ManagedBean
@RequestScoped
public class ImageScaleBean implements Serializable {

    // return the new width with the image
    public String width(ImageBean image, int maxSize) {
        int newWidth;
        
        if (image == null || image.getWidth() == null || image.getHeight() == null) {
            return String.valueOf(maxSize);
        }

        // Get the width and height
        int width = image.getWidth();
        int height = image.getHeight();

        // check which is bigger as the other
        if (width >= height && width > maxSize) {
            // then we just return the maxSize
            newWidth = maxSize;
        } else if (height > width && height > maxSize) {
            // then we calculate the new width
            newWidth = (width * maxSize / height);
        } else {
            // then we just return the default width 
            newWidth = width;
        }

        // return the new width
        return String.valueOf(newWidth);
    }

    // return the new height with the image
    public String height(ImageBean image, int maxSize) {
        int newHeight;
        
        if (image == null || image.getWidth() == null || image.getHeight() == null) {
            return String.valueOf(maxSize);
        }

        // Get the width and height
        int width = image.getWidth();
        int height = image.getHeight();

        // check which is bigger as the other
        if (width >= height && width > maxSize) {
            // then we calculate the new width
            newHeight = (height * maxSize / width);
        } else if (height > width && height > maxSize) {
            // then we just return the maxSize
            newHeight = maxSize;
        } else {
            // then we just return the default height
            newHeight = height;
        }

        // return the new width
        return String.valueOf(newHeight);
    }
}
