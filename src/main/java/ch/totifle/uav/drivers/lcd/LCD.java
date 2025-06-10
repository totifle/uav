package ch.totifle.uav.drivers.lcd;

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;

import ch.totifle.uav.Logger;
import ch.totifle.uav.Uav;

public class LCD {
    
    private I2CConfig i2cConfig;
    private I2C device;

    private boolean backlightStatus;
    private byte displayControle, displayMode;
    private static final int ADDR = 0x3f;

    /*
     * MISC FLAGS
     */
    private static final byte BACKLIGHT = 0x08;
    private static final byte NO_BACKLIGHT = 0x00;
    private static final byte ENABLE = 0b100;

    /*
     * Commands
     */

    private static final byte[] LINES = {(byte) 0x80, (byte) 0xC0, (byte) 0x94, (byte) 0xD4};

    private static final byte LCD_CLEARDISPLAY = 0x01;
    private static final byte LCD_RETURNHOME = 0x02;
    private static final byte LCD_ENTRYMODESET = 0x04;
    private static final byte LCD_DISPLAYCONTROL = 0x08;
    private static final byte LCD_CURSORSHIFT = 0x10;
    private static final byte LCD_FUNCTIONSET = 0x20;
    //private static final byte LCD_SETCGRAMADDR = 0x40;
    //private static final byte LCD_SETDDRAMADDR = (byte) 0x80;

    // flags for display entry mode;
    //private static final byte LCD_ENTRYRIGHT = 0x00;
    private static final byte LCD_ENTRYLEFT = 0x02;
    //private static final byte LCD_ENTRYSHIFTINCREMENT = 0x01;
    private static final byte LCD_ENTRYSHIFTDECREMENT = 0x00;

    // flags for display on/off control;
    private static final byte LCD_DISPLAYON = 0x04;
    private static final byte LCD_CURSORON = 0x02;
    private static final byte LCD_BLINKON = 0x01;

    // flags for display/cursor shift;
    //private static final byte LCD_DISPLAYMOVE = 0x08;
    private static final byte LCD_CURSORMOVE = 0x00;
    private static final byte LCD_MOVERIGHT = 0x04;
    //private static final byte LCD_MOVELEFT = 0x00;

    // flags for function set;
    //private static final byte LCD_8BITMODE = 0x10;
    private static final byte LCD_4BITMODE = 0x00;
    private static final byte LCD_2LINE = 0x08;
    //private static final byte LCD_1LINE = 0x00;
    //private static final byte LCD_5x10DOTS = 0x04;
    private static final byte LCD_5x8DOTS = 0x00;

    // flags for backlight control;
    //private static final byte LCD_BACKLIGHT = 0x08;
    //private static final byte LCD_NOBACKLIGHT = 0x00;




    public LCD(){

        backlightStatus = true;
        displayControle = LCD_DISPLAYON | LCD_CURSORON | LCD_BLINKON;
        displayMode = LCD_ENTRYLEFT | LCD_ENTRYSHIFTDECREMENT;

        i2cConfig = Uav.i2c.createConfig("lcd", ADDR);
        try{
            device = Uav.i2c.getProvider().create(i2cConfig);
        }catch (Exception e){
            Logger.log("Error while creating LCD", Logger.Type.WARNING);
        }

    }

    public void init() throws InterruptedException{

        byte function = LCD_5x8DOTS | LCD_2LINE | LCD_4BITMODE;

        device.write(BACKLIGHT);
        Thread.sleep(1000);

        write( (byte) (0x03<<4));
        Thread.sleep(5);
       
        write( (byte) (0x03<<4));
        Thread.sleep(5);
       
        write( (byte) (0x03<<4));
        Thread.sleep(5);

        write( (byte) (0x02<<4));
        Thread.sleep(1000);
        
        writeCommand((byte)(LCD_FUNCTIONSET | function));

        setDisplay(true);
       
        clear();

        //home();

        sendDisplayMode();

        Thread.sleep(1000);
       
    }

    public void sendDisplayMode(){
        this.writeCommand((byte) (LCD_ENTRYMODESET | displayMode));
    }

    private void writeCommand(byte data){
        write((byte) ((data & 0xF0) | (backlightStatus ? BACKLIGHT : NO_BACKLIGHT)));
        write((byte) ((data<<4 & 0xF0) | (backlightStatus ? BACKLIGHT : NO_BACKLIGHT)));
    }

    private void writeData(char caracter){
        byte data = (byte) caracter;
        write((byte) ((data & 0xF0) | (backlightStatus ? BACKLIGHT : NO_BACKLIGHT) | 0x01));
        write((byte) ((data << 4 & 0xF0) | (backlightStatus ? BACKLIGHT : NO_BACKLIGHT) | 0x01));
    }

    public void writeLine(String text, int line, int align){
        this.writeCommand(LINES[line]);

        for(int i = 0; i < text.length(); i++){
            this.writeData(text.charAt(i));
        }
    }

    public void clear(){
        writeCommand(LCD_CLEARDISPLAY);
    }

    public void home(){
        writeCommand(LCD_RETURNHOME);
    }

    public void setDisplay(boolean status){

        if(status){
            displayControle |= LCD_DISPLAYON;
        }else{
            displayControle &= ~LCD_DISPLAYON;
        }
        
        this.writeCommand((byte)(LCD_DISPLAYCONTROL | displayControle));
    }

    
    public void setBacklight(boolean on){
        backlightStatus = on;
        writeCommand((byte)0x0);
    }
/*
    displayControle = LCD_DISPLAYON | LCD_CURSORON | LCD_BLINKON;
        displayMode = LCD_ENTRYLEFT | LCD_ENTRYSHIFTDECREMENT;*/
    
    public void setCursor(boolean status){
        if(status) {
            displayControle |= LCD_CURSORON;
        }else{
            displayControle &= ~LCD_CURSORON;
        }

        
        this.writeCommand((byte)(LCD_DISPLAYCONTROL | displayControle));
    }
    
    public void setBlink(boolean status){
        if(status) {
            displayControle |= LCD_BLINKON;
        }else{
            displayControle &= ~LCD_BLINKON;
        }

        this.writeCommand((byte)(LCD_DISPLAYCONTROL | displayControle));
    }

    public void shiftDisplay(boolean right){

        byte data = LCD_CURSORMOVE | LCD_CURSORSHIFT;

        if(right){
            data |= LCD_MOVERIGHT;
        }

        writeCommand(data);
    }

    public void write(byte data) {
        try{
        data = (byte) (data | (backlightStatus ? BACKLIGHT : NO_BACKLIGHT));

        device.write(data);
        
        Thread.sleep(5);
        
        device.write((byte)(data | ENABLE));
        
        Thread.sleep(5);
       
        device.write((byte)(data & (~ ENABLE)));
        
        }catch (Exception e){}
    }
}
