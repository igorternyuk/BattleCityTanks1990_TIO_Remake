package com.igorternyuk.tanks.gameplay.entities;

import com.igorternyuk.tanks.gameplay.entities.player.Player;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author igor
 */
public class EntityManager {

    private LevelState level;
    private List<Entity> entities = new ArrayList<>();
    private Map<EntityType, List<Entity>> entitiesByType = new HashMap();
    private Player player;

    public EntityManager(LevelState level) {
        this.level = level;
        for (EntityType type : EntityType.values()) {
            entitiesByType.put(type, new ArrayList<>());
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public void addEntity(Entity entity) {
        if (entity.getEntityType() == EntityType.PLAYER_TANK) {
            this.player = (Player) entity;
        }
        this.entities.add(entity);
        List<Entity> lista = this.entitiesByType.get(entity.getEntityType());
        lista.add(entity);
    }

    public void removeEntity(Entity entity) {
        this.entities.remove(entity);
        List<Entity> lista = this.entitiesByType.get(entity.getEntityType());
        lista.remove(entity);
    }
    
    public void removeEntitiesByType(EntityType... entityTypes){
        this.entities.removeIf(entity -> {
            for(int i = 0; i < entityTypes.length; ++i){
                if(entityTypes[i] == entity.getEntityType()){
                    if(this.entitiesByType.containsKey(entityTypes[i])){
                        this.entitiesByType.get(entityTypes[i]).clear();
                    }
                    return true;
                }
            }
            return false;
        });
    }
    
    public void removeEntitiesExcepts(EntityType... entityTypes){
        this.entities.removeIf(entity -> {
            for(int i = 0; i < entityTypes.length; ++i){
                if(entityTypes[i] == entity.getEntityType()){
                    return false;
                }
            }
            this.entitiesByType.get(entity.getEntityType()).clear();
            return true;
        });
        
    }

    public void removeAllEntities() {
        this.entities.clear();
        this.entitiesByType.keySet().forEach(key -> {
            this.entitiesByType.get(key).clear();
        });
    }

    public int entityCount() {
        return this.entities.size();
    }

    public List<Entity> getEntitiesByType(EntityType entityType) {
        return this.entitiesByType.get(entityType);
    }

    public List<Entity> getAllEntities() {
        return this.entities;
    }

    public void update(KeyboardState keyboardState, double frameTime) {
        //Remove the dead entities
        this.entities.removeIf(e -> !e.isAlive());
        this.entitiesByType.keySet().forEach(key -> {
            this.entitiesByType.get(key).removeIf(e -> !e.isAlive());
        });
        //Update all entitites
        for (int i = this.entities.size() - 1; i >= 0; --i) {
            this.entities.get(i).update(keyboardState, frameTime);
        }
        //System.out.println("entities.size() = " + entities.size());
    }

    public void draw(Graphics2D g) {
        this.entitiesByType.get(EntityType.EAGLE).get(0).draw(g);
        for (int i = this.entities.size() - 1; i >= 0; --i) {
            Entity entity = this.entities.get(i);
            if (entity.getEntityType() != EntityType.EAGLE) {
                entity.draw(g);
            }
        }
    }
}
