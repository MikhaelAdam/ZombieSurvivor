package com.project.zombiesurvivor.singleton;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.project.zombiesurvivor.observer.GameEvent;
import com.project.zombiesurvivor.observer.Observer;
import com.project.zombiesurvivor.observer.Subject;
import com.project.zombiesurvivor.Main;

import java.util.ArrayList;
import java.util.List;

public class GameManager implements Subject {

    // ── Singleton ──────────────────────────────────────────────────────────────
    private static GameManager instance;

    private GameManager() {}

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // ── State ──────────────────────────────────────────────────────────────────
    private Main game;
    private int score = 0;
    private int health = 100;
    private int wave = 1;
    private int killCount = 0;
    private boolean gameOver = false;

    // ── Shared textures (generated programmatically – no external assets) ──────
    private Texture playerTexture;
    private Texture enemyTexture;
    private Texture bulletTexture;
    private Texture backgroundTexture;

    // ── Observers ──────────────────────────────────────────────────────────────
    private final List<Observer> observers = new ArrayList<>();

    // ── Init ───────────────────────────────────────────────────────────────────
    public void init(Main game) {
        this.game = game;
        generateTextures();
    }

    private void generateTextures() {
        playerTexture    = createCircleTexture(28, Color.CYAN);
        enemyTexture     = createCircleTexture(26, Color.RED);
        bulletTexture    = createCircleTexture(8,  Color.YELLOW);
        backgroundTexture = createSolidTexture(1, 1, new Color(0.08f, 0.10f, 0.08f, 1f));
    }

    /** Creates a filled circle on a transparent background. */
    private Texture createCircleTexture(int diameter, Color color) {
        Pixmap pixmap = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.CLEAR);
        pixmap.fill();
        pixmap.setColor(color);
        pixmap.fillCircle(diameter / 2, diameter / 2, diameter / 2 - 1);
        // Darker outline
        pixmap.setColor(color.r * 0.5f, color.g * 0.5f, color.b * 0.5f, 1f);
        pixmap.drawCircle(diameter / 2, diameter / 2, diameter / 2 - 1);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createSolidTexture(int w, int h, Color color) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    // ── Game state helpers ─────────────────────────────────────────────────────
    public void resetGame() {
        score = 0;
        health = 100;
        wave = 1;
        killCount = 0;
        gameOver = false;
    }

    public void addScore(int points) {
        score += points;
        notifyObservers(GameEvent.SCORE_CHANGED, score);
    }

    public void damagePlayer(int amount) {
        health = Math.max(0, health - amount);
        notifyObservers(GameEvent.HEALTH_CHANGED, health);
        if (health <= 0 && !gameOver) {
            gameOver = true;
            notifyObservers(GameEvent.PLAYER_DIED, score);
        }
    }

    public void registerKill() {
        killCount++;
        notifyObservers(GameEvent.ENEMY_KILLED, killCount);
        int newWave = (killCount / 10) + 1;
        if (newWave != wave) {
            wave = newWave;
            notifyObservers(GameEvent.WAVE_CHANGED, wave);
        }
    }

    // ── Subject implementation ─────────────────────────────────────────────────
    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(GameEvent event, Object data) {
        for (Observer o : observers) o.onNotify(event, data);
    }

    // ── Dispose ────────────────────────────────────────────────────────────────
    public void dispose() {
        if (playerTexture    != null) playerTexture.dispose();
        if (enemyTexture     != null) enemyTexture.dispose();
        if (bulletTexture    != null) bulletTexture.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public Main getGame()          { return game; }
    public int getScore()                        { return score; }
    public int getHealth()                       { return health; }
    public int getWave()                         { return wave; }
    public boolean isGameOver()                  { return gameOver; }
    public Texture getPlayerTexture()            { return playerTexture; }
    public Texture getEnemyTexture()             { return enemyTexture; }
    public Texture getBulletTexture()            { return bulletTexture; }
    public Texture getBackgroundTexture()        { return backgroundTexture; }
}
