package ch.totifle.uav.pilot.servos;

public interface Servo {
    
    int getPosition();
    void setPosition(int position);
    void mapPosition(int position, int lower, int upper);
    int getAsPWM();
    int min();
    int max();
    void trim(int val);

}
