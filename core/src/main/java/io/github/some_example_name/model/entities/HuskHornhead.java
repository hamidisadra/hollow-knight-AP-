package io.github.some_example_name.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.awt.font.TextHitInfo;

public class HuskHornhead extends Enemy{
    public enum State {
        STANDING, WALKING, START_ATTACKING, ATTACKING, TURNING, DEATH
    }

    public HuskHornhead.State currentState = State.WALKING;
    public HuskHornhead.State previousState = State.WALKING;


    public boolean isTurning = false;
    public float turnTimer = 0f;

    public float actionTimer = 2f;
    public float attackSpeed = 450f;
    public float visionWidth = 400f;


    public HuskHornhead(float x, float y) {
        this.positionX = x;
        this.positionY = y;
        this.spawnX = x;
        this.spawny = y;
        this.width = 300f;
        this.height = 260f;
        this.speed = 100f;
        this.hp = 6;

        this.hitBox = new Rectangle(x + 160f, y, width - 40f, height - 40f);
        this.velocityY = 0f;
    }

    @Override
    public boolean update(float delta, float gravity, Knight knight, Array<Rectangle> platforms, Array<Rectangle> spikes) {
        boolean dealtDamage = false;

        if (this.isDead) {
            huskHorneheadStateUpdate(this, delta);
            return false;
        }

        this.positionY -= gravity * delta;
        this.isOnGround = false;
        this.updateHitBox();

        for (Rectangle bound : platforms) {
            if (this.hitBox.overlaps(bound)) {
                this.positionY = bound.y + bound.height;
                this.isOnGround = true;
                this.updateHitBox();
                break;
            }
        }

        if (this.isOnGround) {

            Rectangle visionBox = new Rectangle(
                isGoingRight ? positionX + (width / 2) : positionX - visionWidth,
                positionY,
                visionWidth + (width / 2), height
            );

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

            switch (currentState) {
                case STANDING: {
                    this.velocityX = 0;
                    actionTimer -= delta;

                    if (actionTimer <= 0) {
                        currentState = State.WALKING;
                        actionTimer = 3f;
                    }

                    if (visionBox.overlaps(knight.hitBox)) {
                        currentState = State.START_ATTACKING;
                        actionTimer = 0.5f;
                    }
                    break;
                }

                case WALKING: {
                    this.velocityX = isGoingRight ? speed : -speed;
                    actionTimer -= delta;

                    if (actionTimer <= 0) {
                        currentState = State.STANDING;
                        actionTimer = 1.5f;
                    }

                    if (visionBox.overlaps(knight.hitBox)) {
                        currentState = State.START_ATTACKING;
                        actionTimer = 0.5f;
                    }
                    break;
                }

                case START_ATTACKING: {
                    this.velocityX = 0;
                    actionTimer -= delta;

                    if (actionTimer <= 0) {
                        currentState = State.ATTACKING;
                    }
                    break;
                }

                case ATTACKING: {
                    this.velocityX = isGoingRight ? attackSpeed : -attackSpeed;
                    break;
                }

                case TURNING: {
                    this.velocityX = 0;
                    actionTimer -= delta;

                    if (actionTimer <= 0) {
                        this.isGoingRight = !this.isGoingRight;
                        this.velocityX = isGoingRight ? speed : -speed;

                        hitWall = false;
                        hasGroundAhead = true;

                        currentState = State.WALKING;
                        actionTimer = 3f;
                    }
                    break;
                }

                case DEATH: {
                    float distance = Math.abs(knight.positionX - this.positionX);

                    if (distance > 600f) {
                        this.currentState = State.WALKING;
                        this.hp = 6;
                        this.positionX = this.spawnX;
                        this.positionY = this.spawny;

                        this.velocityX = isGoingRight ? this.speed : -this.speed;

                        this.updateHitBox();
                    }
                }
            }

            if (currentState == State.WALKING || currentState == State.ATTACKING) {
                if (hitWall || !hasGroundAhead) {
                    currentState = State.TURNING;
                    actionTimer = 0.6f;
                } else {
                    this.positionX += this.velocityX * delta;
                    this.updateHitBox();
                }
            }
        }

        if (knight.isAttacking && knight.attackHitBox.overlaps(this.hitBox)) {
            this.hp--;
            this.positionX += knight.isGoingRight ? 40f : -40f;
            knight.attackHitBox.set(0, 0, 0, 0);
            dealtDamage = true;

            if (this.hp <= 0) {
                this.isDead = true;
                this.currentState = State.DEATH;
            }
        }

        huskHorneheadStateUpdate(this, delta);
        return dealtDamage;
    }

    public State getState() {
        if (isDead) return State.DEATH;
        return currentState;
    }

    private void huskHorneheadStateUpdate(HuskHornhead huskHornhead, float delta) {
        huskHornhead.previousState = huskHornhead.currentState;
        huskHornhead.currentState = huskHornhead.getState();

        if (huskHornhead.previousState == huskHornhead.currentState) {
            huskHornhead.stateDuration += delta;
        } else {
            huskHornhead.stateDuration = 0f;
        }
    }
}
