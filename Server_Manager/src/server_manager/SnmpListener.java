package server_manager;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

/**
 *
 * @author Florian
 */
public class SnmpListener implements ResponseListener
{
    private Snmp snmpManager;
    
    public SnmpListener (Snmp s)
    { 
        snmpManager = s; 
    }
    
    /*public void onResponse(ResponseEvent event)
    {
        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
        System.out.println("Réponse reçue (PDU): "+event.getResponse());
        
        synchronized(snmpManager)
        {
        snmpManager.notify();
        }
    }*/
    
    public void onResponse(ResponseEvent event)
    {
        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
        PDU rep = event.getResponse();
        int nValues = rep.size();
        for (int i=0; i<nValues; i++)
        {
            VariableBinding vb = rep.get(i);
            System.out.println(i + ") OID: "+ vb.getOid());
            Variable value = vb.getVariable();
            System.out.println("Value = "+ value.toString());
            System.out.println("Syntax = "+ value.getSyntax());
            System.out.println("SyntaxString = "+ value.getSyntaxString());
        }
        
        synchronized(snmpManager)
        {
            snmpManager.notify();
        }
    }
}