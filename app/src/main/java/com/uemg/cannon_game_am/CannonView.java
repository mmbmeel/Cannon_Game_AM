// CannonView.java
// Mostra e controla o Cannon Game
package com.uemg.cannon_game_am;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class CannonView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CannonView";
    private static final String[] BACKGROUND_POKEBALL = {
            "000111111000",
            "001111111100",
            "011222222110",
            "112222222211",
            "112222222211",
            "111111111111",
            "111111111111",
            "113333333311",
            "113333333311",
            "011333333110",
            "001111111100",
            "000111111000"
    };

    // constantes para o jogo
    public static final int MISS_PENALTY = 2; // segundos deduzidos num erro
    public static final int HIT_REWARD = 3; // segundos adicionados num acerto

    // constantes para o Cannon
    public static final double CANNON_BASE_RADIUS_PERCENT = 3.0 / 40;
    public static final double CANNON_BARREL_WIDTH_PERCENT = 3.0 / 40;
    public static final double CANNON_BARREL_LENGTH_PERCENT = 1.0 / 10;

    // constantes para a Cannonball
    public static final double CANNONBALL_RADIUS_PERCENT = 3.0 / 80;
    public static final double CANNONBALL_SPEED_PERCENT = 3.0 / 2;

    // constantes para os Targets
    public static final double TARGET_WIDTH_PERCENT = 1.0 / 40;
    public static final double TARGET_LENGTH_PERCENT = 3.0 / 20;
    public static final double TARGET_FIRST_X_PERCENT = 3.0 / 5;
    public static final double TARGET_SPACING_PERCENT = 1.0 / 60;
    public static final double TARGET_PIECES = 9; // 9 alvos obrigatorios
    public static final double TARGET_MIN_SPEED_PERCENT = 3.0 / 4;
    public static final double TARGET_MAX_SPEED_PERCENT = 6.0 / 4;

    // constantes para o Blocker
    public static final double BLOCKER_WIDTH_PERCENT = 1.0 / 40;
    public static final double BLOCKER_LENGTH_PERCENT = 1.0 / 4;
    public static final double BLOCKER_X_PERCENT = 1.0 / 2;
    public static final double BLOCKER_SPEED_PERCENT = 1.0;

    // tamanho do texto 1/18 da dimensão do ecrã
    public static final double TEXT_SIZE_PERCENT = 1.0 / 18;

    private CannonThread cannonThread; // controla o game loop
    private Activity activity; // para mostrar o dialogo Game Over na thread da GUI
    private boolean dialogIsDisplayed = false;

    // objetos do jogo
    private Cannon cannon;
    private Blocker blocker;
    private ArrayList<Target> targets;

    // variáveis de dimensões
    private int screenWidth;
    private int screenHeight;

    // variáveis para o game loop e estatísticas
    private boolean gameOver = true;
    private double timeLeft; // tempo restante em segundos
    private int shotsFired; // tiros efetuados pelo utilizador
    private double totalElapsedTime; // segundos decorridos

    // constantes e variáveis para gestão de sons
    public static final int TARGET_SOUND_ID = 0;
    public static final int CANNON_SOUND_ID = 1;
    public static final int BLOCKER_SOUND_ID = 2;
    private SoundPool soundPool; // reproduz efeitos sonoros
    private SparseIntArray soundMap; // mapeia IDs para o SoundPool

    // variáveis Paint usadas ao desenhar itens no ecrã
    private Paint textPaint;
    private Paint backgroundPaint;

    // construtor
    public CannonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = getActivityFromContext(context);

        getHolder().addCallback(this); // regista o callback do SurfaceHolder

        // configura atributos de áudio para o jogo
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setUsage(AudioAttributes.USAGE_GAME);
        attrBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);

        // inicializa o SoundPool com suporte para múltiplos canais de som
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(5);
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();

        // cria o mapa de sons e pré-carrega os ficheiros de recursos
        soundMap = new SparseIntArray(3);
        soundMap.put(TARGET_SOUND_ID, soundPool.load(context, R.raw.target_hit, 1));
        soundMap.put(CANNON_SOUND_ID, soundPool.load(context, R.raw.cannon_fire, 1));
        soundMap.put(BLOCKER_SOUND_ID, soundPool.load(context, R.raw.blocker_hit, 1));

        textPaint = new Paint();
        textPaint.setAntiAlias(false);
        textPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        textPaint.setColor(0xFFF5F4F0);
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(false);
    }

    private Activity getActivityFromContext(Context context) {
        Context currentContext = context;
        while (currentContext instanceof ContextWrapper) {
            if (currentContext instanceof Activity) {
                return (Activity) currentContext;
            }
            currentContext = ((ContextWrapper) currentContext).getBaseContext();
        }
        return null;
    }

    // Retorna se o ecrã está em modo Portrait (vertical)
    public boolean isPortrait() {
        return screenHeight > screenWidth;
    }

    // chamado quando o tamanho da SurfaceView muda
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w;
        screenHeight = h;

        textPaint.setTextSize(Math.max(14, (int) (0.042 * (isPortrait() ? screenWidth : screenHeight))));

        if (oldw != 0 && oldh != 0 && (oldw != w || oldh != h)) {
            newGame();
        }
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    // reproduz um som com o soundId fornecido se o som estiver ativado nas preferências
    public void playSound(int soundId) {
        SharedPreferences prefs = getContext().getSharedPreferences("CannonGamePrefs", Context.MODE_PRIVATE);
        boolean isSoundOn = prefs.getBoolean("sound_enabled", true);

        if (isSoundOn && soundPool != null) {
            int streamId = soundMap.get(soundId, -1);
            if (streamId != -1) {
                soundPool.play(streamId, 1.0f, 1.0f, 1, 0, 1.0f);
            }
        }
    }

    // reinicia todos os elementos do ecrã e inicia um novo jogo
    public void newGame() {
        Random random = new Random();
        targets = new ArrayList<>();

        if (isPortrait()) {
            // Em Portrait: o canhão fica no topo
            cannon = new Cannon(this,
                    (int) (CANNON_BASE_RADIUS_PERCENT * screenWidth),
                    (int) (CANNON_BARREL_LENGTH_PERCENT * screenHeight),
                    (int) (CANNON_BARREL_WIDTH_PERCENT * screenWidth));

            int targetY = (int) (TARGET_FIRST_X_PERCENT * screenHeight);

            for (int n = 0; n < TARGET_PIECES; n++) {
                double velocity = screenWidth * (random.nextDouble() * (
                        TARGET_MAX_SPEED_PERCENT - TARGET_MIN_SPEED_PERCENT) + TARGET_MIN_SPEED_PERCENT);

                int color = (n % 2 == 0) ?
                        getResources().getColor(R.color.palette_blue_dark, getContext().getTheme()) :
                        getResources().getColor(R.color.palette_blue_bright, getContext().getTheme());

                velocity *= -1;

                targets.add(new Target(this, color, HIT_REWARD,
                        (int) ((0.5 - TARGET_LENGTH_PERCENT / 2) * screenWidth),
                        targetY,
                        (int) (TARGET_LENGTH_PERCENT * screenWidth),
                        (int) (TARGET_WIDTH_PERCENT * screenHeight),
                        (float) velocity, 0));

                targetY += (TARGET_WIDTH_PERCENT + TARGET_SPACING_PERCENT) * screenHeight;
            }

            blocker = new Blocker(this, Color.BLACK, MISS_PENALTY,
                    (int) ((0.5 - BLOCKER_LENGTH_PERCENT / 2) * screenWidth),
                    (int) (BLOCKER_X_PERCENT * screenHeight),
                    (int) (BLOCKER_LENGTH_PERCENT * screenWidth),
                    (int) (BLOCKER_WIDTH_PERCENT * screenHeight),
                    (float) (BLOCKER_SPEED_PERCENT * screenWidth), 0);

        } else {
            // Em Landscape: o canhão fica na esquerda
            cannon = new Cannon(this,
                    (int) (CANNON_BASE_RADIUS_PERCENT * screenHeight),
                    (int) (CANNON_BARREL_LENGTH_PERCENT * screenWidth),
                    (int) (CANNON_BARREL_WIDTH_PERCENT * screenHeight));

            int targetX = (int) (TARGET_FIRST_X_PERCENT * screenWidth);

            for (int n = 0; n < TARGET_PIECES; n++) {
                double velocity = screenHeight * (random.nextDouble() * (
                        TARGET_MAX_SPEED_PERCENT - TARGET_MIN_SPEED_PERCENT) + TARGET_MIN_SPEED_PERCENT);

                int color = (n % 2 == 0) ?
                        getResources().getColor(R.color.palette_blue_dark, getContext().getTheme()) :
                        getResources().getColor(R.color.palette_blue_bright, getContext().getTheme());

                velocity *= -1;

                targets.add(new Target(this, color, HIT_REWARD,
                        targetX,
                        (int) ((0.5 - TARGET_LENGTH_PERCENT / 2) * screenHeight),
                        (int) (TARGET_WIDTH_PERCENT * screenWidth),
                        (int) (TARGET_LENGTH_PERCENT * screenHeight),
                        0, (float) velocity));

                targetX += (TARGET_WIDTH_PERCENT + TARGET_SPACING_PERCENT) * screenWidth;
            }

            blocker = new Blocker(this, Color.BLACK, MISS_PENALTY,
                    (int) (BLOCKER_X_PERCENT * screenWidth),
                    (int) ((0.5 - BLOCKER_LENGTH_PERCENT / 2) * screenHeight),
                    (int) (BLOCKER_WIDTH_PERCENT * screenWidth),
                    (int) (BLOCKER_LENGTH_PERCENT * screenHeight),
                    0, (float) (BLOCKER_SPEED_PERCENT * screenHeight));
        }

        timeLeft = 10;
        shotsFired = 0;
        totalElapsedTime = 0.0;
        gameOver = false;

        stopGame();

        cannonThread = new CannonThread(getHolder());
        cannonThread.setRunning(true);
        cannonThread.start();

        hideSystemBars();
    }

    private void updatePositions(double elapsedTimeMS) {
        double interval = elapsedTimeMS / 1000.0;

        if (cannon.getCannonball() != null)
            cannon.getCannonball().update(interval);

        blocker.update(interval);

        for (GameElement target : targets)
            target.update(interval);

        timeLeft -= interval;

        if (timeLeft <= 0) {
            timeLeft = 0.0;
            gameOver = true;
            if (cannonThread != null) {
                cannonThread.setRunning(false);
            }
            saveScore();
            showGameOverDialog(R.string.lose);
        }

        if (targets.isEmpty()) {
            gameOver = true;
            if (cannonThread != null) {
                cannonThread.setRunning(false);
            }
            saveScore();
            showGameOverDialog(R.string.win);
        }
    }

    // Guarda a pontuação obtida no SharedPreferences se for a maior pontuação (High Score)
    private void saveScore() {
        int targetsHit = (int) TARGET_PIECES - (targets != null ? targets.size() : 0);
        int finalScore = (targetsHit * 500) + (int) (timeLeft * 100) - (shotsFired * 10);
        if (finalScore < 0) finalScore = 0;

        Context ctx = getContext();
        if (ctx == null) return;
        SharedPreferences prefs = ctx.getSharedPreferences("CannonGamePrefs", Context.MODE_PRIVATE);
        int currentHighScore = prefs.getInt("high_score", 0);
        if (currentHighScore == 0) {
            currentHighScore = prefs.getInt("high_score_1", 0);
        }

        if (finalScore > currentHighScore) {
            prefs.edit().putInt("high_score", finalScore).apply();
        }
    }

    public void alignAndFireCannonball(MotionEvent event) {
        Point touchPoint = new Point((int) event.getX(), (int) event.getY());
        double angle;

        if (isPortrait()) {
            double deltaX = touchPoint.x - (screenWidth / 2.0);
            double deltaY = touchPoint.y;
            angle = Math.atan2(deltaX, deltaY);
        } else {
            double centerMinusY = (screenHeight / 2.0) - touchPoint.y;
            angle = Math.atan2(touchPoint.x, centerMinusY);
        }

        cannon.align(angle);

        if (cannon.getCannonball() == null || !cannon.getCannonball().isOnScreen()) {
            cannon.fireCannonball();
            ++shotsFired;
        }
    }

    private void showGameOverDialog(final int messageId) {
        if (activity == null) return;

        activity.runOnUiThread(() -> {
            showSystemBars();
            dialogIsDisplayed = true;

            int targetsHit = (int) TARGET_PIECES - (targets != null ? targets.size() : 0);
            int finalScore = (targetsHit * 500) + (int) (timeLeft * 100) - (shotsFired * 10);
            if (finalScore < 0) finalScore = 0;

            SharedPreferences prefs = activity.getSharedPreferences("CannonGamePrefs", Context.MODE_PRIVATE);
            int savedHighScore = prefs.getInt("high_score", 0);
            if (savedHighScore == 0) {
                savedHighScore = prefs.getInt("high_score_1", 0);
            }

            boolean isNewRecord = finalScore > savedHighScore;
            if (isNewRecord) {
                prefs.edit().putInt("high_score", finalScore).apply();
                savedHighScore = finalScore;
            }

            StringBuilder statsBuilder = new StringBuilder();
            statsBuilder.append(getResources().getString(R.string.results_format, shotsFired, totalElapsedTime));
            statsBuilder.append("\n\n");
            statsBuilder.append(String.format(Locale.getDefault(), getResources().getString(R.string.current_score_format), finalScore));
            statsBuilder.append("\n");
            statsBuilder.append(String.format(Locale.getDefault(), getResources().getString(R.string.high_score_format), savedHighScore));

            if (isNewRecord && finalScore > 0) {
                statsBuilder.append("\n\n").append(getResources().getString(R.string.new_record_message));
            }

            String results = statsBuilder.toString();

            View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_game_over, null);

            TextView txtTitle = dialogView.findViewById(R.id.txtDialogTitle);
            TextView txtStats = dialogView.findViewById(R.id.txtDialogStats);
            Button btnReset = dialogView.findViewById(R.id.btnDialogReset);
            Button btnMenu = dialogView.findViewById(R.id.btnDialogMenu);

            txtTitle.setText(getResources().getString(messageId));
            txtStats.setText(results);

            AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            btnReset.setOnClickListener(v -> {
                dialogIsDisplayed = false;
                dialog.dismiss();
                newGame();
            });

            btnMenu.setOnClickListener(v -> {
                dialogIsDisplayed = false;
                dialog.dismiss();
                stopGame();
                releaseResources();

                if (activity instanceof FragmentActivity) {
                    FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                    } else {
                        fm.beginTransaction()
                                .replace(R.id.fragmentContainerView, new MenuFragment())
                                .commit();
                    }
                }
            });

            dialog.show();
            android.view.Window window = dialog.getWindow();
            if (window != null) {
                float density = getResources().getDisplayMetrics().density;
                int availableWidth = getResources().getDisplayMetrics().widthPixels - (int) (32 * density);
                int dialogWidth = Math.min(availableWidth, (int) (480 * density));
                window.setLayout(dialogWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
    }

    public void drawGameElements(Canvas canvas) {
        drawBattleBackground(canvas);

        double displayTime = Math.max(0.0, timeLeft);
        int targetsHit = (int) TARGET_PIECES - (targets != null ? targets.size() : 0);
        int displayScore = Math.max(0, targetsHit * 500 - shotsFired * 10 + (int) (displayTime * 100));
        float hudTop = screenHeight * (isPortrait() ? 0.045f : 0.07f);
        float textSize = textPaint.getTextSize();
        backgroundPaint.setStyle(Paint.Style.FILL);
        if (isPortrait()) {
            String timer = String.format(Locale.getDefault(), "TEMPO %.1f", displayTime);
            float timerWidth = textPaint.measureText(timer);
            float pivotX = screenWidth * 0.065f;
            float pivotY = (screenHeight + timerWidth) / 2f;

            canvas.save();
            canvas.rotate(-90, pivotX, pivotY);
            backgroundPaint.setColor(0xE51A1A1D);
            canvas.drawRect(pivotX - 8, pivotY - textSize, pivotX + timerWidth + 8,
                    pivotY + textSize * 0.45f, backgroundPaint);
            backgroundPaint.setStyle(Paint.Style.STROKE);
            backgroundPaint.setStrokeWidth(3f);
            backgroundPaint.setColor(0xFFD7353F);
            canvas.drawRect(pivotX - 8, pivotY - textSize, pivotX + timerWidth + 8,
                    pivotY + textSize * 0.45f, backgroundPaint);
            backgroundPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(timer, pivotX, pivotY, textPaint);
            canvas.restore();

            String score = String.format(Locale.getDefault(), "PTS %d", displayScore);
            float scoreWidth = textPaint.measureText(score);
            float scoreX = Math.max(screenWidth * 0.38f, screenWidth - scoreWidth - 20);
            backgroundPaint.setColor(0xE51A1A1D);
            canvas.drawRect(scoreX - 8, hudTop - textSize, scoreX + scoreWidth + 8,
                    hudTop + textSize * 0.45f, backgroundPaint);
            backgroundPaint.setStyle(Paint.Style.STROKE);
            backgroundPaint.setStrokeWidth(3f);
            backgroundPaint.setColor(0xFFD7353F);
            canvas.drawRect(scoreX - 8, hudTop - textSize, scoreX + scoreWidth + 8,
                    hudTop + textSize * 0.45f, backgroundPaint);
            backgroundPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(score, scoreX, hudTop, textPaint);
        } else {
            float hudLeft = screenWidth * 0.035f;
            String hud = String.format(Locale.getDefault(), "TEMPO %.1f   PTS %d", displayTime, displayScore);
            float hudWidth = textPaint.measureText(hud);
            backgroundPaint.setColor(0xE51A1A1D);
            canvas.drawRect(hudLeft - 8, hudTop - textSize, hudLeft + hudWidth + 8,
                    hudTop + textSize * 0.45f, backgroundPaint);
            backgroundPaint.setStyle(Paint.Style.STROKE);
            backgroundPaint.setStrokeWidth(3f);
            backgroundPaint.setColor(0xFFD7353F);
            canvas.drawRect(hudLeft - 8, hudTop - textSize, hudLeft + hudWidth + 8,
                    hudTop + textSize * 0.45f, backgroundPaint);
            backgroundPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(hud, hudLeft, hudTop, textPaint);
        }

        cannon.draw(canvas);

        if (cannon.getCannonball() != null && cannon.getCannonball().isOnScreen())
            cannon.getCannonball().draw(canvas);

        blocker.draw(canvas);

        for (GameElement target : targets)
            target.draw(canvas);
    }

    private void drawBattleBackground(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        float cx = width * (isPortrait() ? 0.5f : 0.69f);
        float cy = height * (isPortrait() ? 0.56f : 0.53f);
        float radius = Math.min(width, height) * (isPortrait() ? 0.29f : 0.38f);
        float cell = radius * 2 / 12f;
        float left = cx - radius;
        float top = cy - radius;
        int[] ballColors = {
                Color.TRANSPARENT,
                0xFF505056,
                0xFFD7353F,
                0xFFF5F4F0
        };
        for (int row = 0; row < BACKGROUND_POKEBALL.length; row++) {
            String pixels = BACKGROUND_POKEBALL[row];
            for (int column = 0; column < pixels.length(); column++) {
                int color = ballColors[pixels.charAt(column) - '0'];
                if (color != Color.TRANSPARENT) {
                    backgroundPaint.setColor(color);
                    float x = left + column * cell;
                    float y = top + row * cell;
                    canvas.drawRect(x, y, x + cell, y + cell, backgroundPaint);
                }
            }
        }
        backgroundPaint.setColor(0xFF17171A);
        canvas.drawRect(cx - cell * 2, cy - cell * 2, cx + cell * 2, cy + cell * 2, backgroundPaint);
        backgroundPaint.setColor(0xFFF5F4F0);
        canvas.drawRect(cx - cell, cy - cell, cx + cell, cy + cell, backgroundPaint);
    }

    public void testForCollisions() {
        if (cannon != null && cannon.getCannonball() != null && cannon.getCannonball().isOnScreen()) {
            if (blocker != null && cannon.getCannonball().collidesWith(blocker)) {
                blocker.playSound();
                cannon.getCannonball().reverseVelocity();
                timeLeft -= blocker.getMissPenalty();
                if (timeLeft <= 0) {
                    timeLeft = 0.0;
                    gameOver = true;
                    if (cannonThread != null) {
                        cannonThread.setRunning(false);
                    }
                    saveScore();
                    showGameOverDialog(R.string.lose);
                }
            } else if (targets != null) {
                for (int n = 0; n < targets.size(); n++) {
                    if (cannon.getCannonball().collidesWith(targets.get(n))) {
                        targets.get(n).playSound();
                        timeLeft += targets.get(n).getHitReward();
                        cannon.removeCannonball();
                        targets.remove(n);
                        --n;
                        break;
                    }
                }
            }
        }
    }

    public void stopGame() {
        if (cannonThread != null) {
            cannonThread.setRunning(false);
            boolean retry = true;
            while (retry) {
                try {
                    cannonThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    Log.e(TAG, "Thread interrompida", e);
                }
            }
            cannonThread = null;
        }
    }

    public void releaseResources() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!dialogIsDisplayed) {
            newGame();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        if (!gameOver && (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE)) {
            alignAndFireCannonball(e);
        }
        return true;
    }

    private class CannonThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private boolean threadIsRunning = true;

        public CannonThread(SurfaceHolder holder) {
            surfaceHolder = holder;
            setName("CannonThread");
        }

        public void setRunning(boolean running) {
            threadIsRunning = running;
        }

        @Override
        public void run() {
            Canvas canvas = null;
            long previousFrameTime = System.currentTimeMillis();

            while (threadIsRunning) {
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas != null) {
                        synchronized (surfaceHolder) {
                            long currentTime = System.currentTimeMillis();
                            double elapsedTimeMS = currentTime - previousFrameTime;
                            if (elapsedTimeMS > 100) {
                                elapsedTimeMS = 100; // Limita o delta por frame a 100ms
                            }
                            totalElapsedTime += elapsedTimeMS / 1000.0;

                            updatePositions(elapsedTimeMS);
                            testForCollisions();
                            drawGameElements(canvas);
                            previousFrameTime = currentTime;
                        }
                    }
                } finally {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
