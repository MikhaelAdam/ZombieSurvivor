package com.project.zombiesurvivor.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.project.zombiesurvivor.singleton.GameManager;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private static final float SPEED         = 220f;
    private static final float RADIUS        = 14f;
    private static final float SHOOT_COOLDOWN = 0.18f;

    private final Vector2 position;
    private float shootTimer = 0f;
    private final List<Bullet> bullets = new ArrayList<>();

    private final float worldW;
    private final float worldH;

    public Player(float startX, float startY, float worldW, float worldH) {
        position = new Vector2(startX, startY);
        this.worldW = worldW;
        this.worldH = worldH;
    }

    public void update(float delta, Camera camera) {
        handleMovement(delta);
        shootTimer -= delta;
        if (Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT) && shootTimer <= 0f) {
            shoot(camera);
            shootTimer = SHOOT_COOLDOWN;
        }
        for (Bullet b : bullets) b.update(delta);
        bullets.removeIf(b -> !b.isAlive() || isOffscreen(b.getX(), b.getY()));
    }

    private void handleMovement(float delta) {
        float dx = 0, dy = 0;
        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP))    dy += 1;
        if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN))  dy -= 1;
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT))  dx -= 1;
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) dx += 1;

        if (dx != 0 || dy != 0) {
            Vector2 dir = new Vector2(dx, dy).nor().scl(SPEED * delta);
            position.x = Math.max(RADIUS, Math.min(worldW - RADIUS, position.x + dir.x));
            position.y = Math.max(RADIUS, Math.min(worldH - RADIUS, position.y + dir.y));
        }
    }

    private void shoot(Camera camera) {
        Vector3 mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        float dx = mouse.x - position.x;
        float dy = mouse.y - position.y;
        bullets.add(new Bullet(position.x, position.y, dx, dy));
    }

    private boolean isOffscreen(float x, float y) {
        float margin = 20f;
        return x < -margin || x > worldW + margin || y < -margin || y > worldH + margin;
    }

    public void render(SpriteBatch batch) {
        for (Bullet b : bullets) b.render(batch);
        int size = (int)(RADIUS * 2);
        batch.draw(GameManager.getInstance().getPlayerTexture(),
            position.x - RADIUS, position.y - RADIUS, size, size);
    }

    public Circle getBounds()      { return new Circle(position.x, position.y, RADIUS); }
    public List<Bullet> getBullets() { return bullets; }
    public float getX()            { return position.x; }
    public float getY()            { return position.y; }
}
