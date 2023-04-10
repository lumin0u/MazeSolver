package fr.lumin0u.mazesolver;

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;

public class Main
{
	private static long seed = 10;
	public static Frame frame;
	
	public static void main(String[] args)
	{
		BinaryTree<Double> testTree = new BinaryTree<>();
		
		System.out.println(Arrays.toString(testTree.toArray()));
		testTree.add(1.0);
		System.out.println(Arrays.toString(testTree.toArray()));
		testTree.add(2.0);
		System.out.println(Arrays.toString(testTree.toArray()));
		testTree.add(4.0);
		System.out.println(Arrays.toString(testTree.toArray()));
		testTree.add(3.0);
		System.out.println(Arrays.toString(testTree.toArray()));
		testTree.add(2.0);
		System.out.println(Arrays.toString(testTree.toArray()));
		testTree.remove(2.0);
		System.out.println(Arrays.toString(testTree.toArray()));
		testTree.remove(2.0);
		System.out.println(Arrays.toString(testTree.toArray()));
		testTree.remove(1.0);
		System.out.println(Arrays.toString(testTree.toArray()));
		System.out.println(testTree.min());
		System.out.println(testTree.max());
		
		
		int xSize = 1000;
		int zSize = 1000;
		
		frame = new Frame(xSize, zSize);
		try
		{
			Thread.sleep(20);
		}catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		for(int i = 0; i < 1000; i++)
		{
			seed = new Random().nextLong();
			System.out.println("seed : " + seed);
			
			Random random = new Random(seed);
			
			Maze maze = Maze.generateRandom(xSize, zSize, 1, random);
			
			Vector2 start = null;
			Vector2 end = null;
			do
				start = new Vector2(random.nextInt(xSize), random.nextInt(zSize));
			while(maze.isSolidNode(start));
			do
				end = new Vector2(random.nextInt(xSize), random.nextInt(zSize));
			while(maze.isSolidNode(end) || end.equals(start));
			
			maze.setNodeColor(start, new Color(0x0000FF));
			maze.setNodeColor(end, new Color(0xFF0000));
			maze.setSolidNode(start, false);
			maze.setSolidNode(end, false);
			
			frame.drawMaze(maze, false);
			
			APath path = new APath(start, end, maze, frame);
			
			path.findPath(false);
		}
	}
}
