package model;

import java.awt.*;

import model.SceneObject.WeaponType;
import tools.*;
import tools.QuadTreeObject;//import java.util.*;
public class Asteroid extends SceneObject {

	public Asteroid(double x, double y, int size)  {
		
		super(x, y, 0, 0, 0, 10, new Color(170, 50, 0));
		spin = MyMath.nextInt(1, 7);
		// determine the correct design file and behaviour for size of asteroid
		int w;
		if (size > 1) {
			if (size > 2) {
				w = 3;
				collisionradius = 20;
			} else {
				w = 2;
				collisionradius = 15;
			}
		} else {
			w = 1;
			collisionradius = 10;
		}
		createCollisionStats(w, true, 0, 0, 1000, 1000, 0);
	}

	public Asteroid(SceneObject controller, int speed, double scatter){
        super(controller.getX(), controller.getY(), 0, 0, 0, 10, new Color(170, 50, 0));
        spin = MyMath.nextInt(1, 7);
        int size = MyMath.nextInt(1, 3);
		// determine the correct design file and behaviour for size of asteroid
		int w;
		if (size > 1) {
			if (size > 2) {
				w = 3;
				collisionradius = 20;
			} else {
				w = 2;
				collisionradius = 15;
			}
		} else {
			w = 1;

			collisionradius = 10;
		}
		createCollisionStats(w, true, 0, 0, 1000, 1000, 0);
		force_x = MyMath.Cos(controller.getDirection() + scatter) * speed;
		force_y = MyMath.Sin(controller.getDirection() + scatter) * speed;
    }
	
	public void update() { // abstract.super
		updateMotion();
	}

	public void draw(Graphics g, int xpov, int ypov) {
		g.setColor(colour);
		this.drawCircle(g, xpov, ypov, getCollisionRadius(), true);
	}

	@Override
	public CollisionType getCollisionID() {
		// TODO Auto-generated method stub
		return CollisionType.ASTEROID;
	}

	@Override
	public SceneObject getController() {
		// TODO Auto-generated method stub
		return null;
	}




}
