package ch.totifle.uav.drivers.PCA9685;


public enum ServoRegisters {


    MODE1(0x00),
    MODE2(0x01),
    SUBADR1(2),
    SUBADR2(3),
    SUBADR3(4),
    ALLCALLADR(5),
    CH0_ON_TIME_LSB(6),
    CH0_ON_TIME_MSB(7),
    CH0_OFF_TIME_LSB(8),
    CH0_OFF_TIME_MSB(9),
    ALL_CH_ON_TIME_LSB (250),
    ALL_CH_ON_TIME_MSB (251),
    ALL_CH_OFF_TIME_LSB(252),
    ALL_CH_OFF_TIME_MSB(253),
    PRESCALE(254),
    TEST_MODE(255);
    

    private final int address; 

    private ServoRegisters(int addr){
        this.address = addr;
    }
    public int getAddress() {
        return this.address;
    }

   

}
