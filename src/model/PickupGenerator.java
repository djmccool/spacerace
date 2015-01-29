package model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;

import tools.MyMath;


public class PickupGenerator extends SceneObject {
	public static SpawnRuleType default_spawn_rule = SpawnRuleType.TOP_SCORE;
	public static int default_points_needed = 6;
	public static int default_pickups_per_activation = 4;

	public enum SpawnRuleType {
		TOP_SCORE("top"), TOTAL_SCORE("total"), ON_CHECKPOINT_REACHED("checkpoint"), BOTTOM_SCORE("bottom");
		private String id;
		private SpawnRuleType(String id){
			this.id = id;
		}
		public String toString(){
			return id;
		}
		public static SpawnRuleType findById(String id){
			for(int i = 0; i < values().length; i++){
				if(values()[i].toString().equals(id)){
					return values()[i];
				}
			}
			return null;
		}
	}

	private int points_needed; // the interval of points which will trigger the
								// generator
	private int ammunitionRemaining = 0; // the exact number of pickups still to
											// be fired
	private int pickups_per_activation;
	private SpawnRuleType spawnRuleType;
	LinkedList<Pickup> pickups = new LinkedList<Pickup>();

	/**
	 * creates a generator with specific behaviour
	 * @param t: the type of pickup to be generated. optionally leave this null
	 * for random pickups.
	 * @param s: the type of score event which will trigger the release of pickups.
	 * @param pointsneeded: the interval of points which will trigger the release of pickups
	 * @param pickups_per_activation: the number of pickups released by each trigger event
	 */
	public PickupGenerator(double x, double y, WeaponType t, SpawnRuleType s,
			int pointsneeded, int pickups_per_activation) {
		super(x, y, 0, 0, 0, 25, Color.blue);
		points_needed = pointsneeded;
		pickupType = t;
		spawnRuleType = s;
		this.pickups_per_activation = pickups_per_activation;
	}

	/**
	 * creates a generator with default behaviour
	 * @param x
	 * @param y
	 * @param t
	 */
	public PickupGenerator(double x, double y, WeaponType t) {
		super(x, y, 0, 0, 0, 25, Color.blue);
		points_needed = default_points_needed;
		pickupType = t;
		spawnRuleType = default_spawn_rule;
		pickups_per_activation = default_pickups_per_activation;
	}

	/**
	 * creates a generator with default behaviour and random pickup type
	 * @param x
	 * @param y
	 */
	public PickupGenerator(double x, double y) {
		super(x, y, 0, 0, 0, 25, Color.blue);
		pickupType = WeaponType.RANDOM;
		points_needed = default_points_needed;
		spawnRuleType = default_spawn_rule;
		pickups_per_activation = default_pickups_per_activation;
	}

	@Override
	public CollisionType getCollisionID() {
		return SceneObject.CollisionType.INVISIBLE;
	}

	@Override
	public void update() {

	}

	@Override
	public WeaponType getWeapon() {
		return SceneObject.WeaponType.PICKUP;
	}

	@Override
	public void useAmmo() {
		ammunitionRemaining--;
	}

	@Override
	public int getAmmo() {
		return ammunitionRemaining;
	}

	@Override
	public void draw(Graphics g, int xpov, int ypov) {
		g.setColor(colour);
		this.drawCircle(g, xpov, ypov, getCollisionRadius(), false);
		g.setColor(Color.BLACK);
		this.drawCircle(g, xpov, ypov, getCollisionRadius() - 1, true);
	}

	@Override
	public boolean getFiring() {
		return true;
	}

	/**
	 * increases the amount of pickups in the activation queue for this generator.
	 * this will almost instantly release all of the pickups.
	 */
	public void trigger() {
		ammunitionRemaining += pickups_per_activation;
	}

	/**
	 * returns the type of rule which determines the behaviour of the generator
	 * @return
	 */
	public SpawnRuleType getType() {
		return spawnRuleType;
	}

	/**
	 * returns true if the score argument is a multiple of the score needed to
	 * trigger the release of pickup items
	 * 
	 * @param score
	 * @return
	 */
	public boolean submitScoreChange(int score) {
		if(points_needed == 0){
			return true;
		}
		if (score % points_needed == 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public void addPickup(Pickup p){
		pickups.add(p);
	}
	@Override
	public void removePickup(Pickup p){
		pickups.remove(p);
	}
	
	@Override
	public Pickup findSuitablePickup(WeaponType t){
		if(MyMath.nextInt(0, 1) ==1){
			Iterator<Pickup> i = pickups.descendingIterator();
			while(i.hasNext()){
				Pickup p = i.next();
				if(p.getWeapon() == t || t== null){
					return p;
				}
			}
		}else{
			for(Pickup p : pickups){
				if(p.getWeapon() == t || t== null){
					return p;
				}
			}
		}
		
		return null;
	}
}
