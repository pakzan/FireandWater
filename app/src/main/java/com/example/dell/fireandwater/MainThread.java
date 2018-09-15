package com.example.dell.fireandwater;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import java.util.concurrent.Executor;

/**
 * Created by Dell on 14/07/2015.
 */
public class MainThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    public boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel){
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;

    }

    @Override
    public void run() {


        int FPS = 60;
        long startTime;
        long waitTime;
        long timeMillis;
        long targetTime = 1000 / FPS;

        while (running) {

            if (gamePanel.startPlaying) {
                startTime = System.nanoTime();
                canvas = null;
                try {
                    canvas = this.surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        this.gamePanel.update();
                        this.gamePanel.draw(canvas);
                    }
                } catch (Exception e) {
                } finally {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                    }
                }

                timeMillis = (System.nanoTime() - startTime) / 1000000;
                waitTime = targetTime - timeMillis;
                try {
                    this.sleep(waitTime);
                } catch (Exception e) {
                }
                gamePanel.startPlaying = true;

            }
        }
    }


    public void setRunning(boolean b){running = b;}
}
