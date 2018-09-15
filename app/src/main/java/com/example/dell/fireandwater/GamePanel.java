package com.example.dell.fireandwater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Dell on 14/07/2015.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static Context context;
    private MainThread thread;
    private static Player player;
    private static Computer computer;
    protected static ArrayList<Food> food;
    public static boolean startPlaying = true;
    private static Random rand = new Random();
    public static int width;
    public static int height;
    private static float posX = -1;
    private static float posY = -1;
    protected static float scaleFactorX = 1;
    protected static float scaleFactorY = 1;
    private static long foodStartTime = System.nanoTime();
    private static long foodProduceTime = System.nanoTime();
    private static long tapStart = System.nanoTime();
    public static ExecutorService executor;

    public GamePanel(Context context, int width, int height){
        super(context);
        getHolder().addCallback(this);
        this.width = width;
        this.height = height;
        this.context = context;
        //tapSound = MediaPlayer.create(context, R.raw.tap);

        reset();
        thread = new MainThread(getHolder(), this);
        executor = Executors.newSingleThreadExecutor();
        executor.execute(thread);

        setFocusable(true);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        if (!thread.running) {
            reset();
            thread.setRunning(true);
            thread.start();
        }
        /*if (!checkWin.checkWin())
            reset();*/
        startPlaying = true;
        FireAndWater.viewChanged = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

    }

    public static void reset(){
        player = new Player();
        computer = new Computer();
        food = new ArrayList<Food>();

        player.damageAlpha = 0;
        computer.damageAlpha = 0;

        scaleFactorX = 1;
        scaleFactorY = 1;
    }

    public static void update(){
        long foodElapsed = (System.nanoTime() - foodStartTime) / 1000000;
        long foodProduce = (System.nanoTime() - foodProduceTime) / 1000000;

        //generate food
        if (foodProduce > 1000) {
            foodProduceTime = System.nanoTime();
            int addFood = rand.nextInt(100 / (int)(Player.size + Computer.size) + 1) + 2;
            for (int i = 0; i < addFood; i++)
                food.add(new Food());
        }

        //remove food outside boundary
        if (foodElapsed > 10) {
            foodStartTime = System.nanoTime();
            for (int i = 0; i < food.size(); i++) {
                food.get(i).update();
                if (food.get(i).locY < -player.size || food.get(i).locX < -player.size ||
                        food.get(i).locX > height / GamePanel.scaleFactorX + player.size ||
                        food.get(i).locY > width / GamePanel.scaleFactorX + player.size) {
                    food.remove(i);
                }
            }

            computer.update();
            player.update();

            if (player.life >= 255 || computer.life >= 255 ||
                    player.size <= 5 || computer.size <= 5)
                reset();
        }
    }

    @Override
    public void draw(Canvas canvas){
        if (startPlaying) {

            if (player.size > computer.size && player.size > getWidth() / 4) {
                scaleFactorX = (float) (getWidth() / (4 * player.size));
                scaleFactorY = (float) (getWidth() / (4 * player.size));
            }
            else if (computer.size > player.size && computer.size > getWidth() / 4) {
                scaleFactorX = (float) (getWidth() / (4 * computer.size));
                scaleFactorY = (float) (getWidth() / (4 * computer.size));
            }else{
                scaleFactorX = 1;
                scaleFactorY = 1;
            }

            final int saveState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPaint(paint);

            computer.draw(canvas);
            player.draw(canvas);

            for(Food fd : food)
                fd.draw(canvas);
            /*checkWin.checkWin();
            checkWin.DrawResult(canvas);
            Bitmap refresh = BitmapFactory.decodeResource(getResources(), R.drawable.refresh);
            Rect src = new Rect(0, 0, refresh.getWidth(), refresh.getHeight());
            Rect des = new Rect(width - 80, 20, width - 20, 80);
            canvas.drawBitmap(refresh, src, des, null);*/

            canvas.restoreToCount(saveState);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            float eventX = event.getX();
            float eventY = event.getY();

            if (Square(eventX - player.locX) + Square(eventY - player.locY) < Square((int)player.size)) {
                tapStart = System.nanoTime();
            }
        }

        if(event.getAction() == MotionEvent.ACTION_UP) {
            float eventX = event.getX();
            float eventY = event.getY();
            posX = -1;
            posY = -1;

            if (Square(eventX - player.locX) + Square(eventY - player.locY) < Square((int)player.size)) {
                if (System.nanoTime() - tapStart < 100 * 1000000) {
                    if (player.type == 0)
                        player.type = 1;
                    else
                        player.type = 0;
                }
            }
        }

        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            float eventX = event.getX();
            float eventY = event.getY();
            if (Square(eventX - player.locX) + Square(eventY - player.locY) < Square((int)player.size) && posX != -1 && posY != -1) {
                player.locX += eventX - posX;
                player.locY += eventY - posY;
            }

            posX = eventX;
            posY = eventY;
            /*if (checkWin.checkWin() && touchimage.onTouchEvent(event, context)) {
                //tapSound.start();

                checkWin.checkWin();
            }*/
            startPlaying = true;
        }
        return true;
    }

    public float Square(float x){
        return x * x;
    }

}