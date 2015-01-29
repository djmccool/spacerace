package model;

import java.awt.Color;
import java.awt.Graphics;

import tools.LineMap;
import tools.MyMath;

public class Emp extends SceneObject{
	protected SceneObject controller;
    protected int scanradius;
	protected static LineMap emp = new LineMap("LM_emp");
	
	public Emp(SceneObject controller, double speed, int scatter){
        super(controller.getX(), controller.getY(), 0, 0, (int)(controller.getDirection() + scatter), 6, controller.getColour());
        this.controller = controller;
        createSteeringStats(speed, controller.getDirection() + scatter, speed, 1, 7);
        createCollisionStats(4, false, 0, 0, 100, 100, 1);
        createSmartStats(true);
        scanradius = 0;
        map = emp;
        drawOnTop = true;
	}

	@Override
	public CollisionType getCollisionID() {
		return CollisionType.WEAPON;
	}

	@Override
	public void update() {
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
	@Override
	public SceneObject getController(){
        return this.controller;
    }
    public int getScanRadius(){
        return scanradius;
    }
    public void resetScanRadius(){
        scanradius = 0;
    }

	@Override
	public void draw(Graphics g, int xpov, int ypov) {
		g.setColor(this.colour);
        this.drawLineMap(g, xpov, ypov, false, false);
		
	}

}
