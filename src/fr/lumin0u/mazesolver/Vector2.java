package fr.lumin0u.mazesolver;

import java.util.Objects;

public class Vector2 implements Cloneable
{
	private int x;
	private int z;
	
	public Vector2(int x, int z)
	{
		this.x = x;
		this.z = z;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getZ()
	{
		return z;
	}
	
	public Vector2 setX(int x)
	{
		this.x = x;
		return this;
	}
	
	public Vector2 setZ(int z)
	{
		this.z = z;
		return this;
	}
	
	public double distance(Vector2 l1)
	{
		return Math.sqrt(Math.pow(l1.x - x, 2) + Math.pow(l1.z - z, 2));
	}
	
	public Vector2 shifted(int x, int z)
	{
		return clone().add(new Vector2(x, z));
	}
	
	public Vector2 shifted(Direction direction)
	{
		return clone().add(direction.toVector());
	}
	
	public Vector2 add(Vector2 l1)
	{
		x+=l1.x;
		z+=l1.z;
		return this;
	}
	
	public Vector2 add(Direction l1)
	{
		x+=l1.toVector().getX();
		z+=l1.toVector().getZ();
		return this;
	}
	
	public boolean isOutOfBounds(int minX, int minZ, int maxX, int maxZ)
	{
		return x < minX || z < minZ || x >= maxX || z >= maxZ;
	}
	
	@Override
	public String toString()
	{
		return "Location [x=" + x + ", z=" + z + "]";
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		Vector2 location = (Vector2) o;
		return x == location.x && z == location.z;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(x, z);
	}
	
	@Override
	protected Vector2 clone()
	{
//		try
//		{
//			return (Location)super.clone();
//		}catch(CloneNotSupportedException e)
//		{
//			e.printStackTrace();
//		}
		return new Vector2(x, z);
	}
}
