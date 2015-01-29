package model;
import java.awt.*;
import tools.*;
public class Bullet extends SceneObject{
    protected SceneObject controller;
    public Bullet(SceneObject controller, double speed, double scatter){
        //scatter will alter the direction of the bullet if <> 0
    	
        super(controller.getX(), controller.getY(), 0, 0, (int)(controller.getDirection() + scatter), 9, controller.getColour());
        this.controller = controller;
        this.force_x = MyMath.Cos(facedirection)*speed;
        this.force_y = -MyMath.Sin(facedirection)*speed;
        drawOnTop = true;
    }
    public SceneObject getController(){
        return this.controller;
    }
    public void update(){
        updateMotion();
    }
    public void draw(Graphics g, int xpov, int ypov){
	int xcenter = (int)g.getClipBounds().getWidth()/2;
	int ycenter = (int)g.getClipBounds().getHeight()/2;
	g.setColor(colour);
	g.drawOval((int)x- xpov + xcenter -collisionradius/2, (int)y - ypov + ycenter -collisionradius/2, collisionradius, collisionradius);
	g.setColor(colour.darker());
	g.fillOval((int)x- xpov + xcenter -collisionradius/2, (int)y - ypov + ycenter -collisionradius/2, collisionradius, collisionradius);
    }
	@Override
	public CollisionType getCollisionID() {
		return CollisionType.WEAPON;
	}
}
