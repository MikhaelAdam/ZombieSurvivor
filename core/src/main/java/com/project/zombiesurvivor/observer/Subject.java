package com.project.zombiesurvivor.observer;

public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(GameEvent event, Object data);
}
