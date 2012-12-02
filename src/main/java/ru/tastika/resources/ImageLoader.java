package ru.tastika.resources;


import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;


/**
 * Loader for the resource images. The class
 * uses the getResource Method to get the
 * Resource from the relative path.
 */
public class ImageLoader {


    /**
     * contains string objects which respresents the search paths
     */
    protected static ArrayList<String> searchPath = new ArrayList<String>();


    /**
     * Returns an Image from the same path.
     * @param imageName An image name with the file extension
     *                  buttonEdge.g. Can.gif
     */
    public static Image getImage(String imageName) {
        return getImageIcon(imageName).getImage();
    }


    /**
     * Returns an ImageIcon from the same path.
     * @param imageName An image name with the file extension
     *                  buttonEdge.g. Can.gif
     */
    public static ImageIcon getImageIcon(String imageName) {
        if (searchPath.size() > 0) {
            int i = searchPath.size() - 1;
            ImageIcon image = getImageIcon(i--, imageName);
            while (image == null && 0 <= i) {
                image = getImageIcon(i--, imageName);
            }
            return image;
        }
        else {
            return null;
        }
    }


    /**
     * Returns an ImageIcon from the same path.
     * @param imageName       An image name with the file extension
     *                        buttonEdge.g. Can.gif
     * @param searchPathIndex - index of path which was placed into searchPath
     * @return ImageIcon with specified imageName
     */
    public static ImageIcon getImageIcon(int searchPathIndex, String imageName) {
        return getImageIcon(searchPathIndex, imageName, false);
    }


    /**
     * Returns an ImageIcon from the same path.
     * @param imageName               An image name with the file extension
     *                                buttonEdge.g. Can.gif
     * @param searchPathIndex         - index of path which was placed into searchPath
     * @param onlyIndicatedSearchPath - search only in specified path
     * @return ImageIcon with specified imageName
     */
    public static ImageIcon getImageIcon(int searchPathIndex, String imageName, boolean onlyIndicatedSearchPath) {
        // precondition test
        if (imageName == null) {
            return null;
        }

        // image loading
        if (0 <= searchPathIndex && searchPathIndex < searchPath.size()) {
            URL url = ImageLoader.class.getResource(searchPath.get(searchPathIndex) + imageName);
            if (url != null) {
                return new ImageIcon(url);
            }
            else if (!onlyIndicatedSearchPath) {
                return getImageIcon(searchPathIndex - 1, imageName);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }


    /**
     * pushes the specified path to the search path
     * <p/>
     * An example for a search path file name is 'com/jgraph/pad/resources'.
     */
    public static int pushSearchPath(String path) {
        if (path == null) {
            return -1;
        }

        if (!path.endsWith("/")) {
            path = path + "/";
        }

        int index = searchPath.indexOf(path);
        if (index == -1) {
            searchPath.add(path);
            index = searchPath.size() - 1;
        }
        return index;
    }


    /**
     * removes the searchpath at the specified index
     */
    public static void removeSearchPath(int index) {
        searchPath.remove(index);
    }

}
