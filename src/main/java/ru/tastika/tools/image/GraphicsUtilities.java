package ru.tastika.tools.image;


import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 * User: hobal
 * Date: 29.06.2010
 * Time: 21:28:55
 */
public class GraphicsUtilities {


    public static BufferedImage getBufferedImage(String fullPath) throws IOException {
        File file = new File(fullPath);

        BufferedImage image;
        if (file.isFile()) {
            return getBufferedImage(file);
        }
        else {
            URL resource = new URL(fullPath);
            image = ImageIO.read(resource);
        }
        return image;
    }


    public static BufferedImage getBufferedImage(File file) throws IOException {
        return ImageIO.read(file);
    }


    public static BufferedImage scaleBufferedImage(BufferedImage image, double scaleFactor) {
        if (scaleFactor == 1.0d) {
            return image;
        }
        else {
            AffineTransform at = new AffineTransform();
            at.scale(scaleFactor, scaleFactor);
            AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            return atop.filter(image, null);
        }
    }

}
