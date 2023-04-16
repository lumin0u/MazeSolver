package fr.lumin0u.mazesolver;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public class BinaryTree<T extends Comparable<T>> implements Collection<T>
{
	private static final int MAX_HEIGHT = 1000;
	
	private Node root;
	private int size;
	private int height = -1;
	
	public BinaryTree()
	{}
	
	public BinaryTree(Collection<? extends T> other) {
		addAll(other);
	}
	
	public static <T extends Comparable<T>> BinaryTree<T> of(T[] array) {
		if(array == null)
			return new BinaryTree<>();
		
		BinaryTree<T> tree = new BinaryTree<>();
		tree.root = tree.nodeFromArray(array, 0, array.length);
		tree.size = array.length;
		return tree;
	}
	
	private Node nodeFromArray(Object[] array, int a, int b) {
		if(a > b-1)
			return null;
		
		int m = (a+b) / 2;
		return new Node((T) array[m], nodeFromArray(array, a, m), nodeFromArray(array, m + 1, b));
	}
	
	public void rearrange() {
		root = nodeFromArray(toArray(), 0, size);
		height = size == 0 ? -1 : Math.getExponent(size);
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public boolean isEmpty() {
		return root == null;
	}
	
	@Override
	public boolean contains(Object o) {
		try {
			Node node = root;
			while(node != null) {
				if(node.value.equals(o))
					return true;
				node = node.value.compareTo((T) o) > 0 ? node.left : node.right;
			}
		} catch(ClassCastException ignore) {}
		return false;
	}
	
	@Override
	public Iterator<T> iterator() {
		return root == null ? Collections.emptyIterator() : new BinaryNodeIterator(root);
	}
	
	@Override
	public Comparable<T>[] toArray() {
		if(root == null)
			return new Comparable[0];
		Comparable<T>[] array = new Comparable[size];
		root.push(array, 0);
		return array;
	}
	
	@Override
	public <T1> T1[] toArray(T1[] array) {
		if(array.length >= size)
		{
			root.push(array, 0);
			if(array.length != size)
				array[size] = null;
			
			return array;
		}
		array = (T1[]) Array.newInstance(array.getClass().arrayType(), size);
		root.push(array, 0);
		return array;
	}
	
	@Override
	public boolean add(T t) {
		if(root == null)
		{
			root = new Node(t, null, null);
			size = 1;
			height = 0;
			return true;
		}
		
		if(height >= MAX_HEIGHT) {
			rearrange();
		}
		
		Node node = root;
		
		int depth = 0;
		
		while(true)
		{
			depth++;
			if(node.value.compareTo(t) >= 0) {
				if(node.left == null) {
					node.left = new Node(t, null, null);
					size++;
					height = Math.max(depth, height);
					return true;
				}
				node = node.left;
			}
			else {
				if(node.right == null) {
					node.right = new Node(t, null, null);
					size++;
					height = Math.max(depth, height);
					return true;
				}
				node = node.right;
			}
		}
	}
	
	@Override
	public boolean remove(Object a) {
		if(root == null || !(a instanceof Comparable<?> obj)) {
			return false;
		}
		
		boolean removed = false;
		
		if(root.value.equals(obj)) {
			Node prevLeft = root.left;
			Node prevRight = root.right;
			root = root.extractReplaceNode();
			if(root != null) {
				if(root != prevLeft)
					root.left = prevLeft;
				if(root != prevRight)
					root.right = prevRight;
			}
			removed = true;
		}
		else {
			try {
				removed = root.remove(obj);
			} catch(ClassCastException ignore) {}
		}
		
		if(removed)
			size--;
		return removed;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return c.stream().distinct().allMatch(this::contains);
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean a = false;
		for(T o : c)
			a |= add(o);
		return a;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean a = false;
		for(Object o : c)
			a |= remove(o);
		return a;
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return removeIf(o -> !c.contains(o));
	}
	
	@Override
	public void clear() {
		root = null;
		size = 0;
	}
	
	public T min() {
		if(root == null)
			return null;
		return root.min().value;
	}
	
	public T max() {
		if(root == null)
			return null;
		return root.max().value;
	}
	
	public int height() {
		return height;
	}
	
	@Override
	public String toString() {
		return root == null ? "{}" : root.toString();
	}
	
	private class Node
	{
		final T value;
		Node left;
		Node right;
		
		private Node(T value, Node left, Node right) {
			Objects.requireNonNull(value);
			this.value = value;
			this.left = left;
			this.right = right;
		}
		
		private int push(Object[] array, int i) {
			if(left != null)
				i = left.push(array, i);
			array[i++] = value;
			if(right != null)
				i = right.push(array, i);
			return i;
		}
		
		private Node min() {
			return left == null ? this : left.min();
		}
		
		private Node max() {
			return right == null ? this : right.max();
		}
		
		private Node minsParent(Node parent) {
			return left == null ? parent : left.minsParent(this);
		}
		
		private Node maxsParent(Node parent) {
			return right == null ? parent : right.maxsParent(this);
		}
		
		private int height() {
			return 1 + Math.max(left == null ? -1 : left.height(), right == null ? -1 : right.height());
		}
		
		private void add(T obj) {
			if(obj.compareTo(value) > 0) {
				if(right == null)
					right = new Node(obj, null, null);
				else
					right.add(obj);
			}
			else {
				if(left == null)
					left = new Node(obj, null, null);
				else
					left.add(obj);
			}
		}
		
		private <R> boolean remove(Comparable<R> obj) {
			// dangerous
			if(obj.compareTo((R) value) < 0) {
				if(left == null) {
					return false;
				}
				if(left.value.equals(obj)) {
					Node prevLeft = left.left;
					Node prevRight = left.right;
					left = left.extractReplaceNode();
					if(left != null) {
						if(left != prevLeft)
							left.left = prevLeft;
						if(left != prevRight)
							left.right = prevRight;
					}
					return true;
				}
				
				return left.remove(obj);
			}
			else {
				if(right == null) {
					return false;
				}
				if(right.value.equals(obj)) {
					Node prevLeft = right.left;
					Node prevRight = right.right;
					right = right.extractReplaceNode();
					if(right != null) {
						if(right != prevLeft)
							right.left = prevLeft;
						if(right != prevRight)
							right.right = prevRight;
					}
					return true;
				}
				
				return right.remove(obj);
			}
		}
		
		private Node extractReplaceNode() {
			if(left != null) {
				Node p = left.maxsParent(this);
				if(p != this) {
					Node m = p.right;
					p.right = m.left;
					return m;
				}
				else {
					return left;
				}
			}
			if(right != null) {
				Node p = right.minsParent(this);
				if(p != this) {
					Node m = p.left;
					p.left = m.right;
					return m;
				}
				else {
					return right;
				}
			}
			return null;
		}
		
		@Override
		public String toString() {
			return "{" + (left == null ? "  " : left == this ? "me " : left.toString() + " ") + value + (right == null ? "  " : right == this ? " me" : " " + right.toString()) + "}";
		}
	}
	
	private class BinaryNodeIterator implements Iterator<T>
	{
		final Node node;
		Iterator<T> subLeft;
		Iterator<T> subRight;
		boolean inRight = false;
		
		private BinaryNodeIterator(Node node) {
			this.node = node;
			
			if(node.left != null)
				subLeft = new BinaryNodeIterator(node.left);
			else
				subLeft = Collections.emptyIterator();
		}
		
		@Override
		public boolean hasNext() {
			return !inRight || subRight.hasNext();
		}
		
		@Override
		public T next() {
			T obj;
			if(!inRight && subLeft.hasNext()) {
				obj = subLeft.next();
			}
			else if(!inRight) {
				obj = node.value;
				inRight = true;
				
				if(node.right != null)
					subRight = new BinaryNodeIterator(node.right);
				else
					subRight = Collections.emptyIterator();
			}
			else {
				obj = subRight.next();
			}
			return obj;
		}
	}
}
