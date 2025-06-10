package ch.totifle.uav;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import ch.totifle.uav.drivers.DriverI2C;
import ch.totifle.uav.drivers.DriverSerial;
import ch.totifle.uav.drivers.BNO055.BNO055;
import ch.totifle.uav.drivers.PA1010D.PA1010D;
import ch.totifle.uav.drivers.PCA9685.PCA9685;
import ch.totifle.uav.gui.Gui;
import ch.totifle.uav.pilot.Pilot;
import ch.totifle.uav.pilot.servos.NG90;
import ch.totifle.uav.pilot.servos.Servo;


public class Uav 
{

    public static JSONObject configs;

    public static Context pi4j;
    public static DriverI2C i2c;

    public static DriverSerial serial;
    public static Thread serialThread;

    public static PCA9685 servoHat;
    public static BNO055 imu;
    public static PA1010D gps;

    public static Pilot pilot;
    public static Thread pilotThread;

    public static boolean running = true;

    public static Servo yaw, pitch, roll, throttle;

    public static Gui gui;

    public static void main( String[] args )
    {
        pi4j = Pi4J.newAutoContext();
        
        i2c = new DriverI2C();

        serial = new DriverSerial();

        servoHat = new PCA9685("servo", 0x40);

        imu = new BNO055("IMU", 0x28);

        gps = new PA1010D("GPS", 0x10);

        pilot = new Pilot();

        gui = new Gui();

        init();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Logger.log("Insomnia", Logger.Type.WARNING);
        }

        run();
        

        

    }

    public static void init(){

        
        Thread.currentThread().setName("main");
        JSONParser parser = new JSONParser();

        try {
            configs = (JSONObject) parser.parse(new FileReader("./config.json"));

            roll = servoFromConfig("roll");
            pitch = servoFromConfig("pitch");
            yaw = servoFromConfig("yaw");
            throttle = servoFromConfig("throttle");
        } catch (Exception e) {
            Logger.log("Failed to attach servos. Config file not found?", Logger.Type.ERROR);
        }

        try{
            gps.init();
        }catch(Exception e){

        }

        serial.init();
        Logger.init();

        
        

        try {
            servoHat.init();
        } catch (InterruptedException e) {
            Logger.log("Fail to init servo hat", Logger.Type.ERROR);
        }

        try{
            imu.init();
        }catch(Exception e){
            
        }
        

        try{
           pilot.init();
        }catch(Exception e){
            
        }
        

       

        
    }

    public static void run() {

        serialThread = new Thread(serial);
        serialThread.setName("serial Thread");
        serialThread.start();

        pilotThread = new Thread(pilot);
        pilotThread.setName("pilot thread");
        pilotThread.start();

        Thread gpsThread = new Thread(gps);
        gpsThread.setName("gps thread");
        gpsThread.start();

        while (running) {
            tick();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Logger.log("Insomnia", Logger.Type.WARNING);
            }
        }
    }

    private static Servo servoFromConfig(String name) {
        
        JSONObject servoData = (JSONObject) ((JSONObject) configs.get("channels")).get(name);

        
        switch ((String) servoData.get("type")) {
            case "NG90":
            default:
                int trim =  Long.valueOf((long)servoData.get("trim")).intValue();
                int lowEP = Long.valueOf((long)servoData.get("low_endpoint")).intValue();
                int hithEP = Long.valueOf((long)servoData.get("high_endpoint")).intValue();
                int channel = Long.valueOf((long)servoData.get("channel")).intValue();
                
                Logger.log("attach new servo at channel " + channel + ". Trim: " + trim + " | lep: " + lowEP + " | hep: " + hithEP, Logger.Type.INFO);
                return new NG90(trim, lowEP, hithEP, channel);
        }

    }

    public static void tick(){        

        if(pilot.isUsable()){        

            servoHat.sendPosition(roll.getAsPWM(), roll.getChannel());
            servoHat.sendPosition(pitch.getAsPWM(), pitch.getChannel());
            servoHat.sendPosition(yaw.getAsPWM(), yaw.getChannel());
            servoHat.sendPosition(throttle.getAsPWM(), throttle.getChannel());  

        }
        gui.draw();

        Logger.tick();
    }


    public static float map(float val, float lower, float upper, float min, float max){
        float range_in = upper-lower;
        float range_out = max-min;

        return ((((val-lower)/range_in)*range_out)+min);
    }

    public static void stop(){

        running = false;

        
        
        servoHat.stop();
        imu.stop();
        gps.stop();
        pilot.stop();
        gui.end();
        i2c.stop();
        serial.stop();
        
    }

}
