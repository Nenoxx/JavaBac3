package server_manager;

import java.util.Vector;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

/**
 *
 * @author nenoxx
 */
public class SnmpListener implements ResponseListener
{
    private Snmp snmpManager;
    private DefaultTableModel dtm;
    private boolean noeud = true;
    private String OID;
    private boolean set = false;
    private JTextField OIDText;
    
    public SnmpListener (Snmp s, DefaultTableModel d)
    { 
        snmpManager = s; 
        dtm = d;
    }
    
    public void LinkOIDField(JTextField t){
        OIDText = t;
    }
    
    public void onResponse(ResponseEvent event)
    {
         ((Snmp)event.getSource()).cancel(event.getRequest(), this);

        //Récupération de la réponse et affichage de celle-ci
        PDU rep = event.getResponse();

        if(rep.getErrorStatusText().equals("Success"))
        {
            VariableBinding vb = rep.get(0);
            Variable value = vb.getVariable();

            Vector v = new Vector();
            v.add(vb.getOid());
            v.add(value.toString());
            v.add(value.getSyntaxString());
            v.add(event.getPeerAddress());
            dtm.addRow(v);

            if(value.getSyntaxString().equals("OCTET STRING"))
                set = true;

            noeud = true;
            OID = "." + vb.getOid();
            OIDText.setText(OID);
        }
        else
        {
            set = false;
            noeud = false;
        }

        synchronized(snmpManager)
        {
            snmpManager.notify();
        }
    }
    
    public boolean getNoeud()
    {
        return noeud;
    }
    
    public boolean getSet()
    {
        return set;
    }
    
    public String getOID()
    {
        return OID;
    }
}