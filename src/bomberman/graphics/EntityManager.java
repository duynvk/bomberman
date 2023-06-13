package bomberman.graphics;

import bomberman.game.Handler;
import bomberman.graphics.bomb.Bomb;
import bomberman.graphics.bomb.Explosion;
import bomberman.graphics.enemy.Egg;
import bomberman.graphics.enemy.Enemy;
import bomberman.graphics.enemy.Rocket;
import bomberman.graphics.enemy.Skull;
import bomberman.graphics.item.Item;
import javafx.scene.canvas.GraphicsContext;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class EntityManager {
    private List<GameObject> entities;
    private List<GameObject> trash;
    private List<GameObject> icm;
    private Handler handler;

    private final Comparator<GameObject> renderOrder = new Comparator<GameObject>() {
        @Override
        public int compare(GameObject a, GameObject b) {
            if (a instanceof Explosion) return 1;
            if (a instanceof Bomb) return 1;
            if (a instanceof Egg) return 1;
            if (a instanceof Rocket) return 1;
            if (a instanceof Skull) return 1;
            if (a.boundsY != b.boundsY)
                return a.boundsY < b.boundsY ? -1 : 1;
            return 0;
        }
    };

    public EntityManager(Handler handler) {
        this.handler = handler;
        this.entities = new LinkedList<>();
        this.trash = new LinkedList<>();
        this.icm = new LinkedList<>();
    }

    public void add(GameObject o) {
        icm.add(o);
    }

    public void remove(GameObject o) {
        trash.add(o);
    }

    public List<GameObject> getEntities() {
        return entities;
    }

    public void update() {
        for (GameObject o : entities) o.update();
        for (GameObject o : trash) entities.remove(o);
        entities.addAll(icm);
        trash.clear();
        icm.clear();
        entities.sort(renderOrder);
    }

    public void render(GraphicsContext gc) {
        entities.sort(renderOrder);
        for (GameObject o : entities) {
            if (o instanceof Tile)
                o.render(gc);
        }
        for (GameObject o : entities) {
            if (o instanceof Bomb || o instanceof Item)
                o.render(gc);
        }
        for (GameObject o : entities) {
            if (o instanceof Enemy)
                o.render(gc);
        }
        for (GameObject o : entities) {
            if (o instanceof Explosion)
                o.render(gc);
        }
    }
}
