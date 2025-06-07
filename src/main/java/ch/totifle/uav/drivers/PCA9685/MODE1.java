package ch.totifle.uav.drivers.PCA9685;

public enum MODE1 {

    RESTART((byte)0b10000000),
    EXTCLK((byte)0b01000000),
    AI((byte)0b00100000),
    SLEEP((byte)0b00010000),
    SUB1((byte)0b00001000),
    SUB2((byte)0b00000100),
    SUB3((byte)0b00000010),
    ALLCALL((byte)0b00000001);

    private byte bit;  

    private MODE1(byte bit){
        this.bit = bit;
    }

    public byte getBit(){
        return this.bit;
    }
}
