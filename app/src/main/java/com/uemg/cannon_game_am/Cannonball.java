// Cannonball.java
// Representa a bola de canhão que o canhão dispara
package com.uemg.cannon_game_am;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Cannonball extends GameElement {
    private boolean onScreen;
    private final Paint orbPaint = new Paint();

    // construtor
    public Cannonball(CannonView view, int color, int soundId, int x, int y, int radius, float velocityX, float velocityY) {
        super(view, color, soundId, x, y, 2 * radius, 2 * radius, velocityX, velocityY);
        onScreen = true;
        orbPaint.setAntiAlias(false);
        orbPaint.setStyle(Paint.Style.FILL);
    }

    // obtém o raio da Cannonball
    private int getRadius() {
        return (shape.right - shape.left) / 2;
    }

    // testa se a Cannonball colide com o GameElement fornecido
    public boolean collidesWith(GameElement element) {
        boolean isMovingForward = view.isPortrait() ? (velocityY > 0) : (velocityX > 0);
        return (Rect.intersects(shape, element.shape) && isMovingForward);
    }

    // retorna verdadeiro se esta Cannonball estiver no ecrã
    public boolean isOnScreen() {
        return onScreen;
    }

    // inverte a velocidade para ricochetear no Blocker
    public void reverseVelocity() {
        if (view.isPortrait()) {
            velocityY = -Math.abs(velocityY);
            shape.offset(0, (int) velocityY);
        } else {
            velocityX = -Math.abs(velocityX);
            shape.offset((int) velocityX, 0);
        }
    }

    public void reverseVelocityX() {
        velocityX = -Math.abs(velocityX);
        shape.offset((int) velocityX, 0);
    }

    public void reverseVelocityY() {
        velocityY = -Math.abs(velocityY);
        shape.offset(0, (int) velocityY);
    }

    // atualiza a posição da Cannonball
    @Override
    public void update(double interval) {
        super.update(interval);

        // se a Cannonball sair do ecrã
        if (shape.top < 0 || shape.left < 0 || shape.bottom > view.getScreenHeight() || shape.right > view.getScreenWidth())
            onScreen = false;
    }

    // desenha a Cannonball no canvas fornecido
    @Override
    public void draw(Canvas canvas) {
        float cx = shape.left + getRadius();
        float cy = shape.top + getRadius();
        float radius = getRadius();
        canvas.drawCircle(cx, cy, radius, setColor(orbPaint, 0xFF18243B));
        canvas.drawCircle(cx, cy, radius * 0.88f, setColor(orbPaint, Color.WHITE));
        canvas.save();
        canvas.clipRect(cx - radius, cy - radius, cx + radius, cy);
        canvas.drawCircle(cx, cy, radius * 0.88f, setColor(orbPaint, 0xFFD43B48));
        canvas.restore();
        canvas.drawRect(cx - radius * 0.88f, cy - radius * 0.09f,
                cx + radius * 0.88f, cy + radius * 0.09f, setColor(orbPaint, 0xFF18243B));
        canvas.drawCircle(cx, cy, radius * 0.28f, setColor(orbPaint, Color.WHITE));
        canvas.drawCircle(cx, cy, radius * 0.16f, setColor(orbPaint, 0xFF18243B));
    }

    private Paint setColor(Paint target, int color) {
        target.setColor(color);
        return target;
    }
}
