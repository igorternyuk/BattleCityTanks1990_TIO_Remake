package com.igorternyuk.tanks.gameplay.entities;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.explosion.Explosion;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.resourcemanager.AudioIdentifier;
import com.igorternyuk.tanks.resourcemanager.ResourceManager;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author igor
 */
public abstract class Entity {

    protected LevelState level;
    protected EntityType entityType;
    protected double x, y;
    protected double speed;
    protected Direction direction;
    protected boolean moving = false;
    protected int health = 100;
    protected boolean blinking = false;
    protected double blinkTimer;
    protected double blinkPeriod;
    protected double blinkingTimer;
    protected double blinkingDuration;
    protected boolean needToDraw = true;
    protected Entity parent = null;
    protected List<Entity> children = new ArrayList<>();

    public Entity(LevelState level, EntityType type, double x, double y,
            double speed, Direction direction) {
        this.level = level;
        this.entityType = type;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.direction = direction;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }
    
     public double getX() {
        return (this.parent != null) ? (this.parent.getX() + this.x) : this.x;
    }

    public double getY() {
        return (this.parent != null) ? (this.parent.getY() + this.y) : this.y;
    }

    public double left() {
        return getX();
    }

    public double top() {
        return getY();
    }

    public double right() {
        return getX() + getWidth();
    }

    public double bottom() {
        return getY() + getHeight();
    }
    
    public Point getPosition(){
        return new Point((int)getX(), (int)getY());
    }
    
    public Rectangle getBoundingRect(){
        return new Rectangle((int)getX(), (int)getY(), (int)getWidth(),
                (int)getHeight());
    }
    
    public final void setParent(Entity parent){
        this.parent = parent;
    }
    
    public final void removeParent(){
        this.parent = null;
    }
    
    public final boolean hasParent(){
        return this.parent != null;
    }

    public final void attachChild(Entity child) {
        child.setParent(this);
        this.children.add(child);
    }

    public final void detachChild(Entity child) {
        child.removeParent();
        this.children.remove(child);
    }

    public boolean hasChild(Entity child) {
        return this.children.contains(child);
    }

    public List<Entity> getChildren() {
        return this.children;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }
    
    
    public double getSpeed() {
        return this.speed;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public void destroy() {
        this.health = 0;
    }
    
    public void hit(int damage) {
        this.health -= damage;
    }

    public int getHealth() {
        return this.health;
    }
    
    protected void explode(ExplosionType explosionType){
        Explosion explosion = new Explosion(this.level, explosionType,
               this.x, this.y);
        int dx = (getWidth() - explosion.getWidth()) / 2;
        int dy = (getHeight()- explosion.getHeight()) / 2;
        explosion.setPosition(this.x + dx, this.y + dy);
        this.level.getEntityManager().addEntity(explosion);
        ResourceManager.getInstance().getAudio(AudioIdentifier.EXPLOSION).play();
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public abstract int getWidth();

    public abstract int getHeight();
    
    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void startBlinking(double blinkPeriod, double duration) {
        this.blinkTimer = 0;
        this.blinkingTimer = 0;
        this.blinkPeriod = blinkPeriod;
        this.blinkingDuration = duration;
        this.blinking = true;
    }

    public void startInfiniteBlinking(double blinkPeriod) {
        startBlinking(blinkPeriod, -1);
    }

    public void stopBlinking() {
        this.blinking = false;
        this.needToDraw = true;
    }

    public boolean collides(Entity other) {
        return !(right() < other.left()
                || left() > other.right()
                || top() > other.bottom()
                || bottom() < other.top());
    }

    protected boolean fixBounds() {
        boolean boundsFixed = false;
        if (left() < 0) {
            this.x = 0;
            boundsFixed = true;
        }
        if (right() > this.level.getMapWidth()) {
            this.x = this.level.getMapWidth() - getWidth();
            boundsFixed = true;
        }
        if (top() < 0) {
            this.y = 0;
            boundsFixed = true;
        }
        if (bottom() > this.level.getMapHeight()) {
            this.y = this.level.getMapHeight() - getHeight();
            boundsFixed = true;
        }
        return boundsFixed;
    }

    protected boolean isOutOfBounds() {
        return right() < 0
                || left() > this.level.getMapWidth()
                || bottom() < 0
                || top() > this.level.getMapHeight();
    }
    
    public boolean canMoveInDirection(Direction direction){
        double dx = direction.getDx() * this.speed * Game.FRAME_TIME_IN_SECONDS;
        double dy = direction.getDy() * this.speed * Game.FRAME_TIME_IN_SECONDS;
        setPosition(this.x + dx, this.y + dy);
        TileMap tileMap = this.level.getTileMap(); 
        boolean result = !tileMap.hasCollision(this)
                && !tileMap.isOutOfBounds(this);
        setPosition(this.x - dx, this.y - dy);
        return result;
    }
    
    protected void move(double frameTime) {
        this.x += this.speed * this.direction.getDx() * frameTime;
        this.y += this.speed * this.direction.getDy() * frameTime;
    }

    protected void updateBlinkTimer(double frameTime) {
        if (!this.blinking) {
            return;
        }

        this.blinkTimer += frameTime;
        this.blinkingTimer += frameTime;

        if (this.blinkingDuration > 0 && this.blinkingTimer
                >= this.blinkingDuration) {
            this.needToDraw = true;
            this.blinking = false;
            return;
        }

        if (this.blinkTimer >= this.blinkPeriod) {
            this.blinkTimer = 0;
            this.needToDraw = !this.needToDraw;
        }
    }

    public void update(KeyboardState keyboardState, double frameTime) {
        this.children.forEach(child -> {
            child.update(keyboardState, frameTime);
        });
    }

    public void draw(Graphics2D g) {
        this.children.forEach(child -> {
            child.draw(g);
        });
    }
}
