package tools;

import java.util.Iterator;
import java.util.LinkedList;

import model.SceneObject;

public class ConcurrentArray<T> implements Iterable<T> {
	public static final int default_capacity = 300;
	protected int capacity;
	protected int size;
	protected Object[] objects;
	@SuppressWarnings("unchecked")
	public ConcurrentArray(){
		size = 0;
		capacity = default_capacity;
		objects = (T[]) new Object[default_capacity];
	}
	public void add(T t){
		ensure_capacity();
		objects[size] = t;
		size++;
	}
	public void ensure_capacity(){
		if(size >= capacity){
			capacity *= 2;
		}
		Object[] new_array = new Object[capacity];
		for(int i = 0; i < size; i++){
			new_array[i] = objects[i];
		}
		objects = new_array;
	}
	
	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return new ArrayIterator<T>();
	}
	class ArrayIterator<T> implements Iterator<T>{
		int index;
		public ArrayIterator(){
			index = -1;
		}
		@Override
		public boolean hasNext() {
			return index+1 < size;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			index++;
			if(index >= size){
				throw new IndexOutOfBoundsException("index = " + index + ", size = " + size);
			}
			T obj = (T)objects[index];
			return obj;
		}

		@Override
		/**
		 * removes the element currently under the iterator.
		 */
		public void remove() {
			objects[index] = objects[size - 1];
			size--;
			index--;
		}
	}
}
