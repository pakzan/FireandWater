package com.example.dell.fireandwater;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by Dell on 15/07/2015.
 */
public class Food {
    public int type;
    public int size;
    protected boolean toLeft;
    public float locX;
    public float locY;
    public double X;
    private Random rand = new Random();
    public Paint element = new Paint();

    public Food(){
        locX = rand.nextInt(FireAndWater.height) / GamePanel.scaleFactorX;
        X = locX;

        if (rand.nextInt(2) == 1) {
            toLeft = true;
            locY = FireAndWater.width / GamePanel.scaleFactorX + (float)Player.size;
        }
        else {
            toLeft = false;
            locY = -(float)Player.size;
        }

        type = rand.nextInt(2);
        if (type == 0) {
            element.setColor(Color.RED);
            size = (int)(Computer.size) / 8 + rand.nextInt((int)(Computer.size) / 8);
        }
        else {
            element.setColor(Color.BLUE);
            size = (int)(Player.size) / 8 + rand.nextInt((int)(Player.size) / 8);
        }
        element.setStyle(Paint.Style.FILL);
    }
    
    public void update(){
        //if got eaten, then disappear
        //Player
        if (Square(locX - Player.locX) + Square(locY - Player.locY) < Square((int)Player.size)) {
            if (type == Player.type)
                Player.size = Math.hypot(Player.size, size);
            else
                Player.size = Math.sqrt(Square((int)Player.size) - Square(size));
            locY = -(float)Player.size - 1;
            return;
        }
        //Computer
        else if (Square(locX - Computer.locX) + Square(locY - Computer.locY) < Square((int)Computer.size)) {
            if (type == Computer.type)
                Computer.size = Math.hypot(Computer.size, size);
            else
                Computer.size = Math.sqrt(Square((int)Computer.size) - Square(size));
            locY = -(float)Player.size - 1;
            return;
        }

        //continue to move
        //horizontal
        if (toLeft)
            locY -= (int)(Player.size / 100) + 1;
        else
            locY += (int)(Player.size / 100) + 1;

        //vertical
        if (type == Player.type) {
            //food get away from player
            if (locX > Player.locX)
                X += Player.size / 300;
            else
                X -= Player.size / 300;
        }else {
            //food get away from computer
            if (locX > Computer.locX)
                X += Computer.size / 300;
            else
                X -= Computer.size / 300;
        }
        locX = (int)X;
    }

    public void draw(Canvas canvas){
        canvas.drawCircle(locX, locY, size, element);
    }

    public float Square(float x){
        return x * x;
    }

    public int Square(int x){
        return x * x;
    }
}
