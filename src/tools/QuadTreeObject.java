package tools;
/**
 * 
 * @author Dan McKerricher
 * Interface for objects which can be contained and sorted in QuadTreeObjects
 *
 */
public interface QuadTreeObject {
	public int index = 0;
	public double getY();
	public double getX();
	public int getCollisionRadius();
	/**
	 * returns the array-index location of its containing node in the quad tree
	 * 
	 * @return
	 */
}
