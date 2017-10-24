/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpoolthread;

import java.net.Socket;
import java.util.*;
/**
 *
 * @author Arnaud
 */
public class ListeTaches implements SourceTaches{
    private LinkedList<Socket> listeTaches;
    
    public ListeTaches(){
        listeTaches = new LinkedList();
    }
    
    @Override
    public synchronized Socket getTache() throws InterruptedException{
        System.out.println("getTache avant wait");
        while(!existTaches()){
            wait();
        }
        return listeTaches.remove();
    }
    
    @Override
    public synchronized boolean existTaches(){
        return !listeTaches.isEmpty();
    }
    
    @Override
    public synchronized void recordTache(Socket s){
        listeTaches.addLast(s);
        System.out.println("ListeTaches : tache dans la file");
        notify();
    }
         
}
