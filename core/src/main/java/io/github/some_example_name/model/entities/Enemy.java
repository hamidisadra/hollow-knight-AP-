package io.github.some_example_name.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class Enemy {
    public float positionX, positionY;
    public float spawnX, spawny;
    public float width, height;
    public float velocityX, velocityY;
    public float speed;
    public int hp;

    public boolean isGoingRight = true, isDead = false, isOnGround = false;

    public Rectangle hitBox;

    public float stateDuration = 0f;

    public void updateHitBox() {
        hitBox.setPosition(this.positionX, this.positionY);
    }

    public abstract boolean update(float delta, float gravity, Knight knight, Array<Rectangle> platforms, Array<Rectangle> spikes);
}
