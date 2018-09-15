package com.example.dell.fireandwater;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by Dell on 14/07/2015.
 */
public class Computer {
    public static double size;
    public static int type = 1;
    public static float locX;
    public static float locY;
    private static float prevX = 0;
    private static float prevY = 0;
    protected static float life;
    private static double speed;
    private static boolean leave;
    private static int rest;
    public static int damageAlpha = 0;
    private static int id, sameId, banId = 0;
    private Random rand = new Random();
    public static Paint element = new Paint();
    public static Paint damage = new Paint();
    public static Paint lifeColor = new Paint();

    public Computer(){
        size = 50;
        life = 0;
        rest = 0;
        locY = FireAndWater.width - 100;
        locX = FireAndWater.height / 2;
        leave = true;

        element.setColor(Color.BLUE);
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

        speed = Player.size / 25 + 1;

        if (Square(locX - Player.locX) + Square(locY - Player.locY) < Square(2 * (int)size + (int)Player.size) &&
                size * 9 / 10 > Player.size) {
            //if size bigger than player, then push player

            if (locX > Player.locX)
                locX -= speed + 1;
            else
                locX += speed + 1;

            if (locY > Player.locY)
                locY -= speed + 1;
            else
                locY += speed + 1;

        }else if (mainLeave()) {
            //if being push by player, then escape

            float escapeX = 0;
            float escapeY = 0;
            if (locX >= Player.locX)
                escapeX = FireAndWater.height / GamePanel.scaleFactorX - Player.locX;
            else
                escapeX = Player.locX;

            if (locY >= Player.locY)
                escapeY = FireAndWater.width / GamePanel.scaleFactorX - Player.locY;
            else
                escapeY = Player.locY;

            //find the best path to escape
            if (escapeX >= escapeY){
                if (locX > Player.locX)
                    locX += speed + 1;
                else if (locX < Player.locX)
                    locX -= speed + 1;
                else if (locX == Player.locX){
                    if (Player.locX < FireAndWater.height / GamePanel.scaleFactorX / 2)
                        locX += speed + 1;
                    else
                        locX -= speed + 1;
                }

                if (locY > FireAndWater.width / GamePanel.scaleFactorX / 2 + speed + 1)
                    locY -= speed + 1;
                else if (locY < FireAndWater.width / GamePanel.scaleFactorX / 2 - speed - 1)
                    locY += speed + 1;
            }else if (escapeY >= escapeX){
                if (locY > Player.locY)
                    locY += speed + 1;
                else if (locY < Player.locY)
                    locY -= speed + 1;
                else if (locY == Player.locY) {
                    if (Player.locY < FireAndWater.width / GamePanel.scaleFactorY / 2)
                        locY += speed + 1;
                    else
                        locY -= speed + 1;
                }

                if (locX > FireAndWater.height / GamePanel.scaleFactorX / 2 + speed + 1)
                    locX -= speed + 1;
                else if (locX < FireAndWater.height / GamePanel.scaleFactorX / 2 - speed - 1)
                    locX += speed + 1;
            }
        }else{
            eatFood();
        }

        //if being push by player to the corner, then stop both movement
        if (limitMovement() && stopMovement()){
            Player.locX = prevX;
            Player.locY = prevY;
        }else
            stopMovement();

        prevX = Player.locX;
        prevY = Player.locY;
    }

    public void draw(Canvas canvas){
        canvas.drawCircle(locX, locY, (int)size, element);
        canvas.drawCircle(locX, locY, (int)size, lifeColor);
        canvas.drawCircle(locX, locY, (int)size, damage);
    }

    public float Square(float x){
        return x * x;
    }

    public int Square(int x){
        return x * x;
    }

    public void eatFood(){
        boolean eat = false;
        int n = 2;
        int tempId = 0;
        float tempX = locX;
        float tempY = locY;
        float tempX1 = locX;
        float tempY1 = locY;
        do {
            tempX1 = tempX;
            tempY1 = tempY;
            for (Food fd : GamePanel.food) {
                if (type == fd.type &&
                        Square(tempX1 - fd.locX) + Square(tempY1 - fd.locY) < Square((int) size + n * 200) &&
                        fd.locY < FireAndWater.width / GamePanel.scaleFactorX - fd.size && fd.locY > fd.size &&
                        fd.locX < FireAndWater.height / GamePanel.scaleFactorX - fd.size && fd.locX > fd.size &&
                        fd.hashCode() != banId) {
                    eat = true;
                    tempId = fd.hashCode();
                    //eat food
                    if (tempX1 >= fd.locX)
                        tempX1 -= speed;
                    else
                        tempX1 += speed;

                    if (tempY1 >= fd.locY && (!fd.toLeft || tempY1 - fd.locY < size + 300))
                        tempY1 -= speed;
                    else if (tempY1 < fd.locY && (fd.toLeft || fd.locY - tempY1 < size + 300))
                        tempY1 += speed;
                    else
                        eat = false;

                    if (Player.size > size){
                        if (Square(fd.locX - Player.locX) + Square(fd.locY - Player.locY) < Square((int)size + 3 * (int)Player.size))
                            eat = false;
                    }

                    if (eat)
                        break;
                }
            }
            n++;
        }while(!eat && size + n * 200 < FireAndWater.width / GamePanel.scaleFactorX);

        for (Food fd : GamePanel.food) {
            if (fd.size > size / 64 && type != fd.type &&
                    Square(tempX1 - fd.locX) + Square(tempY1 - fd.locY) < Square((int)size + fd.size + 10)) {
                //escape harmful food
                float escapeX = 0;
                float escapeY = 0;
                escapeX = Square(tempX1 - fd.locX);
                escapeY = Square(tempY1 - fd.locY);

                if (escapeX >= escapeY) {
                    if (tempX1 > tempX && tempX1 < fd.locX || tempX1 < tempX && tempX1 > fd.locX)
                        tempX1 = tempX + (tempX - fd.locX) / Math.abs(tempX - fd.locX);
                }else{
                    if (tempY1 > tempY && tempY1 < fd.locY || tempY1 < tempY && tempY1 > fd.locY)
                        tempY1 = tempY + (tempY - fd.locY) / Math.abs(tempY - fd.locY);
                }

                if (id == tempId) {
                    sameId++;
                    if (sameId > 100){
                        sameId = 0;
                        banId = id;
                    }
                }
                else{
                    sameId = 0;
                    id = tempId;
                }

                break;
            }
        }
        locX = tempX1;
        locY = tempY1;
    }

    public boolean mainLeave(){

        if (Player.size > size) {
            if (Square(locX - Player.locX) + Square(locY - Player.locY) < Square((int) size + 2 * (int) Player.size) || !false) {

                rest++;
                if (Square(locX - Player.locX) + Square(locY - Player.locY) < Square((int) size + (int) Player.size + 100) || rest > 200)
                    rest = 0;
                else if (rest > 100)
                    return false;

                leave = false;
                if (Square(locX - Player.locX) + Square(locY - Player.locY) > Square((int) (size + 2.5 * Player.size)))
                    leave = true;
                return true;
            }else
                rest = 0;
        }
        return false;
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
        if (size <= Player.size){
            if (Math.pow((double)(locX - Player.locX), 2) + Math.pow((double)(locY - Player.locY), 2)
                    < Math.pow((size + Player.size), 2)){

                double LenY = (locY - Player.locY) / (size + Player.size);
                double LenX = (locX - Player.locX) / (size + Player.size);
                int n = 0;
                do {
                    locX += LenX;
                    locY += LenY;
                    n += 1;
                }
                while (Math.pow((double) (locX - Player.locX), 2) + Math.pow((double) (locY - Player.locY), 2)
                        < Math.pow((size + Player.size), 2) && n < 100);

                if (size < Player.size * 9 / 10) {
                    //do damage
                    size -= 0.001 * (Player.size - size);
                    Player.size -= 0.002 * (Player.size - size);
                    life += 0.3;

                    if (damageAlpha < 150)
                        damageAlpha += 5;
                    else
                        damageAlpha = 0;
                }
                return true;
            } else
                damageAlpha = 0;
        }
        return false;
    }
}
