package ui;


import javax.swing.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import tools.LevelLoader;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import level.*;
import model.Ship;

public class Game extends JFrame implements KeyListener, Runnable {
	Level level;
    volatile Thread mainLoop;
    HomePanel introScreen;
    
    public static void main(String[] args) {
        new Game();
    }
    public Game() {
    	KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
    	
    	introScreen = new HomePanel(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(800, 800));
        this.setSize(new Dimension(100, 100));
        this.add(introScreen);
        this.pack();
        repaint();
        this.setVisible(true);
        
    }

    public void start(int level_number) {
    	this.remove(introScreen);
        addKeyListener(this);
        if(level_number > 0){
        	LevelLoader loader = new LevelLoader();   
            try {
    			XMLReader reader = XMLReaderFactory.createXMLReader();
    			reader.setContentHandler(loader);
    			reader.parse(new InputSource(getClass().getResourceAsStream("/data/Level_" + level_number + ".xml")));
    		} catch (SAXException e) {
    			level = new Race();
    		} catch (IOException e) {
    			level = new Race();
    		}
            level = loader.initializeLevel();
        }else if(level_number == 0){
        	level = new Race();
        }else{
        	level = new Race(false);
        }
        level.setVisible(true);
        level.setFocusable(true);
        
        this.setPreferredSize(new Dimension(Level.default_window_width, Level.default_window_height));
        this.setSize(new Dimension(Level.default_window_width, Level.default_window_height));
        this.add(level);
        this.pack();
        mainLoop = new Thread(this);
        mainLoop.start();
    }
    public void stop(){
        mainLoop = null;
        this.dispose();
        new Game();
    }
    public void run(){
        Thread thisThread = Thread.currentThread();
        while(thisThread == mainLoop){
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
            	
            }
            level.repaint();
            if(level.isOver){
            	stop();
            }
        }
        
        
    }
    public void keyReleased(KeyEvent e) {
        level.keyReleased(e);
    }

    public void keyPressed(KeyEvent e) {
        level.keyPressed(e);
    }

    public void keyTyped(KeyEvent e) {
        
    }
    private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
            	if(level != null){
            		level.keyPressed(e);
            	}
                
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                if(level != null){
                	level.keyReleased(e);
                }
            } else if (e.getID() == KeyEvent.KEY_TYPED) {
                
            }
            return false;
        }
    }
}