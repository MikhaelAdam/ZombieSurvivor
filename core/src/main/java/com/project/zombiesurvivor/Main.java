package com.project.zombiesurvivor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.project.zombiesurvivor.singleton.GameManager;
import com.project.zombiesurvivor.screens.MenuScreen;

public class Main extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        GameManager.getInstance().init(this);
        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }
    @Override
    public void dispose() {
        batch.dispose();
        GameManager.getInstance().dispose();
    }
}
