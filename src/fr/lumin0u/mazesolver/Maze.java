package fr.lumin0u.mazesolver;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

public class Maze
{
	protected Color[][] nodesColor;
	protected boolean[][] nodes;
	private List<Vector2> toDraw;
	protected int xSize;
	protected int zSize;
	
	public Maze(boolean[][] nodes)
	{
		this.nodes = nodes;
		
		if(nodes.length < 1 || nodes[0].length < 1)
			throw new IllegalArgumentException("Maze isn't in a good format.");
		
		xSize = nodes.length;
		zSize = nodes[0].length;
		
		nodesColor = new Color[xSize][zSize];
		
		toDraw = new ArrayList<>();
		
		for(int x = 0; x < xSize; x++)
		{
			for(int z = 0; z < zSize; z++)
			{
				toDraw.add(new Vector2(x, z));
				if(nodes[x][z])
					nodesColor[x][z] = Color.BLACK;
				else
					nodesColor[x][z] = Color.WHITE;
			}
		}
	}
	
	public boolean isSolidNode(Vector2 v)
	{
		return isSolidNode((int)v.getX(), (int)v.getZ());
	}
	
	public boolean isSolidNode(int x, int z)
	{
		if(x >= 0 && z >= 0 && x < xSize && z < zSize)
			return nodes[x][z];
		else
			return true;
	}
	
	public void setSolidNode(Vector2 v, boolean solid)
	{
		setSolidNode((int)v.getX(), (int)v.getZ(), solid);
	}
	
	public void setSolidNode(int x, int z, boolean solid)
	{
		if(x >= 0 && z >= 0 && x < xSize && z < zSize)
			nodes[x][z] = solid;
	}
	
	public Color getNodeColor(Vector2 v)
	{
		return getNodeColor((int)v.getX(), (int)v.getZ());
	}
	
	public Color getNodeColor(int x, int z)
	{
		if(x >= 0 && z >= 0 && x < xSize && z < zSize)
			return nodesColor[x][z];
		else
			return null;
	}
	
	public void setNodeColor(Vector2 v, Color c)
	{
		setNodeColor((int)v.getX(), (int)v.getZ(), c);
	}
	
	public void setNodeColor(int x, int z, Color c)
	{
		if(!getNodeColor(x, z).equals(c))
			toDraw.add(new Vector2(x, z));
		
		if(x >= 0 && z >= 0 && x < xSize && z < zSize)
			nodesColor[x][z] = c;
	}
	
	public List<Vector2> getToDraw()
	{
		return toDraw;
	}
	
	public int getXSize()
	{
		return xSize;
	}
	
	public int getZSize()
	{
		return zSize;
	}
	
	public static Maze generateRandom(int xSize, int zSize, int form, Random random)
	{
		boolean[][] maze = new boolean[xSize][zSize];
		
		long startDate = System.currentTimeMillis();
		
		switch(form)
		{
			case 1:
			{
				Map<Vector2, Boolean> walls = new HashMap<>();
				Graphics g = Main.frame.getImageGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, Main.frame.getWidth(), Main.frame.getHeight());
				
				maze = generateMaze(xSize, zSize, random);
				
//				while(walls.size() < xSize * zSize / 2)
//				{
//					int x = (int)((random.nextInt(4) - 1) * 0.5);
//					int z = x == 0 ? random.nextInt(2) * 2 - 1 : 0;
//
//					Location direction = new Location(x, z);
//				walls.putAll(generateBranch(xSize, zSize, walls, new Location(random.nextInt(xSize), random.nextInt(zSize)), direction, random, 0));
//
//				for(int x = 0; x < xSize; x++)
//					for(int z = 0; z < zSize; z++)
//						if(walls.containsKey(new Location(x, z)))
//							maze[x][z] = !walls.get(new Location(x, z));
//						else
//							maze[x][z] = true;
				break;
			}
			
			default:
			{
				for(int x = 0; x < xSize; x++)
					for(int z = 0; z < zSize; z++)
						maze[x][z] = random.nextInt(100) < 57;
			}
		}

		System.out.println("maze generation : " + (System.currentTimeMillis() - startDate) + " ms");
		
		return new Maze(maze);
	}
	
	private static long count = 0;
	
	private static boolean[][] generateMaze(int xSize, int zSize, Random random)
	{
		List<Wall> walls = new ArrayList<>();
		boolean[][] maze = new boolean[xSize][zSize];
		boolean[][] done = new boolean[xSize][zSize];
		for(boolean[] array : maze)
			Arrays.fill(array, true);
		
		walls.add(new Wall(random, new Vector2(random.nextInt(xSize), random.nextInt(zSize)), Direction.random(random)));
		
		for(int i = 0; !walls.isEmpty(); i++)
		{
//			try
//			{
//				Thread.sleep(2);
//			}catch(InterruptedException e)
//			{
//				e.printStackTrace();
//			}
			
			for(Wall wall : new ArrayList<>(walls))
			{
				wall.remaining--;
				
				if(wall.point.isOutOfBounds(0, 0, xSize, zSize) || done[wall.point.getX()][wall.point.getZ()])
				{
					walls.remove(wall);
					break;
				}
				
				done[wall.point.getX()][wall.point.getZ()] = true;
				maze[wall.point.getX()][wall.point.getZ()] = false;
				Main.frame.drawNode(wall.point, Color.CYAN);
				
				if(wall.lastRotation > 1 && random.nextInt(20) < wall.lastRotation / 2)
				{
					wall.direction = Direction.randomPerpendicular(wall.direction, random);
					wall.lastRotation = 0;
				}
				
				for(Direction dir : wall.direction.getPerpendiculars())
				{
					Vector2 nPoint = wall.point.shifted(dir);
					if(nPoint.isOutOfBounds(0, 0, xSize, zSize) || done[nPoint.getX()][nPoint.getZ()])
						continue;
					if(wall.lastRotation > 3 && random.nextInt(20 - wall.lastRotation) < 2)
					{
						walls.add(new Wall(random, nPoint, dir));
						wall.lastRotation = 0;
					}
					else if(random.nextInt(10) > 5)
					{
						done[nPoint.getX()][nPoint.getZ()] = true;
						maze[nPoint.getX()][nPoint.getZ()] = true;
						Main.frame.drawNode(nPoint, Color.BLUE);
					}
				}
				
				wall.lastRotation++;
				
				wall.point.add(wall.direction);
			}
			walls.removeIf(wall -> wall.remaining <= 0);
			
			if(i % 100 == 0)
				Main.frame.print();
		}
		
		return maze;
	}
	
	static class Wall
	{
		private int remaining;
		private Vector2 point;
		private Direction direction;
		private int lastRotation;
		private int lastBranch;
		
		public Wall(Random random, Vector2 point, Direction direction)
		{
			this.remaining = Integer.MAX_VALUE;//random.nextInt(100)+30;
			this.point = point;
			this.direction = direction;
		}
	}
}
