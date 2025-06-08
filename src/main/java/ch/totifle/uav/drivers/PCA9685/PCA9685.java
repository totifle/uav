package ch.totifle.uav.drivers.PCA9685;

import com.pi4j.io.i2c.I2C;

import ch.totifle.uav.Uav;

public class PCA9685 {

    private final int ADDR;
    private I2C device;

    private byte currentMode1, currentMode2;
    
    private static final byte PRESCALE = 121;

    public PCA9685(String id, int addr){
        this.ADDR = addr;

        device = Uav.i2c.newDevice(id, addr);
    }

    public void sendPosition(int pos, int channel){
        pos /=5;
        int off = 4000-pos;


        byte[] buffer = new byte[4];

        buffer[0] = (byte)(pos & 0xff);
        buffer[1] = (byte)((pos>>8) & 0x0f);
        buffer[2] = (byte)(off & 0xff);
        buffer[3] = (byte)((off>>8) & 0x0f);

        device.writeRegister(ServoRegisters.CH0_ON_TIME_LSB.getAddress() + channel*4, buffer);

    }

    private void writeMode1(MODE1 mode, boolean state){
        if(state){
            currentMode1 |= mode.getBit();
            System.out.println("new mode 1 : " + Byte.toString(currentMode1));
        }else{
            currentMode1 &= ~mode.getBit();
            
            System.out.println("new mode 1 : " + Byte.toString(currentMode1));
        }
        writeToRegister(ServoRegisters.MODE1, currentMode1);
    }

    private void writeMode2(MODE2 mode, boolean state){
        if(state){
            currentMode2 |= mode.getBit();
            System.out.println("new mode 2 : " + Byte.toString(currentMode2));
        }else{
            currentMode2 &= ~mode.getBit();
            
            System.out.println("new mode 2 : " + Byte.toString(currentMode2));
        }
        writeToRegister(ServoRegisters.MODE2, currentMode1);
    }

    public void reset(){
        int mode1 = device.readRegister(ServoRegisters.MODE1.getAddress());
        System.out.println("Resetting. Mode 1 : " + Integer.toBinaryString(mode1));

        if((mode1 & MODE1.SLEEP.getBit()) != 0){
            writeMode1(MODE1.SLEEP, false);
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
        }
        writeMode1(MODE1.RESTART, true);
    }
    
    
    public void init() throws InterruptedException{
        currentMode1 = (byte)(MODE1.AI.getBit()|MODE1.SUB1.getBit()|MODE1.SUB2.getBit()|MODE1.SUB3.getBit()|MODE1.ALLCALL.getBit());
        currentMode2 = (byte)(MODE2.OUTDRV.getBit()|MODE2.INVRT.getBit());

        writeToRegister(ServoRegisters.MODE1, currentMode1);
        writeToRegister(ServoRegisters.MODE2, currentMode2);

      
        /*
         * write prescale in register
         */
        writeMode1(MODE1.SLEEP, true);          //sleep mode must be on to write prescale
        Thread.sleep(1);
        writeToRegister(ServoRegisters.PRESCALE, PRESCALE);//write prescale
        writeMode1(MODE1.SLEEP, false);  
        
        Thread.sleep(1);
       
        reset();

        Thread.sleep(1);

        System.out.println(Integer.toBinaryString((device.readRegister(ServoRegisters.PRESCALE.getAddress()))));
        System.out.println(Integer.toBinaryString((device.readRegister(ServoRegisters.MODE1.getAddress()))));
        System.out.println(Integer.toBinaryString((device.readRegister(ServoRegisters.MODE2.getAddress()))));

        
        System.out.println();
    }

    
    public void write(byte data) {
        device.write(data);
    }

    
    public void writeToRegister(ServoRegisters register, byte data) {
        device.writeRegister(register.getAddress(), data);
    }

    
    public void writeToRegister(ServoRegisters register, byte[] data) {
        device.writeRegister(register.getAddress(), data);
    }
    
}
