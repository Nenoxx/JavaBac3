/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpoolthread;

import java.net.Socket;
import java.sql.Connection;

/**
 *
 * @author Arnaud
 */
public interface SourceTaches {
    public Socket getTache() throws InterruptedException;
    public boolean existTaches();
    public void recordTache(Socket s);
}
