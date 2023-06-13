package bomberman.gui;

import bomberman.game.Handler;
import bomberman.game.Main;
import bomberman.game.Utilities;
import bomberman.graphics.Bomber;
import bomberman.graphics.Level;
import bomberman.sound.Sound;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;

public class GameState extends State {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Bomber player;
    private final Level level;
    private Sound sound = new Sound("game_2");
    private final int current_level;
    private long prev = 0;
    private boolean pendingVictory = false;
    private boolean tu_choi;

    public GameState(int current, boolean tu_choi) {
        this.current_level = current;
        canvas = new Canvas(Main.WIDTH, Main.HEIGHT);
        handler = new Handler(this, canvas);
        level = new Level(handler, "./resources/levels/Level" + current + ".txt");
        handler.setLevel(level);
        player = new Bomber(handler,
                level.getPlayerSpawnX() * Utilities.TILE_HEIGHT + level.getOffsetX(),
                level.getPlayerSpawnY() * Utilities.TILE_WIDTH + level.getOffsetY());
        player.setAIPlay(tu_choi);
        level.setPlayer(this.player);
        this.tu_choi = tu_choi;
        player.setBoundaries(5, 5, 30, 40);
        gc = canvas.getGraphicsContext2D();
        State.mainPane.getChildren().add(canvas);
        sound.play();
    }

    @Override
    public void update() {
        if (level.isFinished()) {
            nextLevel();
            return;
        }
        level.update();
        if (!player.isDeceased()) {
            player.update();
            if (!player.isAIPlay()) {
                scene.setOnKeyPressed(event -> player.handleKeyPressed(event));
                scene.setOnKeyReleased(event -> player.handleKeyReleased(event));
            }
        } else {
            gc.clearRect(0, 0, Main.WIDTH, Main.HEIGHT);
            State.mainPane.getChildren().add(Utilities.home_screen);
            Main.state = new MenuState();
            sound.stop();
            State.setCurrentState(Main.state);
            State.getCurrentState().update();
            State.getCurrentState().render();
        }
    }

    public void nextLevel() {
        if (current_level == Utilities.TOTAL_LEVEL) {
            gc.clearRect(0, 0, Main.WIDTH, Main.HEIGHT);
            State.mainPane.getChildren().add(Utilities.win_screen);
            Main.state = new MenuState();
            sound.stop();
            State.setCurrentState(Main.state);
            State.getCurrentState().update();
            State.getCurrentState().render();
        } else {
            long cur = System.currentTimeMillis();
            if (!pendingVictory) {
                prev = cur;
                sound.stop();
                sound = new Sound("win");
                sound.play();
                pendingVictory = true;
            }
            if (cur <= prev + 7000)
                return;
            gc.clearRect(0, 0, Main.WIDTH, Main.HEIGHT);
            sound.stop();
            Main.state = new GameState(current_level + 1, tu_choi);
            State.setCurrentState(Main.state);
            State.getCurrentState().update();
            State.getCurrentState().render();
        }
    }

    int go = 0;

    @Override
    public void render() {
        gc.clearRect(0, 0, Main.WIDTH, Main.HEIGHT);
        level.render(gc);
        if (!player.isDeceased())
            player.render(gc);
    }
}
