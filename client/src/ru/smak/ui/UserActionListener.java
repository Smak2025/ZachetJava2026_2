package ru.smak.ui;

import ru.smak.painting.DPoint;

@FunctionalInterface
public interface UserActionListener {
    void onAction(ActionType type, DPoint point);
}
