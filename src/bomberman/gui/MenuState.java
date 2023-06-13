package bomberman.gui;

import bomberman.game.Handler;
import bomberman.game.Main;
import bomberman.game.Utilities;
import bomberman.sound.Sound;

import static java.lang.System.exit;

public class MenuState extends State {
    private static final Sound sound = new Sound("game_1");
    public static GameButton[] button = new GameButton[3];

    public MenuState() {
        handler = new Handler(this);
        button[0] = new GameButton("sprites/button_start_normal.png", "sprites/button_start_hover.png");
        button[1] = new GameButton("sprites/button_auto_normal.png", "sprites/button_auto_hover.png");
        button[2] = new GameButton("sprites/button_quit_normal.png", "sprites/button_quit_hover.png");
        sound.play();
    }

    public void update() {
        createMainMenuUI();
        setUpButton();
    }

    public void setUpButton() {
        button[0].setOnMousePressed(mouseEvent -> {
            button[0].flip();
            createNewGame();
        });

        button[1].setOnMousePressed(event -> {
            button[1].flip();
            createNewGame2();
        });

        button[2].setOnMousePressed(event -> {
            button[2].flip();
            exitGame();
        });
    }

    public void createNewGame() {
        State.mainPane.getChildren().remove(Utilities.home_screen);
        State.mainPane.getChildren().remove(button[0]);
        State.mainPane.getChildren().remove(button[1]);
        State.mainPane.getChildren().remove(button[2]);
        sound.stop();
        Main.state = new GameState(1, false);
        State.setCurrentState(Main.state);
        State.getCurrentState().update();
        State.getCurrentState().render();
    }

    public void createNewGame2() {
        State.mainPane.getChildren().remove(Utilities.home_screen);
        State.mainPane.getChildren().remove(button[0]);
        State.mainPane.getChildren().remove(button[1]);
        State.mainPane.getChildren().remove(button[2]);
        sound.stop();
        Main.state = new GameState(1, true);
        State.setCurrentState(Main.state);
        State.getCurrentState().update();
        State.getCurrentState().render();
    }

    public void exitGame() {
        exit(0);
    }

    private void createMainMenuUI() {
        mainPane.getChildren().removeAll(button);
        for (int i = 0; i < button.length; ++i) {
            button[i].setPrefSize(100, 50);
            button[i].relocate(Main.WIDTH / 2, Main.HEIGHT / 2 + i * 150);
            button[i].setVisible(true);
        }
        mainPane.getChildren().addAll(button);
    }

    @Override
    public void render() {
        //button[0].fadeIn(1300);
    }
}
