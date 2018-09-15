package com.example.dell.fireandwater;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Dell on 14/07/2015.
 */
public class Player {
    public static double size;
    public static int type;
    public static float locX;
    public static float locY;
    public static boolean stop;
    protected static float life;
    private static float prevX = 0;
    private static float prevY = 0;
    public static int damageAlpha = 0;
    public static Paint element = new Paint();
    public static Paint lifeColor = new Paint();
    public static Paint damage = new Paint();

    public Player(){
        size = 50;
        locY = 100;
        locX = FireAndWater.height / 2;
        life = 0;
        stop = false;
        
        element.setColor(Color.RED);
        element.setStyle(Paint.Style.FILL);

        damage.setARGB(0, 255, 215, 0);
        damage.setStyle(Paint.Style.FILL);

        lifeColor.setColor(Color.WHITE);
        lifeColor.setStyle(Paint.Style.FILL);
    }

    public void update(){
        if (type == 0)
            element.setColor(Color.RED);
        else
            element.setColor(Color.BLUE);

        damage.setAlpha(damageAlpha);
        lifeColor.setAlpha((int)life);

        //move player by gravity
        if (!stop) {
            locX -= FireAndWater.x / GamePanel.scaleFactorX;
            locY += FireAndWater.y / GamePanel.scaleFactorX;
        }else
            stop = false;

        //if being push by computer to the corner, then stop both movement
        if (limitMovement() && stopMovement()){
            Computer.locX = prevX;
            Computer.locY = prevY;
        }else
            stopMovement();

        prevX = Computer.locX;
        prevY = Computer.locY;
    }

    public void draw(Canvas canvas){
        canvas.drawCircle(locX, locY, (int)size, element);
        canvas.drawCircle(locX, locY, (int)size, lifeColor);
        canvas.drawCircle(locX, locY, (int)size, damage);
    }

    public boolean limitMovement(){
        //set maximum and minimum boundary
        boolean b = false;
        if (locX > FireAndWater.height / GamePanel.scaleFactorX) {
            locX = FireAndWater.height / GamePanel.scaleFactorX;
            b = true;
        }
        else if (locX < 0) {
            locX = 0;
            b = true;
        }

        if (locY > FireAndWater.width / GamePanel.scaleFactorX) {
            locY = FireAndWater.width / GamePanel.scaleFactorX;
            b = true;
        }
        else if (locY < 0) {
            locY = 0;
            b = true;
        }
        return b;
    }

    public boolean stopMovement(){
        //stop player movement if collision occur
        if (size < Computer.size){
            if (Math.pow((double)(locX - Computer.locX), 2) + Math.pow((double)(locY - Computer.locY), 2)
                    < Math.pow((size + Computer.size), 2)){

                double LenY = (locY - Computer.locY) / (size + Computer.size);
                double LenX = (locX - Computer.locX) / (size + Computer.size);
                int n = 0;
                do {
                    locX += LenX;
                    locY += LenY;
                    n += 1;
                }while (Math.pow((double)(locX - Computer.locX), 2) + Math.pow((double)(locY - Computer.locY), 2)
                        < Math.pow((size + Computer.size), 2) && n < 100);

                if (size < Computer.size * 9 / 10) {
                    //do damage
                    size -= 0.001 * (Computer.size - size);
                    Computer.size -= 0.002 * (Computer.size - size);
                    life += 0.3;
                    stop = false;

                    if (damageAlpha < 150)
                        damageAlpha += 5;
                    else
                        damageAlpha = 0;
                }
                return true;
            }else
                damageAlpha = 0;
        }
        return false;
    }
}
