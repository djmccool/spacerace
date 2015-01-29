package test;
import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import tools.ConcurrentArray;
public class TestConcurrentArray {
	ConcurrentArray<Integer> arr;
	
	@Before
	public void setUp(){
		arr = new ConcurrentArray<Integer>();
		arr.add(1);
		arr.add(2);
		arr.add(3);
	}
	
	@Test
	public void testSize(){
		int count = 0;
		for(Integer i : arr){
			count++;
		}
		assertTrue(count == 3);
	}
	
	@Test
	public void testRemoveFirst(){
		Iterator<Integer> iter = arr.iterator();
		while(iter.hasNext()){
			if(iter.next() == 1){
				iter.remove();
			}
		}
		int count = 0;
		for(Integer i : arr){
			count++;
			if(i == 1){
				fail();
			}
		}
		assertTrue(count == 2);
	}
	
	@Test
	public void testRemoveLast(){
		Iterator<Integer> iter = arr.iterator();
		while(iter.hasNext()){
			if(iter.next() == 3){
				iter.remove();
			}
		}
		int count = 0;
		for(Integer i : arr){
			count++;
			if(i == 3){
				fail();
			}
		}
		assertTrue(count == 2);
	}
	
	@Test
	public void testRemoveMiddle(){
		Iterator<Integer> iter = arr.iterator();
		while(iter.hasNext()){
			if(iter.next() == 2){
				iter.remove();
			}
		}
		int count = 0;
		for(Integer i : arr){
			count++;
			if(i == 2){
				fail();
			}
		}
		assertTrue(count == 2);
	}
	
	@Test
	public void testRemoveAll(){
		Iterator<Integer> iter = arr.iterator();
		while(iter.hasNext()){
			iter.next();
			iter.remove();
		}
		int count = 0;
		for(Integer i : arr){
			count++;
		}
		assertTrue(count == 0);
	}
}
