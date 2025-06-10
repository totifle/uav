package ch.totifle.uav.drivers.BNO055;

import com.pi4j.io.i2c.I2C;

import ch.totifle.uav.Logger;
import ch.totifle.uav.Uav;

public class BNO055 {

    public static byte ACC_G_RANGE_2 = (byte)(0b00);
    public static byte ACC_G_RANGE_4 = (byte)(0b01);
    public static byte ACC_G_RANGE_8 = (byte)(0b10);
    public static byte ACC_G_RANGE_16 = (byte)(0b11);
    
    private final I2C device; 

    private boolean onPage0;


    public BNO055(String id, int addr){
        this.onPage0 = true;

        device = Uav.i2c.newDevice(id, addr);
    }

    public void init(){

        Logger.log("Initialising BNO05", Logger.Type.INFO);

        changeMode(OPR_MODE.CONFIG_MODE);

        device.writeRegister(Page0.SYS_TRIGGER, 0b00100000);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        Logger.log("BNO055 sys stat: " + Integer.toBinaryString(device.readRegister(Page0.SYS_STAT)), Logger.Type.INFO);
        Logger.log("BNO055 sys error: " + Integer.toBinaryString(device.readRegister(Page0.SYS_ERROR)), Logger.Type.INFO);
        

        changeMode(OPR_MODE.NDOF);

    }

    public float[] getQuaterions(){

        int wLSB = device.readRegister(Page0.QUA_DATA_W_LSB);
        int wMSB = device.readRegister(Page0.QUA_DATA_W_MSB);
        int xLSB = device.readRegister(Page0.QUA_DATA_X_LSB);
        int xMSB = device.readRegister(Page0.QUA_DATA_X_MSB);
        int yLSB = device.readRegister(Page0.QUA_DATA_Y_LSB);
        int yMSB = device.readRegister(Page0.QUA_DATA_Y_MSB);
        int zLSB = device.readRegister(Page0.QUA_DATA_Z_LSB);
        int zMSB = device.readRegister(Page0.QUA_DATA_Z_MSB);

        float w = ((short)(wLSB | (wMSB<<8)))/16384.0f;
        float x = ((short)(xLSB | (xMSB<<8)))/16384.0f;
        float y = ((short)(yLSB | (yMSB<<8)))/16384.0f;
        float z = ((short)(zLSB | (zMSB<<8)))/16384.0f;
        
        return new float[]{w,x,y,z};

    }

    public float[] getEuler(){

        byte[] buffer = device.readRegisterNBytes(Page0.EUL_HEADING_LSB, 0, 6);

        int headingLSB = Byte.toUnsignedInt(buffer[0]);
        int headingMSB = Byte.toUnsignedInt(buffer[1]);

        int rollLSB = Byte.toUnsignedInt(buffer[2]);
        int rollMSB = Byte.toUnsignedInt(buffer[3]);

        int pitchLSB = Byte.toUnsignedInt(buffer[4]);
        int pitchMSB = Byte.toUnsignedInt(buffer[5]);        

        float heading = (headingLSB | headingMSB<<8)/16.0f;
        float roll = ((short)(rollLSB | (rollMSB<<8)))/16.0f;
        float pitch = ((short)(pitchLSB | (pitchMSB<<8)))/16.0f;

        return new float[]{roll, pitch, heading};
    }

    public String twoByteToString(int msb, int lsb){

        String outMSB = "00000000" + Integer.toBinaryString(msb);
        String outLSB = "00000000" + Integer.toBinaryString(lsb);

        if(!outMSB.equals("00000000")){
            outMSB = outMSB.substring(outMSB.length()-8);
        }
        if(!outLSB.equals("00000000")){
            outLSB = outLSB.substring(outLSB.length()-8);
        }

        return outMSB + " " + outLSB;

    }


    public void changeMode(byte mode){

        if(!onPage0){
            changePage(false);
        }
        device.writeRegister(Page0.OP_MODE, mode);
    }

    public void changePage(boolean page1){
        this.onPage0 = !page1;

        device.writeRegister(Page0.PAGE_ID, (byte)(page1 ? 1:0));
    }

    public void stop() {
        device.close();
        Logger.log("BNO055 stopped", Logger.Type.INFO);
    }
}
