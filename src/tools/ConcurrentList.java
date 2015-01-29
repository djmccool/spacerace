package tools;

import java.util.Iterator;
import java.util.LinkedList;
/**
 * A class which encapsulates a list of generic data
 * this class provides a concurrent-safe strategy for removing items from a list
 * 
 * @author Daniel McKerricher
 *
 * @param <T> the generic data type
 */
public class ConcurrentList<T> implements Iterable<T> {
	private LinkedList<T> data;
	private LinkedList<T> dataToRemove;
	private LinkedList<T> dataToAdd;
	public ConcurrentList(){
		data = new LinkedList<T>();
		dataToRemove = new LinkedList<T>();
		dataToAdd = new LinkedList<T>();
	}
	/**
	 * returns the iterator of the underlying linked list
	 */
	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}
	/**
	 * adds and removes items from the underlying structure
	 * which have been previously queued for addition/removal
	 */
	public void update(){
		for(T t : dataToAdd){
			data.add(t);
		}
		for(T t : dataToRemove){
			data.remove(t);
		}
		dataToAdd.clear();
		dataToRemove.clear();
	}
	/**
	 * adds an item to the queue for later addition to the list
	 * the item will be added to the list when update() is called
	 * @param t
	 */
	public void add(T t){
		dataToAdd.add(t);
	}
	/**
	 * adds an item to the queue for later removal from the list
	 * the item will be removed from the list when update() is called
	 * @param t
	 */
	public void remove(T t){
		dataToRemove.add(t);
	}
	public void clear(){
		data = new LinkedList<T>();
		dataToRemove = new LinkedList<T>();
		dataToAdd = new LinkedList<T>();
	}
}
