package model;

import java.awt.*;

import tools.*;

public class Ship extends SceneObject {
	protected static LineMap phoenix = new LineMap("LM_phoenix");		
	protected static LineMap broken_ship = new LineMap("LM_broken_ship");
	protected static LineMap broken_wing = new LineMap("LM_broken_wing");
	protected Color injuredColour;
	protected int ammunitionRemaining;

	public Ship(double x, double y, int hitpointsmax, int shieldmax,
			Color colour, boolean isAuto) {

		super(x, y, 0, 0, 0, 10, colour);
		createCollisionStats(4, false, shieldmax, shieldmax, hitpointsmax,
				hitpointsmax, 1);
		createSteeringStats(0, 0, 5, 0.5, 4);
		this.colour = colour;
		map = phoenix;
		createSmartStats(isAuto);
		injuredColour = colour.darker();
		timeToRespawn = default_respawn_time;
		drawOnTop = true;
	}

	public boolean hasWon(int pointsNeeded) {
		boolean hasWon = false;
		if (getScore() >= pointsNeeded) {
			hasWon = true;
		}
		return hasWon;
	}

	
	public void addDamage(int dmg) {
		super.addDamage(dmg);
	}

	public void fullShield() {
		shieldpoints = shieldmax;
	}

	public void setWeapon(WeaponType type) {
		weaponType = type;
		ammunitionRemaining = type.getDefaultAmmunition();
	}

	public int getAmmo() {
		return ammunitionRemaining;
	}

	public void exhaustAmmo() {
		ammunitionRemaining = 0;
	}

	public void useAmmo() {
		ammunitionRemaining -= 1;
	}

	public void update() {
		updateAI();
		updateSteering();
		updateMotion();
		if(timeToRespawn > 0){
			timeToRespawn--;
			steering.isFiring = false;
		}else{
			 
		}
	}

	public void draw(Graphics g, int xpov, int ypov) {
		g.setColor(Color.black);
		this.drawCircle(g, xpov, ypov, getCollisionRadius(), true);
		int rnd = MyMath.nextInt(0, this.hitpointsmax);
		if (rnd > this.hitpoints) {
			g.setColor(this.injuredColour);
		} else {
			g.setColor(this.colour);
		}
		this.drawLineMap(g, xpov, ypov, false, true);
		if (this.shieldpoints > 0) {
			rnd = MyMath.nextInt(0, this.shieldmax);
			if (rnd > this.shieldpoints) {
				g.setColor(this.injuredColour);
			} else {
				g.setColor(this.colour);
			}
			this.drawCircle(g, xpov, ypov, getCollisionRadius(), false);
		}
	}

	@Override
	public CollisionType getCollisionID() {
		return CollisionType.SHIP;
	}
}