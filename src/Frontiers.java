import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import model.Frontier;

public class Frontiers {
	 
	Comparator<Frontier> distanceComparator = new Comparator<Frontier>() {
        @Override
        public int compare(Frontier f1, Frontier f2) {
            return f1.totalCost.intValue() - f2.totalCost.intValue();
        };
	};
	
	PriorityQueue<Frontier> pQueue = new PriorityQueue<Frontier>(150, distanceComparator);
    
	HashMap<String, Frontier> checkFrontiers = new HashMap<String, Frontier>(); 
	
	public Frontier getPriorityFrontier() {
		Frontier frontier = pQueue.poll();
		checkFrontiers.remove(frontier.name);
		return frontier;
	}
	
	public void addFrontier(
			String name, 
			Double distanceFromSource, 
			String parentNode,
			Double distanceFromParent,
			Double totalCost
			) {
		Frontier newFrontier = new Frontier(name, distanceFromSource, parentNode, distanceFromParent, totalCost);
		checkFrontiers.put(name, newFrontier);
		pQueue.add(newFrontier);
	}
	
	public void addFrontier(Frontier frontier) {
		checkFrontiers.put(frontier.name, frontier);
		pQueue.add(frontier);
	}
	
	public boolean isFrontierPresent(String name) {
		return checkFrontiers.get(name) != null;
	}
	
	public Frontier getFrontierByKey(String name) {
		return checkFrontiers.get(name);
	}
	
	public Frontier getFrontierFromPQueueIfPresent(String name) {
		Iterator<Frontier> it = pQueue.iterator();
		
		while(it.hasNext()) {
			Frontier frontier = it.next();
			if(frontier.name.equals(name)) {
				return frontier;
			}
		}
		
		return null;
	}
	
	public void updateFrontierByKey(
			Frontier existingFrontier, 
			Double updatedDistance, 
			String updatedParent,
			Double distanceFromParent,
			Double totalCost
			) {
		existingFrontier.distanceFromSource = updatedDistance;
		existingFrontier.parentNode = updatedParent;
		existingFrontier.distanceFromParent = distanceFromParent;
		existingFrontier.totalCost = totalCost;
	}
       
}
