package com.project.zombiesurvivor.observer;

public class UIObserver implements Observer{
    private int score = 0;
    private int health = 100;
    private int wave = 1;

    @Override
    public void onNotify(GameEvent event, Object data) {
        switch (event) {
            case SCORE_CHANGED:
                score = (int) data;
                break;
            case HEALTH_CHANGED:
                health = (int) data;
                break;
            case WAVE_CHANGED:
                wave = (int) data;
                break;
            case ENEMY_KILLED:
                // Additional kill-specific logic can go here
                break;
            case PLAYER_DIED:
                // Handled by GameScreen directly
                break;
        }
    }

    public int getScore() { return score; }
    public int getHealth() { return health; }
    public int getWave() { return wave; }
}
