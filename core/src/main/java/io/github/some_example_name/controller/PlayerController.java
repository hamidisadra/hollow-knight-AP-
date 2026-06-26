package io.github.some_example_name.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import io.github.some_example_name.model.entities.Enemy;
import io.github.some_example_name.model.entities.Knight;
import io.github.some_example_name.model.entities.MossFly;
import io.github.some_example_name.model.entities.Mosscreep;
import io.github.some_example_name.view.PlayView;

public class PlayerController implements Screen {

    private float startDelay = 2f;

    private final HollowKnightGame game;
    private final PlayView playView;
    private final Knight knight;

    private final LevelController levelController;

    private float lastSafePlaceX, lastSafePlaceY;

    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport;

    private final float gravity = 2500;
    private final float speed = 800f;
    private final float dashSpeed = 1200f;
    private final float dashDuration = 0.48f;
    private final float jumpSpeed = 1500f;
    private final float doubleJumpSpeed = 1600f;
    private final float maxFallSpeed = 2500f;
    private final float jumpCutVelocity = 400f;

    private final ShapeRenderer debugRender;

    private boolean dealtDamage = false;

    public PlayerController(HollowKnightGame game) {
        this.game = game;
        this.game.menuMusic.stop();


        this.levelController = new LevelController("green path map/map.tmx");


        TiledMapTileLayer mainLayer = (TiledMapTileLayer) levelController.map.getLayers().get(0);
        float mapWidth = mainLayer.getWidth() * mainLayer.getTileWidth();
        float mapHeight = mainLayer.getHeight() * mainLayer.getTileHeight();

        camera.setToOrtho(false, mapWidth / 10, mapHeight / 10);
        this.viewport = new ExtendViewport(mapWidth / 10, mapHeight / 10, camera);
        viewport.apply();

        this.knight = new Knight(levelController.playerSpawnX, levelController.playerSpawnY);

        lastSafePlaceX = knight.positionX;
        lastSafePlaceY = knight.positionY;


        this.playView = new PlayView(levelController.map);
        this.debugRender = new ShapeRenderer();

        this.game.greenPathMusic.play();
        this.game.greenPathatmosMusic.play();
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

        for (Enemy enemy : levelController.enemies) {
            if (enemy.update(delta, gravity, knight, levelController.platforms, levelController.spikes)) {
                dealtDamage = true;
            }
        }

        camera.position.x = knight.positionX;
        camera.position.y = knight.positionY;
        camera.update();
    }


    private void spikesHandler() {

        if (levelController.spikes.isEmpty()) return;

        boolean touchSpikes = false;
        for (Rectangle spike : levelController.spikes) {
            if (knight.hitBox.overlaps(spike)) {
                touchSpikes = true;
                break;
            }
        }

        if (knight.isOnGround && !touchSpikes) {
            lastSafePlaceX = knight.positionX;
            lastSafePlaceY = knight.positionY;
        }

        for (Rectangle spike : levelController.spikes) {
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

        for (Rectangle bounds : levelController.platforms) {
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

        for (Rectangle bounds : levelController.platforms) {
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
            knight.attackHitBox.set(knight.positionX - (knight.width / 2), knight.positionY - 40f, knight.width, 40f);

            for (Rectangle spike : levelController.spikes) {
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

        playView.render(knight, levelController.enemies, camera);

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
        levelController.dispose();
    }
}
