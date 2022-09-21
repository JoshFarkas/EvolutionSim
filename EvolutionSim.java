import java.util.*;
import java.awt.Point;

public class EvolutionSim {
   private int worldSize;
   private int numOrganisms;
   private int maxFood;
   private int dayLength;

   private Set<Organism> organisms;
   private WorldNode[][] world;
   private int time;
   private Random rand;
   
   public EvolutionSim (int worldSize, int numOrganisms, int maxFood, int dayLength) {
      rand = new Random();
      organisms = new HashSet<>();
      // this.worldSize = worldSize;
      this.numOrganisms = numOrganisms;
      this.maxFood = maxFood;
      this.dayLength = dayLength;
      this.worldSize = worldSize;
      world = new WorldNode[worldSize][worldSize];
      time = 0;

      // Initialize World
      for (int y = 0; y < worldSize; y++) {
         for (int x = 0; x < worldSize; x++) {
            world[y][x] = new WorldNode(false);
         }
      }
   }
   
   public static void main(String[] args) {
      System.out.println();
      System.out.println();
      System.out.println();
      System.out.println("RUNNING CODE...");

      EvolutionSim sim = new EvolutionSim(20, 10, 60, 20);
      sim.initOrganisms(sim.numOrganisms);
      sim.runSim(10);
      // Drawing d = new Drawing();

   }
   
   public void initOrganisms(int n) {
      for (int i = 0; i < n; i++) {
         Organism o = new Organism(rand.nextInt(worldSize), rand.nextInt(worldSize), rand.nextInt(5), 1);
         organisms.add(o);
         world[o.y][o.x].addOrganism(o);
      }
   }

   
   // Spawn food in the world at random positions
   public void spawnFood(int count) {
      if (count > 0) {
         int x = rand.nextInt(worldSize);
         int y = rand.nextInt(worldSize);
         if (world[y][x].isEmpty()) { // Empty tile
            world[y][x].addFood();
            spawnFood(count - 1);
         } else {                    // Already has food
            spawnFood(count);
         }
      }
   }
   
   public void removeAllFood() {
      for (int i = 0; i < worldSize; i++) {
         for (int j = 0; j < worldSize; j++) {
            world[i][j].removeFood();
         }
      }
   }

   public void runSim(int days) {
      display();
      for (int day = 0; day < days; day++) {
         if (organisms.size() == 0) {
            break;
         }
         runDay(day);
      }
   }


   private void pause(int ms) {
      try {
         Thread.sleep(ms);
      } catch (InterruptedException e) {
         System.err.format("IOException: %s%n", e);
      }
   }

   // Simulate full day
   public void runDay(int day) {
      Scanner readInput = new Scanner(System.in);
      spawnFood(maxFood);
      for (time = 0; time < dayLength; time++) {
         tick();
         String s = readInput.nextLine();
         // pause(1000); // pause for 1s
      }
      readInput.close();
      endDay(day);
   }
   
   // tick all organisms on the world to do their next action
   public void tick() {
      for (Organism o : organisms) {
         o.tick();
      }
      display();
   }

   public void endDay(int day) {
      System.out.println("END OF DAY " + day + "\n");
      removeAllFood();
      Iterator<Organism> itr = organisms.iterator();
      while(itr.hasNext()) {
         itr.next().endDay(itr);
      }
      // for (Organism o : organisms) {
      //    o.endDay();
      // }
   }

   public void display() {
      System.out.println();
      System.out.println();
      System.out.print("    ");
      for (int i = 0; i < worldSize; i++) {
         System.out.print(i % 10 + " ");
      }
      System.out.println();

      for (int i = 0; i < worldSize; i++) { // Row
         System.out.print((i < 10 ? "  " : " ") + i + " ");
         String toPrint = "";
         int orgNum = 1;
         for (int j = 0; j < worldSize; j++) { // Col
            if (world[i][j].hasOrganism()) {
               System.out.print("O");
               for (Organism o : world[i][j].organisms) {
                  toPrint += "[" + orgNum + "] V: " + o.vision + " R: " + o.reproduction + " F: " + o.food + " | ";
                  orgNum++;
               }
            } else if (world[i][j].hasFood) {
               System.out.print( "F");
            } else {
               System.out.print("-");
            }
            System.out.print(" ");
         }
         System.out.println(toPrint);
      }
   }





   // ORGANISM CLASS
   private class Organism {
      private static int nameCounter = 1;
      private static final int VARIANCE = 5; // How different offspring will be from this creature
      private String name;
      private int food; // current food found
      private int age;
   
      // Traits:
      private int hunger; // How much food is needed each day to survive
      private int vision; // Vision range in tiles
      private int reproduction; // How much excess food is needed to reproduce, but also increases odds
   
      // Variables
      private int x;
      private int y;
      private WorldNode currNode;
   
      Random rand;
   
      public Organism(int x, int y, int vision, int reproduction) {
         // Traits:
         this.vision = vision;
         this.hunger = vision / 2 + 1;
         this.reproduction = reproduction;
   
         // Variables:
         this.x = x;
         this.y = y;
         this.food = 0;
         this.age = 0;
         currNode = world[y][x];
   
         rand = new Random();
   
         this.name = "Organism " + nameCounter;
         nameCounter++;
   
      }
   
      public Organism reproduce() {
         int v = 0;
         if (rand.nextInt(100) <= VARIANCE) {
            if (rand.nextInt(2) == 0) {
               v = 1;
            } else {
               v = -1;
            }
         }
   
         int r = 0;
         if (rand.nextInt(100) <= VARIANCE) {
            if (rand.nextInt(2) == 0) {
               r = 1;
            } else {
               r = -1;
            }
         }
         return new Organism(x, y, Math.max(0, vision + v), Math.max(0, reproduction + r));
      }
   
      private Point findClosestFood() {
         int bestDist = worldSize + 1; // Set to a number higher than possible so any found obj is closer
         List < Point > closestFood = new ArrayList < > ();
         for (int a = this.x - vision; a < this.x + vision + 1; a++) {
            if (a >= 0 && a < worldSize) {
               for (int b = this.y - vision; b < this.y + vision; b++) {
                  if (b >= 0 && b < worldSize) {
                     if (world[a][b].hasFood) {
                        int dist = Math.max(a, b);
                        if (dist < bestDist) {
                           bestDist = dist;
                           closestFood.clear();
                        }
                        if (dist == bestDist) {
                           closestFood.add(new Point(a, b));
                        }
                     }
                  }
               }
            }
         }
         if (closestFood.size() == 0) {
            return null;
         }
         Point out = closestFood.get(rand.nextInt(closestFood.size()));
         System.out.print(out);
         return out;
      }
   
      public String printInfo() {
         String output = "Name: " + name + " x: " + x + " y: " + y + " food: " + food + " vision: " + vision;
         System.out.println(output);
         return output;
      }
   
      public void move(Point d) {
         int xdir = 0;
         int ydir = 0;

         if (d == null) { // No destination
            xdir = rand.nextInt(-1, 2);
            ydir = rand.nextInt(-1, 2);
         } else { // Has destination
            if (d.x > x) {
               xdir = 1;
            } else if (d.x < x) {
               xdir = -1;
            }
      
            if (d.y > y) {
               ydir = 1;
            } else if (d.y < y) {
               ydir = -1;
            }
         }

         System.out.print(" x:" + x + " y: " + y + " xdir: " + xdir + " ydir: " + ydir);

         currNode.removeOrganism(this);
         x += xdir;
         y += ydir;
         // Clamp between 0 and worldSize
         x = x > worldSize - 1 ? worldSize - 1 : x < 0 ? 0 : x;
         y = y > worldSize - 1 ? worldSize - 1 : y < 0 ? 0 : y;
         System.out.println(" newX: " + x + " newY: " + y);
         currNode = world[y][x];
         currNode.addOrganism(this);
      }
   
      public void tick() {
         move(findClosestFood());
         if (currNode.hasFood) {
            currNode.removeFood();
            food++;
         }
         
      }
   
      public void endDay(Iterator<Organism> itr) {
         if (food < hunger) {
            die(itr);
         }
         if (food >= hunger + reproduction) {
            if (rand.nextInt(reproduction + 5) < reproduction) { // r = 0 -> 0 chance, r = 1 -> 1/5, r = 2 -> 1/3...
               reproduce();
            }
         }
         food = 0;
         age++;
      }
   
      public void die(Iterator<Organism> itr) {
         currNode.removeOrganism(this);
         itr.remove();
      }
   }



   // WORLDNODE CLASS
   public class WorldNode {
      public Set<Organism> organisms;
      public boolean hasFood;
      
      public WorldNode(boolean hasFood) {
          this(hasFood, new HashSet<>());
      }
  
      public WorldNode(boolean hasFood, Set<Organism> organisms) {
          this.organisms = organisms;
          this.hasFood = hasFood;
      }
        
      public void addFood() {
          hasFood = true;
      }
        
      public void removeFood() {
          hasFood = false;
      }
  
      public boolean isEmpty() {
          return (!(hasFood || hasOrganism()));
      }
  
      public void addOrganism(Organism o) {
          organisms.add(o);
      }
  
      public void removeOrganism(Organism o) {
          organisms.remove(o);
          if (organisms.size() == 0) {
          }
      }

      public boolean hasOrganism() {
         return organisms.size() > 0;
      }
  }
}