package bomberman.graphics.bomb;

import bomberman.game.Handler;
import bomberman.game.Utilities;
import bomberman.graphics.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import java.util.LinkedList;

public class Explosion extends GameObject {
    private Handler handler;
    private long duration;
    private int oX;
    private int oY;
    private long prev = System.currentTimeMillis();
    private int currentAnimation = 0;
    private LinkedList<WritableImage> explosion = new LinkedList<>();

    public Explosion(Handler handler, long duration, LinkedList<WritableImage> explosion, int oX, int oY) {
        this.handler = handler;
        this.duration = duration;
        this.explosion = explosion;
        this.oX = oX;
        this.oY = oY;
        this.x = handler.getLevel().getOffsetX() + oX * Utilities.TILE_WIDTH;
        this.y = handler.getLevel().getOffsetY() + oY * Utilities.TILE_HEIGHT;
        this.width = Utilities.TILE_WIDTH;
        this.height = Utilities.TILE_HEIGHT;
        setBounds();
    }

    @Override
    public void update() {
        long cur = System.currentTimeMillis();
        if (cur > prev + duration) {
            if (currentAnimation == explosion.size()) {
                handler.getLevel().getEntityManager().remove(this);
                return;
            }
            texture = explosion.get(currentAnimation);
            currentAnimation += 1;
            prev = cur;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        this.width = this.height = 50;
        gc.drawImage(this.texture, this.x, this.y, this.width, this.height);
    }
}
