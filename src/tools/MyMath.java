package tools;

import java.util.*;

import model.SceneObject;

public abstract class MyMath {

    private static Random rnd = new Random();

    public static int nextInt(int min, int max) {
        return (int) (rnd.nextDouble() * (max - min + 1)) + min;
    }

    public static double Cos(double degrees) {
        return Math.cos((degrees / 360.) * 2. * Math.PI);
    }

    public static double Sin(double degrees) {
        return Math.sin((degrees / 360.) * 2. * Math.PI);
    }

    public static double hypotenuse(double x1, double y1, double x2, double y2) {
        double xdif = (x1 - x2);
        double ydif = (y1 - y2);
        double hyp = Math.sqrt(Math.pow(xdif, 2) + Math.pow(ydif, 2));
        return hyp;
    }

    public static double squarehypotenuse(double x1, double y1, double x2, double y2) {
        double xdif = (x1 - x2);
        double ydif = (y1 - y2);
        double hyp = Math.pow(xdif, 2) + Math.pow(ydif, 2);
        return hyp;
    }
    public static double squarehypotenuse(SceneObject a, SceneObject b){
    	double xdif = (a.getX() - b.getX());
    	double ydif = (a.getY() - b.getY());
    	double hyp = Math.pow(xdif,  2) + Math.pow(ydif,  2);
    	return hyp;
    }

    public static double aTan(double y, double x) {
        //for determining the angle between two cartesian co-ordinates
        double temp = 0;
        if (x == 0) {
            if (y < 0) {
                temp = 270;
            } else {
                temp = 90;
            }
        } else {
            temp = (Math.atan(y / x) / (2 * Math.PI) * 360);
        }
        if (x < 0) {
            temp += 180;
        } else {
            if (y < 0) {
                //temp += 360;
            }
        }
        return temp;
    }
    public static double getDirection(double x1, double y1, double x2, double y2){
    	double degrees = MyMath.aTan(-y2 + y1, x2 - x1);
		if (degrees < 0) {
			degrees += 360;
		} else if (degrees > 360) {
			degrees -= 360;
		}
		return degrees;
    }
}
