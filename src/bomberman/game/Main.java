package bomberman.game;

import bomberman.gui.MenuState;
import bomberman.gui.State;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class Main extends Application {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public static State state;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        state = new MenuState();
        State.setCurrentState(state);
        Utilities.home_screen.setFitHeight(HEIGHT);
        Utilities.home_screen.setFitWidth(WIDTH);
        State.mainPane.getChildren().setAll(Utilities.home_screen);
        State.getCurrentState().update();
        State.getCurrentState().render();

        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setScene(State.scene);
        stage.setTitle("my-game");
        stage.setFullScreen(true);
        stage.show();

        new AnimationTimer() {
            long previousTime = 0;

            @Override
            public void handle(long currentTime) {
                State.getCurrentState().update();
                State.getCurrentState().render();
                if (previousTime == 0) {
                    previousTime = currentTime;
                    return;
                }

                float secondsElapsed = (currentTime - previousTime) / 1e9f;

                if (secondsElapsed >= 0.01f) {
                    State.getCurrentState().update();
                    State.getCurrentState().render();
                }
            }
        }.start();
    }
}
