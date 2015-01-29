package level;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import model.*;
import tools.*;

import java.util.*;
import java.util.List;

public class Race extends Level {
	private int default_number_of_checkpoints = 5; // used only for completely random maps
	CheckPoint start;
	protected List<CheckPoint> checkpoints = new LinkedList<CheckPoint>();

	/**
	 * randomly picks a map (no xml) and adds default number of asteroids.
	 * this method has hard-coded custom maps to choose from.
	 */
	public Race() {
		this(true);
	}

	/**
	 * randomly picks a map (no xml), and maybe adds default number of asteroids.
	 * this method has hard-coded custom maps to choose from.
	 * 
	 * @param warmup: if false, the default number of asteroids will be added.
	 * if true, a smaller default number of asteroids will be added.
	 */
	public Race(boolean warmup) {
		super(warmup);
		int racestyle = MyMath.nextInt(0, 5);
		switch (racestyle) {
		case 0:
			createRace(default_number_of_checkpoints);
			break;
		case 1:
			createCustomRace(4, 0.3, 0, 1, 9, 8, 12, 13, 7, 3, 6, 4);
			break;
		case 2:
			createCustomRace(4, 0.4, 1, 13, 12, 8, 11, 15, 14, 2, 3, 7, 4, 0);
			break;
		case 3:
			createCustomRace(4, 0.6, 0, 12, 15, 3);
			break;
		case 4:
			createCustomRace(4, 0.4, 2, 8, 7, 0, 13);
			break;
		case 5:
			createCustomRace(5, 0.9, 1, 5, 11, 15, 21, 17, 23, 19, 13, 9, 3, 7);
			break;
		}
	}

	/**
	 * create an empty race (no checkpoints) with specified parameters for
	 * dimensions, asteroids, pickups.
	 * 
	 * @param width
	 * @param height
	 * @param number_of_asteroids
	 *            : the maximum AND starting number of asteroids
	 * @param number_of_pickups
	 * @param score_needed
	 * @param bouncy
	 *            : whether the wall will reflect asteroids
	 */
	public Race(int width, int height, int number_of_asteroids,
			int number_of_pickups, int score_needed, boolean bouncy) {
		super(width, height, number_of_asteroids, number_of_asteroids,
				number_of_pickups, score_needed, bouncy);
	}

	/**
	 * creates an empty race (no checkpoints) with specified parameters for
	 * dimensions, asteroids, pickups.
	 * 
	 * @param width
	 * @param height
	 * @param number_of_asteroids
	 * @param maximum_number_of_asteroids
	 * @param number_of_pickups
	 * @param score_needed
	 * @param bouncy
	 */
	public Race(int width, int height, int number_of_asteroids,
			int maximum_number_of_asteroids, int number_of_pickups,
			int score_needed, boolean bouncy) {
		super(width, height, number_of_asteroids, maximum_number_of_asteroids,
				number_of_pickups, score_needed, bouncy);
	}

	/**
	 * Clears the list of checkpoints and then adds the new checkpoints to the
	 * list
	 * 
	 * @param points
	 *            : the checkpoints to add to ensure expected functionality
	 *            these should already be linked in correct order
	 */
	public void addCheckPoints(List<CheckPoint> points) {
		checkpoints = new LinkedList<CheckPoint>();
		for (CheckPoint c : points) {
			checkpoints.add(c);
		}
	}

	/**
	 * attempts to place all ships at the first checkpoint if the checkpoint
	 * list is empty, places ships at 0,0
	 * 
	 * @param number_of_ships
	 * @param include_human
	 */
	public void placeShipsAtStart(int number_of_ships, boolean include_human) {
		int startX = 0;
		int startY = 0;
		if (checkpoints.size() > 0) {
			start = checkpoints.iterator().next();
			startX = (int) start.getX();
			startY = (int) start.getY();
		}
		placeShipsAtPoint(startX, startY, default_number_of_computers + 1, true);
		for (Ship s : ships) {
			s.setCheckPoint(start);
			s.setTarget(s.getCheckPoint().getNext());
		}
	}

	/**
	 * unimplemented
	 */
	public void placeShipsAtStart(List<Ship> ships) {

	}

	/**
	 * creates a race with pre-specified checkpoints.
	 * 
	 * @param width
	 *            : the number of quadrants along each side of the level
	 *            "square." this determines the total number of quadrants that
	 *            the level will be divided into. (total number of quadrants =
	 *            width^2)
	 * 
	 * @param uncertainty
	 *            : if 0, each checkpoint will be placed exactly at the center
	 *            of its quadrant. if 1, each checkpoint will be placed
	 *            completely randomly within its quadrant.
	 * 
	 * @param quadrants
	 *            : a list of integers representing the quadrant locations of
	 *            each checkpoint each integer value in this list should be less
	 *            than the total number of quadrants. (for each integer value i,
	 *            i < width^2)
	 */
	public void createCustomRace(int width, double uncertainty,
			int... quadrants) {
		if (width < 3) {
			width = 3;
		}
		if (quadrants.length > 1) {
			int quadrantwidth = default_level_width / width;
			int quadrantheight = default_level_height / width;
			int randomdistance;
			if (default_level_width > default_level_height) {
				randomdistance = MyMath.nextInt(0,
						(int) (quadrantwidth / 2 * uncertainty));
			} else {
				randomdistance = MyMath.nextInt(0,
						(int) (quadrantheight / 2 * uncertainty));
			}
			int randomangle = MyMath.nextInt(0, 359);
			int randomX = (int) (MyMath.Cos(randomangle) * randomdistance);
			int randomY = (int) (MyMath.Sin(randomangle) * randomdistance);
			int startX = ((quadrants[0] % width) * quadrantwidth) + randomX;
			startX -= default_level_width / 2 - quadrantwidth / 2;
			int startY = ((quadrants[0] / width) * quadrantheight) + randomY;
			startY -= default_level_height / 2 - quadrantwidth / 2;
			placeShipsAtPoint(startX, startY, default_number_of_computers + 1,
					true);
			start = new CheckPoint(startX, startY);
			checkpoints.add(start);
			CheckPoint current = null;
			for (int i = 1; i < quadrants.length; i++) {
				if (default_level_width > default_level_height) {
					randomdistance = MyMath.nextInt(0,
							(int) (quadrantwidth / 2 * uncertainty));
				} else {
					randomdistance = MyMath.nextInt(0,
							(int) (quadrantheight / 2 * uncertainty));
				}
				randomangle = MyMath.nextInt(0, 359);
				randomX = (int) (MyMath.Cos(randomangle) * randomdistance);
				randomY = (int) (MyMath.Sin(randomangle) * randomdistance);
				int x = ((quadrants[i] % width) * quadrantwidth) + randomX;
				x -= default_level_width / 2 - quadrantwidth / 2;
				int y = ((quadrants[i] / width) * quadrantheight) + randomY;
				y -= default_level_height / 2 - quadrantheight / 2;
				if (current == null) {
					current = new CheckPoint(x, y, start);
					checkpoints.add(current);
				} else {
					current = new CheckPoint(x, y, current);
					checkpoints.add(current);
				}
			}
			start.setNext(current);
			for (Iterator<Ship> i = ships.iterator(); i.hasNext();) {
				Ship s = i.next();
				s.setCheckPoint(start);
				s.setTarget(s.getCheckPoint().getNext());
			}
		}
	}

	/**
	 * creates a totally random list of checkpoints. each checkpoint will be
	 * placed somewhere within the level.
	 * 
	 * @param numberOfCheckPoints
	 */
	public void createRace(int numberOfCheckPoints) {
		double startX = MyMath.nextInt((-default_level_width / 2) + 30,
				(default_level_width / 2) - 30);
		double startY = MyMath.nextInt((-default_level_height / 2) + 30,
				(default_level_height / 2) - 30);
		placeShipsAtPoint(startX, startY, 4, true);
		start = new CheckPoint(startX, startY);
		checkpoints.add(start);
		CheckPoint current = null;
		// this.numberOfCheckPoints = numberOfCheckPoints;
		for (int i = 0; i < numberOfCheckPoints - 1; i++) {
			int x = MyMath.nextInt((-default_level_width / 2) + 30,
					(default_level_width / 2) - 30);
			int y = MyMath.nextInt((-default_level_height / 2) + 30,
					(default_level_height / 2) - 30);
			if (current == null) {
				current = new CheckPoint(x, y, start);
				checkpoints.add(current);

			} else {
				current = new CheckPoint(x, y, current);
				checkpoints.add(current);
			}
		}
		start.setNext(current);

		for (Iterator<Ship> i = ships.iterator(); i.hasNext();) {
			Ship s = i.next();
			s.setCheckPoint(start);
			s.setTarget(s.getCheckPoint().getNext());
		}
	}

	/**
	 * calls level.collision_detection() and also checks for
	 * collisions between ships and their next checkpoints
	 */
	public void collision_detection() {
		super.collision_detection();
		for (Ship s : ships) {
			if (s.getCheckPoint() != null) {
				CheckPoint c = s.getCheckPoint().getNext();
				if (isColliding(s, c)) {
					s.setScore(s.getScore() + 1);
					s.advanceCheckPoint();
					s.fullShield();
					updateScore();
				}
			}
		}
	}
	@Override
	public void updateAll(){
		for(CheckPoint cp : checkpoints){
			updateObject(cp);
		}
		super.updateAll();
	}
}
