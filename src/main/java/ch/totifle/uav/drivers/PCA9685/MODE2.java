package ch.totifle.uav.drivers.PCA9685;

public enum MODE2 {

    INVRT((byte)0b00010000),       // Invert output logic state
    OCH((byte)0b00001000),         // 0 -> output change on STOP; 1->ACK
    OUTDRV((byte)0b00000100),      // 0 -> output open-drain (1/Ø); 1-> totem pole (1/0)
    OUTNE_MSB((byte)0b00000010),   // https://files.waveshare.com/upload/6/68/PCA96_datasheet.pdf page 15
    OUTNE_LSB((byte)0b00000001); 

    private byte bit;

    private MODE2(byte b){
        this.bit = b;
    }

    public byte getBit(){
        return this.bit;
    }
}
