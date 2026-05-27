package com.project.zombiesurvivor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.project.zombiesurvivor.Main;

public class GameOverScreen implements Screen {

    private final Main game;
    private final int finalScore;

    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;
    private final GlyphLayout layout;

    private float blinkTimer = 0f;
    private boolean showPrompt = true;

    public GameOverScreen(Main game, int finalScore) {
        this.game       = game;
        this.finalScore = finalScore;

        titleFont = new BitmapFont();
        bodyFont  = new BitmapFont();
        layout    = new GlyphLayout();

        titleFont.getData().setScale(5f);
        titleFont.setColor(Color.RED);

        bodyFont.getData().setScale(2f);
        bodyFont.setColor(Color.WHITE);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.04f, 0.0f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        blinkTimer += delta;
        if (blinkTimer >= 0.55f) {
            showPrompt = !showPrompt;
            blinkTimer = 0f;
        }

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        game.batch.begin();

        // GAME OVER title
        layout.setText(titleFont, "GAME OVER");
        titleFont.draw(game.batch, layout, (w - layout.width) / 2f, h * 0.72f);

        // Final score
        layout.setText(bodyFont, "Final Score: " + finalScore);
        bodyFont.setColor(new Color(1f, 0.85f, 0.2f, 1f));
        bodyFont.draw(game.batch, layout, (w - layout.width) / 2f, h * 0.53f);

        // Blink restart prompt
        if (showPrompt) {
            layout.setText(bodyFont, "Press ENTER to play again   |   ESC to quit");
            bodyFont.setColor(Color.WHITE);
            bodyFont.getData().setScale(1.5f);
            bodyFont.draw(game.batch, layout, (w - layout.width) / 2f, h * 0.34f);
            bodyFont.getData().setScale(2f);
        }

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
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
