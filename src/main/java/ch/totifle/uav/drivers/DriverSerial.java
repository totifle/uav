package ch.totifle.uav.drivers;

import com.fazecast.jSerialComm.*;

import ch.totifle.uav.Uav;

public class DriverSerial implements Runnable{

    private SerialPort port;
    private int[] channels;

    public DriverSerial(){

        channels = new int[14];

    }

    public void init(){

        port = SerialPort.getCommPort("/dev/ttyS0");

        System.out.println("port open successfully: " + port.openPort());
        
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        port.setBaudRate(115200);
        
    }

    @Override
    public void run() {
        byte[] buffer = new byte[30];
        while(Uav.running){
            buffer = new byte[30];
            
            byte[] commandBuffer = new byte[2];

            while(commandBuffer[0] != 0x40 || commandBuffer[1] != 0x20){
                commandBuffer[1] = commandBuffer[0];
                
                port.readBytes(commandBuffer, 1);
            }

            while(port.bytesAvailable()<buffer.length){}

            port.readBytes(buffer, buffer.length);


            int[] temp_channels = readChannels(buffer);
            if(temp_channels == null){
                System.out.println("No data from channels");
                continue;
            }
            channels = temp_channels;
            
            Uav.pilot.setChannels(temp_channels);


            
        }

        port.closePort();
    }

    private int[] readChannels(byte[] buffer){
        int ch[] = new int[14];
        int lsb, msb;
        int checksum = (0x20+0x40);

        
        for(int j = 0; j<ch.length; j++){

            /*lsb = (short) buffer[j*2];
            msb = (short) (buffer[j*2+1]<<8);
            ch[j] = (short) (lsb+msb); */

            lsb = (Byte.toUnsignedInt(buffer[j*2]));
            msb = (Byte.toUnsignedInt(buffer[j*2+1]));
            checksum +=  lsb + msb;
            int val = lsb+(msb<<8);
            
            ch[j] = val;
            
        }

        checksum +=  (Byte.toUnsignedInt(buffer[buffer.length-2]) + (Byte.toUnsignedInt(buffer[buffer.length-1])<<8));
       
        return checksum == 0xffff ? ch : null;
    }

    public int[] getChannels(){
        return channels;
    }
}
