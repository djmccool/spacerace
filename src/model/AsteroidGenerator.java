package model;

import java.awt.Color;
import java.awt.Graphics;

import level.Level;

import tools.MyMath;


public class AsteroidGenerator extends SceneObject {
	protected static int default_cooldown_max = 30;
	protected int cooldown_max;
	protected int cooldown;
	public AsteroidGenerator(double x, double y, double force_x,
			double force_y, int facedirection, int collisionradius, Color colour) {
		super(x, y, force_x, force_y, facedirection, collisionradius, colour);
		cooldown = MyMath.nextInt(1, default_cooldown_max);
	}
	public AsteroidGenerator(double x, double y, int facedirection){
		super(x, y, 0, 0, facedirection, 0, Color.BLACK);
		cooldown_max = default_cooldown_max;
		cooldown = MyMath.nextInt(1, cooldown_max);
	}

	public AsteroidGenerator(double x, double y, int facedirection, int cooldown_max){
		super(x, y, 0, 0, facedirection, 0, Color.BLACK);
		this.cooldown_max = cooldown_max;
		cooldown = MyMath.nextInt(1, cooldown_max);
	}
	@Override
	public CollisionType getCollisionID() {
		// TODO Auto-generated method stub
		return CollisionType.TOKEN;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if(cooldown == 0){
			cooldown = cooldown_max;
		}
		cooldown --;
	}

	@Override
	public void draw(Graphics g, int xpov, int ypov) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean getFiring(){
		return (cooldown == 0);
	}
	
	@Override
	public WeaponType getWeapon(){
		return weaponType.ASTEROID;
	}
	public int getAmmo(){
		return Level.asteroids_in_queue;
	}
}
