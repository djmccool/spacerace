package model;

import java.awt.Color;
import java.awt.Graphics;

import tools.MyMath;


public class Debris extends SceneObject{

	public static int default_debris_hitpoints = 9;
	public Debris(double x, double y, double force_x, double force_y,
			int facedirection, int collisionradius, Color colour) {
		super(x, y, force_x, force_y, facedirection, collisionradius, colour);
		// TODO Auto-generated constructor stub
	}
	public Debris(SceneObject controller, int speed, double scatter, boolean wing){
		super(controller.getX(), controller.getY(), 0, 0, 0, 5, controller.getColour());
		spin = MyMath.nextInt(-3, 3);
		createCollisionStats(1, true, 0, 0, default_debris_hitpoints, default_debris_hitpoints, 0);
		force_x = MyMath.Cos(controller.getDirection() + scatter) * speed;
		force_y = MyMath.Sin(controller.getDirection() + scatter) * speed;
		if(wing){
			map = Ship.broken_wing;
		}else{
			map = Ship.broken_ship;
		}
	}

	@Override
	public CollisionType getCollisionID() {
		return SceneObject.CollisionType.ASTEROID;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		this.updateSpin();
		this.updateMotion();
		
	}

	@Override
	public void draw(Graphics g, int xpov, int ypov) {
		this.drawLineMap(g, xpov, ypov, false, false);
		
	}

}
