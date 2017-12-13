/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProtocoleLUGAP;

import java.io.*;
import requetepoolthreads.Reponse;

/**
 *
 * @author Arnaud
 */
public class ReponseLUGAP implements Reponse, Serializable {

    public static int OK = 100;
    public static int NOK = 101;
    private static final long serialVersionUID = 124L;
    
    private int codeRetour;
    private String chargeUtile;
    
    public ReponseLUGAP(int c, String charge){
        codeRetour = c;
        chargeUtile = charge;
    }
    
    @Override
    public int getCode(){
        return codeRetour;
    }
    
    public String getChargeUtile(){
        return chargeUtile;
    }
    
    public void setChargeUtile(String charge){
        chargeUtile = charge;
    }   
}
