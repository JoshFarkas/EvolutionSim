import java.util.*;
import java.awt.Point;

public class EvolutionSim {
   private int worldSize;
   private int numOrganisms;
   private int maxFood;
   private int dayLength;

   private List<Organism> organisms;
   private List<Organism> newOrgs;

   private WorldNode[][] world;
   private int time;
   private Random rand;
   // private String visionOutput;
   private int simSpeed;

   private List<Double> visionAverages;
   private List<Double> reproAverages;

   private static Scanner readInput = new Scanner(System.in);

   
   public EvolutionSim (int worldSize, int numOrganisms, int maxFood, int dayLength, int simSpeed) {
      rand = new Random();
      organisms = new LinkedList<>();
      newOrgs = new ArrayList<>();
      // this.worldSize = worldSize;
      this.numOrganisms = numOrganisms;
      this.maxFood = maxFood;
      this.dayLength = dayLength;
      this.worldSize = worldSize;
      world = new WorldNode[worldSize][worldSize];
      time = 0;
      // visionOutput = "";
      this.simSpeed = simSpeed;

      visionAverages = new ArrayList<>();
      reproAverages = new ArrayList<>();

      // Initialize World
      for (int y = 0; y < worldSize; y++) {
         for (int x = 0; x < worldSize; x++) {
            world[y][x] = new WorldNode(false);
         }
      }
   }
   
   // Initialize and start simulation
   public static void main(String[] args) {

      System.out.println("\n\n"); // Spacer Lines
      System.out.println("RUNNING CODE...\n");
      EvolutionSim sim;
      if (askBool("Custom Sim?")) {
         sim = new EvolutionSim(askInt("World Size (20):"),
                                askInt("Starting Organisms (10):"), 
                                askInt("Amount of Food (60):"),
                                askInt("Number of Ticks Per Day (20):"), 
                                askInt("Sim Speed (0ms):"));
      } else {
         sim = new EvolutionSim(20, 10, 60, 20, 0);

      }
      sim.initOrganisms(sim.numOrganisms);
      sim.start(askInt("How many days do you want to simulate?"));
      
      readInput.close();

   }

   // pre: takes a string q which will be asked to the user
   // post: returns a String response
   private static String ask(String q) {
      System.out.println(q);
      return readInput.nextLine().toLowerCase();
   }

   // pre: takes a string q which will be asked to the user
   // post: returns true if the user responds y, false otherwise
   private static boolean askBool(String q) {
      return ask(q + " (y/n)").equals("y");
   } 

   // pre: takes a string q which will be asked to the user
   // post: returns an integer response, returns 0 if respose is not an int
   private static int askInt(String q) {
      String s = ask(q);
      int n;
      try {
         n = Integer.parseInt(s);
      }
      catch (NumberFormatException e) {
         n = 0;
      }
      return n;
   }
   
   // pre: n = number of organisms to be initialized
   // post: creates n new organisms with random traits and adds them to the world
   public void initOrganisms(int n) {
      for (int i = 0; i < n; i++) {
         Organism o = new Organism(rand.nextInt(worldSize), rand.nextInt(worldSize), rand.nextInt(5), rand.nextInt(1, 4));
         organisms.add(o);
         world[o.y][o.x].addOrganism(o);
      }
   }

   // pre: count = number of food to spawn
   // post: spawns food in the world at random positions
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
   
   // post: removes all food from the world
   public void removeAllFood() {
      for (int i = 0; i < worldSize; i++) {
         for (int j = 0; j < worldSize; j++) {
            world[i][j].removeFood();
         }
      }
   }

   // pre: days = number of days to run the simulation
   // post: runs the entire simulation
   public void start(int days) {
      int qr = 0; // Number of days to quick run
      boolean displayTicks = true;
      for (int day = 0; day < days; day++) {
         if (qr == 0) {
            // if (askBool("Selection Event?")) {
            //    int f = askInt("How much food should be removed?");
            // }
            displayTicks = true;
            if (askBool("Quick run days?")) {
               qr = askInt("How many days to quick run?");
               if (askBool("Display Each Tick?")) {
                  displayTicks = false;
               }
            }
         }
         if (organisms.size() == 0) {
            System.out.println("All Organisms Died");
            endSim();
            break;
         }
         if (qr > 0) {
            runDay(day, simSpeed, false, displayTicks);
            qr--;
         } else {
            runDay(day, 0, true, true);
         }
      }
      endSim();
   }

   // pause thread (code) for ms milliseconds
   private void pause(int ms) {
      try {
         Thread.sleep(ms);
      } catch (InterruptedException e) {
         System.err.format("IOException: %s%n", e);
      }
   }

   // pre: day -> current day number, ms -> time to delay per tick, 
   //      manual -> if the user should manually progress through the day,
   //      displayTicks -> whether each tick should be displayed
   // post: simulate full day, ticks many times
   public void runDay(int day, int ms, boolean manual, boolean displayTicks) {
      System.out.println("Day " + day);
      spawnFood(maxFood);

      for (time = 0; time < dayLength; time++) {
         tick(displayTicks);
         if (manual) {
            ask("Press ENTER to continue");
         } else {
            pause(ms);
         }
      }
      if (!displayTicks) {
         display();
      }
      endDay(day);
   }
   
   // pre: display -> whether or not to display
   // post: tick all organisms on the world to do their next action then display
   public void tick(boolean display) {
      for (Organism o : organisms) {
         o.tick();
      }
      if (display) {
         display();
      }
      // visionOutput = "";
   }

   // pre: takes in day number for display purposes
   // post: runs all organisms endDay() method and stores data
   public void endDay(int day) {
      System.out.println("END OF DAY " + (day + 1) + "\n");
      removeAllFood();
      int numOrgs = organisms.size();
      Iterator<Organism> itr = organisms.iterator();
      double vTotal = 0;
      double rTotal = 0;
      while(itr.hasNext()) {

         Organism o = itr.next();
         // Update stats
         vTotal += o.vision;
         rTotal += o.reproduction;
         o.endDay(itr);
      }
      visionAverages.add(vTotal/numOrgs);
      reproAverages.add(rTotal/numOrgs);
      for (Organism n : newOrgs) {
         organisms.add(n);
      }
      newOrgs.clear();
   }

   // ends simulation
   public void endSim() {
      graphData();
      System.out.println("SIM ENDED");
   }

   // Displays the world, and all organism's information
   public void display() {
      System.out.println();
      System.out.println("Tick Number: " + time);
      System.out.print("    ");
      for (int i = 0; i < worldSize; i++) {
         System.out.print((i / 10 > 0 ? i / 10 : " ") + " ");
      }
      System.out.println();
      System.out.print("    ");
      for (int i = 0; i < worldSize; i++) {
         System.out.print(i % 10 + " ");
      }
      System.out.println();

      for (int i = 0; i < worldSize; i++) { // Row
         System.out.print((i < 10 ? "  " : " ") + i + " "); // Styling for double digits
         for (int j = 0; j < worldSize; j++) { // Col
            if (world[i][j].hasOrganism()) {
               // Print display char of some organism in the tile
               System.out.print(world[i][j].organisms.get(0).symbol);
            } else if (world[i][j].hasFood) {
               System.out.print( "#");
            } else {
               System.out.print("-");
            }
            System.out.print(" ");
         }
         System.out.println();
      }
      System.out.println();
      for (Organism o : organisms) {
         System.out.println(o.data());
      }
      // System.out.println("\nVISION:");
      // System.out.println(visionOutput);
   }

   // displays avg vision and reproduction over time
   public void graphData() {
      ask("Press ENTER to see data");
      for (int i = 0; i < visionAverages.size(); i++) {
         // Long lines just limit the decimal to 3 places
         System.out.println("Day " + i 
                           + " | Vision: " + visionAverages.get(i).toString().substring(0, Math.min(visionAverages.get(i).toString().length(), 4))
                           + " | Reproduction: " + visionAverages.get(i).toString().substring(0, Math.min(visionAverages.get(i).toString().length(), 4);
      }
   }





   // ORGANISM CLASS
   private class Organism {
      private static int nameCounter = 0;
      private static Queue<Character> freeSymbols = new LinkedList<>();
      private static final int VARIANCE = 15; // How different offspring will be from this creature
      private char symbol; // ASCII symbol used to display
      private int food; // current food found
      private int age;

   
      // Traits:
      private int hunger; // How much food is needed each day to survive
      private int vision; // Vision range in tiles
      private int reproduction; // How much excess food is needed to reproduce, but also increases odds
   
      // Location:
      private int x;
      private int y;
      private WorldNode currNode;
   
      Random rand;
   
      // Initialize values and traits
      public Organism(int x, int y, int vision, int reproduction) {
         // Traits:
         this.vision = vision;
         this.hunger = vision + 1; // 0: 1, 1: 2
         this.reproduction = reproduction;
   
         // Variables:
         this.x = x;
         this.y = y;
         this.food = 0;
         this.currNode = world[y][x];
         this.age = 0;
   
         rand = new Random();
            

         // Chooses symbol for organism
         if (freeSymbols.size() > 0) {
            this.symbol = freeSymbols.remove();
         } else {
            this.symbol = (nameCounter < 26 ? (char) ('a' + nameCounter) : (char) ('A' + nameCounter - 26));
         }    

         nameCounter++;
   
      }
      
      // Create new organism with a chance of mutation
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
         Organism o = new Organism(x, y, Math.max(0, vision + v), Math.max(0, reproduction + r));
         System.out.println("[" + symbol + "] reproduced:");
         System.out.println("Parent: " + data());
         System.out.println("Child:  " + o.data());
         newOrgs.add(o);
         return o;
      }
      
      // returns a point at the closest food, chooses randomly if multiple are equidistant
      private Point findClosestFood() {
         int bestDist = worldSize + 1; // Set to a number higher than possible so any found obj is closer
         List<Point> closestFood = new ArrayList<>();
         for (int a = this.x - vision; a < this.x + vision + 1; a++) {
            // Handles if a is out of bounds
            if (a < 0 || a >= worldSize) {
               continue;
            }
            for (int b = this.y - vision; b < this.y + vision + 1; b++) {
               // Handles if b is out of bounds
               if (b < 0 || b >= worldSize) {
                  continue;
               }
               // System.out.println("[" + symbol + "] checking x: " + a + ", y: " + b);

               if (world[b][a].hasFood) {
                  int dist = Math.max(Math.abs(x - a), Math.abs(y - b));
                  // System.out.println("[" + symbol + "] food spotted at x: " + a + ", y: " + b + " dist: " + dist);

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
         if (closestFood.size() == 0) {
            // visionOutput += "[" + this.symbol + "] " + " NO FOOD FOUND\n";
            return null;
         }
         Point out = closestFood.get(rand.nextInt(closestFood.size()));
         // visionOutput += "[" + this.symbol + "] " + "x: " + out.x + " y: " + out.y + "\n";
         return out;
      }

      // Failed Recursive Method:
      //
      // public Point findClosestFood(Point pos, int v) {
      //    if (v < 0) {
      //       return null;
      //    } else if (world[pos.y][pos.x].hasFood) {
      //       return pos;
      //    } else {
      //       for (int a = -1; a < 2; a++) {
      //          for (int b = -1; b < 2; b++) {
      //             if (!(a == 0 && b == 0)) {
      //                return findClosestFood(new Point(pos.x + a, pos.y + b), v - 1);
      //             }
      //          }
      //       }
      //    }
      // }
      
      // returns useful information about the organism
      public String data() {
         return  "[" + symbol + "] x: " + x + " | y: " + y + " | V: " + vision + " | R: " + reproduction + " | F: " + food + " | A: " + age;
      }
      
      // move towards a point d, if input is null move randomly
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

         // System.out.print(" x:" + x + " y: " + y + " xdir: " + xdir + " ydir: " + ydir);

         currNode.removeOrganism(this);
         x += xdir;
         y += ydir;
         // Clamp between 0 and worldSize
         x = x > worldSize - 1 ? worldSize - 1 : x < 0 ? 0 : x;
         y = y > worldSize - 1 ? worldSize - 1 : y < 0 ? 0 : y;
         // System.out.println(" newX: " + x + " newY: " + y);
         currNode = world[y][x];
         currNode.addOrganism(this);
      }
      
      // move towards food
      public void tick() {
         move(findClosestFood());
         if (currNode.hasFood) {
            currNode.removeFood();
            food++;
         }
         
      }
   
      // die and reproduce
      public void endDay(Iterator<Organism> itr) {
         if (food < hunger) {
            die(itr);
         }
         if (food >= hunger + reproduction) {
            if (rand.nextInt(reproduction + 4) < reproduction) { // r = 0 -> 0 chance, r = 1 -> 1/4, r = 2 -> 2/5...
               reproduce();
               
            }
         }
         age++;
         food = 0;
      }
      
      // Remove this organism from the world and delete it
      public void die(Iterator<Organism> itr) {
         System.out.println("[" + symbol + "] died - Food: " + food);
         freeSymbols.add(symbol);
         currNode.removeOrganism(this);
         itr.remove();
      }
   }



   // Stores data about coordinates in the world
   public class WorldNode {
      public List<Organism> organisms;
      public boolean hasFood;
      
      public WorldNode(boolean hasFood) {
          this(hasFood, new LinkedList<>());
      }
  
      public WorldNode(boolean hasFood, List<Organism> organisms) {
          this.organisms = organisms;
          this.hasFood = hasFood;
      }
      
      // adds food to this node
      public void addFood() {
          hasFood = true;
      }
        
      // removes food from this node
      public void removeFood() {
          hasFood = false;
      }
  
      // returns true if there is no food or organism at this node
      public boolean isEmpty() {
          return (!(hasFood || hasOrganism()));
      }
  
      // adds an organism o to this node
      public void addOrganism(Organism o) {
          organisms.add(o);
      }
  
      // removes an organism o from this node
      public void removeOrganism(Organism o) {
          organisms.remove(o);
          if (organisms.size() == 0) {
          }
      }

      // returns true if there are any organisms at this node
      public boolean hasOrganism() {
         return organisms.size() > 0;
      }
  }
}