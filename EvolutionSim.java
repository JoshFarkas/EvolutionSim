package AdvProgrammingTopics;
import java.util.*;
import java.awt.Point;

public class EvolutionSim {
   public static int worldSize = 10;
   private int numOrganisms;
   private int maxFood;
   private int dayLength;

   private Set<Organism> organisms = new HashSet<>();
   private WorldNode[][] world;
   private int time;
   private Random rand;
   
   public EvolutionSim (int worldSize, int numOrganisms, int maxFood, int dayLength) {
      rand = new Random();
      // this.worldSize = worldSize;
      this.numOrganisms = numOrganisms;
      this.maxFood = maxFood;
      this.dayLength = dayLength;
      world = new WorldNode[worldSize][worldSize];
      time = 0;

      // Initialize World
      for (int y = 0; y < worldSize; y++) {
         for (int x = 0; x < worldSize; x++) {
            world[y][x] = new WorldNode(false, false);
         }
      }
      
   }
   
   public static void main(String[] args) {
      System.out.println("RUNNING CODE...");

      EvolutionSim sim = new EvolutionSim(10, 2, 10, 24);
      Drawing d = new Drawing();

   }
   
   public void initOrganisms(int n) {
      for (int i = 0; i < n; i++) {
         Organism o = new Organism(
                           rand.nextInt(worldSize), // x
                           rand.nextInt(worldSize), // y
                           0,                       // vision
                           1                        // reproduction  
                        );
         world[o.y][o.x].addOrganism(o);
      }
   }

   // Spawn shelters on the world at random positions
   // public void spawnShelters(int count) {
   //    if (worldSize * worldSize < count) {
   //       throw new IllegalArgumentException();
   //    }
   //    if (count > 0) {
   //       int x = rand.nextInt(worldSize);
   //       int y = rand.nextInt(worldSize);
   //       if (world[y][x].isEmpty()) { // Empty tile
   //          world[y][x].addShelter();
   //          spawnShelters(count - 1);
   //       } else {                     // Already has a shelter
   //          spawnShelters(count);
   //       }
   //    }
   // }
   
   // Spawn food in the world at random positions
   public void spawnFood(int count) {
      if (count > 0) {
         int x = rand.nextInt(worldSize);
         int y = rand.nextInt(worldSize);
         if (!world[y][x].isEmpty()) { // Empty tile
            world[y][x].addFood();
            spawnFood(count - 1);
         } else {                    // Already has food
            spawnFood(count);
         }
      }
   }
   
   // Simulate full day
   public void runDay() {
      spawnFood(maxFood);
      for (time = 0; time < dayLength; time++) {
         tick();
      }
      endDay();
   }
   
   // tick all organisms on the world to do their next action
   public void tick() {
      for (Organism o : organisms) {
         o.tick(time);
      }
   }

   public void endDay() {
      for (Organism o : organisms) {
         o.endDay();
      }
   }

   public void display() {
      for (int i = 0; i < worldSize; i++) {

      }
   }

   public void initDisplay() {
      

   }

   // public void writeState() {
   //    try {
   //       PrintStream output = new PrintStream("output.txt");
   //       output.println("[T]");

   //       output.close();

   //    }  catch (FileNotFoundException e) {
   //       e.printStackTrace();
   //    } 
   // }

   

   
   
   

   
  }
  
   
}