package model;

public class Frontier {
	public String name;
	public Double distanceFromSource;
	public Double distanceFromParent;
	public String parentNode;
	public Double totalCost;
	
	public Frontier(
			String name, 
			Double distanceFromSource, 
			String parentNode, 
			Double distanceFromParent,
			Double totalCost
			) {
		this.name = name;
		this.distanceFromSource = distanceFromSource;
		this.parentNode = parentNode;
		this.distanceFromParent = distanceFromParent;
		this.totalCost = totalCost;
 	}
}
