package AdvProgrammingTopics;
import java.util.*;
import java.awt.Point;

// Graphics Libraries
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JFrame;


public class EvolutionSim {
   private int worldSize;
   private int numOrganisms;
   private int maxFood;
   private int dayLength;

   private List<Organism> organisms = new ArrayList<>();
   private WorldNode[][] world;
   private int time;
   private Random rand;
   
   public EvolutionSim(int worldSize, int numOrganisms, int numShelters, int maxFood, int dayLength) {
      rand = new Random();
      this.worldSize = worldSize;
      this.numOrganisms = numOrganisms;
      this.numShelters = numShelters;
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

      // spawnShelters(numShelters);
      
   }
   
   public static void main(String[] args) {
      System.out.println("RUNNING CODE...");
      int worldSize = 100;
      int numOrganisms = 1;
      int numShelters = 1;
      int maxFood = 1;
      int dayLength = 1;
      EvolutionSim sim = new EvolutionSim(worldSize, numOrganisms, numShelters, maxFood, dayLength);
      Drawing d = sim.new Drawing();

      // Scanner reader = new Scanner(System.in);
      // System.out.println("---------------------------");
      // System.out.println("How big do you want the world to be?");
      // int worldSize = reader.nextInt();
      // System.out.println("How many organisms do you want in the world?");
      // int numOrganisms = reader.nextInt();
      // System.out.println("How many shelters do you want in the world?");
      // int numShelters = reader.nextInt();
      // System.out.println("How much food do you want in the world?");
      // int maxFood = reader.nextInt();
      // System.out.println("How long do you want each day to last?");
      // int dayLength = reader.nextInt();
      // reader.close();
      

      // sim.runDay();

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

   

   
   
   

   private class Drawing extends Canvas {
      private int WINDOWSIZE = 700;
      public Drawing() {
         JFrame frame = new JFrame("My Drawing");
         Canvas canvas = new EvolutionSim.Drawing();
         canvas.setSize(WINDOWSIZE, WINDOWSIZE);
         frame.add(canvas);
         frame.pack();
         frame.setVisible(true);
      }
  
      public void paint(Graphics g) {
         System.out.println("PAINT RUNNING");
         int tileSize = WINDOWSIZE/worldSize;
         g.drawRect(100, 100, 100, 100);
         for (int r = 0; r < worldSize; r += tileSize) {
            for (int c = 0; c < worldSize; c += tileSize) {
               if (g.getColor() == Color.LIGHT_GRAY) {
                  g.setColor(Color.DARK_GRAY);
               } else {
                  g.setColor(Color.LIGHT_GRAY);
               }

               g.drawRect(r, c, tileSize, tileSize);
            }
         }
      }
  }
  
   
}