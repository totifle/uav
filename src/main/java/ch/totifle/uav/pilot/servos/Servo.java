package ch.totifle.uav.pilot.servos;

public interface Servo {
    
    int getPosition();
    void setPosition(int position);
    void mapPosition(float position, float lower, float upper);
    int getAsPWM();
    int min();
    int max();
    void trim(int val);
    int getChannel();

}
