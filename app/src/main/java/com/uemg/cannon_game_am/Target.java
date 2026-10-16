// Target.java
// Subclasse de GameElement personalizada para o Target
package com.uemg.cannon_game_am;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Target extends GameElement {
    private int hitReward; // a recompensa de acerto para este target
    private final Paint spritePaint = new Paint();

    private static final String[] PIXEL_CREATURE = {
            "000100010000",
            "000111011100",
            "001122211110",
            "011222222110",
            "112244222211",
            "112222222211",
            "012225222210",
            "001222222100",
            "011222222110",
            "112222222211",
            "001111111100",
            "000110011000"
    };
    private static final int[] PALETTE = {
            Color.TRANSPARENT,
            0xFF18181B,
            0xFFFFE52E,
            0xFFFFB900,
            0xFF17171A,
            0xFFFFF59D
    };

    // construtor
    public Target(CannonView view, int color, int hitReward, int x, int y, int width, int length, float velocityX, float velocityY) {
        super(view, color, CannonView.TARGET_SOUND_ID, x, y, width, length, velocityX, velocityY);
        this.hitReward = hitReward;
        spritePaint.setAntiAlias(false);
    }

    // retorna a recompensa de acerto para este Target
    public int getHitReward() {
        return hitReward;
    }

    @Override
    public void draw(Canvas canvas) {
        float cell = Math.min(shape.width(), shape.height()) / 12f;
        if (cell < 1f) {
            return;
        }
        float left = shape.centerX() - cell * 6f;
        float top = shape.centerY() - cell * 6f;
        for (int row = 0; row < 12; row++) {
            String pixels = PIXEL_CREATURE[row];
            for (int column = 0; column < 12; column++) {
                int color = PALETTE[pixels.charAt(column) - '0'];
                if (color != Color.TRANSPARENT) {
                    spritePaint.setColor(color);
                    canvas.drawRect(left + column * cell, top + row * cell,
                            left + (column + 1) * cell, top + (row + 1) * cell, spritePaint);
                }
            }
        }
    }
}
