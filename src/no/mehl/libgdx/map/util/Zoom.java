package no.mehl.libgdx.map.util;

import com.badlogic.gdx.math.Vector2;

public class Zoom {

    private int level;
    private Vector2 cameraPos;

    public Zoom(int level, Vector2 cameraPos) {
        this.level = level;
        this.cameraPos =cameraPos;
    }

    public int getLevel() {
        return this.level;
    }

    public Vector2 getCameraPos() {
        return this.cameraPos;
    }

}


