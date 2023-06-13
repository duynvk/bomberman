package bomberman.graphics.item;

import bomberman.game.Handler;
import bomberman.graphics.Bomber;
import bomberman.graphics.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

public abstract class Item extends GameObject {
    public Item() {
    }

    public Item(WritableImage texture, int x, int y, int width, int height) {
        super(texture, x, y, width, height);
    }


    public abstract boolean consumable(Bomber o);

    public abstract void consume(Bomber o);

    @Override
    public void update() {

    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(this.texture, this.x, this.y, this.width, this.height);
    }
}
