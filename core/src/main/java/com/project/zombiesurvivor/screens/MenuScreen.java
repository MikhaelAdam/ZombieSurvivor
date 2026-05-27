package com.project.zombiesurvivor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.project.zombiesurvivor.Main;

public class MenuScreen implements Screen {

    private final Main game;
    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;
    private final GlyphLayout layout;
    private float blinkTimer = 0f;
    private boolean showPrompt = true;

    public MenuScreen(Main game) {
        this.game    = game;
        titleFont    = new BitmapFont();
        bodyFont     = new BitmapFont();
        layout       = new GlyphLayout();

        titleFont.getData().setScale(4f);
        titleFont.setColor(Color.RED);

        bodyFont.getData().setScale(1.6f);
        bodyFont.setColor(Color.LIGHT_GRAY);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        blinkTimer += delta;
        if (blinkTimer >= 0.55f) {
            showPrompt = !showPrompt;
            blinkTimer = 0f;
        }

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        game.batch.begin();

        // Title
        layout.setText(titleFont, "ZOMBIE SURVIVOR");
        titleFont.draw(game.batch, layout, (w - layout.width) / 2f, h * 0.72f);

        // Subtitle
        layout.setText(bodyFont, "Survive as long as you can!");
        bodyFont.setColor(Color.GRAY);
        bodyFont.draw(game.batch, layout, (w - layout.width) / 2f, h * 0.55f);

        // Controls hint
        layout.setText(bodyFont, "WASD to move   |   Left click to shoot");
        bodyFont.setColor(new Color(0.5f, 0.8f, 0.5f, 1f));
        bodyFont.draw(game.batch, layout, (w - layout.width) / 2f, h * 0.44f);

        // Blinking start prompt
        if (showPrompt) {
            layout.setText(bodyFont, "Press ENTER or SPACE to start");
            bodyFont.setColor(Color.WHITE);
            bodyFont.draw(game.batch, layout, (w - layout.width) / 2f, h * 0.3f);
        }

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        titleFont.dispose();
        bodyFont.dispose();
    }
}
