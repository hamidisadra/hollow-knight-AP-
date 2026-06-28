package io.github.some_example_name.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class CrystalGuardian extends Enemy{
    public enum State {
        STANDING, ATTACKING, SHOOTING, TURNING, DEATH, EVADE
    }

    public CrystalGuardian.State currentState = State.STANDING;
    public CrystalGuardian.State previousState = State.STANDING;

    public float actionTimer = 2f;
    public float attackSpeed = 400f;
    public float visionWidth = 750f;

    public Rectangle laserHitbox;
    public float laserTime;

    public CrystalGuardian(float x, float y) {
        this.positionX = x;
        this.positionY = y;
        this.spawnX = x;
        this.spawny = y;
        this.width = 300f;
        this.height = 260f;
        this.speed = 0f;
        this.hp = 10;

        this.hitBox = new Rectangle(x + 160f, y, width - 40f, height - 20f);
        this.laserHitbox = new Rectangle(0, 0, 0, 0);
        this.velocityY = 0f;
    }

    @Override
    public boolean update(float delta, float gravity, Knight knight, Array<Rectangle> platforms, Array<Rectangle> spikes) {
        boolean dealtDamage = false;

        if (isDead) {
            laserHitbox.set(0, 0,0, 0);
            crystalGuardianStateUpdate(this, delta);
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

        boolean knightIsRight = knight.positionX > this.positionX;

        switch (currentState) {
            case STANDING: {
                this.velocityX = 0f;
                laserHitbox.set(0, 0, 0, 0);

                if (knightIsRight != isGoingRight) {
                    currentState = State.TURNING;
                    actionTimer = 0.6f;
                }

                else if (visionBox.overlaps(knight.hitBox)) {
                    currentState = State.SHOOTING;
                    actionTimer = 1.4f;
                    laserTime = 0.5f;
                }
                break;
            }
            case TURNING: {
                this.velocityX = 0f;
                actionTimer -= delta;

                if (actionTimer <= 0) {
                    this.isGoingRight = !this.isGoingRight;
                    currentState = State.STANDING;
                }

                break;
            }

            case SHOOTING: {
                this.velocityX = 0f;
                actionTimer -= delta;

                if (actionTimer <= 0) {
                    laserHitbox.set(isGoingRight ? positionX + width : positionX - visionWidth,
                        positionY + (height / 2), visionWidth, 40f);

                    laserTime -= delta;

                    if (laserTime <= delta) {
                        currentState = State.ATTACKING;
                        laserHitbox.set(0, 0, 0, 0);
                        actionTimer = 3f;
                    }
                }

                break;
            }

            case ATTACKING: {
                this.velocityX = isGoingRight ? attackSpeed : -attackSpeed;
                actionTimer -= delta;

                if (actionTimer <= 0 || hitWall || !hasGroundAhead) {
                    currentState = State.STANDING;
                }

                else {
                    this.positionX += this.velocityX * delta;
                    this.updateHitBox();
                }

                break;
            }

            case EVADE: {
                this.velocityX = isGoingRight ? -150f : 150f;
                actionTimer -= delta;

                if (hitWall || !hasGroundAhead) {
                    this.velocityX = 0;
                }

                else this.positionX += this.velocityX * delta;

                this.updateHitBox();

                if (actionTimer <= 0) {
                    currentState = State.STANDING;
                }

                break;
            }

            case DEATH: {
                float distance = Math.abs(knight.positionX - this.positionX);

                if (distance > 600f) {
                    this.currentState = State.STANDING;
                    this.hp = 10;
                    this.positionX = this.spawnX;
                    this.positionY = this.spawny;

                    this.velocityX = 0f;

                    this.updateHitBox();
                }
            }
        }

        if (knight.isAttacking && knight.attackHitBox.overlaps(this.hitBox)) {
            this.hp--;
            this.positionX += knight.isGoingRight ? 70f : -70f;
            knight.attackHitBox.set(0, 0, 0, 0);
            dealtDamage = true;

            if (this.hp <= 0) {
                this.isDead = true;
                this.updateHitBox();
                this.currentState = CrystalGuardian.State.DEATH;

                this.laserHitbox.set(0, 0, 0, 0);
            }

            else {
                this.currentState = State.EVADE;
                this.actionTimer = 0.56f;
            }
        }

        crystalGuardianStateUpdate(this, delta);
        return dealtDamage;
    }

    public CrystalGuardian.State getState() {
        if (isDead) return CrystalGuardian.State.DEATH;
        return currentState;
    }

    private void crystalGuardianStateUpdate(CrystalGuardian crystalGuardian, float delta) {
        crystalGuardian.previousState = crystalGuardian.currentState;
        crystalGuardian.currentState = crystalGuardian.getState();

        if (crystalGuardian.previousState == crystalGuardian.currentState) {
            crystalGuardian.stateDuration += delta;
        } else {
            crystalGuardian.stateDuration = 0f;
        }
    }
}
