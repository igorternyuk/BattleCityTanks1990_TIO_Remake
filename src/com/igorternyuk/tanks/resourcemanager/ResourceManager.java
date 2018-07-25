package com.igorternyuk.tanks.resourcemanager;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
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

    private static ResourceManager instance;

    public static synchronized ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    private Map<ImageIdentifier, BufferedImage> images;
    private Map<FontIdentifier, Font> fonts;

    private ResourceManager() {
        this.images = new HashMap<>();
        this.fonts = new HashMap<>();
    }

    public void loadImage(ImageIdentifier identifier, String pathToImage) {
        BufferedImage image;
        try {
            image = ImageIO.read(
                    this.getClass().getResourceAsStream(pathToImage));
            this.images.put(identifier, image);
        } catch (IOException ex) {
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE,
                    null, ex);
            throw new RuntimeException("Image " + pathToImage + " not found");
        }
    }

    public BufferedImage getImage(ImageIdentifier identifier) {
        if (!this.images.containsKey(identifier)) {
            throw new RuntimeException("Image " + identifier + " not loaded");
        }
        return this.images.get(identifier);
    }

    public void unloadImage(ImageIdentifier identifier) {
        this.images.remove(identifier);
    }

    public void loadFont(FontIdentifier identifier, String path) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, getClass().
                    getResourceAsStream(path));
            String family = font.getFamily();
            System.out.println("family = " + family);
            System.out.println("Size = " + font.getSize());
            this.fonts.put(identifier, font);
            GraphicsEnvironment ge = GraphicsEnvironment.
                    getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (FontFormatException ex) {
            Logger.getLogger(ResourceManager.class.getName()).
                    log(Level.SEVERE, null, ex);
            throw new RuntimeException("Wrong font format");
        } catch (IOException ex) {
            Logger.getLogger(ResourceManager.class.getName()).
                    log(Level.SEVERE, null, ex);
            throw new RuntimeException("Could not load font " + path);
        }
    }

    public Font getFont(FontIdentifier identifier) {
        if (!this.fonts.containsKey(identifier)) {
            throw new RuntimeException("Font " + identifier + " not loaded");
        }
        return this.fonts.get(identifier);
    }

    public void unloadFont(FontIdentifier identifier) {
        this.fonts.remove(identifier);
    }
}
