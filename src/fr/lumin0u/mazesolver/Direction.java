package fr.lumin0u.mazesolver;

import java.util.Random;

public enum Direction
{
	LEFT(-1, 0),
	RIGHT(1, 0),
	UP(0, -1),
	DOWN(0, 1);
	
	private final int x;
	private final int z;
	
	Direction(int x, int z)
	{
		this.x = x;
		this.z = z;
	}
	
	public Vector2 toVector()
	{
		return new Vector2(x, z);
	}
	
	public boolean isVertical()
	{
		return z != 0;
	}
	
	public boolean isHorizontal()
	{
		return x != 0;
	}
	
	public Direction[] getPerpendiculars()
	{
		return isVertical() ? new Direction[]{LEFT, RIGHT} : new Direction[]{UP, DOWN};
	}
	
	public static Direction random(Random random)
	{
		return values()[random.nextInt(values().length)];
	}
	
	public static Direction randomPerpendicular(Direction direction, Random random)
	{
		Direction[] array = direction.getPerpendiculars();
		return array[random.nextInt(array.length)];
	}
}
