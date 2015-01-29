package model;
import java.awt.*;

import tools.*;
public class Rocket extends SceneObject{
    protected SceneObject controller;
    protected int scanradius;
    protected int timeToMature;
    protected int timeToExplosion;
    protected static LineMap rocket = new LineMap("LM_rocket");        
    public Rocket(SceneObject controller, int speed){
        super(controller.getX(), controller.getY(), 0, 0, (int)controller.getDirection(), 10, controller.getColour());
        this.controller = controller;
        timeToExplosion = 0;
        timeToMature = 15;
        drawOnTop = true;
        this.controller = controller;
        createSteeringStats(speed, controller.getDirection(), speed, 1, 8);
        createCollisionStats(4, false, 0, 0, 100, 100, 0);
        createSmartStats(true);
        scanradius = 0;
        map = rocket;
        drawOnTop = true;
    }
    public void draw(Graphics g, int xpov, int ypov){
        g.setColor(this.getColour());
        this.drawLineMap(g, xpov, ypov, false, false);
    }
    public void update(){
        this.setFiring(false);
        if(timeToMature > 1){
            timeToMature--;
        }else{
            if(timeToExplosion >1){
                timeToExplosion--;
                
            }else{
                timeToExplosion = 5;
                this.setFiring(true);
            }
        }
        updateAI();
        updateSteering();
        updateMotion();
        if(smart.target==null){
            scanradius += 20;
            if(scanradius > 500){
                scanradius = 500;
            }
        }else{
            if (MyMath.squarehypotenuse(x, y, smart.target.getX(), smart.target.getY()) > Math.pow(scanradius + smart.target.getCollisionRadius(), 2)) {
                clearTarget();
            }
        }

    }
    @Override
    public SceneObject getController(){
    	return controller;
    }
	@Override
	public CollisionType getCollisionID() {
		return CollisionType.WEAPON;
	}
}
