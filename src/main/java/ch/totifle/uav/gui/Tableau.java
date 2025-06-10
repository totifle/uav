package ch.totifle.uav.gui;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import ch.totifle.uav.Logger;
import ch.totifle.uav.Uav;

import java.awt.Color;


public class Tableau extends JPanel{

    public Tableau(){
        this.setDoubleBuffered(true);
    }

    
    @Override
    public Dimension getPreferredSize(){
        return new Dimension(1920, 1000);
    }

    @Override
        protected void paintComponent(Graphics g) {
            int[] size = Uav.gui.getWidthHeight();
            super.paintComponent(g);
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, size[0], size[1]);
            g.setColor(Color.GREEN);

            float[] rotation;
            try{
                rotation = Uav.imu.getEuler();
            }catch (Exception e){
                Logger.log("Cant connect to BNO055", Logger.Type.ERROR);
                return;
            }
            int[] xPoints = new int[4];
            int[] yPoints = new int[4];

            xPoints[0] = 0;
            yPoints[0] = size[1];
            xPoints[1] = 0;
            xPoints[2] = size[0];
            xPoints[3] = size[0];
            yPoints[3] = size[1];

            int centerY = Math.round(Uav.map(rotation[1], -180, 180, 0, size[1]));

            int rollDif = Math.round((float)(Math.tan(Math.toRadians(rotation[0]))*(size[0]/2.0)));
            yPoints[1] = centerY-rollDif;
            yPoints[2] = centerY+rollDif;


            g.fillPolygon(xPoints, yPoints, 4);
        }
    
}
