package com.project.zombiesurvivor.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Circle;
import com.project.zombiesurvivor.Main;
import com.project.zombiesurvivor.entities.Bullet;
import com.project.zombiesurvivor.entities.Enemy;
import com.project.zombiesurvivor.entities.Player;
import com.project.zombiesurvivor.factory.EnemyFactory;
import com.project.zombiesurvivor.singleton.GameManager;
import com.project.zombiesurvivor.observer.GameEvent;
import com.project.zombiesurvivor.observer.Observer;
import com.project.zombiesurvivor.observer.UIObserver;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen, Observer {

    // ── Constants ──────────────────────────────────────────────────────────────
    private static final float WORLD_W          = 960f;
    private static final float WORLD_H          = 640f;
    private static final int   POINTS_PER_KILL  = 10;
    private static final int   DAMAGE_PER_HIT   = 10;
    private static final float BASE_SPAWN_RATE  = 1.8f;   // seconds between spawns
    private static final float MIN_SPAWN_RATE   = 0.35f;

    // ── Dependencies ───────────────────────────────────────────────────────────
    private final Main game;
    private final OrthographicCamera camera;
    private final GameManager        gm;
    private final EnemyFactory       enemyFactory;
    private final UIObserver         uiObserver;

    // ── Entities ───────────────────────────────────────────────────────────────
    private final Player       player;
    private final List<Enemy>  enemies = new ArrayList<>();

    // ── Timers ─────────────────────────────────────────────────────────────────
    private float spawnTimer = 0f;

    // ── HUD ────────────────────────────────────────────────────────────────────
    private final BitmapFont  hudFont;
    private final GlyphLayout layout;

    // ── Transition guard ───────────────────────────────────────────────────────
    private boolean transitioning = false;

    public GameScreen(Main game) {
        this.game = game;

        // Reset central state
        gm = GameManager.getInstance();
        gm.resetGame();

        // Register observers
        uiObserver = new UIObserver();
        gm.addObserver(uiObserver);
        gm.addObserver(this);   // screen itself listens for PLAYER_DIED

        enemyFactory = new EnemyFactory();

        // Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_W, WORLD_H);

        // Player starts at centre
        player = new Player(WORLD_W / 2f, WORLD_H / 2f, WORLD_W, WORLD_H);

        // HUD font
        hudFont = new BitmapFont();
        hudFont.getData().setScale(1.4f);
        layout  = new GlyphLayout();
    }

    // ── Screen ─────────────────────────────────────────────────────────────────
    @Override
    public void render(float delta) {
        if (transitioning) return;

        update(delta);
        draw();
    }

    private void update(float delta) {
        player.update(delta, camera);

        spawnTimer += delta;
        float spawnRate = Math.max(MIN_SPAWN_RATE, BASE_SPAWN_RATE - (gm.getWave() - 1) * 0.15f);
        if (spawnTimer >= spawnRate) {
            enemies.add(enemyFactory.createEnemy(WORLD_W, WORLD_H, gm.getWave()));
            spawnTimer = 0f;
        }

        for (Enemy enemy : enemies) {
            enemy.update(delta, player.getX(), player.getY());
        }

        handleCollisions();
        enemies.removeIf(e -> !e.isAlive());
    }

    private void handleCollisions() {
        Circle playerBounds = player.getBounds();

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;

            // Bullet vs enemy
            for (Bullet bullet : player.getBullets()) {
                if (bullet.isAlive() && bullet.getBounds().overlaps(enemy.getBounds())) {
                    bullet.destroy();
                    enemy.destroy();
                    gm.addScore(POINTS_PER_KILL);
                    gm.registerKill();
                    break;
                }
            }

            // Enemy vs player
            if (enemy.isAlive() && enemy.getBounds().overlaps(playerBounds)) {
                if (enemy.tryDamage()) {
                    gm.damagePlayer(DAMAGE_PER_HIT);
                }
            }
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0.08f, 0.10f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        drawGrid();
        player.render(game.batch);
        for (Enemy e : enemies) e.render(game.batch);
        game.batch.end();

        drawHUD();
    }

    /** Draws a subtle grid for spatial reference. */
    private void drawGrid() {
        // We draw thin lines using 1×1 pixel stretches via the background texture
        // (a simple approach without ShapeRenderer to keep dependencies minimal)
    }

    private void drawHUD() {
        // Switch to screen-space projection for HUD
        game.batch.setProjectionMatrix(game.batch.getProjectionMatrix().idt()
            .setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        game.batch.begin();

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float pad = 40f;

        // Score
        hudFont.setColor(Color.WHITE);
        hudFont.draw(game.batch, "Score: " + uiObserver.getScore(), pad, sh - pad);

        // Wave
        hudFont.setColor(new Color(1f, 0.7f, 0.2f, 1f));
        layout.setText(hudFont, "Wave: " + uiObserver.getWave());
        hudFont.draw(game.batch, layout, (sw - layout.width) / 2f, sh - pad);

        // Health bar
        drawHealthBar(pad, sh - 36f);

        game.batch.end();
    }

    private void drawHealthBar(float x, float y) {
        int hp        = uiObserver.getHealth();
        float barW    = 180f;
        float barH    = 16f;
        float filled  = barW * (hp / 100f);

        // Background (dark red)
        hudFont.setColor(new Color(0.4f, 0.05f, 0.05f, 1f));
        game.batch.draw(gm.getBackgroundTexture(), x, y, barW, barH);

        // Filled portion
        Color fillColor = hp > 50
            ? new Color(0.2f, 0.85f, 0.2f, 1f)
            : hp > 25 ? new Color(0.9f, 0.65f, 0.1f, 1f)
              : new Color(0.9f, 0.1f, 0.1f, 1f);
        game.batch.setColor(fillColor);
        game.batch.draw(gm.getBackgroundTexture(), x, y, filled, barH);
        game.batch.setColor(Color.WHITE);

        // Label
        hudFont.setColor(Color.WHITE);
        hudFont.getData().setScale(1.0f);
        hudFont.draw(game.batch, "HP: " + hp, x + barW + 8f, y + barH);
        hudFont.getData().setScale(1.4f);
    }

    // ── Observer ───────────────────────────────────────────────────────────────
    @Override
    public void onNotify(GameEvent event, Object data) {
        if (event == GameEvent.PLAYER_DIED && !transitioning) {
            transitioning = true;
            Gdx.app.postRunnable(() -> {
                game.setScreen(new GameOverScreen(game, (int) data));
                dispose();
            });
        }
    }

    // ── Screen lifecycle ───────────────────────────────────────────────────────
    @Override public void show() {}
    @Override public void resize(int w, int h) { camera.setToOrtho(false, WORLD_W, WORLD_H); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        hudFont.dispose();
        gm.removeObserver(this);
        gm.removeObserver(uiObserver);
    }
}

