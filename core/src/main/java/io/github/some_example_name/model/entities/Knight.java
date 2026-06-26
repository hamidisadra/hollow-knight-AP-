package io.github.some_example_name.model.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;



public class Knight {
    public enum State {
        RUNNING, STANDING, DASHING, JUMPING, DOUBLE_JUMPING,  FALLING, ATTACKING, ATTACKING_DOWN, WALL_SLIDING, WALL_JUMP
    }

    public State currentState = State.STANDING;
    public State previousState = State.STANDING;
    public float stateDuration = 0f;

    public float positionX, positionY;
    public float width = 200f, height = 140f;
    public float velocityX = 0, velocityY = 0;

    public Rectangle hitBox;
    public Rectangle attackHitBox = new Rectangle();

    public boolean isOnGround = false, isGoingRight = true, isDashing = false, canDoubleJump = false, isDoubleJumping = false, isAttacking = false, isPogo = false, isWallSliding = false, isWallJumping = false;

    public float dashTimer = 0f, attackTimer = 0f;

    public Knight (float positionX,float positionY) {
        this.positionX = positionX;
        this.positionY = positionY;

        this.hitBox = new Rectangle((int) positionX, (int) positionY, (int) this.width - 180f, (int) this.height - 70f);
    }

    public void updateHitBox() {
        hitBox.setPosition( this.positionX, this.positionY);
    }

    public State getState() {
        if (isDashing) return State.DASHING;
        if (isAttacking && !isOnGround && Gdx.input.isKeyPressed(Input.Keys.DOWN)) return State.ATTACKING_DOWN;
        if (isAttacking) return State.ATTACKING;
        if (isWallSliding) return State.WALL_SLIDING;
        if (isWallJumping) {

            return State.WALL_JUMP;
        }
        if (isDoubleJumping) return State.DOUBLE_JUMPING;
        if (velocityY > 0) return State.JUMPING;
        if (velocityY < 0) return State.FALLING;
        if (velocityX != 0) return State.RUNNING;

        return State.STANDING;
    }
}
