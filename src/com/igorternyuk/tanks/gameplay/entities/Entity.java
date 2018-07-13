package com.igorternyuk.tanks.gameplay.entities;

import com.igorternyuk.tanks.gameplay.entities.explosion.Explosion;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
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
    protected double absX, absY;
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

    public void attachChild(Entity child) {
        this.children.add(child);
    }

    public void detachChild(Entity child) {
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

    public void setAbsolutePosition(double x, double y){
        this.absX = x;
        this.absY = y;
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
        boolean colided = !(right() < other.left()
                || left() > other.right()
                || top() > other.bottom()
                || bottom() < other.top());
        if(colided){
            System.out.println("this.right() = " + right() + " other.left() = " + other.left());
            System.out.println("this.left() = " + left() + " other.right() = " + other.right());
            System.out.println("this.top() = " + top() + " other.bottom() = " + other.bottom());
            System.out.println("this.bottom() = " + bottom() + " other.top() = " + other.top());
            System.out.println("this.width = " + this.getWidth());
            System.out.println("this.height = " + this.getHeight());
            System.out.println("other.width = " + other.getWidth());
            System.out.println("other.height = " + other.getHeight());
        }
        return colided;
    }

    protected void fixBounds() {
        if (left() < 0) {
            this.x = 0;
        }
        if (right() > this.level.getMapWidth()) {
            this.x = this.level.getMapWidth() - getWidth();
        }
        if (top() < 0) {
            this.y = 0;
        }
        if (bottom() > this.level.getMapHeight()) {
            this.y = this.level.getMapHeight() - getHeight();
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

    public void update(KeyboardState keyboardState, double frameTime) {
        this.children.forEach(child -> {
            child.setPosition(this.x, this.y);
            child.update(keyboardState, frameTime);
        });
    }

    public void draw(Graphics2D g) {
        this.children.forEach(child -> {
            child.draw(g);
        });
    }
}
