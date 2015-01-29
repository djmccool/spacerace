package model;

import java.awt.Color;
import java.awt.Graphics;
import tools.*;

/**
 * 
 * @author Dan McKerricher. An abstract class for describing all types of game
 *         objects. Objects of this class will have position (x, y) and basic
 *         collision information. Subclasses will need to implement how
 *         steering, motion, and automatic steering will function.
 * 
 */
public abstract class SceneObject implements QuadTreeObject{
	public enum CollisionType {
		ASTEROID, WEAPON, SHIP, TOKEN, INVISIBLE;
	}

	public enum AIType {
		// STRING ID -- DOUBLE AGGRESSION -- INT PATIENCE --- PREFERRED WEAPON
		AGGRESSIVE("aggressive", 0.9,          100), 
		PEACEFUL("peaceful",     0.1,          100), 
		RECKLESS("reckless",     0.5,          100), 
		JEALOUS("jealous",       0.4,          100,        WeaponType.MISSILE);

		private String id;
		private double aggression;
		private WeaponType preferredWeapon;
		private int patience;
		private AIType(String id, double aggression, int patience) {
			this.id = id;
			this.aggression = aggression;
			this.patience = patience;
		}

		private AIType(String id, double aggression, int patience, WeaponType t) {
			this.id = id;
			this.aggression = aggression;
			preferredWeapon = t;
			this.patience = patience;
		}
		public String toString() {
			return id;
		}
		public int getPatience(){
			return patience;
		}
		public double getAggression(){
			return aggression;
		}
		public static AIType findById(String id) {
			for (int i = 0; i < values().length; i++) {
				if (values()[i].equals(id)) {
					return values()[i];
				}
			}
			return null;
		}
	}

	public enum WeaponType {
		BULLET("bullet", 40), MISSILE("missile", 10), EMP("emp", 30), ROCKET("rocket", 1), ASTEROID(
				"asteroid"), PICKUP("pickup"), RANDOM("random"); // used for
																	// random
																	// pickup
																	// generation
		private String id;
		private int default_ammunition;

		private WeaponType(String id) {
			this.id = id;
			default_ammunition = 0;
		}

		private WeaponType(String id, int default_ammunition) {
			this.id = id;
			this.default_ammunition = default_ammunition;
		}

		public String toString() {
			return id;
		}

		public int getDefaultAmmunition() {
			return default_ammunition;
		}

		public static WeaponType findById(String id) {
			for (int i = 0; i < values().length; i++) {
				if (values()[i].toString().equals(id)) {
					return values()[i];
				}
			}
			return null;
		}
	}

	protected static double[] motion_halflife = new double[] { .99, .95, .93 };

	protected Color colour = new Color(0, 0, 0);
	// location and collision radius for basic detection
	protected double x;
	protected double y;
	protected int collisionradius;
	// protected boolean isAlive = true;
	public boolean drawOnTop = false;
	public boolean drawOffscreen = false;
	public WeaponType weaponType;
	protected WeaponType pickupType;

	// respawning data
	public static int default_respawn_time = 90;
	protected int timeToRespawn;

	// basic motion stats
	protected double force_x;
	protected double force_y;
	protected int spin;
	protected int facedirection;

	protected SteeringObject steering;
	protected SmartObject smart;
	protected SceneObject controller;

	// collision stats
	protected boolean isCollideable = false; // starting value at construction -
												// updated by inheriting classes
	protected boolean isFloating;
	protected double weight = 10; // 10 is unmoveable - starting value at
									// construction
	protected int shieldpoints;
	protected int shieldmax;
	protected int shieldrate;
	protected int hitpoints = 1000; // 1000 is unkillable - starting value at
									// construction
	protected int hitpointsmax = 1000;
	protected int explosivity; // 0 does not produce an explosion. 3 = large
								// explosion
	protected int cooldown = 0;

	// linemaps
	protected static LineMap arrow = new LineMap("LM_arrow");
	protected LineMap map;

	/**
	 * 
	 * @param x
	 * @param y
	 * @param force_x
	 * @param force_y
	 * @param facedirection
	 *            visible rotation of object relative to its direction of
	 *            travel. does not affect direction of travel.
	 * @param collisionradius
	 *            (used for physical collisions and for seeking)
	 * @param colour
	 */
	public SceneObject(double x, double y, double force_x, double force_y,
			int facedirection, int collisionradius, Color colour) {
		this.x = x;
		this.y = y;
		this.force_x = force_x;
		this.force_y = force_y;
		this.facedirection = facedirection;
		this.collisionradius = collisionradius;
		this.colour = colour;
	}

	/**
	 * instantiates a nested steering object this object describes the motion
	 * constraints of the sceneobject
	 * 
	 * @param enginespeed
	 *            : the starting speed of the object
	 * @param enginedirection
	 *            : the starting direction of the object
	 * @param maxspeed
	 *            : constraint
	 * @param acceleration
	 *            : constraint
	 * @param turnspeed
	 *            : constraint
	 */
	protected void createSteeringStats(double enginespeed, int enginedirection,
			double maxspeed, double acceleration, double turnspeed) {
		steering = new SteeringObject(enginespeed, enginedirection, maxspeed,
				acceleration, turnspeed);
	}

	/**
	 * instantiates a nested smart object with no targets or checkpoints
	 * 
	 * @param auto
	 *            : if true the object will attempt to steer towards the nearest
	 *            checkpoint or a suitable target if false the object has no
	 *            targets, no checkpoints, and no determined behaviour aside
	 *            from possible keyboard input
	 * 
	 */
	protected void createSmartStats(boolean auto) {
		smart = new SmartObject(auto);
	}


	/**
	 * instantiates a nested smart object with a specific target ship
	 * 
	 * @param target
	 *            : the target ship which this object will attempt to follow or
	 *            ram
	 * @param auto
	 *            : if true the object will attempt to steer towards the target
	 *            if false the object will keep reference of the target but not
	 *            follow it
	 */
	protected void createSmartStats(Ship target, boolean auto) {
		smart = new SmartObject(target, auto);
	}

	/**
	 * instantiates a nested smart object with a specified checkpoint
	 * 
	 * @param checkpoint
	 *            : the checkpoint which the object will register as
	 *            "most recently passed" (the object should steer to the "next"
	 *            checkpoint after this one)
	 * @param auto
	 *            : if true the object will steer towards the checkpoint if
	 *            false the object will keep reference of the checkpoint but not
	 *            follow it
	 */
	protected void createSmartStats(CheckPoint checkpoint, boolean auto) {
		smart = new SmartObject(checkpoint, auto);
	}

	/**
	 * sets the variables which describe object collision
	 * 
	 * @param weight
	 *            : a number between 1-10 10 = unmoveable 1-9 increasing
	 *            influence in collisions, and slower max speed while floating
	 * @param isFloating
	 *            : if true the object will drift at a speed determined by its
	 *            weight
	 * @param shield
	 *            : starting shield value of object
	 * @param shieldmax
	 *            : constraint on maximum shield value
	 * @param hitpoints
	 *            : starting hit points
	 * @param hitpointsmax
	 *            : constraint on maximum hitpoints
	 * @param explosivity
	 *            : 0 produces no explosion when object is destroyed, 3 produces
	 *            a large explosion
	 */
	protected void createCollisionStats(int weight, boolean isFloating,
			int shield, int shieldmax, int hitpoints, int hitpointsmax,
			int explosivity) {
		isCollideable = true;
		this.isFloating = isFloating;
		this.weight = weight;
		this.shieldpoints = shield;
		this.shieldmax = shieldmax;
		this.hitpoints = hitpoints;
		this.hitpointsmax = hitpointsmax;
		this.explosivity = explosivity;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	/**
	 * sets the score of the object if the object has a smart object else does
	 * nothing
	 * 
	 * @param score
	 */
	public void setScore(int score) {
		if (smart != null) {
			smart.score = score;
		}

	}

	public abstract CollisionType getCollisionID();

	/**
	 * returns the collision radius as an int returns 0 if no collision radius
	 * 
	 * @return
	 */
	public int getCollisionRadius() {
		return collisionradius;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}


	/**
	 * returns the engine direction of the object if the object has a steering
	 * component else returns the facedirection of the object
	 * 
	 * @return
	 */
	/*
	 * public int getDirection() { if (steering == null) { return facedirection;
	 * } return steering.enginedirection; }
	 */
	/**
	 * returns the x direction component of the forces applied to this object
	 * 
	 * @return
	 */
	public double getForceX() {
		return force_x;
	}

	/**
	 * returns the y direction component of the forces applied to this object
	 * 
	 * @return
	 */
	public double getForceY() {
		return force_y;
	}

	/**
	 * returns the colour of this object if it has been set all objects have a
	 * pre-set colour of black
	 * 
	 * @return
	 */
	public Color getColour() {
		return this.colour;
	}

	/**
	 * returns the score of the object if it has a smart component else returns
	 * 0
	 * 
	 * @return
	 */
	public int getScore() {
		return (smart == null) ? 0 : smart.score;
	}

	public int getHitPoints() {
		return hitpoints;
	}

	public int getShieldPoints() {
		return shieldpoints;
	}

	/**
	 * returns true if the object is firing (or spawning any sceneobject)
	 * returns false if the object has no smart component or is not firing
	 * 
	 * The main game loop will use this to add new game objects to the game,
	 * regardless of object type
	 * 
	 * @return
	 */
	public boolean getFiring() {
		if (steering == null) {
			return false;
		}
		return steering.isFiring & cooldown == 0 & timeToRespawn < 1;
	}

	public int getDirection() {
		if (steering == null) {
			return facedirection;
		} else {
			return steering.enginedirection;
		}
	}

	public WeaponType getWeapon() {
		return weaponType;
	}

	public WeaponType getPickupType() {
		return pickupType;
	}

	/**
	 * returns an int value corresponding to the current ammunition by default
	 * returns 1: extending classes should override
	 * 
	 * @return
	 */
	public int getAmmo() {
		return 1;
	}

	public boolean isRespawning() {
		return timeToRespawn > 0;
	}

	public int getTimeToRespawn() {
		return timeToRespawn;
	}

	/**
	 * currently unimplemented method
	 */
	public void useAmmo() {

	}

	/**
	 * increments the check point if this object has a smart component otherwise
	 * does nothing
	 */
	public void advanceCheckPoint() {
		if (smart != null) {
			smart.advanceCheckPoint();
		}
	}

	/**
	 * returns the current checkpoint of this object (usually the checkpoint
	 * which it has most recently passed) returns null if there is no checkpoint
	 * 
	 * @return
	 */
	public CheckPoint getCheckPoint() {
		if (smart != null) {
			return smart.currentcheckpoint;
		} else {
			return null;
		}
	}

	/**
	 * sets the checkpoint of the object. also sets the target of the object to
	 * the checkpoint.
	 * 
	 * @param checkpoint
	 */
	public void setCheckPoint(CheckPoint checkpoint) {
		if (smart != null) {
			smart.setCheckPoint(checkpoint);
		}
	}

	public void resetAtCheckPoint() {
		timeToRespawn = default_respawn_time;
		if (smart != null) {
			smart.resetAtCheckPoint();
		}
		force_x = 0;
		force_y = 0;
	}

	/**
	 * adds damage to the object. objects which have 1000 hitpoints will not be
	 * damaged. "1000" hitpoints is "unkillable"
	 * 
	 * @param dmg
	 *            : the amount of damage to be done. if negative will give the
	 *            object health (constrained by maxhp)
	 */
	public void addDamage(int dmg) {
		if (dmg < 0) {
			if (shieldpoints - dmg < shieldmax) {
				shieldpoints -= dmg;
			} else {
				hitpoints -= dmg - (shieldmax - shieldpoints);
				shieldpoints = shieldmax;
			}
			if (hitpoints > hitpointsmax) {
				hitpoints = hitpointsmax;
			}
		}
		if (hitpoints < 1000) {
			if (shieldpoints > dmg) {
				shieldpoints -= dmg;
			} else {
				hitpoints -= (dmg - shieldpoints);
				shieldpoints = 0;
			}
		} else {

		}

	}

	/**
	 * attempts to add a force to an object using polar vector this method will
	 * do nothing if the object is not collideable or if it has a weight of 1000
	 * 
	 * @param direction
	 * @param magnitude
	 */
	public void addForce(double direction, double magnitude) {
		if (isCollideable) {
			if (weight < 10) {
				magnitude /= (weight / 3);
				// System.out.println("adding force to asteroid, weight " +
				// weight + ", magnitude " + magnitude +
				// ". (sceneobject.addforce)");
				force_x = force_x - (MyMath.Cos(direction) * magnitude);
				force_y = force_y - (MyMath.Sin(direction) * magnitude);
			} else {
				// do nothing. 10 is too heavy to move
			} // this shouldn't happen.
		} else {
			// if it's not collidable this.addForce() should not be called.
			// debug
			System.out.println("A(n) " + this.getClass().getName()
					+ " was given a force and is uncollideable");
		}
	}

	/**
	 * attempts to add a force to an object using cartesian vector this method
	 * will do nothing if the object is not collideable or if it has a weight of
	 * 1000
	 * 
	 * @param force_x
	 * @param force_y
	 */
	public void addForceComponents(double force_x, double force_y) {
		if (isCollideable) {
			if (weight < 10) {
				this.force_x += force_x;
				this.force_y += force_y;
			}
		}
	}

	/**
	 * method for rebounding the object off a wall the object will be brought
	 * back into the game screen and its appropriate direction of travel will be
	 * reversed
	 * 
	 * @param nsew
	 *            : string corresponding to which wall was struck "N" = North
	 *            "S" = South "E" = East "W" = West
	 * @param width
	 *            , height: the dimensions of the level
	 */
	public void rebound(String nsew, int width, int height) {
		// designed for rebounding off walls
		// reverses forces and engine direction in appropriate direction if
		// necessary
		double engine_x = 0;
		double engine_y = 0;
		if (steering != null) {
			engine_x = MyMath.Cos(facedirection);
			engine_y = -MyMath.Sin(facedirection);
		}
		if (nsew.equalsIgnoreCase("N")) {
			y = (height / 2) - collisionradius;
			if (force_y > 0) {
				force_y *= -1;
			}
			if (engine_y > 0) {
				engine_y *= -1;
			}
		} else if (nsew.equalsIgnoreCase("S")) {
			y = -(height / 2) + collisionradius;
			if (force_y < 0) {
				force_y *= -1;
			}
			if (engine_y < 0) {
				engine_y *= -1;
			}
		} else if (nsew.equalsIgnoreCase("E")) {
			x = (width / 2) - collisionradius;
			if (force_x > 0) {
				force_x *= -1;
			}
			if (engine_x > 0) {
				engine_x *= -1;
			}
		} else if (nsew.equalsIgnoreCase("W")) {
			x = -(width / 2) + collisionradius;
			if (force_x < 0) {
				force_x *= -1;
			}
			if (engine_x < 0) {
				engine_x *= -1;
			}
		}

		if (steering != null) {
			facedirection = (int) MyMath.aTan(-engine_y, engine_x);
		}
	}

	/**
	 * attempts to set the target of the object if the object has no smart
	 * component, no effect
	 * 
	 * @param target
	 */
	public void setTarget(SceneObject target) {
		if (smart != null) {
			smart.target = target;
		}
	}

	/**
	 * returns the current target of the object if the object has no smart
	 * component, returns InvalidSmartCommand
	 * 
	 * @return
	 */
	public SceneObject getTarget() {
		if (smart == null) {
			return null;
		}
		return smart.target;
	}

	/**
	 * attempts to clear the target of the object. no effect if the object has
	 * no target or smart component.
	 */
	public void clearTarget() {
		if (smart != null) {
			smart.target = null;
			setTurningLeft(false);
			setTurningRight(false);
			setAccelerating(true);
			if (smart.currentcheckpoint != null) {
				smart.target = smart.currentcheckpoint.getNext();
			}
		}
	}

	/**
	 * sets whether or not the object is turning left
	 * 
	 * @param isTurningLeft
	 *            : if true, sets turningright to false and turning left to true
	 *            if false, sets turningleft to false
	 */
	public void setTurningLeft(boolean isTurningLeft) {
		if (steering == null) {
			return; // do nothing
		}
		steering.isTurningLeft = isTurningLeft;
		if (isTurningLeft) {
			steering.isTurningRight = false;
		}

	}

	/**
	 * sets whether or not the object is turning right
	 * 
	 * @param isTurningRight
	 *            : if true, sets turningleft to false and turningright to true
	 *            if false, sets turningright to false
	 */
	public void setTurningRight(boolean isTurningRight) {
		if (steering == null) {
			return; // do nothing
		}
		steering.isTurningRight = isTurningRight;
		if (isTurningRight) {
			steering.isTurningLeft = false;
		}
	}

	/**
	 * sets whether or not the object is braking
	 * 
	 * @param isBraking
	 */
	public void setBraking(boolean isBraking) {
		if (steering == null) {
			return; // do nothing
		}
		steering.isBraking = isBraking;
		if (isBraking) {
			steering.isAccelerating = false;
		}
	}

	public void setAccelerating(boolean isAccelerating) {
		if (steering == null) {
			return; // do nothing
		}
		steering.isAccelerating = isAccelerating;
		if (isAccelerating) {
			steering.isDecelerating = false;
		}
	}

	public void setDecelerating(boolean isDecelerating) {
		if (steering == null) {
			return; // do nothing
		}
		steering.isDecelerating = isDecelerating;
		if (isDecelerating) {
			steering.isAccelerating = false;
		}
	}

	public void setFiring(boolean isFiring) {
		if (steering == null) {
			return; // do nothing
		}
		steering.isFiring = isFiring;
	}

	public void toggleAuto() {
		if (smart != null) {
			smart.isAuto = !smart.isAuto;
			setFiring(false);
		}
	}
	public boolean isAuto(){
		if(smart== null){
			return false;
		}
		return smart.isAuto;
	}

	public abstract void update();

	/**
	 * allows a floating object to rotate. this will not affect the direction of
	 * travel of the object
	 */
	public void updateSpin() {
		facedirection += spin;
		if (facedirection < 0) {
			facedirection += 360;
		} else if (facedirection > 360) {
			facedirection -= 360;
		}
	}

	/**
	 * moves the object according to its direction of travel and forces also
	 * applies a half-life and maximum constraint on forces
	 */
	public void updateMotion() {
		if (isCollideable) {
			double maxFloatSpeed = 6 - weight;
			if (weight > 6) {
				maxFloatSpeed = 0;
			}
			if (isFloating == false) {
				maxFloatSpeed = 0;
			}
			double restrainCoefficient;
			restrainCoefficient = Math.pow(force_x, 2) + Math.pow(force_y, 2);
			restrainCoefficient /= Math.pow(maxFloatSpeed, 2);
			if (restrainCoefficient > 1) {
				// System.out.println("restraining: x = " + force_x + ", y = " +
				// force_y + "; maxFloatSpeed = " + maxFloatSpeed);
				switch ((int) maxFloatSpeed) {

				}
				force_x *= 0.95;
				force_y *= 0.95;

				// System.out.println("---> x = " + force_x + ", y = " + force_y
				// + ". (sceneobject.updateMotion)");
			}
		}
		x += force_x;
		y += force_y;
	}

	/**
	 * attempts to update the AI component of the object this will set the
	 * controls of the object to allow it to aim to nearest target If object has
	 * no target or is not on auto (human player), this will have limited effect
	 * See: SmartObject.updateAI()
	 * 
	 * NOTE: the object will not steer until updateSteering() has been called
	 */
	public void updateAI() {
		if (steering == null) {
			return;
		}
		if (smart == null) {
			return;
		}
		smart.updateAI();

	}

	/**
	 * steers the object according to its controls (ie, boolean isSteeringLeft,
	 * isTurningRight, etc)
	 */
	public void updateSteering() {
		steering.updateSteering();
	}

	/**
	 * draws the linemap of this object
	 * 
	 * @param g
	 * @param xpov
	 * @param ypov
	 * @param drawradius
	 * @param drawarrow
	 */
	public void drawLineMap(Graphics g, int xpov, int ypov, boolean drawradius,
			boolean drawarrow) {
		
		if (map != null) {
			int xcenter = (int) g.getClipBounds().getWidth() / 2;
			int ycenter = (int) g.getClipBounds().getHeight() / 2;
			g.setColor(colour);
			map.draw(facedirection, (int) (x - xpov) + xcenter,
					(int) (y - ypov) + ycenter, g);

			if (drawarrow && smart != null) {
				if (smart.currentcheckpoint != null) {
					arrow.draw((int) MyMath.aTan(-smart.currentcheckpoint
							.getNext().getY() + y, smart.currentcheckpoint
							.getNext().getX() - x), (int) x - xpov + xcenter,
							(int) y - ypov + ycenter, g);
				} else {
					System.out
							.println("A(n) "
									+ this.getClass().getName()
									+ " was asked to draw an arrow when it has no target");
				}
			}
			if (drawradius) {
				g.drawOval((int) (x - xpov) + xcenter - collisionradius,
						(int) (y - ypov) + ycenter - collisionradius,
						collisionradius * 2, collisionradius * 2);
			}
		} else {
			// debug message for objects with null LineMap
			System.out
					.println("a(n) "
							+ this.getClass().getName()
							+ " was asked to draw a LineMap when its LineMap object is set to null");
		}
	}

	public void drawCircle(Graphics g, int xpov, int ypov, int radius,
			boolean fill) {
		int xcenter = (int) g.getClipBounds().getWidth() / 2;
		int ycenter = (int) g.getClipBounds().getHeight() / 2;
		if (fill) {
			g.fillOval((int) (x - xpov) + xcenter - radius, (int) (y - ypov)
					+ ycenter - radius, radius * 2, radius * 2);
		} else {
			g.drawOval((int) (x - xpov) + xcenter - radius, (int) (y - ypov)
					+ ycenter - radius, radius * 2, radius * 2);
		}
	}

	public void drawLine(Graphics g, int xpov, int ypov, SceneObject target) {
		int xcenter = (int) g.getClipBounds().getWidth() / 2;
		int ycenter = (int) g.getClipBounds().getHeight() / 2;
		g.drawLine((int) (x - xpov + xcenter), (int) (y - ypov) + ycenter,
				(int) (target.getX() - xpov) + xcenter,
				(int) (target.getY() - ypov) + ycenter);
	}

	public abstract void draw(Graphics g, int xpov, int ypov);

	protected class SteeringObject {
		// steering stats
		protected double enginespeed;
		protected int enginedirection;
		protected double maxspeed;
		protected double acceleration;
		protected double turnspeed;

		// steering controls
		protected boolean isAccelerating;
		protected boolean isDecelerating;
		protected boolean isBraking;
		protected boolean isTurningLeft;
		protected boolean isTurningRight;
		protected boolean isFiring = false;

		public SteeringObject(double enginespeed, int enginedirection,
				double maxspeed, double acceleration, double turnspeed) {
			this.enginespeed = enginespeed;
			this.enginedirection = enginedirection;
			this.maxspeed = maxspeed;
			this.acceleration = acceleration;
			this.turnspeed = turnspeed;
		}

		public void updateSteering() {

			if (isTurningLeft) {
				facedirection += turnspeed;
			}
			if (isTurningRight) {
				facedirection -= turnspeed;
			}
			if (isAccelerating) {
				enginespeed += acceleration;
				if (enginespeed > maxspeed) {
					enginespeed = maxspeed;

				}
			}
			if (isDecelerating) {
				enginespeed -= acceleration;
				if (enginespeed < -maxspeed) {
					enginespeed = -maxspeed;
				}
			}
			if (isBraking) {
				enginespeed = 0;
			}
			if (facedirection < 0) {
				facedirection += 360;
			} else if (facedirection > 360) {
				facedirection -= 360;
			}
			if (timeToRespawn > 0) {
				enginespeed = 0;
			}
			enginedirection = facedirection;
			double delta_x = MyMath.Cos(facedirection) * enginespeed;
			x += delta_x;
			y -= MyMath.Sin(facedirection) * enginespeed;
			if (isFiring && cooldown == 0) {
				if (weaponType != null) {
					switch (weaponType) {
					case BULLET:
						cooldown = 3;
						break;
					case MISSILE:
						cooldown = 5;
						break;
					}
				}
			}
			cooldown--;
			if (cooldown < 1) {
				cooldown = 0;
			}
		}
	}

	protected class SmartObject {
		public final AIType default_AIType = AIType.AGGRESSIVE;
		protected boolean isAuto;
		protected SceneObject target;
		protected CheckPoint currentcheckpoint;
		protected int score;
		protected AIType type;
		protected int patience;

		protected SceneObject getTarget(){
			return target;
		}
		protected SmartObject(boolean auto) {
			isAuto = auto;
			score = 0;
			type = default_AIType;
			patience = type.getPatience();
		}

		protected SmartObject(Ship target, boolean auto) {
			isAuto = auto;
			score = 0;
			this.target = target;
			type = default_AIType;
			patience = type.getPatience();
		}

		protected SmartObject(CheckPoint currentcheckpoint, boolean auto) {
			isAuto = auto;
			score = 0;
			this.currentcheckpoint = currentcheckpoint;
			type = default_AIType;
			patience = type.getPatience();
		}

		/**
		 * updates the next checkpoint of the object. also sets the target of
		 * the object to this checkpoint.
		 */
		protected void advanceCheckPoint() {
			if (currentcheckpoint != null) {
				currentcheckpoint = currentcheckpoint.getNext();
				target = currentcheckpoint.getNext();
				PickupGenerator g = currentcheckpoint.getNearestGenerator();
				if(g != null){
					target = g.findSuitablePickup(type.preferredWeapon);
				}
			}
			if(target == null){
				target = currentcheckpoint.getNext();
			}
		}

		protected void toggleAuto(boolean auto) {
			isAuto = auto;
		}

		protected void setCheckPoint(CheckPoint currentcheckpoint) {
			this.currentcheckpoint = currentcheckpoint;
		}

		protected void resetAtCheckPoint() {
			if (currentcheckpoint != null) {
				int xrand = MyMath.nextInt(-40, 40);
				int yrand = MyMath.nextInt(-40, 40);
				x = currentcheckpoint.getX() + xrand;
				y = currentcheckpoint.getY() + yrand;
				hitpoints = hitpointsmax;
			}
		}

		protected void updateAI() {

			if (smart.target == null || (!isAuto && timeToRespawn < 1)) {
				// do nothing

			} else {
				setAccelerating(true);
				steering.isTurningRight = false;
				steering.isTurningLeft = false;
				int x1 = (int) x;
				int y1 = (int) y;
				int x2 = (int) target.getX();
				int y2 = (int) target.getY();
				boolean isClose = (MyMath.squarehypotenuse(x1, y1, x2, y2) < 1000);
				double degrees = MyMath.getDirection(x1, y1, x2, y2);
				double difference = degrees - steering.enginedirection;
				if (difference > 180 && difference < 360 - steering.turnspeed) {
					steering.isTurningRight = true;
					if (isClose) {
						setDecelerating(true);
					}
				} else if (difference <= 180 && difference > steering.turnspeed) {
					steering.isTurningLeft = true;
				} else if (difference < -steering.turnspeed
						&& difference > -180) {
					steering.isTurningRight = true;
				} else if (difference < -180
						&& difference > -360 + steering.turnspeed) {
					steering.isTurningLeft = true;
					if (isClose) {
						setDecelerating(true);
					}
				}
			}
		}
	}

	public SceneObject getController() {
		return null;
	}

	public int getScanRadius() {
		return 0;
	}

	public void addPickup(Pickup p) {

	}

	public void removePickup(Pickup p) {

	}

	public Pickup findSuitablePickup(WeaponType t) {
		return null;
	}
}
