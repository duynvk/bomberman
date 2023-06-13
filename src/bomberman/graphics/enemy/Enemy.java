package bomberman.graphics.enemy;

import bomberman.game.Handler;
import bomberman.game.Utilities;
import bomberman.graphics.bomb.Bomb;
import bomberman.graphics.bomb.Explosion;
import bomberman.graphics.GameObject;
import bomberman.graphics.Tile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static java.lang.Math.abs;

public class Enemy extends GameObject {
    protected long prev = System.currentTimeMillis();
    protected int currentAnimation = 0;
    private final int speed = 1;
    protected boolean alive = true;
    public static final int RIGHT = 1;
    public static final int LEFT = 3;
    public static final int DOWN = 2;
    public static final int UP = 0;

    public Enemy() {

    }

    public Enemy(int x, int y, int width, int height) {
        super(x, y, width, height);
        setBounds();
    }

    public Enemy(WritableImage texture, int x, int y) {
        super(texture, x, y);
    }

    public Enemy(Handler handler, WritableImage texture, int x, int y, int width, int height) {
        super(texture, x, y, width, height);
        this.handler = handler;
        setBounds();
    }

    public boolean isAlive() {
        return alive;
    }

    public int getSpeed() {
        return speed;
    }

    protected void direction(int num) {
        switch (num) {
            case RIGHT:
                this.velocityX = speed;
                this.velocityY = 0;
                break;
            case LEFT:
                this.velocityX = this.getSpeed() * -1;
                this.velocityY = 0;
                break;
            case DOWN:
                this.velocityY = this.getSpeed();
                this.velocityX = 0;
                break;
            case UP:
                this.velocityY = this.getSpeed() * -1;
                this.velocityX = 0;
                break;
        }
    }

    private long lastD = 0;
    private float lastX = -1, lastY = -1;
    private final int[] D = {-1, 0, 1, 0};
    private final int[] C = {0, 1, 0, -1};

    protected void move() {
        int fakeX = this.x;
        int fakeY = this.y;
        if (velocityX != 0)
            this.x += velocityX;
        if (velocityY != 0)
            this.y += velocityY;
        boolean okay = true;
        for (GameObject o : handler.getLevel().getEntityManager().getEntities()) {
            if (o instanceof Tile && !((Tile) o).isWalkable() || o instanceof Bomb) {
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
        }
    }

    public void randomDirection() {
        long cur = System.currentTimeMillis();
        if ((lastX < 0 && lastY < 0) ||
                (abs(getX() - lastX) >= Utilities.TILE_WIDTH || abs(getY() - lastY) >= Utilities.TILE_HEIGHT) ||
                cur - lastD > 500) {
            lastX = getX();
            lastY = getY();
            int px = handler.getLevel().getLevelPosX(this.x);
            int py = handler.getLevel().getLevelPosY(this.y);
            LinkedList<Integer> canBeMoved = new LinkedList<>();

            for (int i = 0; i < 4; ++i) {
                int nx = px + C[i];
                int ny = py + D[i];
                if (0 <= ny && ny < handler.getLevel().getHeight() &&
                        0 <= nx && nx < handler.getLevel().getWidth() &&
                        handler.getLevel().getMap()[ny][nx].isWalkable()) {
                    canBeMoved.add(i);
                }
            }
            if (!canBeMoved.isEmpty()) {
                Random rd = new Random();
                int n = rd.nextInt(canBeMoved.size());
                direction(canBeMoved.get(n));
            }
            lastD = cur;
        }
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

    public void autoChase() {
        long cur = System.currentTimeMillis();
        if ((lastX < 0 && lastY < 0) || (abs(getX() - lastX) >= Utilities.TILE_WIDTH || abs(getY() - lastY) >= Utilities.TILE_HEIGHT) ||
                cur - lastD > 500) {
            lastX = getX();
            lastY = getY();
            int px = handler.getLevel().getLevelPosX(this.x);
            int py = handler.getLevel().getLevelPosY(this.y);
            int playerX = handler.getLevel().getPlayer().getLevelPosX();
            int playerY = handler.getLevel().getPlayer().getLevelPosY();
            boolean[][] moved = new boolean[handler.getLevel().getWidth()][handler.getLevel().getHeight()];
            Queue<EPosition> bfs = new LinkedList<>();
            moved[playerX][playerY] = true;
            bfs.add(new EPosition(playerX, playerY));
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
                        if (!moved[nx][ny]) {
                            moved[nx][ny] = true;
                            bfs.add(new EPosition(nx, ny));
                        }
                    }
                }
            }
            if (direct != -1) direction(direct);
            else {
                LinkedList<Integer> canBeMoved = new LinkedList<>();

                for (int i = 0; i < 4; ++i) {
                    int nx = px + C[i];
                    int ny = py + D[i];
                    if (0 <= ny && ny < handler.getLevel().getHeight() &&
                            0 <= nx && nx < handler.getLevel().getWidth() &&
                            handler.getLevel().getMap()[ny][nx].isWalkable()) {
                        canBeMoved.add(i);
                    }
                }
                if (!canBeMoved.isEmpty()) {
                    Random rd = new Random();
                    int n = rd.nextInt(canBeMoved.size());
                    direction(canBeMoved.get(n));
                }
            }
            lastD = cur;
        }
    }

    public void stop() {
        this.velocityX = this.velocityY = 0;
    }

    public void chaseAndDodge() {
        long cur = System.currentTimeMillis();
        if ((lastX < 0 && lastY < 0) || (abs(getX() - lastX) >= Utilities.TILE_WIDTH || abs(getY() - lastY) >= Utilities.TILE_HEIGHT) ||
                cur - lastD > 500) {
            lastX = getX();
            lastY = getY();
            int px = handler.getLevel().getLevelPosX(this.x);
            int py = handler.getLevel().getLevelPosY(this.y);
            boolean[][] Ex = new boolean[handler.getLevel().getWidth() + 5][handler.getLevel().getHeight() + 5];

            for (GameObject o : handler.getLevel().getEntityManager().getEntities()) {
                if (o instanceof Bomb) {
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
                    int ox = handler.getLevel().getLevelPosX((int) o.getX());
                    int oy = handler.getLevel().getLevelPosY((int) o.getY());
                    Ex[ox][oy] = true;
                }
            }

            boolean danger = false;
            for (int i = 0; i < handler.getLevel().getWidth(); ++i)
                for (int j = 0; j < handler.getLevel().getHeight(); ++j)
                    if (Ex[i][j] && abs(i - px) + abs(j - py) <= 1) {
                        danger = true;
                        break;
                    }

            if (danger) {
                if (Ex[px][py]) {
                    LinkedList<Integer> canBeMoved = new LinkedList<>();

                    for (int i = 0; i < 4; ++i) {
                        int nx = px + C[i];
                        int ny = py + D[i];
                        if (0 <= ny && ny < handler.getLevel().getHeight() &&
                                0 <= nx && nx < handler.getLevel().getWidth() &&
                                handler.getLevel().getMap()[ny][nx].isWalkable() &&
                                !Ex[nx][ny]) {
                            canBeMoved.add(i);
                        }
                    }

                    Random rd = new Random();
                    if (!canBeMoved.isEmpty()) {
                        int n = rd.nextInt(canBeMoved.size());
                        direction(canBeMoved.get(n));
                    } else direction(rd.nextInt(4));
                } else stop();
            } else {
                int playerX = handler.getLevel().getPlayer().getLevelPosX();
                int playerY = handler.getLevel().getPlayer().getLevelPosY();
                boolean[][] moved = new boolean[handler.getLevel().getWidth()][handler.getLevel().getHeight()];
                Queue<EPosition> bfs = new LinkedList<>();
                moved[playerX][playerY] = true;
                bfs.add(new EPosition(playerX, playerY));
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
                            if (!moved[nx][ny]) {
                                moved[nx][ny] = true;
                                bfs.add(new EPosition(nx, ny));
                            }
                        }
                    }
                }

                if (direct != -1) direction(direct);
                else {
                    LinkedList<Integer> canBeMoved = new LinkedList<>();

                    for (int i = 0; i < 4; ++i) {
                        int nx = px + C[i];
                        int ny = py + D[i];
                        if (0 <= ny && ny < handler.getLevel().getHeight() &&
                                0 <= nx && nx < handler.getLevel().getWidth() &&
                                handler.getLevel().getMap()[ny][nx].isWalkable()) {
                            canBeMoved.add(i);
                        }
                    }

                    if (!canBeMoved.isEmpty()) {
                        Random rd = new Random();
                        int n = rd.nextInt(canBeMoved.size());
                        direction(canBeMoved.get(n));
                    }
                }
            }
            lastD = cur;
        }
    }


    @Override
    public void update() {

    }

    @Override
    public void render(GraphicsContext gc) {

    }
}