package tools;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import level.Level;
import level.Race;
import model.AsteroidGenerator;
import model.CheckPoint;
import model.PickupGenerator;
import model.SceneObject;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * part of the spacerace game
 * This class takes level data from xml format and constructs a game level
 * @author Daniel McKerricher
 * Credit to UBC CPSC 210 for providing a lab on sax xml parsing, which 
 * provided the framework for this class
 */
public class LevelLoader extends DefaultHandler {
	private StringBuffer accumulator;
	private List<CheckPoint> checkpoints;
	private List<AsteroidGenerator> generators;
	private List<PickupGenerator> pickups;
	private CheckPoint mostRecent;
	private CheckPoint first;
	private CheckPoint next;
	private int width, height;
	private int numberOfAsteroids;
	private int asteroidMax;
	private int numberOfPickups;
	private String type;
	private boolean asteroidsBounce;
	private int score_needed;
	public LevelLoader(){
		generators = new LinkedList<AsteroidGenerator>();
		checkpoints = new LinkedList<CheckPoint>();
		pickups = new LinkedList<PickupGenerator>();
	}
	public void startDocument(){
		accumulator = new StringBuffer();
	}
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		accumulator.setLength(0);
		if(qName.toLowerCase().equals("levelinfo")){ //------LEVEL INFO---------
			type = atts.getValue("type");
			if(type==null){
				type = "race";
			}
			numberOfAsteroids = parseInteger(atts.getValue("asteroidcount"));
			asteroidMax = parseInteger(atts.getValue("asteroidmax"));
			if(asteroidMax < 0){
				asteroidMax = 0;
			}
			if(numberOfAsteroids < 0){
				numberOfAsteroids = 0;
			}
			if(numberOfAsteroids > asteroidMax){
				numberOfAsteroids = asteroidMax;
			}
			width = height = parseInteger(atts.getValue("size"));
			if(width==0){
				width = parseInteger(atts.getValue("width"));
				height = parseInteger(atts.getValue("height"));
			}
			if(width ==0){
				width = Level.default_level_width;
			}
			if(height == 0){
				height = Level.default_level_height;
			}
			score_needed = parseInteger(atts.getValue("scoreneeded"));
			if(score_needed == 0){
				score_needed = Level.default_score_needed;
			}
			asteroidsBounce = parseBoolean(atts.getValue("asteroidsbounce"));
			numberOfPickups = parseInteger(atts.getValue("pickups"));
			if(numberOfPickups == 0){
				numberOfPickups = Level.default_number_of_pickups;
			}
			
		}else if(qName.toLowerCase().equals("checkpoint")){ //-----CHECKPOINT-------
			
			int x = parseInteger(atts.getValue("x"));
			int y = parseInteger(atts.getValue("y"));
			if(width != 0 && height != 0){
				x = x * width / 1000;
				y = y * height / 1000;
			}
			next = new CheckPoint(x,y);
			checkpoints.add(next);
			if (mostRecent != null){
				mostRecent.setNext(next);
			}else{
				first = next;
			}
			mostRecent = next;
		}else if(qName.toLowerCase().equals("asteroidgenerator")){//------ASTEROID GENERATOR-------
			int x; int y; int d;
			String xlocation = atts.getValue("x");
			if(xlocation == null){
				x = 0;
			}else if(xlocation.toLowerCase().equals("left")){
				x = -width/2;
			}else if(xlocation.toLowerCase().equals("right")){
				x = width/2;
			}else{
				x = parseInteger(xlocation) * width / 1000;
			}
			String ylocation = atts.getValue("y");
			if(ylocation == null){
				y = 0;
			}else if(ylocation.toLowerCase().equals("top")){
				y = -height/2;
			}else if(ylocation.toLowerCase().equals("bottom")){
				y = height/2;
			}else{
				y = parseInteger(ylocation) * height / 1000;
			}
			String direction = atts.getValue("direction");
			if(direction == null){
				d = (int)MyMath.getDirection(x, y, 0, 0);
			}else if(direction.toLowerCase().equals("center")){
				d = (int)MyMath.getDirection(x, -y, 0, 0);
			}else{
				d = parseInteger(direction);
			}
			int cooldown = parseInteger(atts.getValue("cooldown"));
			if(cooldown == 0){
				AsteroidGenerator g = new AsteroidGenerator(x, y, d);
				generators.add(g);
			}else{
				AsteroidGenerator g = new AsteroidGenerator(x, y, d, cooldown);
				generators.add(g);
			}
			
		}else if(qName.toLowerCase().equals("pickup")){//----------PICKUP---------
			int x; int y; 
			int pointsneeded;
			int pickupspertrigger;
			
			x = parseInteger(atts.getValue("x")) * width / 1000;
			y = parseInteger(atts.getValue("y")) * height / 1000;
			pointsneeded = parseInteger(atts.getValue("pointsneeded"));
			pickupspertrigger = parseInteger(atts.getValue("pickups"));
			PickupGenerator.SpawnRuleType t = PickupGenerator.SpawnRuleType.findById(atts.getValue("triggertype"));
			SceneObject.WeaponType w = SceneObject.WeaponType.findById(atts.getValue("weapontype"));

			if(t == null || w == null){
				pickups.add(new PickupGenerator(x, y));
			}else{
				pickups.add(new PickupGenerator(x, y, w, t, pointsneeded, pickupspertrigger));
			}
			
		}else if(qName.toLowerCase().equals("opponent")){//--------OPPONENT--------
			
		}else if(qName.toLowerCase().equals("rules")){//-----------RULES-----------
			
		}
	}
	public void endElement(String uri, String localName, String qName){
		
	}
	public void characters(char[] temp, int start, int length){
		accumulator.append(temp, start, length);
	}
	public void endDocument(){
		
	}
	@Override
	public String toString(){
		String response = "";
		response += "Size: " + width + " by " + height + "\n";
		response += "Number of Asteroids: " + numberOfAsteroids + "/" + asteroidMax + "\n";
		for(CheckPoint c : checkpoints){
			response += "Checkpoint: address=" + c + " x = " + c.getX() + " y = " + c.getY() + "\n";
			response += "pointing to checkpoint at address " + c.getNext() + "\n";
		}
		return response;
	}
	
	/**
	 * gathers all the object data which has been parsed from the xml and uses it to initialize the level
	 * @return
	 */
	public Level initializeLevel(){
		loopCheckPoints();
		initializePickupGenerators();
		Race r = new Race(width, height, numberOfAsteroids, asteroidMax, numberOfPickups, score_needed, asteroidsBounce);
		r.addCheckPoints(checkpoints);
		r.placeShipsAtStart(2, true);
		for(AsteroidGenerator g: generators){
			r.placeAsteroidGenerator(g);
		}
		for(PickupGenerator g: pickups){
			r.placePickupGenerator(g);
		}
		return r;
	}
	/**
	 * for each pickup generator, registers it to the "nearest" checkpoint.
	 * 
	 * The nearest checkpoint is the one for which a path to the generator
	 * represents the shortest detour en route to the next checkpoint
	 */
	public void initializePickupGenerators(){
		for(PickupGenerator p : pickups){
			CheckPoint best = null;
			CheckPoint next = null;
			double bestDistance = 0;
			double distance = 0;
			double normalDistance = 0;
			for(CheckPoint c : checkpoints){
				if(c.getNext()== null){
					next = c;
				}else{
					next = c.getNext();
				}
				normalDistance = MyMath.hypotenuse(c.getX(), c.getY(), next.getX(), next.getY());
				distance = MyMath.hypotenuse(c.getX(), c.getY(), p.getX(), p.getY());
				distance += MyMath.hypotenuse(next.getX(), next.getY(), p.getX(), p.getY());
				if(normalDistance <1){
					normalDistance = 1;
				}
				distance /= normalDistance;
				if(best == null){
					best = c;
					bestDistance = distance;
				}else{
					if(distance < bestDistance){
						best = c;
						bestDistance = distance;
					}
				}
			}
			if(best != null){
				best.registerPickupGenerator(p, bestDistance);
			}
		}
		for(CheckPoint c : checkpoints){
			c.sortPickupGenerators();
		}
	}
	/**
	 * connects the last checkpoint to the first checkpoint
	 */
	public void loopCheckPoints(){
		if(mostRecent != null & first !=null){
			mostRecent.setNext(first);
		}
	}
	/**
	 * wrapper for Integer.parseInt
	 * this method will return 0 if the input does not contain an integer
	 * 
	 */
	private int parseInteger(String input){
		try{
			return Integer.parseInt(input);
		}catch(NumberFormatException e){
			return 0;
		}
	}
	/**
	 * returns true if input string is "true"
	 * returns false if input is null or anything but "true"
	 * @param input
	 * @return
	 */
	private boolean parseBoolean(String input){
		if(input == null){
			return false;
		}
		if(input.toLowerCase().equals("true")){
			return true;
		}
		return false;
	}

}
