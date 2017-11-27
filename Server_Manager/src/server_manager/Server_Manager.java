package server_manager;

import java.io.IOException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;


/**
 *
 * @author Florian
 */
public class Server_Manager
{
    public static void main(String[] args)
    {
        try 
        {
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            
            Snmp snmp = new Snmp(transport);
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("2326ZAMD"));
            Address targetAddress = GenericAddress.parse("udp:10.59.22.57/161");
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(1500);
            target.setVersion(SnmpConstants.version1);
            
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(new int[] {1,3,6,1,2,1,1,4,0})));
            pdu.add(new VariableBinding(new OID(new int[] {1,3,6,1,2,1,1,2})));
            pdu.setType(PDU.GETNEXT);
            
            SnmpListener listener = new SnmpListener(snmp);
            snmp.send(pdu, target, null, listener);
            
            synchronized(snmp)
            {
                snmp.wait();
            }
        } 
        catch (IOException | InterruptedException ex) 
        {
            System.out.println("Exception : " + ex);
        }
    }
}
