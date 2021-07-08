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
						maze[x][z] = random.nextInt(100) < 55;
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
		
		for(int i = 0; i < xSize * zSize / 4; i++)
		{
//			if(walls.isEmpty())
//				walls.add(new Wall(random, new Vector2(random.nextInt(xSize), random.nextInt(zSize)), Direction.random(random)));
			
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
				
				for(Direction dir : wall.direction.getPerpendiculars())
				{
					Vector2 nPoint = wall.point.shifted(dir);
					if(nPoint.isOutOfBounds(0, 0, xSize, zSize) || done[nPoint.getX()][nPoint.getZ()])
						continue;
					if(random.nextInt(10) == 0)
					{
						walls.add(new Wall(random, nPoint, dir));
					}
					else
					{
						done[nPoint.getX()][nPoint.getZ()] = true;
						maze[nPoint.getX()][nPoint.getZ()] = true;
						Main.frame.drawNode(nPoint, Color.BLUE);
					}
				}
				
				if(wall.lastRotation > 5 && random.nextInt(40) < wall.lastRotation / 2)
				{
					wall.direction = Direction.randomPerpendicular(wall.direction, random);
					wall.lastRotation = 0;
				}
				wall.lastRotation++;
				
				wall.point.add(wall.direction);
			}
			walls.removeIf(wall -> wall.remaining <= 0);
			
			if(count % 800 == 0)
				Main.frame.print();
		}
		
		return maze;
		/*
		int wallSize = random.nextInt(100)+30;
		loop: for(int i = 0; i < wallSize; i++)
		{
			count++;
			int max = 5;
			Vector2 nextPoint = point.clone().add(dir1);
			while(done.containsKey(nextPoint) || nextPoint.getX() >= xSize || nextPoint.getZ() >= zSize || nextPoint.getX() < 0 || nextPoint.getZ() < 0)
			{
				nextPoint = point.clone().add(dir1);
				max--;
				dir1 = new Vector2((int)Math.copySign(Math.abs(dir1.getX()) - 1, random.nextInt()), (int)Math.copySign(Math.abs(dir1.getZ()) - 1, random.nextInt()));
				if(max <= 0)
					break loop;
			}
			point.add(dir1);
			done.put(point.clone(), true);
			Main.frame.drawNode(point, Color.BLUE);

			if(random.nextInt(14) == 0)
			{
				dir1 = new Vector2((int)Math.copySign(Math.abs(dir1.getX()) - 1, random.nextInt()), (int)Math.copySign(Math.abs(dir1.getZ()) - 1, random.nextInt()));
			}
			
			if(random.nextInt(5) == 0)
			{
				try
				{
					done.putAll(generateMaze(xSize, zSize, done, point.clone(), new Vector2((int)Math.copySign(Math.abs(dir1.getX()) - 1, random.nextInt()), (int)Math.copySign(Math.abs(dir1.getZ()) - 1, random.nextInt())), random, generation + 1));
				}catch(StackOverflowError | ConcurrentModificationException e)
				{
					break;
				}
			}
			for(int x = -1; x < 2; x++)
			{
				for(int z = -1; z < 2; z++)
				{
					if(Math.abs(x + z) == 1)
					{
						Vector2 p = point.clone().add(new Vector2(x, z));
						if(!done.containsKey(p) && (!p.equals(point.clone().add(dir1)) || i + 1 == wallSize) && random.nextInt(5) > 0)
						{
							Main.frame.drawNode(p, Color.CYAN);
							done.put(p.clone(), false);
						}
					}
				}
			}

			Graphics g = Main.frame.getImageGraphics();
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 0, 30, 10);
			g.setColor(Color.MAGENTA);
			g.drawString(""+generation, 0, 10);
			
			if(count % 800 == 0)
				Main.frame.print();
		}
		
		return done;*/
	}
	
	static class Wall
	{
		private int remaining;
		private Vector2 point;
		private Direction direction;
		private int lastRotation;
		
		public Wall(Random random, Vector2 point, Direction direction)
		{
			this.remaining = random.nextInt(100)+30;
			this.point = point;
			this.direction = direction;
		}
	}
}
