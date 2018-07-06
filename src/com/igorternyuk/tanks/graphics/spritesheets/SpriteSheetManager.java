package com.igorternyuk.tanks.graphics.spritesheets;

import com.igorternyuk.tanks.graphics.images.TextureAtlas;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author igor
 */
public class SpriteSheetManager {

    private Map<SpriteSheetIdentifier, BufferedImage> spriteSheets =
            new HashMap<>();

    public SpriteSheetManager() {

    }

    public void put(SpriteSheetIdentifier identifier, TextureAtlas atlas) {
        this.spriteSheets.put(identifier, atlas.cutOut(identifier.
                getBoundingRect()));
        System.out.println("identifier = " + identifier);
        System.out.println("Bounding rect = " + identifier.getBoundingRect());
    }
    
    public void remove(SpriteSheetIdentifier identifier){
        this.spriteSheets.remove(identifier);
    }
    
    public BufferedImage get(SpriteSheetIdentifier identifier){
        BufferedImage sprite = this.spriteSheets.get(identifier);
        return sprite;
    }
}
