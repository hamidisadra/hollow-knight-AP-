package io.github.some_example_name.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class MossFly extends Enemy{

    public enum State {
        SHAKING, FLYING, TURNING_TO_FLY, APPEAR, DEATH_AIR, DEATH_LAND
    }

    public boolean isShaking = true, isAppearing = false;
    public float actionTimer = 0f;


    public MossFly.State currentState = State.SHAKING;
    public MossFly.State previousState = State.SHAKING;


    public MossFly(float x, float y) {
        this.positionX = x;
        this.positionY = y;
        this.spawnX = x;
        this.spawny = y;
        this.width = 100f;
        this.height = 80f;
        this.speed = 120f;
        this.hp = 3;

        this.hitBox = new Rectangle(x, y, width, height);
        this.velocityX = 0;
        this.velocityY = 0;
    }

    @Override
    public boolean update(float delta, float gravity, Knight knight, Array<Rectangle> platforms, Array<Rectangle> spikes) {
        boolean dealtDamage = false;

        float deltaX = knight.positionX - this.positionX;
        float deltaY = knight.positionY - this.positionY;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        float distanceToSpawn = Math.abs(knight.positionX - this.spawnX);

        if (this.isDead && distanceToSpawn > 1500f) {
            this.isDead = false;
            this.hp = 3;
            this.positionX = this.spawnX;
            this.positionY = this.spawny;
            this.hitBox.height = 80f;

            this.isShaking = true;
            this.isAppearing = false;
            this.velocityX = 0;
            this.velocityY = 0;
        }

        if (this.isDead) {
            float closestFloorY = -1000f;

            for (Rectangle bound : platforms) {
                if (this.positionX + this.width > bound.x &&
                    this.positionX < bound.x + bound.width) {

                    if (bound.y + bound.height <= this.positionY + 50f) {

                        if (bound.y + bound.height > closestFloorY) {
                            closestFloorY = bound.y + bound.height;
                        }
                    }
                }
            }

            if (closestFloorY != -1000f) {
                this.positionY = closestFloorY - 10f;
                this.spawny = this.positionY;
                this.isOnGround = true;
                this.updateHitBox();
            }

            mossFlyStateUpdate(this, delta);
            return false;
        }

        if (this.isShaking) {
            if (distance < 500f) {
                this.isShaking = false;
                this.isAppearing = true;
                this.actionTimer = 0.6f;
            }
        }

        else if (this.isAppearing) {
            this.actionTimer -= delta;
            if (this.actionTimer <= 0) {
                this.isAppearing = false;
            }
        }

        else {
            if (distance > 0) {
                this.velocityX = (deltaX / distance) * this.speed;
                this.velocityY = (deltaY / distance) * this.speed;
            }

            this.isGoingRight = this.velocityX > 0;

            this.positionX += this.velocityX * delta;
            this.updateHitBox();

            for (Rectangle bound : platforms) {
                if (this.hitBox.overlaps(bound)) {

                    if (this.velocityX > 0) {
                        this.positionX = bound.x - this.width;
                    } else if (this.velocityX < 0) {
                        this.positionX = bound.x + bound.width;
                    }
                    this.updateHitBox();
                }
            }

            this.positionY += this.velocityY * delta;
            this.updateHitBox();

            for (Rectangle bound : platforms) {
                if (this.hitBox.overlaps(bound)) {

                    if (this.velocityY > 0) {
                        this.positionY = bound.y - this.height;
                    } else if (this.velocityY < 0) {
                        this.positionY = bound.y + bound.height;
                    }
                    this.updateHitBox();
                }
            }

            if (knight.isAttacking && knight.attackHitBox.overlaps(this.hitBox)) {
                this.hp--;
                this.positionX += this.isGoingRight ? -70f : 70f;
                knight.attackHitBox.set(0, 0, 0, 0);

                dealtDamage = true;

                if (this.hp <= 0) {
                    this.isDead = true;
                    this.isShaking = false;
                    this.isAppearing = false;
                    this.velocityY = 0;
                    this.velocityX = 0;
                    this.hitBox.height = 30f;
                }
            }
        }

        mossFlyStateUpdate(this, delta);
        return dealtDamage;
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
