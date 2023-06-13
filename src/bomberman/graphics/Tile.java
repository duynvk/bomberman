package bomberman.graphics;

import bomberman.game.Handler;
import bomberman.game.Utilities;
import bomberman.graphics.item.BombItem;
import bomberman.graphics.item.FlameItem;
import bomberman.graphics.item.Item;
import bomberman.graphics.item.SpeedItem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.util.LinkedList;
import java.util.Random;

public class Tile extends GameObject {
    private boolean walkable;
    private boolean breakable;
    private boolean isBreaking = false;
    private int currentAnimation = 0;
    private long prev = 0;
    private LinkedList<WritableImage> animation = new LinkedList<>();
    private boolean isBroken;
    private Item item = null;

    public boolean isBroken() {
        return isBroken;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean hasItem() {
        return this.item != null;
    }

    public Tile(WritableImage texture, int x, int y) {
        super(texture, x, y);
    }

    public Tile(WritableImage texture, int x, int y, int width, int height) {
        super(texture, x, y, width, height);
    }

    public Tile(WritableImage texture, int x, int y, int width, int height, boolean walkable, boolean breakable) {
        super(texture, x, y, width, height);
        this.walkable = walkable;
        this.breakable = breakable;
    }

    public Tile(int width, int height) {
        super();
        this.texture = new WritableImage(Utilities.grassReader, 0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    public Tile(Handler handler, int width, int height, PixelReader pixelReader, boolean walkable, boolean breakable) {
        super();
        this.handler = handler;
        this.texture = new WritableImage(pixelReader, 0, 0, width, height);
        this.width = Utilities.TILE_WIDTH;
        this.height = Utilities.TILE_HEIGHT;
        this.walkable = walkable;
        this.breakable = breakable;
    }

    public void setAnimation(LinkedList<WritableImage> animation) {
        this.animation = animation;
    }

    public Tile(Tile tile) {
        this.handler = tile.getHandler();
        this.texture = tile.getTexture();
        this.width = tile.getWidth();
        this.height = tile.getHeight();
        this.walkable = tile.isWalkable();
        this.breakable = tile.isBreakable();
        this.animation = new LinkedList<>(tile.getAnimation());
        maybeItem();
    }

    public void maybeItem() {
        if (this.item == null && new Random().nextInt(10) % 4 == 0) {
            int go = new Random().nextInt(3);
            switch (go) {
                case 0:
                    this.item = new BombItem();
                    break;
                case 1:
                    this.item = new SpeedItem();
                    break;
                case 2:
                    this.item = new FlameItem();
                    break;
            }
        }
    }


    public LinkedList<WritableImage> getAnimation() {
        return animation;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public boolean isBreakable() {
        return breakable;
    }

    public void setBreaking(boolean breaking) {
        isBreaking = breaking;
    }

    @Override
    public void update() {
        long cur = System.currentTimeMillis();
        if (isBreaking && cur >= Utilities.WALL_EXPLOSION_DELAY + prev) {
            if (currentAnimation == animation.size()) {
                isBroken = true;
                if (item != null) {
                    item.setBoundaries(0, 0, Utilities.TILE_WIDTH, Utilities.TILE_HEIGHT);
                    item.setX(this.x);
                    item.setY(this.y);
                    item.setWidth((this.width));
                    item.setHeight(this.height);
                    item.setHandler(handler);
                    handler.getLevel().getEntityManager().add(item);
                }
                return;
            }
            texture = animation.get(currentAnimation);
            currentAnimation += 1;
            prev = cur;
        }
    }

    public boolean isBreaking() {
        return isBreaking;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(this.texture, this.x, this.y, this.width, this.height);
    }

    public void render(GraphicsContext gc, int x, int y) {

    }
}
