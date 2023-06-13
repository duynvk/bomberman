package bomberman.graphics;

import bomberman.game.Handler;
import bomberman.game.Main;
import bomberman.game.Utilities;
import bomberman.graphics.enemy.Egg;
import bomberman.graphics.enemy.Enemy;
import bomberman.graphics.enemy.Rocket;
import bomberman.graphics.enemy.Skull;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import java.util.LinkedList;

public class Level {
    private int width;
    private int height;
    private int offsetX = 0;
    private int offsetY = 0;
    private int playerSpawnX;
    private int playerSpawnY;
    private int monstersLeft = 0;
    private boolean finished = false;

    private final Handler handler;
    private Bomber player;
    private final EntityManager entityManager;
    private final LinkedList<Tile> tiles;
    private final LinkedList<WritableImage> animation = new LinkedList<>();

    private Tile[][] map;

    public Level(Handler handler, String preset) {
        this.handler = handler;
        entityManager = new EntityManager(this.handler);
        tiles = new LinkedList<>();
        initResources();
        loadWorld(preset);
    }

    public void initResources() {
        tiles.add(new Tile(handler, Utilities.TILE_WIDTH, Utilities.TILE_HEIGHT, Utilities.grassReader, true, false));
        tiles.add(new Tile(handler, Utilities.TILE_WIDTH, Utilities.TILE_HEIGHT, Utilities.wallReader, false, false));
        tiles.add(new Tile(handler, Utilities.TILE_WIDTH, Utilities.TILE_HEIGHT, Utilities.cratesReader, false, true));
        tiles.add(new Tile(handler, Utilities.TILE_WIDTH, Utilities.TILE_HEIGHT, Utilities.grassReader_1, true, false));
        for (int i = 1; i <= 3; ++i) animation.add(Utilities.loadWI("brick_exploded_" + i));
        tiles.get(2).setAnimation(new LinkedList<>(animation));
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void loadWorld(String preset) {
        String[][] file = Utilities.loadFile(preset);
        this.width = file[0].length;
        this.height = file.length;
        this.offsetX = (Main.WIDTH - width * Utilities.TILE_WIDTH) / 2;
        this.offsetY = (Main.HEIGHT - height * Utilities.TILE_HEIGHT) / 2;
        this.map = new Tile[this.height][this.width];
        for (int i = 0; i < this.height; ++i)
            for (int j = 0; j < this.width; ++j) {
                switch (file[i][j]) {
                    case "#":
                        this.map[i][j] = new Tile(tiles.get(1));
                        break;
                    case ".":
                    case "@":
                    case "$":
                    case "&":
                    case "%":
                        this.map[i][j] = new Tile(tiles.get((i + j) % 2 == 0 ? 0 : 3));
                        monstersLeft += file[i][j].equals("$") || file[i][j].equals("&") ? 1 : 0;
                        break;
                    case "*":
                        this.map[i][j] = new Tile(tiles.get(2));
                        break;
                    case "!":
                        this.map[i][j] = new Tile(tiles.get(2));
                        Portal portal = new Portal();
                        portal.setHandler(handler);
                        this.map[i][j].setItem(portal);
                        break;
                }
                if (file[i][j].equals("@")) {
                    playerSpawnY = i;
                    playerSpawnX = j;
                }
                if (file[i][j].equals("$")) {
                    Egg enemy = new Egg(handler, Utilities.TILE_HEIGHT * j + offsetX, Utilities.TILE_WIDTH * i + offsetY, 50, 50);
                    enemy.setBoundaries(5, 15, 30, 30);
                    entityManager.add(enemy);
                }
                if (file[i][j].equals("&")) {
                    Rocket enemy = new Rocket(handler, Utilities.TILE_HEIGHT * j + offsetX, Utilities.TILE_WIDTH * i + offsetY, 50, 50);
                    enemy.setBoundaries(5, 15, 30, 30);
                    entityManager.add(enemy);
                }
                if (file[i][j].equals("%")) {
                    Skull enemy = new Skull(handler, Utilities.TILE_HEIGHT * j + offsetX, Utilities.TILE_WIDTH * i + offsetY, 50, 50);
                    enemy.setBoundaries(5, 15, 30, 30);
                    entityManager.add(enemy);
                }
                this.map[i][j].setX(Utilities.TILE_HEIGHT * j + offsetX);
                this.map[i][j].setY(Utilities.TILE_WIDTH * i + offsetY);
                this.map[i][j].setBounds();
                entityManager.add(this.map[i][j]);
            }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMonstersLeft() {
        return monstersLeft;
    }

    public void update() {
        for (int i = 0; i < this.height; ++i)
            for (int j = 0; j < this.width; ++j)
                if (this.map[i][j].isBroken()) {
                    entityManager.remove(this.map[i][j]);
                    this.map[i][j] = new Tile(tiles.get((i + j) % 2 == 0 ? 0 : 3));
                    this.map[i][j].setX(Utilities.TILE_HEIGHT * j + offsetX);
                    this.map[i][j].setY(Utilities.TILE_WIDTH * i + offsetY);
                    this.map[i][j].setBounds();
                    this.map[i][j].setWalkable(true);
                    entityManager.add(this.map[i][j]);
                }
        entityManager.update();
        monstersLeft = 0;
        for (GameObject o : entityManager.getEntities()) if (o instanceof Enemy) monstersLeft += 1;
    }

    public void render(GraphicsContext gc) {
        entityManager.render(gc);
    }

    public Tile[][] getMap() {
        return map;
    }

    public int getLevelPosX(int x) {
        //return (x + width / 2 - offsetX) / Utilities.TILE_WIDTH;
        return (x - offsetX + 1) / Utilities.TILE_WIDTH;
    }

    public int getLevelPosY(int y) {
        //return (y + height / 2 - offsetY) / Utilities.TILE_HEIGHT;
        return (y - offsetY + 1) / Utilities.TILE_HEIGHT;
    }

    public Bomber getPlayer() {
        return player;
    }

    public void setPlayer(Bomber player) {
        this.player = player;
    }

    public int getPlayerSpawnX() {
        return playerSpawnX;
    }

    public int getPlayerSpawnY() {
        return playerSpawnY;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }
}
