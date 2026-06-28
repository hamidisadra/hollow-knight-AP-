package io.github.some_example_name.model.entities;

import com.badlogic.gdx.math.Rectangle;

import java.awt.*;

public class BreakableWall {
    public float x, y, width, height;

    public int hp = 3;

    public boolean isBroken = false;
    public Rectangle hitbox;

    public float shakeTimer;

    public BreakableWall(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.hitbox = new Rectangle(x + 10f, y, width, height);
    }

    public boolean update(Knight knight) {
        boolean dealtDamage = false;

        if (knight.isAttacking && knight.attackHitBox.overlaps(this.hitbox)) {
            hp--;
            shakeTimer = 0.2f;


            knight.positionX += knight.isGoingRight ? -40f : 40f;
            knight.attackHitBox.set(0, 0, 0, 0);

            if (hp <= 0) {
                isBroken = true;
                hitbox.set(0, 0, 0, 0);
            }

            dealtDamage = true;
        }

        return dealtDamage;
    }
}
