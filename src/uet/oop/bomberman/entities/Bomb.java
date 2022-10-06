package uet.oop.bomberman.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.controller.CollisionManager;
import uet.oop.bomberman.graphics.Map;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Bomb extends Entity implements Obstacle {
    public enum status {
        REMAIN, EXPLODED, DISAPPEAR
    }

    protected status bombStatus;
    protected List<Flame> flameExplode = new ArrayList<>();
    private int delayTime = 3;
    private int spriteIndex = 0;
    private CollisionManager collisionManager;
    private Map map;

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        int cnt = 0;
        @Override
        public void run() {
            cnt++;
            if ((delayTime - cnt) <= 0) {
                bombStatus = status.EXPLODED;
                timer.cancel();
                spriteIndex = 0;
            }
        }
    };

    private void pickSprite(Image img) {
        this.img = img;
    }

    public Bomb(int x, int y, Image img, CollisionManager collisionManager) {
        super(x, y, img);
        this.collisionManager = collisionManager;
        this.map = collisionManager.getMap();
        bombStatus = status.REMAIN;
        timer.schedule(task, 0, 1000);
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0:
                    flameExplode.add(new Flame(x, y + 1, Sprite.explosion_vertical_down_last.getFxImage()));
                    break;
                case 1:
                    flameExplode.add(new Flame(x, y - 1, Sprite.explosion_vertical_top_last.getFxImage()));
                    break;
                case 2:
                    flameExplode.add(new Flame(x - 1, y, Sprite.explosion_horizontal_left_last.getFxImage()));
                    break;
                case 3:
                    flameExplode.add(new Flame(x + 1, y, Sprite.explosion_horizontal_right_last.getFxImage()));
                    break;
                case 4:
                    flameExplode.add(new Flame(x, y, Sprite.bomb_exploded.getFxImage()));
                    break;
                default:
                    break;
            }
        }
    }

    public status getBombStatus() {
        return bombStatus;
    }

    @Override
    public void update() {
        if (bombStatus == status.REMAIN) {
            spriteIndex = (spriteIndex + 1) % 1000;
            pickSprite(Sprite.movingSprite(Sprite.bomb, Sprite.bomb_1, Sprite.bomb_2, spriteIndex, 30).getFxImage());
        }
        if (bombStatus == status.EXPLODED) {
            spriteIndex = (spriteIndex + 1) % 1000;
            flameExplode.get(0).pickSprite(Sprite.movingSprite(Sprite.explosion_vertical_down_last,
                                                                Sprite.explosion_vertical_down_last1,
                                                                Sprite.explosion_vertical_down_last2,
                                                                spriteIndex, 30).getFxImage());
            flameExplode.get(1).pickSprite(Sprite.movingSprite(Sprite.explosion_vertical_top_last,
                                                                Sprite.explosion_vertical_top_last1,
                                                                Sprite.explosion_vertical_top_last2,
                                                                spriteIndex, 30).getFxImage());
            flameExplode.get(2).pickSprite(Sprite.movingSprite(Sprite.explosion_horizontal_left_last,
                                                                Sprite.explosion_horizontal_left_last1,
                                                                Sprite.explosion_horizontal_left_last2,
                                                                spriteIndex, 30).getFxImage());
            flameExplode.get(3).pickSprite(Sprite.movingSprite(Sprite.explosion_horizontal_right_last,
                                                                Sprite.explosion_horizontal_right_last1,
                                                                Sprite.explosion_horizontal_right_last2,
                                                                spriteIndex, 30).getFxImage());
            flameExplode.get(4).pickSprite(Sprite.movingSprite(Sprite.bomb_exploded,
                                                                Sprite.bomb_exploded1,
                                                                Sprite.bomb_exploded2,
                                                                spriteIndex, 30).getFxImage());
            bombStatus = (spriteIndex == 15) ? status.DISAPPEAR : bombStatus;
            if (spriteIndex == 15) {
                bombStatus = status.DISAPPEAR;
                bombExplode();
            }
        }
    }

    private List<Entity> explode = new ArrayList<>();

    public void bombExplode() {
        x = x / Sprite.SCALED_SIZE;
        y = y / Sprite.SCALED_SIZE;
        for (int i = 1; i <= 1; i++) {
            System.out.println(x + " " + y);
            // Destroy up side
            if (map.getMap().get(y - 1).get(x) instanceof Brick) {
                map.replace(x, y - 1, new Grass(x, y - 1, Sprite.grass.getFxImage()));
            }

            // Destroy down side
            if (map.getMap().get(y + 1).get(x) instanceof Brick) {
                map.replace(x, y + 1, new Grass(x, y + 1, Sprite.grass.getFxImage()));
            }

            // Destroy left side
            if (map.getMap().get(y).get(x - 1) instanceof Brick) {
                map.replace(x - 1, y, new Grass(x - 1, y, Sprite.grass.getFxImage()));
            }

            // Destroy right side
            if (map.getMap().get(y).get(x + 1) instanceof Brick) {
                map.replace(x + 1, y, new Grass(x + 1, y, Sprite.grass.getFxImage()));
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (bombStatus == status.REMAIN) {
            super.render(gc);
        }
        if (bombStatus == status.EXPLODED) {
            flameExplode.forEach(g-> g.render(gc));
        }
    }
}
