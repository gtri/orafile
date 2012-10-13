package org.codeswarm.orafile;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.codeswarm.orafile.Ora.*;
import static org.testng.Assert.assertEquals;

public class OraTest {

    @Test
    public void testEmptyString() throws Exception {

        OraNamedParamList params = parse("");

        assertEquals(params.asMap().size(), 0);
    }

    @Test
    public void testStringValue() throws Exception {

        OraNamedParamList params = parse("A=B");

        assertEquals(params, params("A", "B"));
    }

    @Test
    public void testListValue() throws Exception {

        OraNamedParamList params = parse("ABC=(XY, YZ)");

        assertEquals(params, params("ABC", asList("XY", "YZ")));
    }

    @Test
    public void testTwoStringValues() throws Exception {

        OraNamedParamList params = parse("ONE = X TWO = Y");

        assertEquals(params, params(params("ONE", "X"), params("TWO", "Y")));
    }

    OraNamedParamList typicalTnsEntry() {

        return params("APPLE_MASTER", params(
            "DESCRIPTION", params(
            params("ADDRESS_LIST", params(
                params("ADDRESS", params(
                    params("PROTOCOL", "TCP"),
                    params("HOST", "db-apple-master"),
                    params("PORT", "1521"))))),
            params("CONNECT_DATA", params(
                params("SID", "apple"),
                params("SERVER", "DEDICATED"))))));
    }

    @Test
    public void testTypicalTnsEntry() throws Exception {

        OraNamedParamList params = parse("APPLE_MASTER =\n" +
            "  (DESCRIPTION =\n" +
            "    (ADDRESS_LIST =\n" +
            "      (ADDRESS = (PROTOCOL = TCP)(HOST = db-apple-master)(PORT = 1521))\n" +
            "    )\n" +
            "    (CONNECT_DATA =\n" +
            "      (SID = apple)\n" +
            "      (SERVER = DEDICATED)\n" +
            "    )\n" +
            "  )");

        assertEquals(params, typicalTnsEntry());
    }

    @Test
    public void testTypicalTnsEntryDense() throws Exception {

        OraNamedParamList params = parse("APPLE_MASTER=(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)" +
            "(HOST=db-apple-master)(PORT=1521)))(CONNECT_DATA=(SID=apple)(SERVER=DEDICATED)))");

        assertEquals(params, typicalTnsEntry());
    }

    @Test
    public void testList() throws Exception {

        OraNamedParamList params = parse("NAMES.DIRECTORY_PATH= (LDAP, TNSNAMES, HOSTNAME)");

        assertEquals(params, params("NAMES.DIRECTORY_PATH", strings("LDAP", "TNSNAMES", "HOSTNAME")));
    }

    @Test
    public void testEscape() throws Exception {

        OraNamedParamList params = parse("BANANA\\#MASTER = " +
            "(A='ba\\'na\\'na')" +
            "(B=\\ \\ q)" +
            "(C= \"one \\\"two\\\" three\"))" +
            "# bananas");

        assertEquals(params, params("BANANA#MASTER", params(
            params("A", "ba'na'na"),
            params("B", "  q"),
            params("C", "one \"two\" three")
        )));
    }

    @Test
    public void testMultipleAddresses() throws Exception {

        OraNamedParamList params = parse("net_service_name=\n" +
            "     (DESCRIPTION=\n" +
            "      (ADDRESS=one)\n" +
            "      (ADDRESS=two)\n" +
            "      (ADDRESS=three)))");

        assertEquals(params, params("NET_SERVICE_NAME", params(
            params("DESCRIPTION", params(
                params("ADDRESS", "one"),
                params("ADDRESS", "two"),
                params("ADDRESS", "three")
            ))
        )));

        OraParam net_service_name = params.get("net_service_name").get(0);
        OraParam description = net_service_name.asNamedParamList().get("description").get(0);
        List<OraParam> address = description.asNamedParamList().get("address");

        assertEquals(address, new ArrayList<OraParam>(
            asList(string("one"), string("two"), string("three"))));
    }

}
