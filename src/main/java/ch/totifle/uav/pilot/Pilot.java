package ch.totifle.uav.pilot;

import ch.totifle.uav.Coordinate;
import ch.totifle.uav.Uav;

public class Pilot implements Runnable {

    private Coordinate ownPos;
    private PilotMode mode;
    private PlaneOrientation orientation;
    private float roll, pitch, yaw, throttle;
    private boolean usable = false;
    //private int[] channels;

    public Pilot(){

    }

    public void init(){

        mode = PilotMode.MANUAL;
    }

    @Override
    public void run() {
        while (Uav.running) {
            
            switch (mode) {
                case MANUAL:
                    fetchDataFromSerial();
                    break;
            
                default:
                    break;
            }

        }

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    } 

    public void fetchDataFromSerial(){

        System.out.println(System.currentTimeMillis() - Uav.serial.getLastRead());

        int[] channels = Uav.serial.getChannels();

        this.roll = Uav.map(channels[0], 1000, 2000, -1, 1);
        this.pitch = Uav.map(channels[1], 1000, 2000, -1, 1);
        this.yaw = Uav.map(channels[3], 1000, 2000, -1, 1);
        this.throttle = Uav.map(channels[2], 1000, 2000, 0, 1);

        Uav.roll.mapPosition(this.roll, -1, 1);
        Uav.pitch.mapPosition(this.pitch, -1, 1);
        Uav.yaw.mapPosition(this.yaw, -1, 1);
        Uav.throttle.mapPosition(this.throttle, 0, 1);

        usable = true;
    }

    public Coordinate getOwnPos() {
        return ownPos;
    }

    public PilotMode getMode() {
        return mode;
    }

    public PlaneOrientation getOrientation() {
        return orientation;
    }

    public float getRoll() {
        return roll;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getThrottle() {
        return throttle;
    }

    public boolean isUsable(){
        return usable;
    }

    

    
}
