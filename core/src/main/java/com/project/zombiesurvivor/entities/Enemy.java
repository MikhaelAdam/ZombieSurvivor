package com.project.zombiesurvivor.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.project.zombiesurvivor.singleton.GameManager;

public class Enemy {

    private static final float RADIUS         = 13f;
    private static final float DAMAGE_COOLDOWN = 0.8f;

    private final Vector2 position;
    private final float speed;
    private boolean alive = true;
    private float damageCooldown = 0f;

    public Enemy(float x, float y, float speed) {
        this.position = new Vector2(x, y);
        this.speed    = speed;
    }

    public void update(float delta, float targetX, float targetY) {
        Vector2 dir = new Vector2(targetX - position.x, targetY - position.y).nor();
        position.x += dir.x * speed * delta;
        position.y += dir.y * speed * delta;
        if (damageCooldown > 0) damageCooldown -= delta;
    }

    public void render(SpriteBatch batch) {
        if (!alive) return;
        int size = (int)(RADIUS * 2);
        batch.draw(GameManager.getInstance().getEnemyTexture(),
            position.x - RADIUS, position.y - RADIUS, size, size);
    }

    /** Returns true if this enemy can deal damage right now. */
    public boolean tryDamage() {
        if (damageCooldown <= 0f) {
            damageCooldown = DAMAGE_COOLDOWN;
            return true;
        }
        return false;
    }

    public Circle getBounds()  { return new Circle(position.x, position.y, RADIUS); }
    public boolean isAlive()   { return alive; }
    public void destroy()      { alive = false; }
    public float getX()        { return position.x; }
    public float getY()        { return position.y; }
}
