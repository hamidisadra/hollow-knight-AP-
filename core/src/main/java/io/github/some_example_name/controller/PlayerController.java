package io.github.some_example_name.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.HollowKnightGame;
import io.github.some_example_name.model.Knight;
import io.github.some_example_name.model.MossFly;
import io.github.some_example_name.model.Mosscreep;
import io.github.some_example_name.view.PlayView;

public class PlayerController implements Screen {

    private float startDelay = 2f;

    private final HollowKnightGame game;
    private final PlayView playView;
    private final Knight knight;

    private final TiledMap map;
    private final Array<Rectangle> platforms;
    private final Array<Rectangle> spikes;
    private final Array<Mosscreep> mosscreeps;
    private final Array<MossFly> mossFlies;

    private float lastSafePlaceX, lastSafePlaceY;

    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport;

    private final float gravity = 2500;
    private final float speed = 800f;
    private final float dashSpeed = 1200f;
    private final float dashDuration = 0.48f;
    private final float jumpSpeed = 1300f;
    private final float doubleJumpSpeed = 1600f;
    private final float maxFallSpeed = 2500f;
    private final float jumpCutVelocity = 400f;

    private final ShapeRenderer debugRender;

    private boolean dealtDamage = false;

    public PlayerController(HollowKnightGame game) {
        this.game = game;
        this.game.menuMusic.stop();

        platforms = new Array<>();
        spikes = new Array<>();
        mosscreeps = new Array<>();
        mossFlies = new Array<>();

        map = new TmxMapLoader().load("green path map/map.tmx");


        TiledMapTileLayer mainLayer = (TiledMapTileLayer) map.getLayers().get(0);
        float mapWidth = mainLayer.getWidth() * mainLayer.getTileWidth();
        float mapHeight = mainLayer.getHeight() * mainLayer.getTileHeight();

        camera.setToOrtho(false, mapWidth / 10, mapHeight / 10);
        this.viewport = new ExtendViewport(mapWidth / 10, mapHeight / 10, camera);
        viewport.apply();

        MapLayer spawnLayer = map.getLayers().get("objects");
        MapObject spawnPoint = spawnLayer.getObjects().get("spawnPlayer");

        float spawnX = spawnPoint.getProperties().get("x", Float.class);
        float spawnY = spawnPoint.getProperties().get("y", Float.class);
        this.knight = new Knight(spawnX, spawnY);

        lastSafePlaceX = knight.positionX;
        lastSafePlaceY = knight.positionY;

        loadRectangles();
        stickToTheGround();

        this.playView = new PlayView(map);

        this.debugRender = new ShapeRenderer();

        this.game.greenPathMusic.play();
        this.game.greenPathatmosMusic.play();
    }

    private void stickToTheGround() {
        float closestFloorY = -1000f;

        for (Mosscreep mosscreep : mosscreeps) {

            for (Rectangle bound : platforms) {
                if (mosscreep.positionX + mosscreep.width > bound.x &&
                    mosscreep.positionX < bound.x + bound.width) {

                    if (bound.y + bound.height <= mosscreep.positionY + 50f) {

                        if (bound.y + bound.height > closestFloorY) {
                            closestFloorY = bound.y + bound.height;
                        }
                    }
                }
            }

            if (closestFloorY != -1000f) {
                mosscreep.positionY = closestFloorY + 2f;
                mosscreep.spawny = mosscreep.positionY;
                mosscreep.isOnGround = true;
                mosscreep.updateHitBox();
            }
        }

        closestFloorY = -1000f;

        for (MossFly mossFly : mossFlies) {

            for (Rectangle bound : platforms) {
                if (mossFly.positionX + mossFly.width > bound.x &&
                    mossFly.positionX < bound.x + bound.width) {

                    if (bound.y + bound.height <= mossFly.positionY + 50f) {

                        if (bound.y + bound.height > closestFloorY) {
                            closestFloorY = bound.y + bound.height;
                        }
                    }
                }
            }

            if (closestFloorY != -1000f) {
                mossFly.positionY = closestFloorY + 2f;
                mossFly.spawny = mossFly.positionY;
                mossFly.isOnGround = true;
                mossFly.updateHitBox();
            }
        }
    }

    private void loadRectangles() {
        MapLayer solidLayer = map.getLayers().get("objects");

        float mapHeightInPixels = map.getProperties().get("height", Integer.class)
            * map.getProperties().get("tileheight", Integer.class);

            for (MapObject object : solidLayer.getObjects()) {
                if ("mossCreep".equals(object.getName())) {
                    float x = object.getProperties().get("x", Float.class);
                    float rawY = object.getProperties().get("y", Float.class);

                    mosscreeps.add(new Mosscreep(x, mapHeightInPixels - rawY));
                }

                else if ("mossFly".equals(object.getName())) {
                    float x = object.getProperties().get("x", Float.class);
                    float rawY = object.getProperties().get("y", Float.class);

                    mossFlies.add(new MossFly(x, mapHeightInPixels - rawY));
                }

                else if (object instanceof RectangleMapObject) {
                    Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                    if ("spike".equals(object.getName())) {
                        spikes.add(rectangle);
                    }
                    else if (!"boss fight area".equals(object.getName())){
                        platforms.add(rectangle);
                    }
                }
            }

    }

    private void update(float delta) {

        if (startDelay > 0) {
            startDelay -= delta;

            camera.position.x = knight.positionX + knight.width / 2;
            camera.position.y = knight.positionY + knight.height / 2;
            camera.update();

            return;
        }

        handleDash(delta);
        handleHorizontalInput();
        handleJumpInput();
        applyGravity(delta);
        handleAttackInput(delta);

        moveHorizontal(delta);
        resolveHorizontalCollisions();

        moveVertical(delta);
        resolveVerticalCollisions(delta);

        updatePogoState();
        isPogo();

        knightStateUpdate(delta);

        spikesHandler();

        updateMosscreeps(delta);
        updateMossFlies(delta);

        camera.position.x = knight.positionX;
        camera.position.y = knight.positionY;
        camera.update();
    }

    private void updateMossFlies(float delta) {
        for (int i = mossFlies.size - 1; i >= 0; i--) {
            MossFly mossFly = mossFlies.get(i);

            float deltaX = knight.positionX - mossFly.positionX;
            float deltaY = knight.positionY - mossFly.positionY;
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            float distanceToSpawn = Math.abs(knight.positionX - mossFly.spawnX);

            if (mossFly.isDead && distanceToSpawn > 1500f) {
                mossFly.isDead = false;
                mossFly.hp = 3;
                mossFly.positionX = mossFly.spawnX;
                mossFly.positionY = mossFly.spawny;
                mossFly.hitBox.height = 80f;

                mossFly.isShaking = true;
                mossFly.isAppearing = false;
                mossFly.velocityX = 0;
                mossFly.velocityY = 0;
            }

            if (mossFly.isDead) {
                mossFly.positionY -= gravity * delta;
                mossFly.isOnGround = false;

                for (Rectangle bound : platforms) {
                    if (mossFly.hitBox.overlaps(bound)) {
                        if (mossFly.positionY + 20f > bound.y + bound.height) {
                            mossFly.positionY = bound.y + bound.height;
                            mossFly.isOnGround = true;
                            break;
                        }
                    }
                }
                mossFly.updateHitBox();
                mossFlyStateUpdate(mossFly, delta);

                continue;
            }

            if (mossFly.isShaking) {
                if (distance < 500f) {
                    mossFly.isShaking = false;
                    mossFly.isAppearing = true;
                    mossFly.actionTimer = 0.6f;
                }
            }

            else if (mossFly.isAppearing) {
                mossFly.actionTimer -= delta;
                if (mossFly.actionTimer <= 0) {
                    mossFly.isAppearing = false;
                }
            }

            else {
                if (distance > 0) {
                    mossFly.velocityX = (deltaX / distance) * mossFly.speed;
                    mossFly.velocityY = (deltaY / distance) * mossFly.speed;
                }

                mossFly.isGoingRight = mossFly.velocityX > 0;

                mossFly.positionX += mossFly.velocityX * delta;
                mossFly.updateHitBox();

                for (Rectangle bound : platforms) {
                    if (mossFly.hitBox.overlaps(bound)) {

                        if (mossFly.velocityX > 0) {
                            mossFly.positionX = bound.x - mossFly.width;
                        } else if (mossFly.velocityX < 0) {
                            mossFly.positionX = bound.x + bound.width;
                        }
                        mossFly.updateHitBox();
                    }
                }

                mossFly.positionY += mossFly.velocityY * delta;
                mossFly.updateHitBox();

                for (Rectangle bound : platforms) {
                    if (mossFly.hitBox.overlaps(bound)) {

                        if (mossFly.velocityY > 0) {
                            mossFly.positionY = bound.y - mossFly.height;
                        } else if (mossFly.velocityY < 0) {
                            mossFly.positionY = bound.y + bound.height;
                        }
                        mossFly.updateHitBox();
                    }
                }

                if (knight.isAttacking && knight.attackHitBox.overlaps(mossFly.hitBox)) {
                    mossFly.hp--;
                    mossFly.positionX += mossFly.isGoingRight ? -70f : 70f;
                    knight.attackHitBox.set(0, 0, 0, 0);

                    dealtDamage = true;

                    if (mossFly.hp <= 0) {
                        mossFly.isDead = true;
                        mossFly.isShaking = false;
                        mossFly.isAppearing = false;
                        mossFly.velocityY = 0;
                        mossFly.velocityX = 0;
                        mossFly.hitBox.height = 30f;
                    }
                }
            }

            mossFlyStateUpdate(mossFly, delta);
        }
    }

    private void mossFlyStateUpdate(MossFly mossFly, float delta) {

        mossFly.previousState = mossFly.currentState;
        mossFly.currentState = mossFly.getState();

        if (mossFly.previousState == mossFly.currentState) {
            mossFly.stateDuration += delta;
        }

        else {
            mossFly.stateDuration = 0;
        }
    }

    private void updateMosscreeps(float delta) {

        for (int i = mosscreeps.size - 1; i >= 0; i--) {
            Mosscreep mosscreep = mosscreeps.get(i);

            float previousY = mosscreep.positionY;

            mosscreep.positionY -= gravity * delta;
            mosscreep.updateHitBox();

            mosscreep.isOnGround = false;

            for (Rectangle bound : platforms) {
                if (mosscreep.hitBox.overlaps(bound)) {
                    if (previousY >= bound.y + bound.height - 15f) {
                        mosscreep.positionY = bound.y + bound.height + 2f;
                        mosscreep.isOnGround = true;
                        mosscreep.updateHitBox();
                        break;
                    }
                }
            }

            if (mosscreep.isOnGround && !mosscreep.isDead && !mosscreep.isTurning) {
                mosscreep.positionX += mosscreep.velocityX * delta;
                mosscreep.updateHitBox();

                boolean hitWall = false;
                for (Rectangle bound : platforms) {
                    if (mosscreep.hitBox.overlaps(bound)) {
                        hitWall = true;
                        if (mosscreep.velocityX > 0) {
                            mosscreep.positionX = bound.x - mosscreep.width;
                        }
                        else if (mosscreep.velocityX < 0) {
                            mosscreep.positionX = bound.x + bound.width;
                        }
                        mosscreep.updateHitBox();
                        break;
                    }
                }

                for (Rectangle spike : spikes) {
                    if (mosscreep.hitBox.overlaps(spike)) {
                        mosscreep.isDead = true;
                        mosscreep.velocityX = 0;
                        mosscreep.hitBox.height = 20f;
                    }
                }

                if (hitWall) {
                    mosscreep.isTurning = true;
                    mosscreep.turnTimer = 0.6f;
                }
            }

            if (mosscreep.isTurning) {
                mosscreep.turnTimer -= delta;
                if (mosscreep.turnTimer <= 0) {
                    mosscreep.isTurning = false;
                    mosscreep.isGoingRight = !mosscreep.isGoingRight;
                    mosscreep.velocityX = mosscreep.isGoingRight ? mosscreep.speed : -mosscreep.speed;
                }
            }

            mosscreep.updateHitBox();

            if (knight.isAttacking && knight.attackHitBox.overlaps(mosscreep.hitBox)) {
                mosscreep.hp--;
                mosscreep.positionX += knight.isGoingRight ? 70f : -70f;
                knight.attackHitBox.set(0, 0, 0, 0);

                dealtDamage = true;

                if (mosscreep.hp <= 0) {
                    mosscreep.isDead = true;
                    mosscreep.velocityX = 0;
                    mosscreep.hitBox.height = 20f;
                }
            }

            float distance = Math.abs(knight.positionX - mosscreep.positionX);

            if (mosscreep.isDead && distance > 1500f) {
                mosscreep.isDead = false;
                mosscreep.hp = 3;
                mosscreep.positionX = mosscreep.spawnX;
                mosscreep.positionY = mosscreep.spawny;

                mosscreep.hitBox.height = 40f;
                mosscreep.velocityX = mosscreep.isGoingRight ? mosscreep.speed : -mosscreep.speed;

                mosscreep.updateHitBox();
            }

            mossCreepStateUpdate(mosscreep, delta);
        }
    }

    private void spikesHandler() {

        if (spikes.isEmpty()) return;

        boolean touchSpikes = false;
        for (Rectangle spike : spikes) {
            if (knight.hitBox.overlaps(spike)) {
                touchSpikes = true;
                break;
            }
        }

        if (knight.isOnGround && !touchSpikes) {
            lastSafePlaceX = knight.positionX;
            lastSafePlaceY = knight.positionY;
        }

        for (Rectangle spike : spikes) {
            if (knight.isPogo) {
                break;
            }

            if (knight.hitBox.overlaps(spike)) {
                knight.positionX = lastSafePlaceX;
                knight.positionY = lastSafePlaceY + 40;
                knight.velocityX = 0;
                knight.velocityY = 0;
                knight.updateHitBox();
                break;
            }
        }
    }

    private void knightStateUpdate(float delta) {
        knight.previousState = knight.currentState;
        knight.currentState = knight.getState();

        if (knight.previousState == knight.currentState) {
            knight.stateDuration += delta;
        } else {
            knight.stateDuration = 0f;
        }
    }

    private void mossCreepStateUpdate(Mosscreep mosscreep, float delta) {
        mosscreep.previousState = mosscreep.currentState;
        mosscreep.currentState = mosscreep.getState();

        if (mosscreep.previousState == mosscreep.currentState) {
            mosscreep.stateDuration += delta;
        } else {
            mosscreep.stateDuration = 0f;
        }
    }

    private void handleDash(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.C) && !knight.isDashing) {
            knight.isDashing = true;
            knight.dashTimer = dashDuration;
            knight.velocityY = 0;
        }

        if (knight.isDashing) {
            knight.dashTimer -= delta;
            knight.velocityX = knight.isGoingRight ? dashSpeed : -dashSpeed;

            if (knight.dashTimer <= 0) {
                knight.isDashing = false;
            }
        }
    }

    private void handleHorizontalInput() {
        if (knight.isDashing) return;

        knight.velocityX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            knight.velocityX = -speed;
            knight.isGoingRight = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            knight.velocityX = speed;
            knight.isGoingRight = true;
        }
    }

    private void handleJumpInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            if (knight.isOnGround) {
                knight.velocityY = jumpSpeed;
                knight.isOnGround = false;
                knight.canDoubleJump = true;
            } else if (knight.isWallSliding) {
                knight.velocityY = jumpSpeed;
                knight.isWallJumping = true;
                knight.canDoubleJump = true;
                knight.isWallSliding = false;
            } else if (knight.canDoubleJump) {
                knight.velocityY = doubleJumpSpeed;
                knight.canDoubleJump = false;
                knight.isPogo = false;
                knight.isDoubleJumping = true;
                knight.isWallJumping = false;
            }
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.Z) && knight.velocityY > jumpCutVelocity && !knight.isDashing && !knight.isPogo) {
            knight.velocityY = jumpCutVelocity;
        }
    }

    private void applyGravity(float delta) {
        if (knight.isDashing) return;

        knight.velocityY -= gravity * delta;

        if (knight.isWallSliding) {
            if (knight.velocityY < -200f) {
                knight.velocityY = -200f;
            }
        }
        else {
            if (knight.velocityY < -maxFallSpeed) {
                knight.velocityY = -maxFallSpeed;
            }
        }
    }

    private void handleAttackInput(float delta) {
        if (knight.isAttacking) {
            knight.attackTimer -= delta;

            if (!Gdx.input.isKeyPressed(Input.Keys.DOWN) && !dealtDamage) {
                float attackWidth = 80f;

                if (knight.isGoingRight) {
                    knight.attackHitBox.set(knight.positionX + knight.hitBox.width, knight.positionY, attackWidth, knight.height);
                }

                else {
                    knight.attackHitBox.set(knight.positionX - attackWidth, knight.positionY, attackWidth, knight.height);
                }
            }

            if (knight.attackTimer <= 0) {
                knight.isAttacking = false;
                knight.attackHitBox.set(0, 0, 0, 0);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.X) && !knight.isAttacking) {
            knight.isAttacking = true;
            knight.attackTimer = 0.5f;
            dealtDamage = false;
        }
    }

    private void moveHorizontal(float delta) {
        knight.positionX += knight.velocityX * delta;
        knight.updateHitBox();
    }

    private void moveVertical(float delta) {
        knight.positionY += knight.velocityY * delta;
        knight.updateHitBox();
        knight.isOnGround = false;
    }

    private void resolveHorizontalCollisions() {
        boolean isWallSliding = false;

            Rectangle horizontalHitBox = new Rectangle(
            knight.hitBox.x,
            knight.hitBox.y + 2f,
            knight.hitBox.width ,
            knight.hitBox.height - 4f
        );

        for (Rectangle bounds : platforms) {
            if (horizontalHitBox.overlaps(bounds)) {
                if (knight.velocityX > 0) {
                    knight.positionX = bounds.x - knight.hitBox.width;
                    if (!knight.isOnGround && Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                        isWallSliding = true;
                    }
                } else if (knight.velocityX < 0) {
                    knight.positionX = bounds.x + bounds.width;
                    if (!knight.isOnGround && Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                        isWallSliding = true;
                    }
                }
                knight.velocityX = 0;
                knight.updateHitBox();
                horizontalHitBox.x = knight.positionX;
            }
        }

        knight.isWallSliding = isWallSliding;
    }

    private void resolveVerticalCollisions(float delta) {
        Rectangle verticalHitBox = new Rectangle(
            knight.hitBox.x + 2f,
            knight.hitBox.y,
            knight.hitBox.width - 4f,
            knight.hitBox.height
        );

        float prevY = knight.positionY - (knight.velocityY * delta);

        for (Rectangle bounds : platforms) {
            if (verticalHitBox.overlaps(bounds)) {
                if (knight.velocityY < 0) {
                    if (prevY >= bounds.y + bounds.height - 25f) {
                        knight.positionY = bounds.y + bounds.height;
                        knight.isOnGround = true;
                        knight.canDoubleJump = true;
                        knight.isWallJumping = false;
                        knight.velocityY = 0;
                    }
                } else if (knight.velocityY > 0) {
                    if (prevY + knight.hitBox.height <= bounds.y + 25f) {
                        knight.positionY = bounds.y - knight.hitBox.height;
                        knight.velocityY = 0;
                    }
                }

                knight.updateHitBox();
                verticalHitBox.y = knight.positionY;
            }
        }
    }

    private void updatePogoState() {
        if (knight.velocityY <= 0) {
            knight.isPogo = false;
            knight.isDoubleJumping = false;
            knight.isWallJumping = false;
        }
    }

    private void isPogo() {
        if (!knight.isAttacking) return;

        if (!knight.isOnGround && Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            knight.attackHitBox.set(knight.positionX, knight.positionY - 40, knight.width, 40);

            for (Rectangle spike : spikes) {
                if (knight.attackHitBox.overlaps(spike)) {
                    knight.velocityY = jumpSpeed;
                    knight.canDoubleJump = true;
                    knight.isDashing = false;
                    knight.isPogo = true;
                    break;
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        playView.render(knight,mosscreeps, mossFlies, camera);

    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        playView.dispose();
        map.dispose();
    }
}
