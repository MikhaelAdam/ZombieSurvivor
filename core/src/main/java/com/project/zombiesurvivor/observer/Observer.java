package com.project.zombiesurvivor.observer;

public interface Observer {
    void onNotify(GameEvent event, Object data);
}
