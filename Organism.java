package AdvProgrammingTopics;
import java.util.*;
import java.awt.Point;
public class Organism {
   private static int nameCounter = 1;
   private static final int VARIANCE = 5; // How different offspring will be from this creature
   public String name;
   private int food; // current food found

   // Traits:
   public int hunger; // How much food is needed each day to survive
   public int vision; // Vision range in tiles
   public int reproduction; // How much excess food is needed to reproduce, but also increases odds
   // private int md;        // Mate desire 0-100


   // Variables
   public int x;
   public int y;
   private WorldNode currNode;
   private Point dest;
   private boolean hasDest;

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
      currNode = world[y][x];
      this.dest.x = -1;
      this.dest.y = -1;
      this.hasDest = false;

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
      return closestFood.get(rand.nextInt(closestFood.size()));
   }

   public String printInfo() {
      String output = "Name: " + name + " x: " + x + " y: " + y + " food: " + food + " vision: " + vision;
      System.out.println(output);
      return output;
   }

   public void move(Point d) {
      int xdir = 0;
      int ydir = 0;

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

      currNode.removeOrganism(this);
      x += xdir;
      y += ydir;
      currNode = world[y][x];
      currNode.addOrganism(this);

      // if reached destination
      if (x == dest.x && y == dest.y) {
         hasDest = false;
         dest.x = -1;
         dest.y = -1;
      }
   }

   public void tick(int time) {
      move(findClosestFood());
   }

   public void endDay() {
      if (food < hunger) {
         die();
      }
      if (food >= hunger + reproduction) {
         if (rand.nextInt(reproduction + 5) < reproduction) { // r = 0 -> 0 chance, r = 1 -> 1/5, r = 2 -> 1/3...
            
         }
      }
   }

   public void die() {
      currNode.removeOrganism(this);
   }
}