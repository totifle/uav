package ch.totifle.uav.drivers.PA1010D;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;

import ch.totifle.uav.Logger;
import ch.totifle.uav.Uav;

public class PA1010D implements Runnable{
    
    private I2C device;
    private static final String NO_SEND = "$PSRF150,0*3E\\r\\n";
    private static final String SEND = "$PSRF150,1*3F\\r\\n";


    public PA1010D(String id, int addr){
        device = Uav.i2c.newDevice(id, addr);
    }

    public void init(){

        
        try{
        int lnCount = 0;
        int total = 0;

        while(lnCount<=3){
            int chara = (device.read());
            total++;
            System.out.print((char)chara);
            if(chara == 0xA){
                lnCount++;
            }else{
                lnCount = 0;
            }
        }

        device.write(NO_SEND.toCharArray());

        Logger.log("had to remove: " + total + " bytes from fucking GPS", Logger.Type.INFO);
        }catch (Exception e){
            Logger.log("Can't connect to PA1010D", Logger.Type.ERROR);
        }

    }

    @Override
    public void run() {

        while(Uav.running){
        
            String buffer = "";
            
            int read = 0x0;
            int penultieme = 0x0;
            while(read != 0xA || (penultieme != 0xD)){
                penultieme = read;
                try{
                    read = device.read();
                }catch (Exception e){
                    Logger.log("Failed to read PA1010D", Logger.Type.ERROR);
                }
                buffer += (char)read;
                

            }
                
                
            System.out.println("buffer length from gps: " + buffer.length());
            System.out.print(buffer);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        
        
    }

    public void stop() {
        device.close();
        Logger.log("PA1010D stopped", Logger.Type.INFO);
    }



    
}
