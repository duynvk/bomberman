package bomberman.graphics;

import bomberman.game.Utilities;
import bomberman.graphics.item.Item;
import javafx.scene.image.WritableImage;

public class Portal extends Item {
    public Portal() {
        super();
        initTexture();
    }

    private void initTexture() {
        this.texture = Utilities.loadWI("portal");
    }

    public Portal(WritableImage texture, int x, int y, int width, int height) {
        super(texture, x, y, width, height);
    }

    @Override
    public boolean consumable(Bomber o) {
        return handler.getLevel().getMonstersLeft() == 0 &&
                this.collide(o);
    }

    @Override
    public void consume(Bomber o) {
        handler.getLevel().setFinished(true);
    }
}
