package ch.totifle.uav;

import java.io.IOException;
import java.util.Scanner;

public class Logger {

    private static Scanner sc;

    public enum Type{
        INFO, 
        WARNING,
        ERROR,
        CRITICAL;
    }

    public static void init(){
        Logger.sc = new Scanner(System.in);
    }

    public static void tick(){

        try {
            if(System.in.available()>0){
                String command = sc.nextLine();

                switch (command) {
                    case "stop":
                        Uav.stop(); 
                        break;
                
                    default:
                        break;
                }

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void log(String msg, Type type){

        switch (type) {
            case INFO:
                System.out.println("[" + Thread.currentThread().getName() + "]" + "[\u001b[1;94mINFO\u001b[0m]" + msg);    
                return;
            case WARNING:
                System.out.println("[" + Thread.currentThread().getName() + "]" + "[\u001b[1;93mWARNING\u001b[0m]" + msg);
                return;
            case ERROR:
                System.out.println("[" + Thread.currentThread().getName() + "]" + "[\u001b[1;95mERROR\u001b[0m]" + msg);    
                return;
            case CRITICAL:
                System.out.println("[" + Thread.currentThread().getName() + "]" + "[\u001b[1;91mCRITICAL\u001b[0m]" + msg);    
                return;
        }
    }
}
