package ch.totifle.uav;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import ch.totifle.uav.drivers.DriverI2C;
import ch.totifle.uav.drivers.DriverSerial;
import ch.totifle.uav.drivers.PCA9685.PCA9685;
import ch.totifle.uav.drivers.lcd.LCD;
import ch.totifle.uav.pilot.servos.NG90;
import ch.totifle.uav.pilot.servos.Servo;

public class Uav 
{

    public static Context pi4j;
    public static DriverI2C i2c;
    public static DriverSerial serial;
    public static Thread serialThread;
    public static LCD lcd;
    public static PCA9685 servoHat;

    public static Servo servo;
    public static void main( String[] args )
    {
        pi4j = Pi4J.newAutoContext();

        i2c = new DriverI2C();

        serial = new DriverSerial();

        lcd = new LCD();

        servoHat = new PCA9685("servo", 0x40);

        servo = new NG90();

        try {
            init();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void init() throws Exception{

        serial.init();

        
        serialThread = new Thread(serial);
        serialThread.start();
        
        lcd.init();


       
        lcd.setDisplay(false);
        lcd.writeLine("hello, world", 0, 0);
        lcd.setDisplay(true);
        Thread.sleep(2000);

        servoHat.init();

        while (true) {
            //lcd.shiftDisplay(false);
            tick();
        }
    }

    public static void tick(){

        int deg = map(serial.getChannels()[0], 1000,2000, -45, 45);
        servo.mapPosition(serial.getChannels()[0], 1000,2000);
        System.out.println(deg);
        System.out.println(serial.getChannels()[0] + " " + (serial.getChannels()[4]-1000) + "->" + (serial.getChannels()[0] - (serial.getChannels()[4]-1000)));
        servoHat.sendPosition(servo.getAsPWM(), 0);

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
        }
    }

    public static void lcd_tick(){
        lcd.clear();
        lcd.home();
        lcd.writeLine("thr: " + serial.getChannels()[2], 0, 0);
        try {
            Thread.sleep(21);
        } catch (InterruptedException e) {
        }
    }

    public static int map(int val, int lower, int upper, int min, int max){
        float range_in = upper-lower;
        float range_out = max-min;

        return Math.round((((val-lower)/range_in)*range_out)+min);
    }


}
