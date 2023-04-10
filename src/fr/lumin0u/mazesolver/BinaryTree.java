package fr.lumin0u.mazesolver;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

public class BinaryTree<T extends Comparable<T>> implements Collection<T>
{
	private Node root;
	private int size;
	
	public BinaryTree()
	{}
	
	public BinaryTree(Collection<T> other)
	{
		addAll(other);
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
		Node node = root;
		while(node != null)
		{
			if(node.value.equals(o))
				return true;
			node = node.value.compareTo((T) o) > 0 ? node.left : node.right;
		}
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
			return true;
		}
		
		Node node = root;
		
		while(true)
		{
			if(node.value.compareTo(t) >= 0)
			{
				if(node.left == null)
				{
					node.left = new Node(t, null, null);
					size++;
					return true;
				}
				node = node.left;
			}
			else
			{
				if(node.right == null)
				{
					node.right = new Node(t, null, null);
					size++;
					return true;
				}
				node = node.right;
			}
		}
	}
	
	@Override
	public boolean remove(Object o) {
		if(root == null || !root.value.getClass().isInstance(o)) {
			return false;
		}
		
		Node node = root;
		Function<Node, Node> replaceNode = n -> root = n;
		
		while(true) {
			if(node.value.equals(o)) {
				Node right = node.right;
				Node left = node.left;
				Node newsParent;
				
				if(node.left != null) {
					// get the rightest node of the left branch
					newsParent = node.left.maxsParent(node);
					
					if(newsParent != node) {
						// move it to the top
						node = replaceNode.apply(newsParent.right);
						// replace its position by its own left branch
						newsParent.right = newsParent.right.left;
						
						node.right = right;
						node.left = left;
					}
					else {
						// woops
						node = replaceNode.apply(node.left);
					}
				}
				else if(node.right != null) {
					// same but with right
					newsParent = node.right.minsParent(node);
					
					if(newsParent != node) {
						node = replaceNode.apply(newsParent.left);
						newsParent.left = newsParent.left.right;
						
						node.right = right;
						node.left = left;
					}
					else {
						node = replaceNode.apply(node.right);
					}
				}
				else {
					// now there's nothing under this node, good news
					node = replaceNode.apply(null);
				}
				
				size--;
				return true;
			}
			
			Node finalNode = node;
			if(node.value.compareTo((T) o) < 0) {
				replaceNode = n -> finalNode.right = n;
				node = node.right;
			}
			else {
				replaceNode = n -> finalNode.left = n;
				node = node.left;
			}
			
			if(node == null) {
				return false;
			}
		}
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
