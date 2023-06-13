package bomberman.graphics;

import bomberman.game.Handler;
import bomberman.game.Utilities;
import bomberman.graphics.bomb.Bomb;
import bomberman.graphics.bomb.Explosion;
import bomberman.graphics.enemy.Egg;
import bomberman.graphics.enemy.Enemy;
import bomberman.graphics.enemy.Rocket;
import bomberman.graphics.item.Item;
import bomberman.sound.Sound;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static java.lang.Math.abs;

public class Bomber extends GameObject {
    private int bomb_count;
    private int bomb_power;
    private int planted;
    private boolean AIPlay = true;
    private boolean alive;
    private boolean deceased;
    private long prev = System.currentTimeMillis();
    private int currentAnimation = 0;
    private int movingMask = 0;
    private int speedRatio = 5;
    private final LinkedList<WritableImage> movingDown = new LinkedList<>();
    private final LinkedList<WritableImage> movingUp = new LinkedList<>();
    private final LinkedList<WritableImage> movingLeft = new LinkedList<>();
    private final LinkedList<WritableImage> movingRight = new LinkedList<>();
    private final LinkedList<WritableImage> dying = new LinkedList<>();
    private LinkedList<WritableImage> current;
    private final Set<GameObject> consumed = new HashSet<>();

    public Bomber(WritableImage texture, int x, int y, short width, short height, int bomb_count) {
        super(texture, x, y, width, height);
        this.bomb_count = bomb_count;
        initTexture();
    }

    public Bomber(WritableImage texture, int x, int y) {
        super(texture, x, y);
        this.bomb_count = 1;
        initTexture();
    }

    public Bomber(WritableImage texture, int x, int y, short width, short height) {
        super(texture, x, y, width, height);
        this.bomb_count = 1;
        initTexture();
    }

    public Bomber() {
        initTexture();
        this.x = 0;
        this.y = 0;
        this.width = 100;
        this.height = 100;
    }

    public Bomber(Handler handler, int x, int y) {
        initTexture();
        this.handler = handler;
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 100;
        this.texture = movingDown.get(0);
        this.bomb_count = 1;
        this.bomb_power = 1;
        this.planted = 0;
        this.alive = true;
        this.deceased = false;
    }

    private void initTexture() {
        for (int i = 1; i <= 3; ++i) movingDown.add(Utilities.loadWI("player_down_" + i));
        for (int i = 1; i <= 3; ++i) movingLeft.add(Utilities.loadWI("player_left_" + i));
        for (int i = 1; i <= 3; ++i) movingRight.add(Utilities.loadWI("player_right_" + i));
        for (int i = 1; i <= 3; ++i) movingUp.add(Utilities.loadWI("player_up_" + i));
        for (int i = 1; i <= 3; ++i) dying.add(Utilities.loadWI("player_dead_" + i));
        current = movingDown;
    }

    public boolean isAIPlay() {
        return AIPlay;
    }

    public void setAIPlay(boolean AIPlay) {
        this.AIPlay = AIPlay;
    }

    public int getLevelPosX() {
        return (x + width / 2 - handler.getLevel().getOffsetX()) / Utilities.TILE_WIDTH;
    }

    public int getLevelPosY() {
        return (y + height / 2 - handler.getLevel().getOffsetY()) / Utilities.TILE_HEIGHT;
    }

    public boolean isDeceased() {
        return deceased;
    }

    public void updateMovement(int prioritized) {
        if (prioritized == 0) {
            for (int i = 0; i < 4; ++i)
                if ((movingMask >> i & 1) == 1)
                    prioritized = 1 << i;
        }
        velocityX = velocityY = 0;
        if (prioritized == 1) {
            current = movingUp;
            velocityY = -2;
        }
        if (prioritized == 2) {
            current = movingLeft;
            velocityX = -2;
        }
        if (prioritized == 4) {
            current = movingDown;
            velocityY = 2;
        }
        if (prioritized == 8) {
            current = movingRight;
            velocityX = 2;
        }
    }

    public void plantBomb() {
        if (planted < bomb_count) {
            Bomb bomb = new Bomb(handler, bomb_power, getLevelPosX(), getLevelPosY());
            bomb.setBoundaries(0, 0, 50, 50);
            handler.getLevel().getEntityManager().add(bomb);
            planted += 1;
            Sound sound = new Sound("bom_set");
            sound.play();
        }
    }

    private final int[] D = {-1, 0, 1, 0};
    private final int[] C = {0, -1, 0, 1};
    private float lastX = -1, lastY = -1;

    public void changeDirection(int direction) {
        direction = 1 << direction;
        movingMask = direction;
        updateMovement(direction);
    }

    public void stop() {
        movingMask = 0;
        lastX = lastY = -1;
    }

    static class EPosition {
        private final int x;
        private final int y;

        EPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public int findDirect(Queue<EPosition> bfs, int px, int py, boolean[][] En, boolean[][] moved) {
        int direct = -1;
        while (!bfs.isEmpty() && direct == -1) {
            EPosition u = bfs.poll();
            for (int i = 0; i < 4; ++i) {
                int nx = u.getX() + C[i];
                int ny = u.getY() + D[i];
                if (0 <= ny && ny < handler.getLevel().getHeight() &&
                        0 <= nx && nx < handler.getLevel().getWidth() &&
                        handler.getLevel().getMap()[ny][nx].isWalkable()) {
                    if (nx == px && ny == py) {
                        direct = (i + 2) % 4;
                        break;
                    }
                    if (!moved[nx][ny] && !En[nx][ny]) {
                        moved[nx][ny] = true;
                        bfs.add(new EPosition(nx, ny));
                    }
                }
            }
        }
        return direct;
    }

    public void AIBomber() {
        if ((lastX < 0 && lastY < 0) || (abs(getX() - lastX) >= Utilities.TILE_WIDTH ||
                abs(getY() - lastY) >= Utilities.TILE_HEIGHT)) {
            lastX = getX();
            lastY = getY();
            int px = handler.getLevel().getLevelPosX(this.x);
            int py = handler.getLevel().getLevelPosY(this.y);
            boolean[][] Ex = new boolean[handler.getLevel().getWidth()][handler.getLevel().getHeight()];
            boolean[][] En = new boolean[handler.getLevel().getWidth()][handler.getLevel().getHeight()];
            boolean danger = false, coBomb = false;
            for (GameObject o : handler.getLevel().getEntityManager().getEntities()) {
                if (o instanceof Bomb) {
                    coBomb = true;
                    int ox = handler.getLevel().getLevelPosX((int) o.getX());
                    int oy = handler.getLevel().getLevelPosY((int) o.getY());
                    int oRad = ((Bomb) o).getRadius();
                    for (int i = ox - oRad; i <= ox + oRad; ++i) {
                        if (0 <= i && i < handler.getLevel().getWidth() &&
                                handler.getLevel().getMap()[oy][i].isWalkable()) Ex[i][oy] = true;
                    }
                    for (int i = oy - oRad; i <= oy + oRad; ++i) {
                        if (0 <= i && i < handler.getLevel().getHeight() &&
                                handler.getLevel().getMap()[i][ox].isWalkable()) Ex[ox][i] = true;
                    }
                }
                if (o instanceof Explosion) {
                    coBomb = true;
                    int ox = handler.getLevel().getLevelPosX((int) o.getX());
                    int oy = handler.getLevel().getLevelPosY((int) o.getY());
                    Ex[ox][oy] = true;
                }
                if (o instanceof Egg || o instanceof Rocket) {
                    int ox = handler.getLevel().getLevelPosX((int) o.getX());
                    int oy = handler.getLevel().getLevelPosY((int) o.getY());
                    for (int i = ox - 1; i <= ox + 1; ++i) {
                        if (0 <= i && i < handler.getLevel().getWidth() &&
                                handler.getLevel().getMap()[oy][i].isWalkable()) En[i][oy] = true;
                    }
                    for (int i = oy - 1; i <= oy + 1; ++i) {
                        if (0 <= i && i < handler.getLevel().getHeight() &&
                                handler.getLevel().getMap()[i][ox].isWalkable()) En[ox][i] = true;
                    }
                    if (abs(ox - px) + abs(oy - py) < 3) danger = true;
                }
            }
            boolean[][] moved = new boolean[handler.getLevel().getWidth()][handler.getLevel().getHeight()];
            Queue<EPosition> bfs = new LinkedList<>();
            if (coBomb) {
                if (Ex[px][py] || En[px][py]) {
                    for (int i = 0; i < handler.getLevel().getWidth(); ++i)
                        for (int j = 0; j < handler.getLevel().getHeight(); ++j) {
                            if (handler.getLevel().getMap()[j][i].isWalkable() && !Ex[i][j] && !En[i][j]) {
                                moved[i][j] = true;
                                bfs.add(new EPosition(i, j));
                            }
                        }
                    int direct = findDirect(bfs, px, py, En, moved);
                    if (direct != -1) changeDirection(direct);
                    else stop();
                } else stop();
            } else {
                for (int i = 0; i < handler.getLevel().getWidth(); ++i)
                    for (int j = 0; j < handler.getLevel().getHeight(); ++j) {
                        if (handler.getLevel().getMap()[j][i].isWalkable() && En[i][j]) {
                            moved[i][j] = true;
                            bfs.add(new EPosition(i, j));
                        }
                    }
                int direct = findDirect(bfs, px, py, En, moved);
                if (direct != -1) {
                    if (danger) {
                        plantBomb();
                        lastX = lastY = -1;
                    } else changeDirection(direct);
                } else {
                    boolean[][] Eb = new boolean[handler.getLevel().getWidth()][handler.getLevel().getHeight()];
                    for (GameObject o : handler.getLevel().getEntityManager().getEntities()) {
                        if (o instanceof Tile && ((Tile) o).isBreakable()) {
                            int ox = handler.getLevel().getLevelPosX((int) o.getX());
                            int oy = handler.getLevel().getLevelPosY((int) o.getY());
                            for (int i = ox - 1; i <= ox + 1; ++i) {
                                if (0 <= i && i < handler.getLevel().getWidth() &&
                                        handler.getLevel().getMap()[oy][i].isWalkable()) Eb[i][oy] = true;
                            }
                            for (int i = oy - 1; i <= oy + 1; ++i) {
                                if (0 <= i && i < handler.getLevel().getHeight() &&
                                        handler.getLevel().getMap()[i][ox].isWalkable()) Eb[ox][i] = true;
                            }
                        }
                    }
                    if (Eb[px][py]) {
                        plantBomb();
                        lastX = lastY = -1;
                    } else {
                        Queue<EPosition> bfsB = new LinkedList<>();
                        boolean[][] movedB = new boolean[handler.getLevel().getWidth()][handler.getLevel().getHeight()];
                        for (int i = 0; i < handler.getLevel().getWidth(); ++i)
                            for (int j = 0; j < handler.getLevel().getHeight(); ++j) {
                                if (handler.getLevel().getMap()[j][i].isWalkable() && Eb[i][j]) {
                                    movedB[i][j] = true;
                                    bfsB.add(new EPosition(i, j));
                                }
                            }
                        direct = findDirect(bfsB, px, py, En, movedB);
                        if (direct != -1) changeDirection(direct);
                        else {
                            Queue<EPosition> bfsP = new LinkedList<>();
                            boolean[][] movedP = new boolean[handler.getLevel().getWidth()][handler.getLevel().getHeight()];
                            for (GameObject o : handler.getLevel().getEntityManager().getEntities()) {
                                if (o instanceof Portal) {
                                    int ox = handler.getLevel().getLevelPosX((int) o.getX());
                                    int oy = handler.getLevel().getLevelPosY((int) o.getY());
                                    movedP[ox][oy] = true;
                                    bfsP.add(new EPosition(ox, oy));
                                }
                            }
                            direct = findDirect(bfsP, px, py, En, movedP);
                            if (direct != -1) changeDirection(direct);
                            else stop();
                        }
                    }
                }
            }
        }
    }

    public void handleKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.W)
            movingMask = movingMask & ~1;
        else if (event.getCode() == KeyCode.A)
            movingMask = movingMask & ~2;
        else if (event.getCode() == KeyCode.S)
            movingMask = movingMask & ~4;
        else if (event.getCode() == KeyCode.D)
            movingMask = movingMask & ~8;
        updateMovement(0);
    }

    public void handleKeyPressed(KeyEvent event) {
        int direction = 0;
        if (event.getCode() == KeyCode.W)
            direction = 1;
        else if (event.getCode() == KeyCode.A)
            direction = 2;
        else if (event.getCode() == KeyCode.S)
            direction = 4;
        else if (event.getCode() == KeyCode.D)
            direction = 8;
        else if (event.getCode() == KeyCode.SPACE && planted < bomb_count)
            plantBomb();
        movingMask |= direction;
        updateMovement(direction);
    }

    private void move() {
        if (movingMask != 0) {
            int fakeX = this.x;
            int fakeY = this.y;
            if (velocityX != 0)
                this.x += velocityX * speedRatio;
            if (velocityY != 0)
                this.y += velocityY * speedRatio;
            boolean okay = true;
            for (GameObject o : handler.getLevel().getEntityManager().getEntities()) {
                if (o instanceof Tile && !((Tile) o).isWalkable()) {
                    updateBoundaries(this.x, this.y);
                    if (o.collide(this)) {
                        okay = false;
                        break;
                    }
                }
            }
            if (!okay) {
                this.x = fakeX;
                this.y = fakeY;
                if (speedRatio != 5) {
                    int fakeSpeedRatio = speedRatio;
                    speedRatio = 5;
                    move();
                    speedRatio = fakeSpeedRatio;
                }
            }
        }
    }

    public void releaseBomb() {
        this.planted -= 1;
    }

    @Override
    public void update() {
        if (movingMask != 0 || !alive) {
            long cur = System.currentTimeMillis();
            if (cur > prev + Utilities.BOMBER_ANIMATION_DELAY) {
                currentAnimation = alive ? (currentAnimation + 1) % current.size() : currentAnimation + 1;
                if (!alive && currentAnimation >= current.size()) {
                    deceased = true;
                    handler.getLevel().getEntityManager().remove(this);
                    return;
                }
                texture = current.get(currentAnimation);
                prev = cur;
                if (alive) {
                    move();
                    updateBoundaries(this.x, this.y);
                }
            }
        }
        if (alive) {
            for (GameObject o : handler.getLevel().getEntityManager().getEntities()) {
                if (o instanceof Explosion || o instanceof Enemy) {
                    if (o.collide(this)) {
                        alive = false;
                        current = dying;
                        currentAnimation = 0;
                        break;
                    }
                }
                if (o instanceof Item && ((Item) o).consumable(this) && !consumed.contains(o)) {
                    ((Item) o).consume(this);
                    consumed.add(o);
                    handler.getLevel().getEntityManager().remove(o);
                }
            }
        }
        if (AIPlay)
            AIBomber();
    }

    @Override
    public void render(GraphicsContext gc) {
        this.width = this.height = 50;
        gc.drawImage(this.texture, this.x, this.y, this.width, this.height);
    }

    public void increaseBomb() {
        bomb_count += 1;
    }

    public void increaseFirePower() {
        bomb_power += 1;
    }

    public void increaseSpeed() {
        speedRatio += 1;
    }
}
