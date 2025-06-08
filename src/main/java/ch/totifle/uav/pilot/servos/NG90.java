package ch.totifle.uav.pilot.servos;

import ch.totifle.uav.Uav;

public class NG90 implements Servo{

    private final static int PWM_MIN = 0;
    private final static int PWM_MAX = 1000;
    private final static int DEG_RANGE = 180;
    private int trim;
    private final static int min = -DEG_RANGE/2;
    private final static int max = DEG_RANGE/2;
    private int lowEndstop, highEndstop;
    private int pos;
    private int channel;

    public NG90(int ch){
        trim = 0; 
        pos = 0;

        lowEndstop = -45;
        highEndstop = 45;
        this.channel = ch;
    }

    public NG90(int trim, int lowEndstop, int highEndstop, int ch){
        this.trim = trim; 
        pos = 0;

        this.lowEndstop = lowEndstop;
        this.highEndstop = highEndstop;
        this.channel = ch;
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
    public void mapPosition(float position, float lower, float upper) {
        pos = Math.round(Uav.map(position, lower, upper, lowEndstop, highEndstop));
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
        return Math.round(Uav.map(pos+trim, min, max, PWM_MIN, PWM_MAX));
    }

    @Override
    public int getChannel(){
        return this.channel;
    }

   
    
}
