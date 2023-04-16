package fr.lumin0u.mazesolver;

import java.awt.*;
import java.util.Random;

public class Main
{
	private static long seed = 10;
	public static Frame frame;
	
	public static void main(String[] args)
	{
		BinaryTree<Integer> testTree = new BinaryTree<>();
		
		Random r = new Random();
		
		for(int i = 0; i < 300; i++) {
			
			//System.out.println(testTree + "\n");
			//int a = r.nextInt();
			//System.out.println("add " + a);
			testTree.add(i);
		}
		
		long time = System.nanoTime();
		
		for(int i = 0; i < 300000; i++) {
			
			//System.out.println(testTree + "\n");
			//int a = r.nextInt();
			//System.out.println("contains " + a + " ? " + testTree.contains(a));
			//System.out.println("remove " + a);
			testTree.add(i*634869748);
		}
		
		System.out.println("dur : " + ((double) (System.nanoTime() - time) / 1000000000));
		
		System.out.println(testTree.size());
		System.out.println(testTree.height());
		System.exit(0);
		
		System.out.println(testTree + "\n");
		System.out.println("add 1");
		testTree.add(1);
		System.out.println(testTree + "\n");
		System.out.println("add 2");
		testTree.add(2);
		System.out.println(testTree + "\n");
		System.out.println("add 4");
		testTree.add(4);
		System.out.println(testTree + "\n");
		System.out.println("add 3");
		testTree.add(3);
		System.out.println(testTree + "\n");
		System.out.println("add 2");
		testTree.add(2);
		System.out.println(testTree + "\n");
		System.out.println("rearrange");
		testTree.rearrange();
		System.out.println(testTree + "\n");
		System.out.println("remove 3");
		testTree.remove(3);
		System.out.println(testTree + "\n");
		System.out.println("remove 2");
		testTree.remove(2);
		System.out.println(testTree + "\n");
		System.out.println("remove 2");
		testTree.remove(2);
		System.out.println(testTree + "\n");
		System.out.println("remove 1");
		testTree.remove(1);
		System.out.println(testTree + "\n");
		
		System.exit(0);
		
		
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
