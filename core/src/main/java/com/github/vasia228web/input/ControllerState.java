package com.github.vasia228web.input;

public interface ControllerState {

    void keyDown(Command command);

    default void keyUp(Command command) {

    }

}
