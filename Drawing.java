package AdvProgrammingTopics;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JFrame;


public class Drawing extends Canvas {
    private final int WINDOWSIZE = 700;
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
