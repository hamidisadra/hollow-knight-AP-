package io.github.some_example_name.model;

import com.badlogic.gdx.math.Rectangle;

public class Mosscreep {

    public enum State {
        WALKING, TURNING, DEATH_AIR, DEATH_LAND
    }

    public float positionX, positionY;
    public float spawnX, spawny;
    public float width = 120f, height = 100f;
    public float velocityX;
    public float speed = 150f;
    public boolean isGoingRight = true, isDead = false, isOnGround = false;

    public int hp = 3;
    public Rectangle hitBox;

    public boolean isWalking, isTurning, isDying;
    public float turnTimer = 0f;

    public Mosscreep.State currentState = State.WALKING;
    public Mosscreep.State previousState = State.WALKING;
    public float stateDuration = 0f;

    public Mosscreep(float x, float y) {
        this.positionX = x;
        this.positionY = y;
        this.spawnX = x;
        this.spawny = y;

        this.hitBox = new Rectangle(x, y, width, height);
        this.velocityX = speed;
    }

    public void updateHitBox() {
        hitBox.setPosition(positionX, positionY);
    }


    public State getState() {
        if (isDead) {
            if (isOnGround) {
                return State.DEATH_LAND;
            }
            else {
                return State.DEATH_AIR;
            }
        }

        if (isTurning) {
            return State.TURNING;
        }

        return State.WALKING;
    }
}
