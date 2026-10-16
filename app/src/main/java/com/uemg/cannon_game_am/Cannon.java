// Cannon.java
// Representa o canhão e dispara a Cannonball
package com.uemg.cannon_game_am;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Cannon {
    private int baseRadius; // raio da base do canhão
    private int barrelLength; // comprimento do cano do canhão
    private Point barrelEnd = new Point(); // ponto final do cano do canhão
    private double barrelAngle; // ângulo do cano do canhão
    private Cannonball cannonball; // a Cannonball do canhão
    private Paint paint = new Paint(); // Paint usada para desenhar o canhão
    private Paint detailPaint = new Paint();
    private CannonView view; // view que contém o canhão

    // construtor
    public Cannon(CannonView view, int baseRadius, int barrelLength, int barrelWidth) {
        this.view = view;
        this.baseRadius = baseRadius;
        this.barrelLength = barrelLength;
        paint.setStrokeWidth(barrelWidth); // define a largura do cano
        paint.setAntiAlias(false);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        detailPaint.setAntiAlias(false);
        align(0);
    }

    // alinha o cano do canhão para o ângulo fornecido
    public void align(double barrelAngle) {
        this.barrelAngle = barrelAngle;
        if (view.isPortrait()) {
            // Em Portrait: a base do canhão está no topo central (screenWidth / 2, 0)
            barrelEnd.x = (int) (barrelLength * Math.sin(barrelAngle)) + view.getScreenWidth() / 2;
            barrelEnd.y = (int) (barrelLength * Math.cos(barrelAngle));
        } else {
            // Em Landscape: a base do canhão está no centro esquerdo (0, screenHeight / 2)
            barrelEnd.x = (int) (barrelLength * Math.sin(barrelAngle));
            barrelEnd.y = (int) (-barrelLength * Math.cos(barrelAngle)) + view.getScreenHeight() / 2;
        }
    }

    // cria e dispara a Cannonball na direção apontada pelo canhão
    public void fireCannonball() {
        int velocityX, velocityY;
        int radius;
        int startX, startY;

        if (view.isPortrait()) {
            velocityX = (int) (CannonView.CANNONBALL_SPEED_PERCENT * view.getScreenHeight() * Math.sin(barrelAngle));
            velocityY = (int) (CannonView.CANNONBALL_SPEED_PERCENT * view.getScreenHeight() * Math.cos(barrelAngle));
            radius = (int) (view.getScreenWidth() * CannonView.CANNONBALL_RADIUS_PERCENT);
            startX = view.getScreenWidth() / 2 - radius;
            startY = -radius;
        } else {
            velocityX = (int) (CannonView.CANNONBALL_SPEED_PERCENT * view.getScreenWidth() * Math.sin(barrelAngle));
            velocityY = (int) (CannonView.CANNONBALL_SPEED_PERCENT * view.getScreenWidth() * -Math.cos(barrelAngle));
            radius = (int) (view.getScreenHeight() * CannonView.CANNONBALL_RADIUS_PERCENT);
            startX = -radius;
            startY = view.getScreenHeight() / 2 - radius;
        }

        cannonball = new Cannonball(view, Color.BLACK, CannonView.CANNON_SOUND_ID, startX, startY, radius, velocityX, velocityY);
        cannonball.playSound();
    }

    // desenha o canhão no Canvas
    public void draw(Canvas canvas) {
        Paint detail = detailPaint;
        detail.setStyle(Paint.Style.STROKE);
        if (view.isPortrait()) {
            float x = view.getScreenWidth() / 2f;
            canvas.drawLine(x, 0, barrelEnd.x, barrelEnd.y, setPaint(detail, 0xFF17171A, paint.getStrokeWidth() * 1.55f));
            canvas.drawLine(x, 0, barrelEnd.x, barrelEnd.y, setPaint(detail, 0xFFD7353F, paint.getStrokeWidth() * 0.9f));
            drawMuzzle(canvas, barrelEnd.x, barrelEnd.y, detail);
            drawCaptureOrb(canvas, x, 0, detail);
        } else {
            float y = view.getScreenHeight() / 2f;
            canvas.drawLine(0, y, barrelEnd.x, barrelEnd.y, setPaint(detail, 0xFF17171A, paint.getStrokeWidth() * 1.55f));
            canvas.drawLine(0, y, barrelEnd.x, barrelEnd.y, setPaint(detail, 0xFFD7353F, paint.getStrokeWidth() * 0.9f));
            drawMuzzle(canvas, barrelEnd.x, barrelEnd.y, detail);
            drawCaptureOrb(canvas, 0, y, detail);
        }
    }

    private Paint setPaint(Paint target, int color, float strokeWidth) {
        target.setColor(color);
        target.setStrokeWidth(strokeWidth);
        return target;
    }

    private void drawCaptureOrb(Canvas canvas, float x, float y, Paint detail) {
        detail.setStyle(Paint.Style.FILL);
        detail.setColor(0xFF17171A);
        canvas.drawCircle(x, y, baseRadius, detail);
        detail.setColor(0xFFF5F4F0);
        canvas.drawCircle(x, y, baseRadius * 0.82f, detail);
        detail.setColor(0xFFD43B48);
        canvas.drawRect(x - baseRadius * 0.78f, y - baseRadius * 0.78f,
                x + baseRadius * 0.78f, y, detail);
        detail.setColor(0xFF17171A);
        canvas.drawRect(x - baseRadius * 0.82f, y - baseRadius * 0.08f,
                x + baseRadius * 0.82f, y + baseRadius * 0.08f, detail);
        detail.setColor(Color.WHITE);
        canvas.drawCircle(x, y, baseRadius * 0.23f, detail);
        detail.setColor(0xFF17171A);
        canvas.drawCircle(x, y, baseRadius * 0.12f, detail);
    }

    private void drawMuzzle(Canvas canvas, float x, float y, Paint detail) {
        float radius = paint.getStrokeWidth() * 0.62f;
        detail.setStyle(Paint.Style.FILL);
        detail.setColor(0xFF17171A);
        canvas.drawCircle(x, y, radius, detail);
        detail.setStyle(Paint.Style.STROKE);
        detail.setStrokeWidth(Math.max(2f, paint.getStrokeWidth() * 0.22f));
        detail.setColor(0xFFF5F4F0);
        canvas.drawCircle(x, y, radius, detail);
    }

    // retorna a Cannonball que este canhão disparou
    public Cannonball getCannonball() {
        return cannonball;
    }

    // remove a Cannonball do jogo
    public void removeCannonball() {
        cannonball = null;
    }
}
