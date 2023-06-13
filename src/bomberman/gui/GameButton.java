package bomberman.gui;

import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import javax.swing.*;
import java.util.Objects;

public class GameButton extends Button {
    private final String normal;
    private final String pressed;
    private boolean isNormal;

    public GameButton(String normal, String pressed) {
        this.normal = normal;
        this.pressed = pressed;
        this.isNormal = false;
        setGraphic(new ImageView(new Image(normal)));
        setStyle("-fx-background-color: transparent;");
    }

    public void flip() {
        isNormal ^= true;
        setGraphic(new ImageView(new Image(isNormal ? normal : pressed)));
    }

    public void fadeIn(int milliSeconds) {
        float tmp;
        if (this.isDisabled())
            tmp = 0.4f;
        else
            tmp = 0.8f;
        FadeTransition ft = new FadeTransition(Duration.millis(milliSeconds), this);
        ft.setFromValue(0.0);
        ft.setToValue(tmp);
        ft.play();
    }
}
