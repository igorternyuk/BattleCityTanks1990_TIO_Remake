package com.igorternyuk.tanks.gameplay.entities;

import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
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
    protected int health = 100;
    protected boolean blinking = false;
    protected double blinkTimer;
    protected double blinkPeriod;
    protected double blinkingTimer;
    protected double blinkingDuration;
    protected boolean needToDraw = true;
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
    
    public void attachChild(Entity child){
        this.children.add(child);
    }
    
    public void detachChild(Entity child){
        this.children.remove(child);
    }
    
    public boolean hasChild(Entity child){
        return this.children.contains(child);
    }
    
    public List<Entity> getChildren(){
        return this.children;
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

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double left() {
        return this.x;
    }

    public double top() {
        return this.y;
    }

    public double right() {
        return this.x + getWidth();
    }

    public double bottom() {
        return this.y + getHeight();
    }

    public void startBlinking(double blinkPeriod, double duration) {
        this.blinkTimer = 0;
        this.blinkingTimer = 0;
        this.blinkPeriod = blinkPeriod;
        this.blinkingDuration = duration;
        this.blinking = true;
    }
    
    public void startInfiniteBlinking(double blinkPeriod){
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

    protected void fixBounds() {
        if (this.x < 0) {
            this.x = 0;
        }
        if (this.x > this.level.getMapWidth()) {
            this.x = this.level.getMapWidth();
        }
        if (this.y < 0) {
            this.y = 0;
        }
        if (this.y > this.level.getMapHeight()) {
            this.y = this.level.getMapHeight();
        }
    }

    protected boolean isOutOfBounds() {
        return right() < 0
                || left() > this.level.getMapWidth()
                || bottom() < 0
                || top() > this.level.getMapHeight();
    }

    protected void move(double frameTime) {
        this.x += this.speed * this.direction.getVx() * frameTime;
        this.y += this.speed * this.direction.getVy() * frameTime;
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

    public void update(KeyboardState keyboardState, double frameTime){
        this.children.forEach(e -> e.update(keyboardState, frameTime));
    }

    public void draw(Graphics2D g){
        this.children.forEach(e -> {
            e.draw(g);
        });
    }
}
