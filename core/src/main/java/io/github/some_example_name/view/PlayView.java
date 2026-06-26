package io.github.some_example_name.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.model.entities.Enemy;
import io.github.some_example_name.model.entities.Knight;
import io.github.some_example_name.model.entities.MossFly;
import io.github.some_example_name.model.entities.Mosscreep;

public class PlayView {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;

    private final OrthogonalTiledMapRenderer mapRenderer;

    private Texture knightTexture, runTexture, dashTexture, idleTexture, airBorneTexture, slashTexture, downSlashTexture, doubleJumpTexture, wallSlidingTexture, wallJumpingTexture, downSlashEffect, slashEffect ;
    private Animation<TextureRegion> runAnimation, dashAnimation, idleAnimation, jumpAnimation, fallingAnimation, slashAnimation, downSlashAnimation, doubleJumpAnimation, wallSlidingAnimation, wallJumpingAnimation, downSlashEffectAnimation, slashEffectAnimation;

    private Texture mossCreepWalk, mossCreepTurn, mossCreepDeathAir, mossCreepDeathLand;
    private Animation<TextureRegion> mossCreepWalkAnimation, mossCreepTurnAnimation, mossCreepDeathAirAnimation, mossCreepDeathLandAnimation;

    private Texture mossFlyShaking, mossFlyAppearing, mossFlyFlying, mossFlyTurning;
    private Animation<TextureRegion> mossFlyShakingAnimation, mossFlyAppearingAnimation, mossFlyFlyingAnimation, mossFlyTurningAnimation;

    private final int[] backgroundLayers;
    private final int[] midLayer;
    private final int[] foregroundLayers;

    public PlayView(TiledMap map) {
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();

        this.mapRenderer = new OrthogonalTiledMapRenderer(map);

        loadTexture();
        loadAnimations();

        /*
        int blackLayer = map.getLayers().getIndex("black layer");
        int blueLayer = map.getLayers().getIndex("blue layer");
        int bg3 = map.getLayers().getIndex("bg3");
        int bg2 = map.getLayers().getIndex("bg2");
        int bg1 = map.getLayers().getIndex("bg1");
        int bg = map.getLayers().getIndex("bg");
        int bg0 = map.getLayers().getIndex("bg0");

        int tileLayer1Index = map.getLayers().getIndex("Tile Layer 1");
        int wall = map.getLayers().getIndex("wall");

        int fg = map.getLayers().getIndex("fg");
        int fg2 = map.getLayers().getIndex("fg2");

        this.backgroundLayers = new int[] { blueLayer, blackLayer, bg3, bg2, bg1, bg, bg0 };
        this.midLayer = new int[] {tileLayer1Index, wall};
        this.foregroundLayers = new int[] { fg, fg2};
         */

        int blackLayer = map.getLayers().getIndex("black layer");
        int greenLayer = map.getLayers().getIndex("greenlayer");
        int bg_1 = map.getLayers().getIndex("bg-1");
        int bg = map.getLayers().getIndex("bg");
        int bg1 = map.getLayers().getIndex("bg1");
        int bg2 = map.getLayers().getIndex("bg2");
        int bg3 = map.getLayers().getIndex("bg3");
        int bg4 = map.getLayers().getIndex("bg4");

        int breakableWall = map.getLayers().getIndex("breakable wall");
        int ground = map.getLayers().getIndex("ground");


        int fg = map.getLayers().getIndex("fg");
        int fg0 = map.getLayers().getIndex("fg0");
        int fg1 = map.getLayers().getIndex("fg1");

        this.backgroundLayers = new int[] { blackLayer, greenLayer, bg_1, bg, bg1, bg2, bg3, bg4 };
        this.midLayer = new int[] {breakableWall, ground};
        this.foregroundLayers = new int[] { fg, fg0, fg1};
    }

    public void loadTexture() {
        this.runTexture = new Texture(Gdx.files.internal("Run.png"));
        this.knightTexture = new Texture(Gdx.files.internal("Run To Idle_005.png"));
        this.dashTexture = new Texture(Gdx.files.internal("Dash.png"));
        this.idleTexture = new Texture(Gdx.files.internal("Idle.png"));
        this.airBorneTexture = new Texture(Gdx.files.internal("Airborne.png"));
        this.slashTexture = new Texture(Gdx.files.internal("Slash.png"));
        this.downSlashTexture = new Texture(Gdx.files.internal("DownSlash.png"));
        this.doubleJumpTexture = new Texture(Gdx.files.internal("Double Jump.png"));
        this.wallSlidingTexture = new Texture(Gdx.files.internal("Wall Slide.png"));
        this.wallJumpingTexture = new Texture(Gdx.files.internal("Walljump.png"));
        this.downSlashEffect = new Texture(Gdx.files.internal("DownSlashEffect.png"));
        this.slashEffect = new Texture(Gdx.files.internal("SlashEffect.png"));

        this.mossCreepWalk = new Texture(Gdx.files.internal("mosscreep/Walk.png"));
        this.mossCreepTurn = new Texture(Gdx.files.internal("mosscreep/Turn.png"));
        this.mossCreepDeathAir = new Texture(Gdx.files.internal("mosscreep/Death Air.png"));
        this.mossCreepDeathLand = new Texture(Gdx.files.internal("mosscreep/Death Land.png"));

        this.mossFlyShaking = new Texture(Gdx.files.internal("mossFly/Shake.png"));
        this.mossFlyAppearing = new Texture(Gdx.files.internal("mossFly/Appear.png"));
        this.mossFlyFlying = new Texture(Gdx.files.internal("mossFly/Fly.png"));
        this.mossFlyTurning = new Texture(Gdx.files.internal("mossFly/TurnToFly.png"));
    }

    public void loadAnimations() {
        // Run Animation
        int frameWidth = runTexture.getWidth() / 13;
        int frameHeight = runTexture.getHeight();
        TextureRegion[][] temp = TextureRegion.split(runTexture, frameWidth, frameHeight);
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < 13; i++) frames.add(temp[0][i]);
        runAnimation = new Animation<>(0.07f, frames, Animation.PlayMode.LOOP);

        // Dash Animation
        frameWidth = dashTexture.getWidth() / 12;
        frameHeight = dashTexture.getHeight();
        temp = TextureRegion.split(dashTexture, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 12; i++) frames.add(temp[0][i]);
        dashAnimation = new Animation<>(0.04f, frames, Animation.PlayMode.NORMAL);

        // Idle Animation
        frameWidth = idleTexture.getWidth() / 9;
        frameHeight = idleTexture.getHeight();
        temp = TextureRegion.split(idleTexture, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 9; i++) frames.add(temp[0][i]);
        idleAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);

        // Airborne Animation
        frameWidth = airBorneTexture.getWidth() / 12;
        frameHeight = airBorneTexture.getHeight();
        temp = TextureRegion.split(airBorneTexture, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 12; i++) frames.add(temp[0][i]);

        jumpAnimation = new Animation<>(0.08f, frames, Animation.PlayMode.NORMAL);

        frames.clear();
        for (int i = 7; i < 12; i++) frames.add(temp[0][i]);
        for (int i = 11; i >= 8; i--) frames.add(temp[0][i]);

        fallingAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);

        // Slash Animation

        frameWidth = slashTexture.getWidth() / 5;
        frameHeight = slashTexture.getHeight();
        temp = TextureRegion.split(slashTexture, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 5; i++) frames.add(temp[0][i]);

        slashAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);

        // Slash Effect Animation

        frameWidth = slashEffect.getWidth() / 5;
        frameHeight = slashEffect.getHeight();
        temp = TextureRegion.split(slashEffect, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 5; i++) frames.add(temp[0][i]);

        slashEffectAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);

        // DownSlash Animation
        frameWidth = downSlashTexture.getWidth() / 5;
        frameHeight = downSlashTexture.getHeight();
        temp = TextureRegion.split(downSlashTexture, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 5; i++) frames.add(temp[0][i]);
        downSlashAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);

        // DownSlash Effect Animation

        frameWidth = downSlashEffect.getWidth() / 5;
        frameHeight = downSlashEffect.getHeight();
        temp = TextureRegion.split(downSlashEffect, frameWidth, frameHeight);
        frames.clear();
        for (int i = 4; i >= 0; i--) frames.add(temp[0][i]);
        downSlashEffectAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);


        // DoubleJump Animation
        frameWidth = doubleJumpTexture.getWidth() / 8;
        frameHeight = doubleJumpTexture.getHeight();
        temp = TextureRegion.split(doubleJumpTexture, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 8; i++) frames.add(temp[0][i]);
        doubleJumpAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);

        // Wall Sliding Animation

        frameWidth = wallSlidingTexture.getWidth() / 4;
        frameHeight = wallSlidingTexture.getHeight();
        temp = TextureRegion.split(wallSlidingTexture, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 4; i++) frames.add(temp[0][i]);
        wallSlidingAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);

        // Wall Jumping Animation
        frameWidth = wallJumpingTexture.getWidth() / 9;
        frameHeight = wallJumpingTexture.getHeight();
        temp = TextureRegion.split(wallJumpingTexture, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 9; i++) frames.add(temp[0][i]);
        wallJumpingAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);

        // MossCreep Walking Animation
        frameWidth = mossCreepWalk.getWidth() / 3;
        frameHeight = mossCreepWalk.getHeight();
        temp = TextureRegion.split(mossCreepWalk, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 3; i++) frames.add(temp[0][i]);
        mossCreepWalkAnimation = new Animation<>(0.2f, frames, Animation.PlayMode.LOOP);

        // MossCreep Turning Animation
        frameWidth = mossCreepTurn.getWidth() / 3;
        frameHeight = mossCreepTurn.getHeight();
        temp = TextureRegion.split(mossCreepTurn, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 3; i++) frames.add(temp[0][i]);
        mossCreepTurnAnimation = new Animation<>(0.2f, frames, Animation.PlayMode.NORMAL);


        // MossCreep DeathAir Animation
        frameWidth = mossCreepDeathAir.getWidth() / 3;
        frameHeight = mossCreepDeathAir.getHeight();
        temp = TextureRegion.split(mossCreepDeathAir, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 3; i++) frames.add(temp[0][i]);
        mossCreepDeathAirAnimation = new Animation<>(0.2f, frames, Animation.PlayMode.NORMAL);


        // MossCreep Walking Animation
        frameWidth = mossCreepDeathLand.getWidth() / 2;
        frameHeight = mossCreepDeathLand.getHeight();
        temp = TextureRegion.split(mossCreepDeathLand, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 2; i++) frames.add(temp[0][i]);
        mossCreepDeathLandAnimation = new Animation<>(0.3f, frames, Animation.PlayMode.LOOP);

        // MossFly Shaking Animation

        frameWidth = mossFlyShaking.getWidth() / 3;
        frameHeight = mossFlyShaking.getHeight();
        temp = TextureRegion.split(mossFlyShaking, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 3; i++) frames.add(temp[0][i]);
        mossFlyShakingAnimation = new Animation<>(0.2f, frames, Animation.PlayMode.LOOP);

        // MossFly appearing Animation

        frameWidth = mossFlyAppearing.getWidth() / 6;
        frameHeight = mossFlyAppearing.getHeight();
        temp = TextureRegion.split(mossFlyAppearing, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 6; i++) frames.add(temp[0][i]);
        mossFlyAppearingAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);


        // MossFly Flying Animation

        frameWidth = mossFlyFlying.getWidth() / 4;
        frameHeight = mossFlyFlying.getHeight();
        temp = TextureRegion.split(mossFlyFlying, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 4; i++) frames.add(temp[0][i]);
        mossFlyFlyingAnimation = new Animation<>(0.15f, frames, Animation.PlayMode.LOOP);

        // MossFly Turning Animation

        frameWidth = mossFlyTurning.getWidth() / 3;
        frameHeight = mossFlyTurning.getHeight();
        temp = TextureRegion.split(mossFlyTurning, frameWidth, frameHeight);
        frames.clear();
        for (int i = 0; i < 3; i++) frames.add(temp[0][i]);
        mossFlyTurningAnimation = new Animation<>(0.2f, frames, Animation.PlayMode.NORMAL);

    }

    public void render(Knight knight, Array<Enemy> enemies, OrthographicCamera camera) {
        mapRenderer.setView(camera);

        mapRenderer.render(backgroundLayers);
        mapRenderer.render(midLayer);


        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        TextureRegion currentFrame;
        Knight.State currenState = knight.currentState;
        float stateDuration = knight.stateDuration;

        TextureRegion currentSlashEffect;

        if (currenState == Knight.State.RUNNING) {
            currentFrame = runAnimation.getKeyFrame(stateDuration, true);
        } else if (currenState == Knight.State.DASHING) {
            currentFrame = dashAnimation.getKeyFrame(stateDuration, false);
        } else if (currenState == Knight.State.STANDING) {
            currentFrame = idleAnimation.getKeyFrame(stateDuration, true);
        } else if (currenState == Knight.State.JUMPING) {
            currentFrame = jumpAnimation.getKeyFrame(stateDuration, false);
        } else if (currenState == Knight.State.FALLING) {
            currentFrame = fallingAnimation.getKeyFrame(stateDuration, true);
        } else if (currenState == Knight.State.ATTACKING_DOWN) {
            currentFrame = downSlashAnimation.getKeyFrame(stateDuration, false);
            currentSlashEffect = downSlashEffectAnimation.getKeyFrame(stateDuration, false);

            batch.draw(
                currentSlashEffect.getTexture(),
                knight.positionX - (knight.width / 2),
                knight.positionY - 40f,
                knight.width, 40f,
                currentFrame.getRegionX(), currentFrame.getRegionY(),
                currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),
                knight.isGoingRight, false
            );
        } else if (currenState == Knight.State.ATTACKING) {
            currentFrame = slashAnimation.getKeyFrame(stateDuration, false);
            currentSlashEffect = slashEffectAnimation.getKeyFrame(stateDuration, false);

            batch.draw(
                currentSlashEffect.getTexture(),
                (knight.isGoingRight ? (knight.positionX + knight.hitBox.width) : (knight.positionX - 80f)),
                knight.positionY,
                80f, knight.height,
                currentFrame.getRegionX(), currentFrame.getRegionY(),
                currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),
                knight.isGoingRight, false
            );
        } else if (currenState == Knight.State.DOUBLE_JUMPING) {
            currentFrame = doubleJumpAnimation.getKeyFrame(stateDuration, false);
        } else if (currenState == Knight.State.WALL_SLIDING) {
            currentFrame = wallSlidingAnimation.getKeyFrame(stateDuration, true);
        } else if (currenState == Knight.State.WALL_JUMP) {
            currentFrame = wallJumpingAnimation.getKeyFrame(stateDuration, false);
        } else {
            currentFrame = idleAnimation.getKeyFrame(stateDuration, true);
        }

        batch.draw(
            currentFrame.getTexture(),
            knight.positionX - 90f, knight.positionY - 2f,
            knight.width, knight.height,
            currentFrame.getRegionX(), currentFrame.getRegionY(),
            currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),
            knight.isGoingRight, false
        );

        for (Enemy enemy : enemies) {
            TextureRegion currentEnemyFrame = null;

            if (enemy instanceof Mosscreep) {
                if (((Mosscreep) enemy).currentState == Mosscreep.State.TURNING) {
                    currentEnemyFrame = mossCreepTurnAnimation.getKeyFrame(enemy.stateDuration, false);
                } else if (((Mosscreep) enemy).currentState == Mosscreep.State.DEATH_AIR) {
                    currentEnemyFrame = mossCreepDeathAirAnimation.getKeyFrame(enemy.stateDuration, false);
                } else if (((Mosscreep) enemy).currentState == Mosscreep.State.DEATH_LAND) {
                    currentEnemyFrame = mossCreepDeathLandAnimation.getKeyFrame(enemy.stateDuration, true);
                } else {
                    currentEnemyFrame = mossCreepWalkAnimation.getKeyFrame(enemy.stateDuration, true);
                }
            } else if (enemy instanceof MossFly) {
                if (((MossFly) enemy).currentState == MossFly.State.TURNING_TO_FLY) {
                    currentEnemyFrame = mossFlyTurningAnimation.getKeyFrame(enemy.stateDuration, false);
                } else if (((MossFly) enemy).currentState == MossFly.State.DEATH_AIR) {
                    currentEnemyFrame = mossCreepDeathAirAnimation.getKeyFrame(enemy.stateDuration, false);
                } else if (((MossFly) enemy).currentState == MossFly.State.DEATH_LAND) {
                    currentEnemyFrame = mossCreepDeathLandAnimation.getKeyFrame(enemy.stateDuration, true);
                } else if (((MossFly) enemy).currentState == MossFly.State.FLYING) {
                    currentEnemyFrame = mossFlyFlyingAnimation.getKeyFrame(enemy.stateDuration, true);
                } else if (((MossFly) enemy).currentState == MossFly.State.APPEAR) {
                    currentEnemyFrame = mossFlyAppearingAnimation.getKeyFrame(enemy.stateDuration, false);
                } else {
                    currentEnemyFrame = mossFlyShakingAnimation.getKeyFrame(enemy.stateDuration, true);
                }
            }

            if (currentEnemyFrame != null) {
                batch.draw(
                    currentEnemyFrame.getTexture(),
                    enemy.positionX, enemy.positionY,
                    enemy.width, enemy.height,
                    currentEnemyFrame.getRegionX(), currentEnemyFrame.getRegionY(),
                    currentEnemyFrame.getRegionWidth(), currentEnemyFrame.getRegionHeight(),
                    enemy.isGoingRight, false
                );
            }
        }

        batch.end();
        mapRenderer.render(foregroundLayers);
    }

    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (knightTexture != null) knightTexture.dispose();
        if (runTexture != null) runTexture.dispose();
        if (dashTexture != null) dashTexture.dispose();
        if (idleTexture != null) idleTexture.dispose();
        if (airBorneTexture != null) airBorneTexture.dispose();
        if (downSlashTexture != null) downSlashTexture.dispose();
        if (doubleJumpTexture != null) doubleJumpTexture.dispose();
        if (wallSlidingTexture != null) wallSlidingTexture.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }
}
