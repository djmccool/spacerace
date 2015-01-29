package model;
import java.awt.*;

import tools.*;
public class Missile extends SceneObject{
    protected SceneObject controller;
    protected int scanradius;
    protected static LineMap missile = new LineMap("LM_missile");
    public Missile(SceneObject controller, double speed, int scatter){
        super(controller.getX(), controller.getY(), 0, 0, (int)(controller.getDirection() + scatter), 6, controller.getColour());
        this.controller = controller;
        createSteeringStats(speed, controller.getDirection() + scatter, speed, 1, 7);
        createCollisionStats(4, false, 0, 0, 100, 100, 1);
        createSmartStats(true);
        scanradius = 0;
        map = missile;
        drawOnTop = true;
    }
    public SceneObject getController(){
        return this.controller;
    }
    public int getScanRadius(){
        return scanradius;
    }
    public void resetScanRadius(){
        scanradius = 0;
    }
    public void update(){
        updateAI();
        updateSteering();
        updateMotion();
        scanradius += 4;
        if(scanradius > 300){
            scanradius = 300;
        }
        if(smart.target==null){
            
        }else{
            if (MyMath.squarehypotenuse(x, y, smart.target.getX(), smart.target.getY()) > Math.pow(scanradius + smart.target.getCollisionRadius(), 2) || smart.target.isRespawning()) {
                clearTarget();
            }
        }
    }
    public void draw(Graphics g, int xpov, int ypov){
        g.setColor(this.colour);
        this.drawLineMap(g, xpov, ypov, false, false);
        //g.setColor(Color.black);
        //this.drawCircle(g, xpov, ypov, scanradius, false);
    }
	@Override
	public CollisionType getCollisionID() {
		return CollisionType.WEAPON;
	}
	
}
