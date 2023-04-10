package fr.lumin0u.mazesolver;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class APath
{
	private Vector2 start;
	private Vector2 end;
	private List<ANode> path;
	private boolean pathOk;
	private Maze maze;
	private int gridSize;
	private Frame frame;
	private int addedMore = 0;
	
	public APath(Vector2 start, Vector2 end, Maze maze, Frame frame)
	{
		this.start = start;
		this.end = end;
		this.maze = maze;
		this.frame = frame;
		path = new ArrayList<>();
		path.add(new ANode(start, null));
		pathOk = false;
		
		this.gridSize = 1;
	}
	
	public List<ANode> findPath(boolean slowly)
	{
		final int search = 500;
		
		if(start.distance(end) < gridSize)
		{
			path.clear();
			path.add(new ANode(end, null));
			pathOk = true;
		}
		
		if(!pathOk)
		{
			long startDate = System.nanoTime();
			addedMore++;
			if(addedMore % 3 == 0)
				path.clear();
			
			if(path.isEmpty())
				path.add(new ANode(start, null));
			
			List<ANode> closed = new ArrayList<>();
			BinaryTree<ANode> open = new BinaryTree<>(path);
			
//			int time = 0;
			
			loop: while(!pathContains(end, open) && !open.isEmpty())
			{
//				time++;
				if(slowly)
				{
					try
					{
						final long sleepTime = 1;
//						System.out.println(sleepTime);
						
						startDate+=TimeUnit.MILLISECONDS.toNanos(sleepTime);
						Thread.sleep(sleepTime);
					}catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
				ANode current = null;
				
				double minFCost = Integer.MAX_VALUE;

				for(ANode node : open)
				{
					minFCost = Math.min(minFCost, node.getfCost());
					if(node.distance(end) <= gridSize)
					{
						current = node;
						if(!canGo(node, end))
							break loop;
						break;
					}
				}
				
				if(current == null)
				{
					for(ANode node : new ArrayList<>(open))
					{
						if(node.getfCost() <= minFCost)
						{
							current = node;
							break;
						}
					}
				}
				
				maze.setNodeColor(current, new Color(0x00FF00));
				
				open.remove(current);
				closed.add(current);
				
				if(current.distance(end) < gridSize)
					break;
				
				List<ANode> allNodes = new ArrayList<>(open);
				allNodes.addAll(closed);
				
				for(ANode neighbour : explore(allNodes, current))
				{
					open.add(neighbour);
					maze.setNodeColor(neighbour, new Color(0xAAAA00));
				}
				
				if(open.isEmpty() || open.size() + closed.size() > search*1000)
					break;
				
				maze.setNodeColor(start, new Color(0x0000FF));
				maze.setNodeColor(end, new Color(0xFF0000));
				
				frame.drawMaze(maze);
			}
			
			for(ANode point : open)
			{
				if(point.distance(end) < gridSize)
				{
					List<ANode> currentPath = new ArrayList<>();
					
					while(point.getSource() != null)
					{
						currentPath.add(point);
						point = point.getSource();
					}
					
					path = currentPath;
				}
			}
			Collections.reverse(path);
			
			System.out.println("pathfinding : " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startDate) + " ms");
			System.out.println("path size : " + path.size());
			
			for(ANode node : path)
				maze.setNodeColor(node, Color.RED);
			
			frame.drawMaze(maze);
			
			try
			{
				Thread.sleep(5000);
			}catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			
			pathOk = true;
		}
		
		return path;
	}
	
	private boolean pathContains(Vector2 v, Collection<ANode> path)
	{
		for(ANode point : path)
			if(point.distance(v) < gridSize)
				return true;
		return false;
	}
	
	private List<ANode> explore(List<ANode> points, ANode from)
	{
		List<ANode> newPoints = new ArrayList<>();
		
		for(int x = -gridSize; x < gridSize * 2; x += gridSize)
		{
			for(int z = -gridSize; z < gridSize * 2; z += gridSize)
			{
//				if(Math.abs(x+z) == gridSize)
				if((x != 0 || z != 0))
				{
					Vector2 point = new Vector2(from.getX() + x, from.getZ() + z);
					ANode dijPoint = new ANode(point, from);
					
					if(!points.contains(dijPoint) && canGo(from, dijPoint))
						newPoints.add(dijPoint);
				}
			}
		}
		
		return newPoints;
	}
	
	private boolean canGo(Vector2 from, Vector2 to)
	{
		if(isSolidPlace(from) || isSolidPlace(to))
			return false;
		
		return true;
	}
	
	private boolean isSolidPlace(Vector2 v)
	{
		return maze.isSolidNode(v);
	}
	
	public Vector2 getStart()
	{
		return start;
	}
	
	public void setStart(Vector2 start)
	{
		if(!start.equals(this.start))
		{
			this.start = start;
			path.clear();
			pathOk = false;
		}
	}
	
	public Vector2 getEnd()
	{
		return end;
	}
	
	public void setEnd(Vector2 end)
	{
		if(!end.equals(this.end))
		{
			this.end = end;
			pathOk = false;
		}
	}
	
	public class ANode extends Vector2 implements Comparable<ANode>
	{
		private ANode source;
		private double gCost;
		private double hCost;
		
		public ANode(int x, int z, ANode source)
		{
			super(x, z);
			this.source = source;
			gCost = distance(start);
			hCost = distance(end);
		}
		
		public ANode(Vector2 v, ANode source)
		{
			super(v.getX(), v.getZ());
			this.source = source;
			gCost = distance(start);
			hCost = distance(end);
		}
		
		public ANode getSource()
		{
			return source;
		}
		
		public void setSource(ANode source)
		{
			this.source = source;
		}
		
		public double getgCost()
		{
			return gCost;
		}
		
		public double gethCost()
		{
			return hCost;
		}
		
		public double getfCost()
		{
			return hCost + gCost * 0.2;
		}
		
		@Override
		public int compareTo(ANode o) {
			return Double.compare(getfCost(), o.getfCost());
		}
	}
}
