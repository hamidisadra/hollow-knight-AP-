package io.github.some_example_name.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Mosscreep extends Enemy{

    public enum State {
        WALKING, TURNING, DEATH_AIR, DEATH_LAND
    }

    public boolean isTurning = false;
    public float turnTimer = 0f;

    public Mosscreep.State currentState = State.WALKING;
    public Mosscreep.State previousState = State.WALKING;


    public Mosscreep(float x, float y) {
        this.positionX = x;
        this.positionY = y;
        this.spawnX = x;
        this.spawny = y;
        this.width = 120f;
        this.height = 100f;
        this.speed = 100f;
        this.hp = 3;

        this.hitBox = new Rectangle(x, y, width, height);
        this.velocityX = speed;
        this.velocityY = 0f;
    }

    @Override
    public boolean update(float delta, float gravity, Knight knight, Array<Rectangle> platforms, Array<Rectangle> spikes) {
        boolean dealtDamage = false;

        float previousY = this.positionY;

        this.positionY -= gravity * delta;
        this.updateHitBox();

        this.isOnGround = false;

        for (Rectangle bound : platforms) {
            if (this.hitBox.overlaps(bound)) {
                if (previousY >= bound.y + bound.height - 15f) {
                    this.positionY = bound.y + bound.height + 2f;
                    this.isOnGround = true;
                    this.updateHitBox();
                    break;
                }
            }
        }

        if (this.isOnGround && !this.isDead && !this.isTurning) {
            this.positionX += this.velocityX * delta;
            this.updateHitBox();

            Rectangle wallSensor = new Rectangle(
                isGoingRight ? positionX + width : positionX - 2f,
                positionY + 10f,
                2f, height - 20f
            );

            Rectangle groundSensor = new Rectangle(
                isGoingRight ? positionX + width + 2f : positionX - 5f,
                positionY - 15f,
                3f, 20f
            );

            boolean hasGroundAhead = false;
            boolean hitWall = false;

            for (Rectangle bound : platforms) {
                if (groundSensor.overlaps(bound)) hasGroundAhead = true;
                if (wallSensor.overlaps(bound)) hitWall = true;
            }

            if (!hasGroundAhead || hitWall) {
                this.isTurning = true;
                this.turnTimer = 0.6f;
            }
        }

        if (this.isTurning) {
            this.turnTimer -= delta;
            if (this.turnTimer <= 0) {
                this.isTurning = false;
                this.isGoingRight = !this.isGoingRight;
                this.velocityX = this.isGoingRight ? this.speed : -this.speed;
            }
        }

        this.updateHitBox();

        if (knight.isAttacking && knight.attackHitBox.overlaps(this.hitBox)) {
            this.hp--;
            this.positionX += knight.isGoingRight ? 70f : -70f;
            knight.attackHitBox.set(0, 0, 0, 0);

            dealtDamage = true;

            if (this.hp <= 0) {
                this.isDead = true;
                this.velocityX = 0;
                this.hitBox.height = 20f;
            }
        }

        float distance = Math.abs(knight.positionX - this.positionX);

        if (this.isDead && distance > 1500f) {
            this.isDead = false;
            this.hp = 3;
            this.positionX = this.spawnX;
            this.positionY = this.spawny;

            this.hitBox.height = 40f;
            this.velocityX = this.isGoingRight ? this.speed : -this.speed;

            this.updateHitBox();
        }

        mossCreepStateUpdate(this, delta);
        return dealtDamage;
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
