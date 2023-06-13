package bomberman.game;

import bomberman.graphics.GameObject;
import bomberman.graphics.Level;
import bomberman.gui.State;
import javafx.scene.canvas.Canvas;

public class Handler {
    private State state;
    private Canvas canvas;
    private Level level;

    public Handler(State state, Canvas canvas) {
        this.state = state;
        this.canvas = canvas;
    }

    public Handler(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }
}
