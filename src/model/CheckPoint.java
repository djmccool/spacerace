package model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class CheckPoint extends SceneObject {
	protected CheckPoint next;
	
	protected LinkedList<NearbyGenerator> nearbyGenerators = new LinkedList<NearbyGenerator>();

	public CheckPoint(double x, double y, CheckPoint next) {
		super(x, y, 0, 0, 0, 30, Color.RED);
		this.next = next;
		
		// this.target = next;
	}

	public CheckPoint(double x, double y) {
		super(x, y, 0, 0, 0, 30, Color.RED);
		next = null;
		drawOffscreen = true;
	}

	public CheckPoint getNext() {
		return next;
		
	}

	public void setNext(CheckPoint next) {
		if (this.next == null) {
			this.next = next;
			// this.target = next;
		}
	}

	public void update() {
		// do nothing!
	}

	public void draw(Graphics g, int xpov, int ypov) {
		g.setColor(Color.BLACK);
		for(NearbyGenerator gen: nearbyGenerators){
			this.drawLine(g, xpov, ypov, gen.pickup);
		}
		g.setColor(colour);
		this.drawLine(g, xpov, ypov, next);
		this.drawCircle(g, xpov, ypov, getCollisionRadius(), false);
		g.setColor(Color.BLACK);
		this.drawCircle(g, xpov, ypov, getCollisionRadius() - 1, true);
		
	}

	@Override
	public CollisionType getCollisionID() {
		return CollisionType.TOKEN;
	}

	/**
	 * adds a pickup generator and its distance ratio to a list maintained by the
	 * checkpoint object.
	 * 
	 * @param p
	 *            : the generator
	 * @param d
	 *            : a measure of distance from the checkpoint and the next
	 *            checkpoint in the race where d = 2norm(checkpoint - generator)
	 *            + 2norm(checkpoint2 - generator) / 2norm(checkpoint -
	 *            checkpoint)
	 */
	public void registerPickupGenerator(PickupGenerator p, double d) {
		nearbyGenerators.add(new NearbyGenerator(p, d));
	}

	public void sortPickupGenerators() {
		Collections.sort(nearbyGenerators);
	}

	public void removeAllGenerators() {
		nearbyGenerators.clear();
	}

	public PickupGenerator getNearestGenerator(){
		if(nearbyGenerators == null){
			return null;
		}
		try{
			if(nearbyGenerators.getFirst() == null){
				return null;
			}
			if(nearbyGenerators.getFirst().getGenerator() == null){
				return null;
			}
			return nearbyGenerators.getFirst().getGenerator();
		}catch(NoSuchElementException e){
			return null;
		}
	}
	public void removeGenerator(SceneObject p) {
		NearbyGenerator generatorToRemove = null;
		for (NearbyGenerator g : nearbyGenerators) {
			if (g.pickup == p) {
				generatorToRemove = g;
			}
		}
		if (generatorToRemove != null) {
			nearbyGenerators.remove(generatorToRemove);
		}
	}

	private class NearbyGenerator implements Comparable<NearbyGenerator> {
		public PickupGenerator pickup;
		public double distanceRatio;
		public NearbyGenerator(PickupGenerator p, double d) {
			pickup = p;
			distanceRatio = d;
		}
		public PickupGenerator getGenerator(){
			return pickup;
		}
		@Override
		public int compareTo(NearbyGenerator g) {
			if (distanceRatio > g.distanceRatio) {
				return -1;
			}
			if (distanceRatio < g.distanceRatio) {
				return 1;
			}
			return 0;
		}
	}

}
