package io.github.some_example_name.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.entities.*;


public class LevelController {
    public TiledMap map;
    public Array<Rectangle> platforms;
    public Array<Rectangle> spikes;
    public Array<Enemy> enemies;

    public float playerSpawnX, playerSpawnY;

    public BreakableWall breakableWall;
    public float voidHeartCharmSpawnX, voidHeartCharmSpawnY;

    public LevelController(String mapFilePath) {
        platforms = new Array<>();
        spikes = new Array<>();
        enemies = new Array<>();

        map = new TmxMapLoader().load(mapFilePath);

        loadMapData();

        for (Enemy enemy : enemies) {
                stickEnemiesToGround(enemy);

        }
    }

    private void loadMapData() {
        MapLayer solidLayer = map.getLayers().get("objects");
        float mapHeightInPixels = map.getProperties().get("height", Integer.class)
            * map.getProperties().get("tileheight", Integer.class);

        for (MapObject object : solidLayer.getObjects()) {
            String name = object.getName();

            if ("spawnPlayer".equals(name)) {
                playerSpawnX = object.getProperties().get("x", Float.class);
                playerSpawnY = object.getProperties().get("y", Float.class);
            }

            else if ("mossCreep".equals(name)) {
                float x = object.getProperties().get("x", Float.class);
                float rawY = object.getProperties().get("y", Float.class);
                enemies.add(new Mosscreep(x, mapHeightInPixels - rawY));
            }

            else if ("mossFly".equals(name)) {
                float x = object.getProperties().get("x", Float.class);
                float rawY = object.getProperties().get("y", Float.class);
                enemies.add(new MossFly(x, mapHeightInPixels - rawY));
            }

            else if ("huskHornheadSpawn".equals(name)) {
                float x = object.getProperties().get("x", Float.class);
                float rawY = object.getProperties().get("y", Float.class);
                enemies.add(new HuskHornhead(x, rawY));
            }

            else if ("CrystalGuardianSpawn".equals(name)) {
                float x = object.getProperties().get("x", Float.class);
                float rawY = object.getProperties().get("y", Float.class);
                enemies.add(new CrystalGuardian(x, rawY));
            }

            else if ("voidHeart".equals(name)) {
                voidHeartCharmSpawnX = object.getProperties().get("x", Float.class);
                voidHeartCharmSpawnY = object.getProperties().get("y", Float.class);
            }

            else if (object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                if ("spike".equals(name)) {
                    spikes.add(rectangle);
                }

                else if ("breakableWall".equals(name)) {
                    this.breakableWall = new BreakableWall(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }

                else if (!"boss fight area".equals(name)) {
                    platforms.add(rectangle);
                }
            }
        }
    }

    private void stickEnemiesToGround(Enemy enemy) {
        Rectangle tempRect = new Rectangle(enemy.hitBox.x, enemy.hitBox.y - 1000f, enemy.hitBox.width, 1000f);

        for (Rectangle bound : platforms) {
            if (tempRect.overlaps(bound)) {
                enemy.positionY = bound.y + bound.height;
                enemy.spawny = bound.y + bound.height;
                enemy.updateHitBox();
                break;
            }
        }



        /*
        for (Enemy enemy : enemies) {
            float closestFloorY = -1000f;

            for (Rectangle bound : platforms) {
                if (enemy.positionX + enemy.width > bound.x &&
                    enemy.positionX < bound.x + bound.width) {

                    if (bound.y + bound.height <= enemy.positionY + 50f) {
                        if (bound.y + bound.height > closestFloorY) {
                            closestFloorY = bound.y + bound.height;
                        }
                    }
                }
            }

            if (closestFloorY != -1000f) {
                enemy.positionY = closestFloorY + 2f;
                enemy.spawny = enemy.positionY;
                enemy.isOnGround = true;
                enemy.updateHitBox();
            }
        }
         */
    }

    public void dispose() {
        if (map != null) map.dispose();
    }
}
