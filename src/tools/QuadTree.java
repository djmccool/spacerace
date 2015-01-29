package tools;

import java.util.Iterator;
public class QuadTree implements Iterable<QuadTreeNode>{

	QuadTreeNode[] nodes;
	@Override
	public Iterator<QuadTreeNode> iterator() {
		// TODO Auto-generated method stub
		return new ArrayIterator();
	}
	class ArrayIterator implements Iterator{
		int index;
		public ArrayIterator(){
			index = 0;
		}
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return index < nodes.length;
		}

		@Override
		public Object next() {
			// TODO Auto-generated method stub
			index++;
			return nodes[index];
		}

		@Override
		/**
		 * No Behaviour.
		 */
		public void remove() {
			// do nothing.
		}
	}
}
