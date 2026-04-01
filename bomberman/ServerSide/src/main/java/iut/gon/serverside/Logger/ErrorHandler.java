package iut.gon.serverside.Logger;

import java.io.FileWriter;
import java.io.IOException;

public class ErrorHandler {

    public ErrorHandler(){}

    public void handle(String s){
        System.out.println("handle");
        try(FileWriter writer= new FileWriter("logs.txt", true)){
            writer.write(s + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
