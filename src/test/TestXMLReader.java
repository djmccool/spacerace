package test;

import java.io.IOException;

import level.Level;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import tools.LevelLoader;
import static org.junit.Assert.*;

public class TestXMLReader {
	private LevelLoader loader;

	@Before
	public void setUp(){
		loader = new LevelLoader();
	}
	@Test
	public void testLevelOne(){
		System.out.println("Testing Level One");
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(loader);
			reader.parse("src/data/Level_1.xml");
			System.out.println(loader);
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testLevelTwo(){
		System.out.println("Testing Level Two");
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(loader);
			reader.parse("src/data/Level_2.xml");
			System.out.println(loader);
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
