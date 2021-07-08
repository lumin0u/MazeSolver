package fr.lumin0u.mazesolver;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JFrame;

public class Frame extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private HashMap<Vector2, Color> nodesColor;
	
	private int xSize;
	private int zSize;
	
	private int nodeSizeX;
	private int nodeSizeZ;
	
	private BufferedImage image;
	
//	private long time;
	
	public Frame(int xSize, int zSize)
	{
		this.xSize = 1000/xSize*xSize;
		this.zSize = 1000/zSize*zSize;
		nodeSizeX = 1000/xSize;
		nodeSizeZ = 1000/zSize;
		this.setSize(this.xSize+20, this.zSize+40);
		
		this.setVisible(true);
		this.setTitle(xSize+" x "+zSize);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
//		this.setResizable(false);
//		this.setMenuBar(null);
		
		nodesColor = new HashMap<>();
		
		image = getGraphicsConfiguration().createCompatibleImage(this.xSize, this.zSize);
	}
	
	public void drawMaze(Maze maze)
	{
		drawMaze(maze, false);
	}
	
	public void drawMaze(Maze maze, boolean hardWay)
	{
		for(Vector2 loc : maze.getToDraw())
			if(nodesColor.get(loc) == null || !nodesColor.get(loc).equals(maze.getNodeColor(loc)) || hardWay)
				drawNode(loc, maze.getNodeColor(loc));
		
		maze.getToDraw().clear();
		print();
	}
	
	public void print()
	{
		Graphics g = getGraphics();
		g.drawImage(image, 10, 30, this);
	}
	
	public Graphics getImageGraphics()
	{
		return image.getGraphics();
	}
	
	public void drawNode(Vector2 loc, Color c)
	{
		Graphics g = image.getGraphics();
		
		int x = (int)loc.getX();
		int z = (int)loc.getZ();
		
		nodesColor.put(new Vector2(x, z), c);
		g.setColor(c);
		g.fillRect(x*nodeSizeX, z*nodeSizeZ, nodeSizeX, nodeSizeZ);
	}
}
