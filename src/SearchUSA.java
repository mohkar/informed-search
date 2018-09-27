import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import model.Frontier;
import model.Neighbor;


public class SearchUSA {
	
	static HashMap<String, List<Neighbor>> distanceMap = new HashMap<String, List<Neighbor>>();
	static Frontiers frontiers = new Frontiers();
	static HashMap<String, Frontier> explored = new HashMap<String, Frontier>();
	static List<Frontier> exploredList = new ArrayList<Frontier>();
	static HashMap<String, List<Double>> location = new HashMap<String, List<Double>>();
	
	public static void main(String[] args) {
		try {
			initializeDistances();
			initializeLocation();
			search(args[0], args[1], args[2]);
//			search("astar", "salem", "dayton");
		} catch(Exception e) {
			System.out.println("Exception occured : " + e);
			System.out.println(e);
		}
	}
	
	
	public static void initializeDistances() throws Exception {
		File f = new File("../resources/distance.csv");
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine(); 
		while (line != null) {
//			System.out.println(line);
			String arr[] = line.split(",");
//			System.out.println(String.join("#", arr));
			addUndirectionalPath(arr);				
			line = br.readLine();
		}
	}
	
	public static void initializeLocation() throws Exception {
		File f = new File("../resources/location.csv");
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine(); 
		while (line != null) {
//			System.out.println(line);
			String arr[] = line.split(",");
//			System.out.println(String.join("#", arr));
			location.put(arr[0], Arrays.asList(Double.parseDouble(arr[1]), Double.parseDouble(arr[2])));				
			line = br.readLine();
		}
	}
	
	public static void addUndirectionalPath(String arr[]) {
		addFirstToSecondPath(arr);
		addSecondToFirstPath(arr);
	}
	
	public static void addFirstToSecondPath(String arr[]) {
		addPath(arr[0], arr[1], arr[2]);
	}
	
	public static void addSecondToFirstPath(String arr[]) {
		addPath(arr[1], arr[0], arr[2]);
	}
	
	public static void addPath(String from, String to, String distance) {
		List<Neighbor> neighbors = distanceMap.get(from);
		if (neighbors != null) {
			neighbors.add(new Neighbor(to, Double.parseDouble(distance)));
		// Might have to put list again
		} else {
			neighbors = new ArrayList<Neighbor>();
			neighbors.add(new Neighbor(to, Double.parseDouble(distance)));
			distanceMap.put(from, neighbors);
		}
	}
	
	public static void search(String algorithm, String src, String dst) throws Exception {
	 
		Frontier frontier = new Frontier(src, 0.0, src, 0.0, 0.0);
		
		while(frontier != null) {
			
			if(frontier.name.equals(dst)) {
				printExploredNodes();
				List<String> fullPath = getFullPath(frontier, src);
				break;
			} 
		
//			System.out.println("Exploring node : " + frontier.name);
			explored.put(frontier.name, frontier);
			exploredList.add(frontier);
			List<Neighbor> neighborList = moveGenerator(frontier.name);
			
			for (int count = 0 ; count < neighborList.size() ; count++) {
				
				Double totalCost = getCostBasedOnAlgorithm(
						algorithm, 
						frontier, 
						neighborList.get(count), 
						dst
						);
				
				String newFrontierName = neighborList.get(count).name;
				if (frontiers.isFrontierPresent(newFrontierName)) {
					Frontier existingFrontier = frontiers.getFrontierByKey(newFrontierName);
					if(totalCost < existingFrontier.totalCost) {
						frontiers.updateFrontierByKey(
								existingFrontier, 
								frontier.distanceFromSource + neighborList.get(count).distance,
								frontier.name, 
								neighborList.get(count).distance,
								totalCost
								);
					}
					
				} else {
					
					if (explored.get(newFrontierName) == null) {
						frontiers.addFrontier(
								newFrontierName, 
								frontier.distanceFromSource + neighborList.get(count).distance,
								frontier.name, 
								neighborList.get(count).distance,
								totalCost
								);
					}
				}
			}
			frontier = frontiers.getPriorityFrontier();
		}	
	}
	
	public static Double getCostBasedOnAlgorithm(
			String algorithm, 
			Frontier frontier, 
			Neighbor neighbor, 
			String dst) throws Exception {

		Double totalCost = null;
		switch(algorithm) {
		case "dynamic" :
			totalCost = dynamicCost(frontier.distanceFromSource, neighbor.distance);
			break;
		case "greedy" : 
			totalCost = greedyCost(neighbor.name, dst);
			break;
		case "astar" : 
			totalCost = astarCost(frontier.distanceFromSource, neighbor, dst);
			break;
		default: 
			totalCost = astarCost(frontier.distanceFromSource, neighbor, dst);
			break;
		}
		
		return totalCost;
	}
	
	public static Double dynamicCost(Double exploredDistance, Double frontierDistance) {
		return exploredDistance + frontierDistance;
	}
	
	public static Double greedyCost(String nodeName, String dst) throws Exception {
		return getHeuristicDistance(nodeName, dst);
	}
	
	public static Double astarCost(Double exploredDistance, Neighbor neighbor, String dst) throws Exception {
		return exploredDistance + neighbor.distance + getHeuristicDistance(neighbor.name, dst);
	}
	
	public static List<String> getFullPath(Frontier frontier, String src) {		
		
		String parent = frontier.parentNode;
		List<String> fullPath = new ArrayList<String>();
		
		fullPath.add(frontier.name);
		fullPath.add(parent);
		int totalNumberOfNodes = 2;
		
		Double totalDistance = frontier.distanceFromParent;
		
		while(!parent.equals(src)) {
			Frontier parentObj = explored.get(parent);
			parent = parentObj.parentNode;
			totalDistance += parentObj.distanceFromParent;
//			System.out.println("distance : " + parentObj.distanceFromParent);
			fullPath.add(parent);
			totalNumberOfNodes++;
		}
		
		printFullPath(fullPath);
		System.out.println("total nodes in path :" + totalNumberOfNodes);
		System.out.println("total distance : " + totalDistance);
		
		return fullPath;	
	}
	
	public static List<Neighbor> moveGenerator(String src) throws Exception {
		List<Neighbor> neighbors = distanceMap.get(src);
		if (neighbors == null) {
			throw new Exception("Enter correct source");
		}
		return neighbors;
	}
	
	public static Double getHeuristicDistance(String frontier, String dst) throws Exception {

		List<Double> frontierLocation = location.get(frontier);
		List<Double> dstLocation = location.get(dst);
		
		if(dstLocation == null) {
			throw new Exception("Enter correct destination");
		}
		
		Double result = Math.sqrt(Math.pow(69.5 * (frontierLocation.get(0) - dstLocation.get(0)), 2) + 
				Math.pow((69.5 * Math.cos(((frontierLocation.get(0) + dstLocation.get(0)) / 360) * Math.PI ) *
				(frontierLocation.get(1) - dstLocation.get(1))), 2)		
				);
//		System.out.println(frontier + " " + dst + " " + result);
		return result;
	}
	
	public static void printExploredNodes() {
		
		System.out.print("explored nodes : ");
		for (int count = 0 ; count < exploredList.size(); count++) {
			System.out.print(exploredList.get(count).name);
			if (count < exploredList.size() - 1) {
				System.out.print(", ");
			}
		}
		
		System.out.println();
		System.out.println("total explored nodes : " + exploredList.size());
	}
	
	public static void printFullPath(List<String> fullPath) {
		
		System.out.print("nodes in path : ");
		for (int count = fullPath.size() - 1; count >= 0; count--) {
			System.out.print(fullPath.get(count));
			if (count > 0) {
				System.out.print(", ");
			}
		}
		System.out.println();
	}
}
