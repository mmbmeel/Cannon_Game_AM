// GameElement.java
// Representa um elemento de jogo delimitado por retângulo
package com.uemg.cannon_game_am;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameElement {
    protected CannonView view; // a view que contém este GameElement
    protected Paint paint = new Paint(); // Paint para desenhar este GameElement
    protected Rect shape; // os limites retangulares do GameElement
    protected float velocityX; // a velocidade horizontal deste GameElement
    protected float velocityY; // a velocidade vertical deste GameElement
    private int soundId; // o som associado a este GameElement

    // construtor público
    public GameElement(CannonView view, int color, int soundId, int x, int y, int width, int length, float velocityX, float velocityY) {
        this.view = view;
        paint.setColor(color);
        shape = new Rect(x, y, x + width, y + length); // define os limites
        this.soundId = soundId;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    // atualiza a posição do GameElement e verifica colisões com as paredes
    public void update(double interval) {
        // atualiza a posição horizontal e vertical
        shape.offset((int) (velocityX * interval), (int) (velocityY * interval));

        // se este GameElement colidir com a parede vertical, inverte a direção Y
        if (shape.top < 0 && velocityY < 0 || shape.bottom > view.getScreenHeight() && velocityY > 0)
            velocityY *= -1;

        // se este GameElement colidir com a parede horizontal, inverte a direção X
        if (shape.left < 0 && velocityX < 0 || shape.right > view.getScreenWidth() && velocityX > 0)
            velocityX *= -1;
    }

    // desenha este GameElement no Canvas fornecido
    public void draw(Canvas canvas) {
        canvas.drawRect(shape, paint);
    }

    // reproduz o som que corresponde a este tipo de GameElement
    public void playSound() {
        view.playSound(soundId);
    }
}
