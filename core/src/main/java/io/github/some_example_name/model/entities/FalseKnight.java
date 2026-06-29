package io.github.some_example_name.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.controller.LevelController;

public class FalseKnight extends Enemy{
    public enum State {
        STANDING, ATTACK_ANTIC, ATTACK, ATTACK_RECOVER,
        RUN_ANTIC, RUN,
        JUMP_ANTIC, JUMP_ATTACK, JUMP, LAND,
        DEATH_FALL, DEATH_LAND,
        STUNNED, STUN_RECOVER,
        DEATH, DEATH_HIT
    }

    public FalseKnight.State currentState = State.STANDING;
    public FalseKnight.State previousMovement = State.STANDING;
    public FalseKnight.State animationTracker = State.STANDING;

    public boolean fight2 = false, shakeCamera = false;
    public float actionTimer = 0f;

    private Array<Rectangle> mapWalls;

    public FalseKnight(float x, float y) {
        this.positionX = x;
        this.positionY = y;

        this.width = 800f;
        this.height = 600f;

        this.hp = 6;

        this.hitBox = new Rectangle(x + width / 3, y + 30f, width / 3, height / 2);

        this.mapWalls = LevelController.mapWalls;
    }

    @Override
    public void updateHitBox() {
        this.hitBox.setPosition(this.positionX + width / 3, this.positionY + 30f);
    }

    @Override
    public boolean update(float delta, float gravity, Knight knight, Array<Rectangle> platforms, Array<Rectangle> spikes) {
        boolean dealtDamage = false;
        shakeCamera = false;

        this.positionX += this.velocityX * delta;
        this.positionY += this.velocityY * delta;

        if (!isOnGround) {
            this.velocityY -= gravity * delta;
        }

        this.isOnGround = false;
        this.updateHitBox();

        for (Rectangle bound : platforms) {
            if (this.velocityY <= 0 && this.hitBox.overlaps(bound)) {
                this.positionY = bound.y + bound.height - 30f;
                this.velocityY = 0;
                this.isOnGround = true;
                this.updateHitBox();
                break;
            }
        }

        Rectangle wallSensor = new Rectangle(
            isGoingRight ? hitBox.x + hitBox.width : hitBox.x - 10f,
            positionY + 10f,
            2f, hitBox.height
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
        }


        for (Rectangle wall : mapWalls) {
            if (wallSensor.overlaps(wall)) hitWall = true;
        }


        if (!isDead) {
            float distance = Math.abs(knight.hitBox.x - this.hitBox.x);
            boolean knightIsRight = knight.positionX > this.hitBox.x;
            float coefficient = fight2 ? 0.5f : 1.0f;

            if (this.isOnGround && (currentState == State.JUMP_ATTACK || currentState == State.JUMP)) {
                currentState = State.LAND;
                this.velocityX = 0f;
                this.velocityY = 0f;

                actionTimer = 0.5f * coefficient;
                if (previousMovement == State.JUMP_ATTACK) shakeCamera = true;
            }

            if (this.isOnGround) {

                switch (currentState) {
                    case STANDING: {
                        this.velocityX = 0f;
                        this.isGoingRight = knightIsRight;
                        actionTimer -= delta;

                        if (actionTimer <= 0) {
                            nextMovement(distance);
                        }
                        break;
                    }

                    case ATTACK_ANTIC: {
                        this.velocityX = 0f;
                        actionTimer -= delta;

                        if (actionTimer <= 0) {
                            this.currentState = State.ATTACK;
                            actionTimer = 0.6f;
                            shakeCamera = true;
                        }
                        break;
                    }

                    case ATTACK: {
                        this.velocityX = 0f;
                        actionTimer -= delta;

                        if (actionTimer <= 0) {
                            currentState = State.ATTACK_RECOVER;
                            actionTimer = 1.0f * coefficient;
                        }
                        break;
                    }

                    case ATTACK_RECOVER: {
                        this.velocityX = 0f;
                        actionTimer -= delta;

                        if (actionTimer <= 0) {
                            currentState = State.STANDING;
                            actionTimer = 0.5f * coefficient;
                        }
                        break;
                    }

                    case RUN_ANTIC: {
                        this.velocityX = 0f;
                        actionTimer -= delta;

                        if (actionTimer <= 0) {
                            this.currentState = State.RUN;
                            actionTimer = 1.5f;
                        }
                        break;
                    }

                    case RUN: {
                        this.velocityX = isGoingRight ? 550f : -550f;
                        actionTimer -= delta;

                        if (actionTimer <= 0 || hitWall) {
                            this.currentState = State.STANDING;
                            actionTimer = 0.3f * coefficient;
                        }
                        break;
                    }

                    case JUMP_ANTIC: {
                        this.velocityX = 0f;
                        actionTimer -= delta;

                        if (actionTimer <= 0) {
                            this.velocityY = 1050f;
                            this.isOnGround = false;

                            if (previousMovement == State.JUMP_ATTACK) {
                                currentState = State.JUMP_ATTACK;
                                this.velocityX = isGoingRight ? 500f : -500f;
                            }

                            else {
                                currentState = State.JUMP;
                                this.velocityX = isGoingRight ? -350f : 350f;
                            }
                        }
                        break;
                    }

                    case LAND: {
                        this.velocityX = 0f;
                        actionTimer -= delta;

                        if (actionTimer <= 0) {
                            currentState = State.STANDING;
                            actionTimer = 0.3f * coefficient;
                        }
                        break;
                    }

                    default: {
                        break;
                    }
                }
            }
        }

        if (isDead) {
            if (this.currentState == State.DEATH_FALL && this.isOnGround) {
                this.currentState = State.DEATH_LAND;
                this.velocityX = 0f;
            }
        }




        if (knight.isAttacking && knight.attackHitBox.overlaps(this.hitBox) && !isDead) {
            this.hp--;
            this.positionX += knight.isGoingRight ? 70f : -70f;
            knight.attackHitBox.set(0, 0, 0, 0);
            dealtDamage = true;

            if (this.hp <= 0) {
                this.isDead = true;

                if (this.isOnGround) {
                     this.currentState = State.DEATH_LAND;
                } else {
                    this.currentState = State.DEATH_FALL;
                }

                this.velocityX = isGoingRight ? -300f : 300f;
                this.velocityY = 300f;

                this.updateHitBox();
            }
        }

        if (knight.isAttacking && knight.attackHitBox.overlaps(this.hitBox) && isDead) {
            knight.attackHitBox.set(0, 0, 0, 0);
            dealtDamage = true;

            this.currentState = State.DEATH_HIT;
        }


        FalseKnightStateUpdate(this, delta);
        return dealtDamage;
    }

    private void nextMovement(float distance) {
        float coefficient = fight2 ? 0.5f : 1.0f;

        State nextMove = State.STANDING;

        if (distance < 1000f) {
            do {

                if (distance < 300f) {
                    if (Math.random() < 0.5) {
                        nextMove = State.ATTACK;
                    }

                    else {
                        nextMove = State.JUMP;
                    }
                }

                else {
                    if (Math.random() < 0.4) {
                        nextMove = State.JUMP_ATTACK;
                    }

                    else {
                        nextMove = State.RUN;
                    }
                }

            } while (previousMovement == nextMove);
        }



        previousMovement = nextMove;

        switch (nextMove) {

            case ATTACK: {
                this.currentState = State.ATTACK_ANTIC;
                this.actionTimer = 0.6f * coefficient;
                break;
            }

            case JUMP, JUMP_ATTACK: {
                this.currentState = State.JUMP_ANTIC;
                this.actionTimer = 0.3f * coefficient;
                break;
            }

            case RUN: {
                this.currentState = State.RUN_ANTIC;
                this.actionTimer = 0.3f * coefficient;
                break;
            }

            default: {
                this.currentState = State.STANDING;
                this.actionTimer = 0.5f;
                break;
            }

        }
    }

    private void FalseKnightStateUpdate(FalseKnight falseKnight, float delta) {
        if (falseKnight.currentState == falseKnight.animationTracker) {
            falseKnight.stateDuration += delta;
        } else {
            falseKnight.animationTracker = falseKnight.currentState;
            falseKnight.stateDuration = 0f;
        }
    }
}
