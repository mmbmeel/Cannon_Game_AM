// Blocker.java
// Subclasse de GameElement personalizada para o Blocker
package com.uemg.cannon_game_am;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Blocker extends GameElement {
    private int missPenalty; // a penalidade de erro para este Blocker
    private final Paint shield = new Paint();

    // construtor
    public Blocker(CannonView view, int color, int missPenalty, int x, int y, int width, int length, float velocityX, float velocityY) {
        super(view, color, CannonView.BLOCKER_SOUND_ID, x, y, width, length, velocityX, velocityY);
        this.missPenalty = missPenalty;
        shield.setAntiAlias(false);
    }

    // retorna a penalidade de erro para este Blocker
    public int getMissPenalty() {
        return missPenalty;
    }

    @Override
    public void draw(Canvas canvas) {
        shield.setStyle(Paint.Style.FILL);
        shield.setColor(0xAA29292D);
        canvas.drawRect(shape, shield);
        shield.setStyle(Paint.Style.STROKE);
        shield.setStrokeWidth(Math.max(2f, Math.min(shape.width(), shape.height()) * 0.12f));
        shield.setColor(0xFFF5F4F0);
        canvas.drawRect(shape, shield);
        shield.setStyle(Paint.Style.FILL);
        shield.setColor(0xFFD7353F);
        float cx = shape.centerX();
        float cy = shape.centerY();
        float unit = Math.max(2f, Math.min(shape.width(), shape.height()) / 5f);
        canvas.drawRect(cx - unit, cy - unit * 2, cx + unit, cy - unit, shield);
        canvas.drawRect(cx, cy - unit, cx + unit, cy + unit, shield);
        canvas.drawRect(cx - unit, cy + unit, cx, cy + unit * 2, shield);
    }
}
