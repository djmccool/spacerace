package model;
import tools.*;

import java.awt.*;

public class Pickup extends SceneObject{
    protected static LineMap pickupBullet = new LineMap("LM_pickupBullet");
    protected static LineMap pickupMissile = new LineMap("LM_pickupMissile");
    protected static LineMap pickupRocket = new LineMap("LM_pickupRocket");
    protected static LineMap pickupEmp = new LineMap("LM_pickupEmp");
    protected SceneObject generator;
    
    public Pickup(double x, double y, WeaponType type){
        super(x, y, 0, 0, 0, 20, new Color(255,255,255));
        this.spin = 4;
        weaponType = type;
        if(weaponType== null){
    		int rnd = MyMath.nextInt(0, 2);
    		weaponType = WeaponType.values()[rnd];
    	}
        if(weaponType==WeaponType.BULLET){
            map = pickupBullet;
        }else if(weaponType==WeaponType.MISSILE){
            map = pickupMissile;
        }else if(weaponType==WeaponType.EMP){
        	map = pickupEmp;
        }else if(weaponType==WeaponType.ROCKET){
            map = pickupRocket;
        }
        super.createCollisionStats(7, true, 0, 0, 0, 0, 0);
    }
    
    public Pickup(SceneObject controller, int speed, double scatter){
    	super(controller.getX(), controller.getY(), 0, 0, 0, 20, Color.white);
    	this.spin = 4;
    	weaponType = controller.pickupType;
    	if(weaponType== null | weaponType == WeaponType.RANDOM){
    		int rnd = MyMath.nextInt(0, 2);
    		weaponType = WeaponType.values()[rnd];
    	}
        if(weaponType==WeaponType.BULLET){
            map = pickupBullet;
        }else if(weaponType==WeaponType.MISSILE){
            map = pickupMissile;
        }else if(weaponType==WeaponType.EMP){
        	map = pickupEmp;
        }else if(weaponType==WeaponType.ROCKET){
            map = pickupRocket;
        }
    	this.controller = controller;
        this.force_x = MyMath.Cos(controller.getDirection() + scatter)*speed;
        this.force_y = -MyMath.Sin(controller.getDirection() + scatter)*speed;
        super.createCollisionStats(7, true, 0, 0, 0, 0, 0);
        this.hitpoints = 1;
    }
    
    public WeaponType getType(){
        return weaponType;
    }
    
    @Override
    public void update(){
        updateSpin();
        updateMotion();
    }
    
    @Override
    public void draw(Graphics g, int xpov, int ypov){
    	this.drawLineMap(g, xpov, ypov, false, false);
    }
   
	@Override
	public CollisionType getCollisionID() {
		return CollisionType.TOKEN;
	}
}
