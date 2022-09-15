package AdvProgrammingTopics;
import java.util.*;
public class WorldNode {
    public Set<Organism> organisms = new HashSet<>();
    public boolean hasShelter;
    public boolean hasFood;
    public boolean hasOrganism;
      
    public WorldNode(boolean hasShelter, boolean hasFood) {
        this.organisms = new HashSet<>();
        this.hasShelter = hasShelter;
        this.hasFood = hasFood;
    }
      
    public void addShelter() {
        hasShelter = true;
    }
      
    public void removeShelter() {
        hasShelter = false;
    }
      
    public void addFood() {
        hasFood = true;
    }
      
    public void removeFood() {
        hasFood = false;
    }
      
    public boolean isEmpty() {
        return !(hasFood || hasShelter);
    }

    public void addOrganism(Organism o) {
        organisms.add(o);
    }

    public void removeOrganism(Organism o) {
        organisms.remove(o);
    }
}