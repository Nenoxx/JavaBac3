/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package requetepoolthreads;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;

/**
 *
 * @author Arnaud
 */
public interface Requete {
    public Runnable createRunnable (ObjectOutputStream oos, ObjectInputStream ois, Connection conn , ConsoleServeur cs);
}
