package bomberman.graphics.item;

import bomberman.game.Utilities;
import bomberman.graphics.Bomber;
import bomberman.graphics.item.Item;

public class FlameItem extends Item {
    public FlameItem() {
        initTexture();
    }

    private void initTexture() {
        this.texture = Utilities.loadWI("flame_item");
    }

    @Override
    public boolean consumable(Bomber o) {
        return o.collide(this);
    }

    @Override
    public void consume(Bomber o) {
        o.increaseFirePower();
        handler.getLevel().getEntityManager().remove(this);
    }
}
