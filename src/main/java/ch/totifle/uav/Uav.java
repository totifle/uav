package ch.totifle.uav;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import ch.totifle.uav.drivers.DriverI2C;
import ch.totifle.uav.drivers.DriverSerial;
import ch.totifle.uav.drivers.PCA9685.PCA9685;
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

    public static Pilot pilot;
    public static Thread pilotThread;

    public static boolean running = true;

    public static Servo yaw, pitch, roll, throttle;

    public static void main( String[] args )
    {
        pi4j = Pi4J.newAutoContext();

        i2c = new DriverI2C();

        serial = new DriverSerial();

        servoHat = new PCA9685("servo", 0x40);

        pilot = new Pilot();
        //servo = new NG90(45, -45, 45);

        try {
            init();
        } catch (Exception e) {
            System.out.println("something went horibly wrong");
            e.printStackTrace();
        }

    }

    public static void init() throws Exception{

        JSONParser parser = new JSONParser();

        configs = (JSONObject) parser.parse(new FileReader("./config.json"));

        roll = servoFromConfig("roll");
        pitch = servoFromConfig("pitch");
        yaw = servoFromConfig("yaw");
        throttle = servoFromConfig("throttle");

        serial.init();

        
        serialThread = new Thread(serial);
        serialThread.start();

        servoHat.init();

        pilot.init();

        pilotThread = new Thread(pilot);
        pilotThread.start();

        while (running) {
            tick();
            Thread.sleep(20);
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
                
                System.out.printf("attach new servo at channel %d. Trim: %d | lep: %d | hep: %d\n", channel, trim, lowEP, hithEP);
                return new NG90(trim, lowEP, hithEP, channel);
        }

    }

    public static void tick(){

        if(!pilot.isUsable()) return;        

        System.out.println("new roll: "  + roll.getPosition() + " from ch val: " + serial.getChannels()[0]);

        servoHat.sendPosition(roll.getAsPWM(), roll.getChannel());
        servoHat.sendPosition(pitch.getAsPWM(), pitch.getChannel());
        servoHat.sendPosition(yaw.getAsPWM(), yaw.getChannel());
        servoHat.sendPosition(throttle.getAsPWM(), throttle.getChannel());        
        
    }


    public static float map(float val, float lower, float upper, float min, float max){
        float range_in = upper-lower;
        float range_out = max-min;

        return ((((val-lower)/range_in)*range_out)+min);
    }


}
