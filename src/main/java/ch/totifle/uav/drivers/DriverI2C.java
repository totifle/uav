package ch.totifle.uav.drivers;

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

import ch.totifle.uav.Uav;

//0x28 : gyro
//0x3f : LCD
//0x40 : servo
//0x70 : ?
public class DriverI2C {

    private I2CProvider i2cProvider;

    public DriverI2C(){

        i2cProvider = Uav.pi4j.provider("linuxfs-i2c");
        
    }

    public I2CConfig createConfig(String id, int addr){
        return I2C.newConfigBuilder(Uav.pi4j).id(id).bus(1).device(addr).build();
    }

    public I2CProvider getProvider(){
        return i2cProvider;
    }

    public I2C newDevice(String id, int addr){
        return this.i2cProvider.create(createConfig(id, addr));
    }

    
}
