package bomberman.graphics.item;

import bomberman.game.Utilities;
import bomberman.graphics.Bomber;
import bomberman.graphics.item.Item;

public class SpeedItem extends Item {
    public SpeedItem() {
        initTexture();
    }

    private void initTexture() {
        this.texture = Utilities.loadWI("speed_item");
    }

    @Override
    public boolean consumable(Bomber o) {
        return o.collide(this);
    }

    @Override
    public void consume(Bomber o) {
        //o.increaseSpeed();
        handler.getLevel().getEntityManager().remove(this);
    }
}
