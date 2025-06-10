package ch.totifle.uav.drivers.BNO055;

public final class OPR_MODE {
    
    public static final byte CONFIG_MODE = ((byte)0b0000);
    public static final byte ACC_ONLY = ((byte)0b0001);
    public static final byte MAG_ONLY = ((byte)0b0001);
    public static final byte GYRO_ONLY = ((byte)0b0011);
    public static final byte ACC_MAG = ((byte)0b0100);
    public static final byte ACC_GYRO = ((byte)0b0101);
    public static final byte MAG_GYRO = ((byte)0b0110);
    public static final byte AMG = ((byte)0b0111);
    public static final byte IMU = ((byte)0b1000);
    public static final byte COMPASS = ((byte)0b1001);
    public static final byte M4G = ((byte)0b1010);
    public static final byte NDOF_FMC_OFF = ((byte)0b1011);
    public static final byte NDOF = ((byte)0b1100);

}
