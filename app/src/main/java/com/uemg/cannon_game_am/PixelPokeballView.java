package com.uemg.cannon_game_am;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public final class PixelPokeballView extends View {
    private static final String[] PIXELS = {
            "0000011111100000",
            "0001111111111000",
            "0011111111111100",
            "0111222222111110",
            "0112222222211110",
            "1122222222222111",
            "1122222222222111",
            "1111111111111111",
            "1111111111111111",
            "1133333333333111",
            "1133333333333111",
            "0113333333333110",
            "0111333333331110",
            "0011113333111100",
            "0001111111111000",
            "0000011111100000"
    };
    private final Paint pixelPaint = new Paint();

    public PixelPokeballView(Context context) {
        this(context, null);
    }

    public PixelPokeballView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pixelPaint.setAntiAlias(false);
        pixelPaint.setStyle(Paint.Style.FILL);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cellSize = Math.min(getWidth(), getHeight()) / 16f;
        float size = cellSize * 16f;
        float left = (getWidth() - size) / 2f;
        float top = (getHeight() - size) / 2f;
        int[] colors = {
                Color.TRANSPARENT,
                0xFF18181B,
                0xFFD7353F,
                0xFFF5F4F0
        };

        for (int row = 0; row < PIXELS.length; row++) {
            String pixels = PIXELS[row];
            for (int column = 0; column < pixels.length(); column++) {
                int color = colors[pixels.charAt(column) - '0'];
                if (color == Color.TRANSPARENT) {
                    continue;
                }
                pixelPaint.setColor(color);
                float x = left + column * cellSize;
                float y = top + row * cellSize;
                canvas.drawRect(x, y, x + cellSize, y + cellSize, pixelPaint);
            }
        }
    }
}
