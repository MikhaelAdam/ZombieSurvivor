package com.project.zombiesurvivor.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.project.zombiesurvivor.singleton.GameManager;

public class Bullet {
    private static final float SPEED  = 520f;
    private static final float RADIUS = 4f;

    private final Vector2 position;
    private final Vector2 velocity;
    private boolean alive = true;

    public Bullet(float x, float y, float dirX, float dirY) {
        position = new Vector2(x, y);
        Vector2 dir = new Vector2(dirX, dirY).nor();
        velocity = dir.scl(SPEED);
    }

    public void update(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
    }

    public void render(SpriteBatch batch) {
        if (!alive) return;
        int size = (int)(RADIUS * 2);
        batch.draw(GameManager.getInstance().getBulletTexture(),
            position.x - RADIUS, position.y - RADIUS, size, size);
    }

    public Circle getBounds() {
        return new Circle(position.x, position.y, RADIUS);
    }

    public boolean isAlive()          { return alive; }
    public void destroy()             { alive = false; }
    public float getX()               { return position.x; }
    public float getY()               { return position.y; }
}
