package com.igorternyuk.tanks.resourcemanager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author igor
 */
public class ResourceManager {

    private Map<ImageIdentifier, BufferedImage> images;

    public ResourceManager() {
        this.images = new HashMap<>();
    }

    public boolean loadImage(ImageIdentifier identifier, String pathToImage) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(
                    this.getClass().getResourceAsStream(pathToImage));
        } catch (IOException ex) {
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        if (image != null) {
            this.images.put(identifier, image);
            return true;
        } else {
            return false;
        }
    }

    public BufferedImage getImage(ImageIdentifier identifier) {
        return this.images.get(identifier);
    }

    public void unloadImage(ImageIdentifier identifier) {
        this.images.remove(identifier);
    }
}
