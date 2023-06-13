package bomberman.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Utilities {
    public static final Image level = new Image("sprites/screen_home.png");

    public static final ImageView home_screen = new ImageView("sprites/screen_home.png");
    public static final ImageView win_screen = new ImageView("sprites/victory-screen.png");
    public static final Image GRASS = new Image("sprites/grass.png");
    public static final Image GRASS_1 = new Image("sprites/grass_1.png");
    public static final Image WALL = new Image("sprites/wall.png");
    public static final Image CRATES = new Image("sprites/brick.png");
    public static final Image PORTAL = new Image("sprites/portal.png");
    public static final PixelReader grassReader = GRASS.getPixelReader();
    public static final PixelReader grassReader_1 = GRASS_1.getPixelReader();
    public static final PixelReader wallReader = WALL.getPixelReader();
    public static final PixelReader cratesReader = CRATES.getPixelReader();

    public static final int TOTAL_LEVEL = 10;
    public static final int TILE_HEIGHT = 50;
    public static final int TILE_WIDTH = 50;
    public static final int BOMBER_ANIMATION_DELAY = 90;
    public static final int BOMB_EXPLOSION_DELAY = 400;
    public static final int WALL_EXPLOSION_DELAY = 200;

    public static String[][] loadFile(String preset) {
        String[][] result = null;
        try {
            Scanner f = new Scanner(new File(preset));
            int m = f.nextInt();
            int n = f.nextInt();
            result = new String[m][n];
            for (int i = 0; i < m; ++i) for (int j = 0; j < n; ++j) result[i][j] = f.next();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("The path " + preset + " does not exist.");
        }
        return result;
    }

    public static WritableImage loadWI(String path) {
        Image image = new Image("sprites/" + path + ".png");
        return new WritableImage(image.getPixelReader(), 0, 0, (int) image.getWidth(), (int) image.getHeight());
    }
}
