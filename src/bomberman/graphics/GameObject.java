package bomberman.graphics;

import bomberman.game.Handler;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

public abstract class GameObject {
    protected WritableImage texture;
    protected int x, y;
    protected int velocityX = 0, velocityY = 0;
    protected int width;
    protected int height;
    protected int boundsX, boundsY, boundsWidth, boundsHeight, boundsOffsetX, boundsOffsetY;

    protected Handler handler;

    public GameObject() {

    }

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setBounds();
    }

    public GameObject(WritableImage texture, int x, int y) {
        this.texture = texture;
        this.x = x;
        this.y = y;
    }

    public GameObject(WritableImage texture, int x, int y, int width, int height) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setBounds();
    }

    public void setBoundaries(int x, int y, int width, int height) {
        boundsX = (int) this.x + x;
        boundsY = (int) this.y + y;
        boundsWidth = width;
        boundsHeight = height;
        boundsOffsetX = x;
        boundsOffsetY = y;
    }

    public void setBounds() {
        this.boundsX = (int) x;
        this.boundsY = (int) y;
        this.boundsWidth = width;
        this.boundsHeight = height;
    }

    public void updateBoundaries(int x, int y) {
        boundsX = (int) this.x + boundsOffsetX;
        boundsY = (int) this.y + boundsOffsetY;
    }

    public void drawBoundary(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(boundsX, boundsY, boundsWidth, boundsHeight);
    }

    public void setTexture(WritableImage texture) {
        this.texture = texture;
    }

    public WritableImage getTexture() {
        return texture;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(boundsX, boundsY, boundsWidth, boundsHeight);
    }

    public boolean collide(GameObject o) {
        return o.getBounds().intersects(this.getBounds());
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(int velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBoundsX() {
        return boundsX;
    }

    public int getBoundsY() {
        return boundsY;
    }

    public int getBoundsWidth() {
        return boundsWidth;
    }

    public int getBoundsHeight() {
        return boundsHeight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public abstract void update();

    public abstract void render(GraphicsContext gc);
}
