package com.project.zombiesurvivor.factory;

import com.badlogic.gdx.math.MathUtils;
import com.project.zombiesurvivor.entities.Enemy;

public class EnemyFactory {

    private static final float BASE_SPEED   = 75f;
    private static final float SPEED_SCALE  = 12f;  // extra speed per wave
    private static final float MAX_SPEED    = 200f;

    /**
     * Spawns an enemy at a random edge of the world.
     *
     * @param worldW  world width  (pixels)
     * @param worldH  world height (pixels)
     * @param wave    current wave number (increases speed)
     */
    public Enemy createEnemy(float worldW, float worldH, int wave) {
        float speed = Math.min(BASE_SPEED + (wave - 1) * SPEED_SCALE, MAX_SPEED);

        // Pick a random edge: 0=top, 1=bottom, 2=left, 3=right
        int edge = MathUtils.random(3);
        float x, y;
        float margin = 30f;

        switch (edge) {
            case 0:  // top
                x = MathUtils.random(0f, worldW);
                y = worldH + margin;
                break;
            case 1:  // bottom
                x = MathUtils.random(0f, worldW);
                y = -margin;
                break;
            case 2:  // left
                x = -margin;
                y = MathUtils.random(0f, worldH);
                break;
            default: // right
                x = worldW + margin;
                y = MathUtils.random(0f, worldH);
                break;
        }

        return new Enemy(x, y, speed);
    }
}
