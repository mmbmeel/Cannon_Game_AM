package com.uemg.cannon_game_am;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public final class PokeballPatternDrawable extends Drawable {
    private static final String[] PIXELS = {
            "0001111000",
            "0011111100",
            "0112222210",
            "1122222211",
            "1122442211",
            "1134443311",
            "1133333311",
            "0133333330",
            "0013333100",
            "0001111000"
    };
    private static final int[] COLORS = {
            Color.TRANSPARENT,
            0xFF505056,
            0xFFD7353F,
            0xFFF5F4F0,
            0xFF18181B
    };
    private static final float[][] POSITIONS = {
                {0.18f, 0.13f, 0f},
                {0.83f, 0.37f, 0f},
                {0.26f, 0.57f, 90f},
                {0.82f, 0.76f, 90f},
                {0.25f, 0.96f, 90f}
    };

    private final Paint paint = new Paint();
    private final float cellSize;

    public PokeballPatternDrawable(float density) {
            cellSize = 7f * density;
            paint.setAntiAlias(false);
            paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.drawColor(0xFF080809);
        float ballSize = cellSize * 10;
        for (float[] position : POSITIONS) {
            float centerX = bounds.left + bounds.width() * position[0];
            float centerY = bounds.top + bounds.height() * position[1];
            canvas.save();
            canvas.rotate(position[2], centerX, centerY);
            drawPokeball(canvas, centerX - ballSize / 2, centerY - ballSize / 2);
            canvas.restore();
        }
    }

    private void drawPokeball(Canvas canvas, float left, float top) {
        for (int row = 0; row < PIXELS.length; row++) {
            String pixels = PIXELS[row];
            for (int column = 0; column < pixels.length(); column++) {
                int color = COLORS[pixels.charAt(column) - '0'];
                if (color == Color.TRANSPARENT) {
                    continue;
                }
                paint.setColor(color);
                float x = left + column * cellSize;
                float y = top + row * cellSize;
                canvas.drawRect(x, y, x + cellSize, y + cellSize, paint);
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
