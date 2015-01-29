package tools;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class QuadTreeNode {
	private double leftBound, rightBound;
	private double topBound, bottomBound;
	private LinkedList<QuadTreeObject> objects;
	private int depth;
	/**
	 * utility method for quad tree functionality.
	 * given an index corresponding to a node in an array,
	 * returns the index of the parent of that node.
	 * This node will return a value of 0 for the parent of
	 * node 0.
	 * @param current
	 * @return
	 */
	public static int findParent(int current){
		int parent = 0;
		parent = (current - 1) / 4;
		return parent;
	}
	/**
	 * utility method for quad tree functionality.
	 * given an index corresponding to a node in an array,
	 * returns the index of the child of that node.
	 * This method does not guarantee that the child index
	 * will be in the bounds of the array.
	 * @param current
	 * @return
	 */
	public static int findChild(int current){
		int child = 0;
		child = (current * 4) + 1;
		return child;
	}
	public QuadTreeNode(int depth, double leftBound, double rightBound, double topBound, double bottomBound){
		this.depth = depth;
		this.leftBound = leftBound;
		this.rightBound = rightBound;
		this.bottomBound = bottomBound;
		this.topBound = topBound;
		objects = new LinkedList<QuadTreeObject>();
	}
	public void remove(QuadTreeObject o){
		objects.remove(o);
	}
	public void add(QuadTreeObject o){
		objects.add(o);
	}
	public void clearBalls(){
		objects.clear();
	}
	public void drawNode(Graphics g, int pov_x, int pov_y){
		int xcenter = (int) g.getClipBounds().getWidth() / 2;
		int ycenter = (int) g.getClipBounds().getHeight() / 2;
		g.setColor(objects.size() == 0 ? Color.CYAN: Color.RED);
		g.drawLine((int)leftBound + 5 - pov_x + xcenter, (int)topBound -pov_y + ycenter, (int)rightBound - pov_x + xcenter, (int)topBound - pov_y + ycenter);
		g.drawLine((int)leftBound + 5 - pov_x + xcenter, (int)topBound -pov_y + ycenter, (int)leftBound + 5 - pov_x + xcenter, (int)bottomBound - 5 - pov_y + ycenter);
		g.drawLine((int)leftBound + 5 - pov_x + xcenter, (int)bottomBound -5 - pov_y + ycenter, (int)rightBound - pov_x + xcenter, (int)bottomBound - 5 - pov_y + ycenter);
		g.drawLine((int)rightBound - pov_x + xcenter, (int)topBound - pov_y + ycenter, (int)rightBound - pov_x + xcenter, (int)bottomBound - 5 - pov_y +ycenter);
	}
	public double getLeftBound(){
		return leftBound;
	}
	public double getRightBound(){
		return rightBound;
	}
	public double getTopBound(){
		return topBound;
	}
	public double getBottomBound(){
		return bottomBound;
	}
}
