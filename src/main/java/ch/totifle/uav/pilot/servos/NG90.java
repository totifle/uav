package ch.totifle.uav.pilot.servos;

import ch.totifle.uav.Uav;

public class NG90 implements Servo{

    private final static int PWM_MIN = 0;
    private final static int PWM_MAX = 1000;
    private final static int DEG_RANGE = 180;
    private int trim;
    private int min;
    private int max;
    private int low_endstop, high_endstop;
    private int pos;

    public NG90(){
        trim = 0; 
        min = -DEG_RANGE/2;
        max = DEG_RANGE/2;
        pos = 0;

        low_endstop = -45;
        high_endstop = 45;
    }

    @Override
    public int getPosition() {
        return pos;
    }

    @Override
    public void setPosition(int position) {
        this.pos = position;
    }

     @Override
    public void mapPosition(int position, int lower, int upper) {
        pos = Uav.map(position, lower, upper, low_endstop, high_endstop);
    }

    @Override
    public int min() {
        return min;
    }

    @Override
    public int max() {
        return max;
    }

    @Override
    public void trim(int val) {
        trim = val;
    }

    @Override
    public int getAsPWM() {
        return Uav.map(pos+trim, min, max, PWM_MIN, PWM_MAX);
    }

   
    
}
