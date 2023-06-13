package bomberman.gui;

import bomberman.game.Handler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.awt.*;

public abstract class State {
    public static Handler handler;
    public static Pane mainPane = new Pane();
    public static Scene scene = new Scene(mainPane, Window.WIDTH, Window.HEIGHT);

    private static State currentState = null;

    public static State getCurrentState() {
        return currentState;
    }

    public static void setCurrentState(State currentState) {
        State.currentState = currentState;
    }

    public abstract void update();

    public abstract void render();
}
