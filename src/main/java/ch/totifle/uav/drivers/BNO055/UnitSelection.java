package ch.totifle.uav.drivers.BNO055;

public class UnitSelection {
    public enum Accel{
        MPS2((byte)0),
        MG((byte)1);

        private final byte value;

        Accel(byte val){
            this.value = val;
        }

        public byte getValue(){
            return this.value;
        }
    }

    public enum Angular{
        DPS((byte)0b00),
        RPS((byte)0b10);

        private final byte value;

        Angular(byte val){
            this.value = val;
        }

        public byte getValue(){
            return this.value;
        }
    }

    public enum EulerAngle{
        DEG((byte)0),
        RAD((byte)0b100);

        private final byte value;

        EulerAngle(byte val){
            this.value = val;
        }

        public byte getValue(){
            return this.value;
        }
    }

    public enum Temp{
        C((byte)0),
        F((byte)0b10000);

        private final byte value;

        Temp(byte val){
            this.value = val;
        }

        public byte getValue(){
            return this.value;
        }
    }


    public static byte selectUnit(Accel accel, Angular angular, EulerAngle eulerAngle, Temp temp, boolean reversePitch){

        return (byte)(accel.getValue() | angular.getValue() | eulerAngle.getValue() | temp.getValue() | (reversePitch ? 0b10000000 : 0));
    }
}
