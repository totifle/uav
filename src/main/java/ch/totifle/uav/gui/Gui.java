package ch.totifle.uav.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import ch.totifle.uav.Logger;
import ch.totifle.uav.Uav;
import javafx.stage.WindowEvent;

public class Gui extends Thread{
    
    private Tableau canvas;
    private int width = 1920;
    private int height = 1000;
    private JFrame frame;


    public Gui(){
        frame = new JFrame("UAV thingamajig");
        canvas = new Tableau();
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

  
        
        canvas.setDoubleBuffered(true);
        frame.add(canvas);

        frame.pack();
        frame.setVisible(true);
        
    }

    public void draw(){
        
        canvas.setDoubleBuffered(true);
        canvas.paintComponent(frame.getGraphics());
        //frame.paintComponents(frame.getGraphics());
        
    }

    public int[] getWidthHeight(){
        return new int[]{frame.getWidth(), frame.getHeight()};
    }

    public void end() {
        frame.dispose();
        this.interrupt();
        Logger.log("GUI stopped", Logger.Type.INFO);
    }
}
