package bomberman.graphics.item;

import bomberman.game.Utilities;
import bomberman.graphics.Bomber;
import bomberman.graphics.item.Item;

public class BombItem extends Item {
    public BombItem() {
        initTexture();
    }

    private void initTexture() {
        this.texture = Utilities.loadWI("bomb_item");
    }

    @Override
    public boolean consumable(Bomber o) {
        return o.collide(this);
    }

    @Override
    public void consume(Bomber o) {
        o.increaseBomb();
        handler.getLevel().getEntityManager().remove(this);
    }
}
