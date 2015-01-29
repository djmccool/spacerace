package level;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tools.QuadTreeNode;

import java.util.List;

import model.*;
import model.SceneObject.WeaponType;
import tools.*;
import java.util.*;

public abstract class Level extends JPanel {

	public static final int default_window_width = 800;
	public static final int default_window_height = 800; // dimensions for
															// window
	protected QuadTreeNode[] QT_nodes = new QuadTreeNode[30];
	protected int QT_depth;
	public static int default_QT_depth = 3;
	public static int default_level_width = 1500;
	public static int default_level_height = 1500;
	private int level_width;
	private int level_height;

	public static final int default_number_of_asteroids = 40;
	public static final int default_warmup_number_of_asteroids = 15;
	public static int asteroids_in_queue = 0;
	private boolean asteroids_bounce;

	public static final int default_number_of_pickups = 0;
	public static final int default_warmup_number_of_pickups = 0;

	public final int default_number_of_computers = 6;
	public static final int default_score_needed = 15;
	protected int score_needed;
	protected int topscore = 0;
	protected int bottomscore = 0;
	protected int totalscore = 0;

	// object lists for updateall() and collision_check()
	protected List<Ship> ships = new LinkedList<Ship>();
	protected ConcurrentArray<SceneObject> bullets = new ConcurrentArray<SceneObject>();
	protected ConcurrentArray<SceneObject> missiles = new ConcurrentArray<SceneObject>();
	protected ConcurrentArray<SceneObject> rockets = new ConcurrentArray<SceneObject>();
	protected ConcurrentArray<SceneObject> asteroids = new ConcurrentArray<SceneObject>();
	protected ConcurrentArray<AsteroidGenerator> asteroidGenerators = new ConcurrentArray<AsteroidGenerator>();
	protected ConcurrentArray<Pickup> pickups = new ConcurrentArray<Pickup>();
	protected LinkedList<SceneObject> topLayer = new LinkedList<SceneObject>();
	protected ConcurrentArray<PickupGenerator> pickupGenerators = new ConcurrentArray<PickupGenerator>();

	public Ship myShip;
	public SceneObject camera;
	protected int camera_x;
	protected int camera_y;
	private Image offscreen;
	private Graphics BufferGraphics; // double-buffering

	public boolean isWon = false;
	public boolean isOver = false;
	public boolean started = false;

	public Ship winner;

	/**
	 * creates a level using specified parameters. Randomly spawns pickups and
	 * asteroids on the map. Does not spawn the asteroid or pickup generators
	 * 
	 * @param width
	 * @param height
	 * @param number_of_asteroids
	 * @param maximum_number_of_asteroids
	 * @param number_of_pickups
	 *            :
	 * @param score_needed
	 *            : the score needed by a single ship to win
	 * @param bouncy
	 *            : whether the walls reflect asteroids
	 */
	public Level(int width, int height, int number_of_asteroids,
			int maximum_number_of_asteroids, int number_of_pickups,
			int score_needed, boolean bouncy) {
		level_width = width;
		level_height = height;
		this.score_needed = score_needed;
		asteroids_bounce = bouncy;
		spawn_pickups(number_of_pickups);
		spawn_asteroids(number_of_asteroids);
		asteroids_in_queue = maximum_number_of_asteroids - number_of_asteroids;
		camera = myShip;
		createTree(default_QT_depth);
	}
	public void createTree(int depth){
		this.QT_depth = depth;
		int numberOfNodes = 0;
		for(int i = 0; i <= depth; i++){
			numberOfNodes += (int)Math.pow(4, i);
		}
		//System.out.println("Level Width = " + level_width);
		//System.out.println("Level Height = " + level_height);
		QT_nodes = new QuadTreeNode[numberOfNodes];
		int currentDepth = 0;
		int nextLevel = 0;
		double left = 0;
		double right = 0;
		double top = 0;
		double bottom = 0;
		int p;
		for(int i = 0; i < QT_nodes.length; i++){
			if(i > nextLevel){
				currentDepth++;
				nextLevel += Math.pow(4, currentDepth);
			}
			if(i == 0){
				//System.out.println("is i ever equal to zero???");
				left = level_width/2;
				right = -level_width/2;
				top = -level_height/2;
				bottom = level_height/2;
			}else{
				p = QuadTreeNode.findParent(i);
				int offset = i - QuadTreeNode.findChild(p);
				//System.out.println("offset = " + offset);
				try {
					if(offset == 0){ // top left
						left = QT_nodes[p].getLeftBound();
						right = (QT_nodes[p].getRightBound() + left) / 2;
						top = QT_nodes[p].getTopBound();
						bottom = (QT_nodes[p].getBottomBound() + top) / 2;
					}else if(offset == 1){ // top right
						right = QT_nodes[p].getRightBound();
						left = (QT_nodes[p].getLeftBound() + right) / 2;
						top = QT_nodes[p].getTopBound();
						bottom = (QT_nodes[p].getBottomBound() + top) / 2;
					}else if(offset == 2){ // bottom left
						left = QT_nodes[p].getLeftBound();
						right = (QT_nodes[p].getRightBound() + left) / 2;
						bottom = QT_nodes[p].getBottomBound();
						top = (QT_nodes[p].getTopBound() + bottom) / 2;
					}else if(offset == 3){ // bottom right
						right = QT_nodes[p].getRightBound();
						left = (QT_nodes[p].getLeftBound() + right) / 2;
						bottom = QT_nodes[p].getBottomBound();
						top = (QT_nodes[p].getTopBound() + bottom) / 2;
					}
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					//System.out.println("NPE in createTree(int depth).  Parent not created");
					left = right = top = bottom = 0;
				}
			}
			//System.out.println("creating new node: " + left + " to " + right + "; " + top + " to " + bottom);
			QT_nodes[i] = new QuadTreeNode(depth, left, right, top, bottom);
		}
		//System.out.println("number of nodes = " + QT_nodes.length);
	}

	/**
	 * creates a level using the default values for width and height. randomly
	 * spawns default numbers of asteroids and pickups on the map
	 */
	public Level() {
		this(default_level_width, default_level_height,
				default_number_of_asteroids, default_number_of_asteroids,
				default_number_of_pickups, default_score_needed, true);
	}

	/**
	 * creates a level using the default values for width and height. randomly
	 * spawns default number of asteroids and pickups on the map
	 * 
	 * @param warmup
	 *            : if true the level will be created with a smaller number of
	 *            asteroids and pickups
	 * 
	 */
	public Level(boolean warmup) {
		this(default_level_width, default_level_height,
				(warmup) ? default_number_of_asteroids
						: default_warmup_number_of_asteroids,
				(warmup) ? default_number_of_asteroids
						: default_warmup_number_of_asteroids,
				(warmup) ? default_number_of_pickups
						: default_warmup_number_of_pickups,
				default_score_needed, true);
	}

	/**
	 * given x and y coordinates and a direction, places an asteroid generator
	 * on the map
	 * 
	 * @param x
	 * @param y
	 * @param direction
	 */
	public void placeAsteroidGenerator(int x, int y, int direction) {
		AsteroidGenerator g = new AsteroidGenerator(x, y, direction);
		asteroidGenerators.add(g);
	}

	/**
	 * given a pre-defined asteroid generator, adds it to the level
	 * 
	 * @param g
	 */
	public void placeAsteroidGenerator(AsteroidGenerator g) {
		asteroidGenerators.add(g);
	}

	/**
	 * given a pre-defined pickup generator, adds it to the level
	 * 
	 */
	public void placePickupGenerator(PickupGenerator g) {
		pickupGenerators.add(g);
	}

	/**
	 * places ships around a point in circular formation, equi-distance from
	 * each other and a distance of 50 from the center point
	 * 
	 * @param x
	 * @param y
	 * @param numberOfShips
	 * @param includeHuman
	 *            : if true, one of the ships will be the human player
	 */
	public void placeShipsAtPoint(double x, double y, int numberOfShips,
			boolean includeHuman) {
		// x and y are the center point
		int distanceFromCenter = 50;
		int num = numberOfShips;
		if (num < 1) {
			num = 1;
		}
		double currentAngle = 45;
		double eachAngle = 360 / numberOfShips;

		if (includeHuman) {
			Ship s = new Ship(
					x + MyMath.Cos(currentAngle) * distanceFromCenter, y
							+ MyMath.Sin(currentAngle) * distanceFromCenter,
					100, 50, Color.RED, false);
			myShip = s;
			ships.add(s);
			currentAngle += eachAngle;
		}
		for (int i = 0; i < numberOfShips - 1; i++) {
			Color c = Color.CYAN;
			switch (i) {
			case 0:
				c = Color.MAGENTA;
				break;
			case 1:
				c = Color.GREEN;
				break;
			case 2:
				c = Color.YELLOW;
				break;
			case 3:
				c = Color.ORANGE;
				break;
			case 4:
				c = Color.PINK;
				break;
			case 5:
				c = new Color(Color.green.getRGB() / 2 + Color.blue.getRGB()
						/ 2);
				break;
			}
			Ship s = new Ship(
					x + MyMath.Cos(currentAngle) * distanceFromCenter, y
							+ MyMath.Sin(currentAngle) * distanceFromCenter,
					100, 50, c, true);
			ships.add(s);
			currentAngle += eachAngle;
		}
	}

	/**
	 * not implemented
	 */
	public void placeShipsAtPoint(int x, int y, LinkedList<Ship> ships) {

	}

	/**
	 * redraws the level
	 */
	@Override
	public void update(Graphics g) {
		//System.out.println("updating");
		// paint(g);
	}

	public void updateScore() {
		int last_topscore = topscore;
		int last_bottomscore = bottomscore;
		bottomscore = topscore;
		totalscore += 1;
		for (Ship s : ships) {
			if (s.getScore() > topscore) {
				topscore = s.getScore();
			}
			if (s.getScore() < bottomscore) {
				bottomscore = s.getScore();
			}
		}
		for (PickupGenerator g : pickupGenerators) {
			switch (g.getType()) {
			case TOP_SCORE:
				if (last_topscore != topscore && g.submitScoreChange(topscore)) {
					g.trigger();
				}
				break;
			case BOTTOM_SCORE:
				if (last_bottomscore != bottomscore
						&& g.submitScoreChange(bottomscore)) {
					g.trigger();
				}
				break;
			case TOTAL_SCORE:
				if (g.submitScoreChange(totalscore)) {
					g.trigger();
				}
				break;
			default:
				g.trigger();
				break;
			}
		}
	}

	/**
	 * MAIN LOOP: each object's update method is called; each object is
	 * rebounded off the edge of the map or removed as appropriate;
	 * 
	 * each object is queried to see if it is "firing" a new object into the
	 * level, and the appropriate object (asteroid, pickup, weapon) is added to
	 * the game loop;
	 * 
	 * each player is checked for victory;
	 */
	public void updateAll() {
		
		topLayer.clear();
		for(QuadTreeNode n : QT_nodes){
			//n.drawNode(BufferGraphics, camera_x, camera_y);
		}
		for(Ship s : ships){
			updateObject(s);
		}
		updateCamera();
		for (Iterator<SceneObject> i = bullets.iterator() ; i.hasNext() ; ) {
			SceneObject bullet = i.next();
			if(!updateObject(bullet)){
				i.remove();
			}
		}
		for (Iterator<SceneObject> i = missiles.iterator() ; i.hasNext() ; ){
			SceneObject missile = i.next();
			if(!updateObject(missile)){
				i.remove();
			}
		}
		for (Iterator<SceneObject> i = rockets.iterator() ; i.hasNext() ; ){
			SceneObject rocket = i.next();
			if(!updateObject(rocket)){
				i.remove();
			}
		}
		for (Iterator<SceneObject> i = asteroids.iterator() ; i.hasNext() ; ){
			SceneObject asteroid = i.next();
			if(!updateObject(asteroid)){
				i.remove();
			}
		}
		for (Iterator<AsteroidGenerator> i = asteroidGenerators.iterator() ; i.hasNext() ; ){
			AsteroidGenerator generator = i.next();
			if(!updateObject(generator)){
				i.remove();
			}
		}
		for (Iterator<Pickup> i = pickups.iterator() ; i.hasNext() ; ){
			Pickup pickup = i.next();
			if(!updateObject(pickup)){
				i.remove();
			}
		}
		for (Iterator<PickupGenerator> i = pickupGenerators.iterator() ; i.hasNext() ; ){
			PickupGenerator pickupGenerator = i.next();
			if(!updateObject(pickupGenerator)){
				i.remove();
			}
		}
		
		for (SceneObject s : topLayer) {
			s.draw(BufferGraphics, (int) camera_x, (int) camera_y);
		}
		checkForVictory();
	}

	/**
	 * Returns true if the object is still alive after updating.
	 * A return value of false indicates that this should be removed.
	 * @param s
	 * @return
	 */
	protected boolean updateObject(SceneObject s){
		boolean survives = true;
		boolean drawObject = true;
		if(s.getHitPoints() <= 0){
			return false;
		}
		s.update();
		if (Math.abs(camera_x - s.getX()) > default_window_width) {
			drawObject = false;
		}
		if(Math.abs(camera_y - s.getY()) > default_window_height){
			drawObject = false;
		}
		if(s.drawOffscreen){
			drawObject = true;
		}
		if (s.getFiring()) {
			int scatter = MyMath.nextInt(-20, 20);
			if (s.getAmmo() > 0) {
				if (s.getWeapon() == SceneObject.WeaponType.BULLET) {
					Bullet b2 = new Bullet(s, 12, scatter / 6);
					Bullet b3 = new Bullet(s, 12, 0);
					bullets.add(b2);
					bullets.add(b3);
					b2.updateMotion();
					b3.updateMotion();
					s.useAmmo();
				} else if (s.getWeapon() == SceneObject.WeaponType.MISSILE) {
					Missile m2 = new Missile(s, 10, scatter / 7);
					missiles.add(m2);
					m2.updateSteering();
					s.useAmmo();
				} else if (s.getWeapon() == SceneObject.WeaponType.ROCKET) {
					Rocket r2 = new Rocket(s, 10);
					rockets.add(r2);
					s.useAmmo();
				} else if (s.getWeapon() == SceneObject.WeaponType.ASTEROID) {
					Asteroid a = new Asteroid(s, 10, scatter / 5);
					asteroids_in_queue--;
					asteroids.add(a);
					s.useAmmo();
				} else if (s.getWeapon() == SceneObject.WeaponType.PICKUP) {
					Pickup p = new Pickup(s, 10, scatter * 9);
					pickups.add(p);
					s.useAmmo();
					s.addPickup(p);
				} else if (s.getWeapon() == SceneObject.WeaponType.EMP){
					Emp e = new Emp(s, 10, scatter / 6);
					missiles.add(e);
					s.useAmmo();
				}
			}
		}
		double x = s.getX();
		double y = s.getY();
		double c = s.getCollisionRadius();

		if (s.getCollisionID() == SceneObject.CollisionType.ASTEROID
				& !asteroids_bounce) {
			if (x - c > level_width / 2) {
				survives = false;
				asteroids_in_queue++;
			} else if (x + c < -level_width / 2) {
				survives = false;
				asteroids_in_queue++;
			}
			if (y - c > level_height / 2) {
				survives = false;
				asteroids_in_queue++;
			} else if (y + c < -level_height / 2) {
				survives = false;
				asteroids_in_queue++;
			}
		} else if (s.getCollisionID() == SceneObject.CollisionType.WEAPON) {
			if (x - c > level_width / 2) {
				survives = false;
			} else if (x + c < -level_width / 2) {
				survives = false;
			}
			if (y - c > level_height / 2) {
				survives = false;
			} else if (y + c < -level_height / 2) {
				survives = false;
			}
		} else {
			if (x + c > level_width / 2) {
				s.rebound("E", level_width, level_height);
			} else if (x - c < -level_width / 2) {
				s.rebound("W", level_width, level_height);
			}
			if (y + c > level_height / 2) {
				s.rebound("N", level_width, level_height);
			} else if (y - c < -level_height / 2) {
				s.rebound("S", level_width, level_height);
			}
		}
		if (drawObject) {
			if (s.drawOnTop) {
				topLayer.add(s);
			} else {

				s.draw(BufferGraphics, (int) camera_x, (int) camera_y);

			}
		}
		return survives;
	}
	/**
	 * draws the border; calls the game loop, and draws the game
	 */
	public void paint(Graphics gfx) {

		if (offscreen == null) {
			offscreen = createImage(default_window_width, default_window_height);
			BufferGraphics = offscreen.getGraphics();
		}
		BufferGraphics.setClip(0, 0, default_window_width,
				default_window_height);
		BufferGraphics.setColor(new Color(0, 10, 25));
		BufferGraphics.fillRect(0, 0, default_window_width,
				default_window_height);
		draw_border();

		updateAll();
		collision_detection();
		BufferGraphics.setColor(Color.red);
		BufferGraphics.drawString("Checkpoints Hit: " + myShip.getScore()
				+ " / " + score_needed, 100, 140);
		BufferGraphics.drawString("Hitpoints: " + myShip.getHitPoints() + " ("
				+ myShip.getShieldPoints() + ")", 100, 120);
		BufferGraphics.drawString("Ammunition: " + myShip.getAmmo() + "", 100,
				160);
		if (isWon) {
			BufferGraphics.drawString(
					"Game Over.  Press Q to Return to Main Screen", 280, 300);
		}
		
		

		gfx.drawImage(offscreen, 0, 0, this);

	}

	/**
	 * based on the position of the camera, draws the borders of the level
	 */
	public void draw_border() {
		BufferGraphics.setColor(Color.WHITE);
		int top = (int) (BufferGraphics.getClipBounds().getHeight() / 2
				- level_height / 2 - camera_y);
		int bottom = (int) (BufferGraphics.getClipBounds().getHeight() / 2
				+ level_height / 2 - camera_y);
		int left = (int) (BufferGraphics.getClipBounds().getWidth() / 2
				- level_width / 2 - camera_x);
		int right = (int) (BufferGraphics.getClipBounds().getWidth() / 2
				+ level_width / 2 - camera_x);
		BufferGraphics.drawLine(left, top, right, top);
		BufferGraphics.drawLine(left, bottom, right, bottom);
		BufferGraphics.drawLine(left, top, left, bottom);
		BufferGraphics.drawLine(right, top, right, bottom);
	}

	/**
	 * for use at level initialization. spawns a certain number of asteroids
	 * randomly on the map. the asteroids begin with no motion.
	 * 
	 * @param num
	 */
	public void spawn_asteroids(int num) {
		while (num > 0) {
			int x = MyMath.nextInt(0, level_width);
			x -= level_width / 2;
			int y = MyMath.nextInt(0, level_height);
			y -= level_height / 2;
			int weight = MyMath.nextInt(1, 3);
			Asteroid a = new Asteroid(x, y, weight);
			asteroids.add(a);
			num--;

		}
	}

	/**
	 * for use at level initialization. spawns a certain number of pickups
	 * randomly on the map the pickups have no motion
	 * 
	 * @param num
	 */
	public void spawn_pickups(int num) {
		for (int i = 0; i < num; i++) {
			int x = MyMath.nextInt(0, level_width);
			x -= level_width / 2;
			int y = MyMath.nextInt(0, level_height);
			y -= level_height / 2;
			int t = MyMath.nextInt(0, 2);
			Pickup p = new Pickup(x, y, WeaponType.values()[t]);
			pickups.add(p);
		}
	}

	/**
	 * removes the pickup from the game loop, notifies any other objects
	 * targeting this pickup (generators or ships) to clear this pickup.
	 * 
	 * @param p
	 */
	public void removePickup(Pickup p) {
		p.addDamage(1);
		SceneObject gen = p.getController();
		if (gen != null) {
			gen.removePickup(p);
		}
		for (Ship s : ships) {
			if (s.getTarget() == p) {
				s.clearTarget();
			}
		}
		for (PickupGenerator g : pickupGenerators) {
			g.removePickup(p);
		}
	}

	/**
	 * given two "asteroids" which are colliding, bounces them away from each
	 * other
	 * 
	 * @param a1
	 * @param a2
	 */
	public void collision_asteroid_asteroid(SceneObject a1, SceneObject a2) {
		double x1 = a1.getX();
		double y1 = a1.getY();
		double r1 = a1.getCollisionRadius();
		double x2 = a2.getX();
		double y2 = a2.getY();
		double r2 = a2.getCollisionRadius();
		if (MyMath.squarehypotenuse(x1, y1, x2, y2) <= Math.pow(r1 + r2, 2)) {
			a1.addForce(MyMath.aTan(y2 - y1, x2 - x1), 1);
			a1.addDamage(1);
			a2.addForce(MyMath.aTan(y1 - y2, x1 - x2), 1);
			a2.addDamage(1);
		}
	}

	/**
	 * given an "asteroid" and a bullet which are colliding, bounces the
	 * asteroid and removes the bullet
	 * 
	 * @param a
	 * @param b
	 */
	public void collision_asteroid_bullet(SceneObject a, SceneObject b) {
		double x1 = a.getX();
		double y1 = a.getY();
		double x2 = b.getX();
		double y2 = b.getY();
		a.addForce(MyMath.aTan(y2 - y1, x2 - x1), 1);
		a.addDamage(1);
		b.addDamage(1);
	}

	/**
	 * given an "asteroid" and a ship which are colliding, bounces the asteroid
	 * and ship, and adds damage to each
	 * 
	 * @param a
	 * @param s
	 */
	public void collision_asteroid_ship(SceneObject a, Ship s) {
		double x1 = a.getX();
		double y1 = a.getY();

		double x2 = s.getX();
		double y2 = s.getY();

		a.addForce(MyMath.aTan(y2 - y1, x2 - x1), 1);
		a.addDamage(25);
		s.addForce(MyMath.aTan(y1 - y2, x1 - x2), 1);
		damageShip(s, 10);
	}

	/**
	 * given an "asteroid" and a missile which are colliding, bounces the
	 * asteroid and a missile, and adds damage to each
	 * 
	 * @param a
	 * @param m
	 */
	public void collision_asteroid_missile(SceneObject a, SceneObject m) {
		double x1 = a.getX();
		double y1 = a.getY();
		double x2 = m.getX();
		double y2 = m.getY();
		a.addForce(MyMath.aTan(y2 - y1, x2 - x1), 1);
		a.addDamage(4);
		m.addForce(MyMath.aTan(y1 - y2, x1 - x2), 1);
		m.addDamage(40);
	}

	/**
	 * given two ships which are colliding, bounces them and adds damage to each
	 * 
	 * @param s1
	 * @param s2
	 */
	public void collision_ship_ship(Ship s1, Ship s2) {
		double x1 = s1.getX();
		double y1 = s1.getY();
		double x2 = s2.getX();
		double y2 = s2.getY();
		s1.addForce(MyMath.aTan(y2 - y1, x2 - x1), 1);
		damageShip(s1, 10);
		s2.addForce(MyMath.aTan(y1 - y2, x1 - x2), 1);
		damageShip(s2, 10);
	}

	/**
	 * given a ship and a bullet which are colliding, bounces the ship, adds
	 * damage to the ship, and removes the bullet
	 * 
	 * @param s
	 * @param b
	 */
	public void collision_ship_bullet(Ship s, SceneObject b) {
		double x1 = s.getX();
		double y1 = s.getY();
		double x2 = b.getX();
		double y2 = b.getY();

		s.addForce(MyMath.aTan(y2 - y1, x2 - x1), 1);
		damageShip(s, 15);
		b.addDamage(1);
	}

	/**
	 * given a ship and a missile which are colliding, bounces the ship, adds
	 * damage to the missile, and removes the missile
	 * 
	 * @param s
	 * @param m
	 */
	public void collision_ship_missile(Ship s, SceneObject m) {
		double x1 = s.getX();
		double y1 = s.getY();
		double x2 = m.getX();
		double y2 = m.getY();
		s.addForce(MyMath.aTan(y2 - y1, x2 - x1), 1);
		if(m instanceof Emp){
			damageShip(s, 2);
			s.addForce(MyMath.aTan(y2 - y1, x2 - x1), -2);
		}else{
			damageShip(s, 30);
			s.addForce(MyMath.aTan(y2 - y1, x2 - x1), 1);
		}
		m.addDamage(40);
	}

	/**
	 * given a ship which is within range of a missile seeking radius, sets the
	 * target of the missile to the ship
	 * 
	 * @param s
	 * @param m
	 */
	public void collision_ship_radar(Ship s, SceneObject m) {
		if (!s.isRespawning()) {
			m.setTarget(s);
		}
	}

	/**
	 * given a ship and a pickup which are colliding, sets the ship weapon and
	 * ammunition to the pickup type and default quantity
	 * 
	 * @param s
	 * @param p
	 */
	public void collision_ship_pickup(Ship s, Pickup p) {
		if(p.getHitPoints() > 0){
			removePickup(p);
			s.setWeapon(p.getType());
		}
	}

	/**
	 * given two sceneobjects, returns true if they are closer than their
	 * combined collision radii
	 */
	public boolean isColliding(SceneObject a, SceneObject b) {
		double x1 = a.getX();
		double y1 = a.getY();
		double r1 = a.getCollisionRadius();
		double x2 = b.getX();
		double y2 = b.getY();
		double r2 = b.getCollisionRadius();
		return isColliding(x1, y1, r1, x2, y2, r2);
	}

	/**
	 * given a sceneobject and the coordinates and radius of another object,
	 * returns true if they are closer than the combined collision radii
	 */
	public boolean isColliding(double x1, double y1, double r1, SceneObject b) {
		double x2 = b.getX();
		double y2 = b.getY();
		double r2 = b.getCollisionRadius();
		return isColliding(x1, y1, r1, x2, y2, r2);
	}

	/**
	 * given the coordinates and collision radii of two objects, returns true if
	 * they are closer than the combined collision radii
	 */
	public boolean isColliding(double x1, double y1, double r1, double x2,
			double y2, double r2) {
		return (MyMath.squarehypotenuse(x1, y1, x2, y2) <= Math.pow(r1 + r2, 2));
	}

	/**
	 * returns true if object B is in front of object A within some degree
	 * margin
	 */
	public boolean isInFront(SceneObject a, SceneObject b, double margin) {
		double degrees = MyMath.getDirection((int) a.getX(), (int) a.getY(),
				(int) b.getX(), (int) b.getY());
		double difference = degrees - a.getDirection();
		if (difference > 180 && difference < 360 - margin) {
			return false;
		} else if (difference <= 180 && difference > margin) {
			return false;
		} else if (difference < -margin && difference > -180) {
			return false;
		} else if (difference < -180 && difference > -360 + margin) {
			return false;
		}
		return true;
	}

	/**
	 * checks all objects in the game for collisions, and performs the collision
	 * methods for each collision found
	 */
	public void collision_detection() {

		// collision detection for asteroids vs other asteroids & ships &
		// bullets

		for (SceneObject a : asteroids) {
			for (Ship s : ships) {
				if (isColliding(a, s)) {
					collision_asteroid_ship(a, s);
				}
			}
			for (SceneObject b : bullets) {
				if (isColliding(a, b)) {
					collision_asteroid_bullet(a, b);
				}
			}
			for (SceneObject m : missiles) {
				if (isColliding(a, m)) {
					collision_asteroid_missile(a, m);
				}
			}
			boolean isMatched = false;
			for (SceneObject a2 : asteroids) {
				if (isMatched) {
					if (isColliding(a, a2)) {
						collision_asteroid_asteroid(a, a2);
					}
				} else {
					if (a2 == a) {
						isMatched = true;
					}
				}
			}
		}
		// collision detection for ships vs ships & bullets & pickups;

		for (Ship s : ships) {
			if (s.isAuto()) {
				s.setFiring(false);
			}
			for (SceneObject b : bullets) {
				if (b.getController() != s) {
					if (isColliding(s, b)) {
						collision_ship_bullet(s, b);
					}
				}
			}
			for (SceneObject m : missiles) {
				if (m.getController() != s) {
					double r2;
					double x2 = m.getX();
					double y2 = m.getY();
					if (m.getTarget() == null) {
						r2 = m.getScanRadius();
						if (isColliding(x2, y2, r2, s)) {
							collision_ship_radar(s, m);
						}
					}
					r2 = m.getCollisionRadius();
					if (isColliding(x2, y2, r2, s)) {
						collision_ship_missile(s, m);
					}
				}
			}
			for (Pickup p : pickups) {
				if (isColliding(s, p)) {
					collision_ship_pickup(s, p);
				}
			}
			boolean isMatched = false;
			for (Ship s2 : ships) {

				if (isMatched) {
					if (isColliding(s, s2)) {
						collision_ship_ship(s, s2);
					}
				} else {
					if (s == s2) {
						isMatched = true;
					}
				}
				if (s != s2 && s.isAuto() && s.getAmmo() > 0) {
					// TODO: add a suitable range condition
					if (isInFront(s, s2, 9) && !s2.isRespawning()
							&& MyMath.squarehypotenuse(s, s2) < 90000) {
						s.setFiring(true);
					}
				}
			}
		}
	}

	/**
	 * This class does not implement KeyListener; This method is called by the
	 * Game class, which does implement keyListener and relays key events to
	 * this class when suitable.
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			myShip.setTurningLeft(true);
			break;
		case KeyEvent.VK_RIGHT:
			myShip.setTurningRight(true);
			break;
		case KeyEvent.VK_UP:
			myShip.setAccelerating(true);

			break;
		case KeyEvent.VK_DOWN:
			myShip.setDecelerating(true);
			break;
		case KeyEvent.VK_CONTROL:
			myShip.setBraking(true);
			break;
		case KeyEvent.VK_SPACE:
			myShip.setFiring(true);
			break;
		case KeyEvent.VK_A:
			myShip.toggleAuto();
			break;
		case KeyEvent.VK_Q:
			if (isWon) {
				isOver = true;
			}
			break;
		case KeyEvent.VK_R:
			isOver = true;
			break;
		}

	}

	/**
	 * This class does not implement KeyListener; This method is called by the
	 * Game class, which does implement keyListener and relays key events to
	 * this class when suitable.
	 */
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			myShip.setTurningLeft(false);
			break;
		case KeyEvent.VK_RIGHT:
			myShip.setTurningRight(false);
			break;
		case KeyEvent.VK_UP:
			myShip.setAccelerating(false);
			break;
		case KeyEvent.VK_DOWN:
			myShip.setDecelerating(false);
			break;
		case KeyEvent.VK_CONTROL:
			myShip.setBraking(false);
			break;
		case KeyEvent.VK_SPACE:
			myShip.setFiring(false);
			break;
		}

	}

	/**
	 * local method which applies damage to a given ship if the ship is
	 * destroyed this method will produce debris and reset the location of the
	 * ship
	 * 
	 * @param ship
	 * @param damage
	 */
	public void damageShip(Ship ship, int damage) {
		if (!ship.isRespawning()) {
			ship.addDamage(damage);
		}

		if (ship.getHitPoints() <= 0) {
			Debris d = new Debris(ship, 6, MyMath.nextInt(-5, 0), true);
			asteroids.add(d);
			d = new Debris(ship, 6, 0, false);
			if (ship == myShip) {
				camera = d;
			}
			asteroids.add(d);
			d = new Debris(ship, 6, MyMath.nextInt(0, 5), true);
			asteroids.add(d);
			ship.resetAtCheckPoint();

		}
	}

	/**
	 * checks each ship for victory condition if any ship has reached the
	 * necessary score, the game will be declared over (isWon = true), and the
	 * variable "winner" will be set to the winning ship
	 */
	public void checkForVictory() {
		if (!isWon) {
			for (Ship s : ships) {
				if (s.hasWon(score_needed)) {
					winner = s;
					Color winnerColour = s.getColour();
					BufferGraphics.setColor(winnerColour);
					BufferGraphics.drawString("Winner", 100, 180);
					isWon = true;
				}
			}
		} else {
			Color winnerColour = winner.getColour();
			BufferGraphics.setColor(winnerColour);
			BufferGraphics.drawString("Winner", 100, 180);
			// for(Ship s : ships){
			// if (s != winner){
			// s.setTarget(winner);
			// s.setWeapon(SceneObject.WeaponType.BULLET);
			// }
			// }
		}
	}

	/**
	 * adjusts the position of the center of the screen relative to the level.
	 * when the player is not respawning, the player ship will be the center of
	 * the screen. when the player is respawning, the camera will move from the
	 * death point to the player ship as it respawns
	 */
	public void updateCamera() {
		if (camera == null) {
			camera = myShip;
			camera_x = (int) myShip.getX();
			camera_y = (int) myShip.getY();
		} else if (!myShip.isRespawning()) {
			camera = myShip;
			camera_x = (int) myShip.getX();
			camera_y = (int) myShip.getY();
		} else {
			double respawnComplete = (double) myShip.getTimeToRespawn()
					/ (double) Ship.default_respawn_time;
			camera_x = (int) (camera.getX() * respawnComplete + myShip.getX()
					* (1 - respawnComplete));
			camera_y = (int) (camera.getY() * respawnComplete + myShip.getY()
					* (1 - respawnComplete));
		}
	}
}
