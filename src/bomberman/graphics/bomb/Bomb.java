package bomberman.graphics.bomb;

import bomberman.game.Handler;
import bomberman.game.Utilities;
import bomberman.graphics.GameObject;
import bomberman.sound.Sound;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import java.util.LinkedList;

public class Bomb extends GameObject {
    private Handler handler;
    private long prev = System.currentTimeMillis();
    private int currentAnimation = 0;
    private int radius;
    private int oX;
    private int oY;
    private LinkedList<WritableImage> bomb = new LinkedList<>();
    private LinkedList<WritableImage> center = new LinkedList<>();
    private LinkedList<WritableImage> up_1 = new LinkedList<>();
    private LinkedList<WritableImage> up_2 = new LinkedList<>();
    private LinkedList<WritableImage> down_1 = new LinkedList<>();
    private LinkedList<WritableImage> down_2 = new LinkedList<>();
    private LinkedList<WritableImage> left_1 = new LinkedList<>();
    private LinkedList<WritableImage> left_2 = new LinkedList<>();
    private LinkedList<WritableImage> right_1 = new LinkedList<>();
    private LinkedList<WritableImage> right_2 = new LinkedList<>();
    private LinkedList<LinkedList<WritableImage>> all = new LinkedList<>();


    public Bomb() {
        radius = 1;
    }

    public Bomb(Handler handler, int radius, int oX, int oY) {
        this.handler = handler;
        this.radius = radius;
        this.oX = oX;
        this.oY = oY;
        this.prev = 0;
        this.x = handler.getLevel().getOffsetX() + oX * Utilities.TILE_HEIGHT;
        this.y = handler.getLevel().getOffsetY() + oY * Utilities.TILE_WIDTH;
        this.width = this.height = 50;
        initTexture();
    }

    private void initTexture() {
        for (int i = 1; i <= 4; ++i) bomb.add(Utilities.loadWI("bomb1_" + i));
        for (int i = 1; i <= 4; ++i) center.add(Utilities.loadWI("bomb_exploded_" + i));
        for (int i = 1; i <= 4; ++i) up_1.add(Utilities.loadWI("explosion" + i + "_up_1"));
        for (int i = 1; i <= 4; ++i) up_2.add(Utilities.loadWI("explosion" + i + "_up_2"));
        for (int i = 1; i <= 4; ++i) down_1.add(Utilities.loadWI("explosion" + i + "_down_1"));
        for (int i = 1; i <= 4; ++i) down_2.add(Utilities.loadWI("explosion" + i + "_down_2"));
        for (int i = 1; i <= 4; ++i) left_1.add(Utilities.loadWI("explosion" + i + "_left_1"));
        for (int i = 1; i <= 4; ++i) left_2.add(Utilities.loadWI("explosion" + i + "_left_2"));
        for (int i = 1; i <= 4; ++i) right_1.add(Utilities.loadWI("explosion" + i + "_right_1"));
        for (int i = 1; i <= 4; ++i) right_2.add(Utilities.loadWI("explosion" + i + "_right_2"));

        all.add(up_1);
        all.add(up_2);
        all.add(right_1);
        all.add(right_2);
        all.add(down_1);
        all.add(down_2);
        all.add(left_1);
        all.add(left_2);
    }

    int[] D = {-1, 0, 1, 0};
    int[] C = {0, 1, 0, -1};

    @Override
    public void update() {
        long cur = System.currentTimeMillis();
        if (cur > prev + Utilities.BOMB_EXPLOSION_DELAY) {
            if (currentAnimation == bomb.size()) {
                Explosion c = new Explosion(handler, 100, new LinkedList<>(center), oX, oY);
                handler.getLevel().getPlayer().releaseBomb();
                Sound sound = new Sound("bom_exploded");
                sound.play();
                for (int i = 0; i < 4; ++i) {
                    int t = 0;
                    while (t <= radius && handler.getLevel().getMap()[oY + D[i] * t][oX + C[i] * t].isWalkable()) t++;
                    if (t <= radius && this.handler.getLevel().getMap()[oY + D[i] * t][oX + C[i] * t].isBreakable())
                        this.handler.getLevel().getMap()[oY + D[i] * t][oX + C[i] * t].setBreaking(true);
                    t -= 1;
                    for (int j = 1; j <= t; ++j) {
                        Explosion xp = new Explosion(handler, 100, new LinkedList<>(all.get(i * 2 + (j == t ? 1 : 0))), oX + C[i] * j, oY + D[i] * j);
                        handler.getLevel().getEntityManager().add(xp);
                    }
                }
                handler.getLevel().getEntityManager().add(c);
                handler.getLevel().getEntityManager().remove(this);
            } else {
                texture = bomb.get(currentAnimation);
                currentAnimation += 1;
                prev = cur;
            }
        }
    }

    public int getRadius() {
        return radius;
    }

    public int getoX() {
        return oX;
    }

    public int getoY() {
        return oY;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(this.texture, this.x, this.y, this.width, this.height);
    }
}
