package io.github.some_example_name.model;

import com.badlogic.gdx.math.Rectangle;

public class MossFly {

    public enum State {
        SHAKING, FLYING, TURNING_TO_FLY, APPEAR, DEATH_AIR, DEATH_LAND
    }

    public float positionX, positionY;
    public float spawnX, spawny;
    public float width = 100f, height = 80f;
    public float velocityX, velocityY;
    public float speed = 120f;
    public boolean isGoingRight = true, isDead = false, isOnGround = false;

    public int hp = 3;
    public Rectangle hitBox;

    public boolean isShaking = true, isAppearing = false;
    public float actionTimer = 0f;

    public MossFly.State currentState = State.SHAKING;
    public MossFly.State previousState = State.SHAKING;
    public float stateDuration = 0f;

    public MossFly(float x, float y) {
        this.positionX = x;
        this.positionY = y;
        this.spawnX = x;
        this.spawny = y;

        this.hitBox = new Rectangle(x, y, width, height);
        this.velocityX = 0;
        this.velocityY = 0;
    }

    public void updateHitBox() {
        hitBox.setPosition(this.positionX, this.positionY);
    }


    public MossFly.State getState() {
        if (isDead) {
            if (!isOnGround) {
                return State.DEATH_AIR;
            }
            else {
                return State.DEATH_LAND;
            }
        }

        if (isShaking) return State.SHAKING;

        if (isAppearing) {
            return State.APPEAR;
        }

        return State.FLYING;
    }
}
