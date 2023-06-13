package bomberman.graphics.enemy;

import bomberman.game.Handler;
import bomberman.game.Utilities;
import bomberman.graphics.bomb.Explosion;
import bomberman.graphics.GameObject;
import bomberman.sound.Sound;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import java.util.LinkedList;

public class Rocket extends Enemy {

    private LinkedList<WritableImage> movingDown = new LinkedList<>();
    private LinkedList<WritableImage> movingUp = new LinkedList<>();
    private LinkedList<WritableImage> movingLeft = new LinkedList<>();
    private LinkedList<WritableImage> movingRight = new LinkedList<>();
    private LinkedList<WritableImage> dying = new LinkedList<>();
    private LinkedList<WritableImage> current;

    public Rocket() {
        initTexture();
    }

    public Rocket(Handler handler, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.handler = handler;
        setBounds();
        initTexture();
    }

    public Rocket(WritableImage texture, int x, int y) {
        super(texture, x, y);
        initTexture();
    }

    public Rocket(Handler handler, WritableImage texture, int x, int y, int width, int height) {
        super(handler, texture, x, y, width, height);
        setBounds();
        initTexture();
    }

    private void initTexture() {
        for (int i = 1; i <= 3; ++i) movingDown.add(Utilities.loadWI("rocket_down_" + i));
        for (int i = 1; i <= 3; ++i) movingLeft.add(Utilities.loadWI("rocket_left_" + i));
        for (int i = 1; i <= 3; ++i) movingRight.add(Utilities.loadWI("rocket_right_" + i));
        for (int i = 1; i <= 3; ++i) movingUp.add(Utilities.loadWI("rocket_up_" + i));
        for (int i = 1; i <= 3; ++i) dying.add(Utilities.loadWI("enemy_dead_" + i));
        current = movingDown;
    }

    @Override
    public void update() {
        if (isAlive()) {
            for (GameObject object : handler.getLevel().getEntityManager().getEntities()) {
                if (object instanceof Explosion && object.collide(this)) {
                    alive = false;
                    current = dying;
                    currentAnimation = 0;
                    return;
                }
            }
            move();
            autoChase();
            long cur = System.currentTimeMillis();
            if (cur > prev + Utilities.BOMBER_ANIMATION_DELAY) {
                currentAnimation = (currentAnimation + 1) % current.size();
                texture = current.get(currentAnimation);
                prev = cur;
                updateBoundaries((int) this.x, (int) this.y);
            }
        } else {
            long cur = System.currentTimeMillis();
            if (cur > prev + Utilities.BOMBER_ANIMATION_DELAY) {
                if (currentAnimation == current.size()) {
                    handler.getLevel().getEntityManager().remove(this);
                    Sound sound = new Sound("kill");
                    sound.play();
                    return;
                }
                texture = current.get(currentAnimation);
                prev = cur;
                updateBoundaries((int) this.x, (int) this.y);
                currentAnimation += 1;
            }
        }
        if (isAlive()) {
            if (this.getVelocityX() > 0)
                current = movingRight;
            else if (this.getVelocityX() < 0)
                current = movingLeft;
            else if (this.getVelocityY() > 0)
                current = movingDown;
            else
                current = movingUp;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        this.width = this.height = 50;
        gc.drawImage(this.texture, this.x, this.y, this.width, this.height);
        //drawBoundary(gc);
    }
}
