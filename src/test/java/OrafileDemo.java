import edu.gatech.gtri.orafile.Orafile;
import edu.gatech.gtri.orafile.OrafileDict;
import edu.gatech.gtri.orafile.OrafileRenderer;
import edu.gatech.gtri.orafile.OrafileVal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OrafileDemo {

    public static void main(String[] args) throws Exception {

        // The sort of thing you might find in tnsnames.ora
        String tnsFileContent = "TheApplication =\n" +
            "  (ADDRESS = (PROTOCOL = TCP)(HOST = app-server)(PORT = 1521))\n" +
            "  (ADDRESS = (PROTOCOL = TCP)(HOST = app-server)(PORT = 1522))\n" +
            "  (CONNECT_DATA =(SID = banana)(SERVER = dedicated)))";

        // Parse the string.
        OrafileDict tns = Orafile.parse(tnsFileContent);

        System.out.println(tns); /* Output:

        THEAPPLICATION
          ADDRESS
            PROTOCOL: TCP
            HOST: app-server
            PORT: 1521
          ADDRESS
            PROTOCOL: TCP
            HOST: app-server
            PORT: 1522
          CONNECT_DATA
            SID: banana
            SERVER: dedicated
        */

        // Get the entry for "theapplication".
        OrafileVal theapplication = tns.get("theapplication").get(0);

        // Find ADDRESS entries and their HOST, PORT, and SID attributes.
        List<Map<String, String>> values = theapplication
            .findParamAttrs("address", Arrays.asList("host", "port", "sid"));

        System.out.println(values); /* Output:

        [{port=1521, sid=banana, host=app-server}, {port=1522, sid=banana, host=app-server}]
        */

        System.out.println(new OrafileRenderer().renderFile(tns)); /* Output:

        TheApplication =
          (ADDRESS =
            (PROTOCOL = TCP)
            (HOST = app-server)
            (PORT = 1521)
          )
          (ADDRESS =
            (PROTOCOL = TCP)
            (HOST = app-server)
            (PORT = 1522)
          )
          (CONNECT_DATA =
            (SID = banana)
            (SERVER = dedicated)
          )
        */
    }

}
