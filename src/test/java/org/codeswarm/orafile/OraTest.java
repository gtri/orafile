package org.codeswarm.orafile;

import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.codeswarm.orafile.Ora.dict;
import static org.codeswarm.orafile.Ora.list;
import static org.codeswarm.orafile.Ora.parse;
import static org.testng.Assert.assertEquals;

public class OraTest {

    @Test
    public void testEmptyString() throws Exception {
        OraDict dict = parse("");
        assertEquals(dict.asMap().size(), 0);
    }

    @Test
    public void testStringValue() throws Exception {
        OraDict dict = parse("A=B");
        assertEquals(dict, dict("A", "B"));
    }

    @Test
    public void testListValue() throws Exception {
        OraDict dict = parse("ABC=(XY, YZ)");
        assertEquals(dict, dict("ABC", asList("XY", "YZ")));
    }

    @Test
    public void testTwoStringValues() throws Exception {
        OraDict dict = parse("ONE = X TWO = Y");
        assertEquals(dict, dict(dict("ONE", "X"), dict("TWO", "Y")));
    }

    OraDict typicalTnsEntry() {
        return dict("APPLE_MASTER", dict(
            "DESCRIPTION", dict(
            dict("ADDRESS_LIST", dict(
                dict("ADDRESS", dict(
                    dict("PROTOCOL", "TCP"),
                    dict("HOST", "db-apple-master"),
                    dict("PORT", "1521"))))),
            dict("CONNECT_DATA", dict(
                dict("SID", "apple"),
                dict("SERVER", "DEDICATED"))))));
    }

    @Test
    public void testTypicalTnsEntry() throws Exception {
        OraDict dict = parse("APPLE_MASTER =\n" +
            "  (DESCRIPTION =\n" +
            "    (ADDRESS_LIST =\n" +
            "      (ADDRESS = (PROTOCOL = TCP)(HOST = db-apple-master)(PORT = 1521))\n" +
            "    )\n" +
            "    (CONNECT_DATA =\n" +
            "      (SID = apple)\n" +
            "      (SERVER = DEDICATED)\n" +
            "    )\n" +
            "  )");
        assertEquals(dict, typicalTnsEntry());
    }

    @Test
    public void testTypicalTnsEntryDense() throws Exception {
        OraDict dict = parse("APPLE_MASTER=(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)" +
            "(HOST=db-apple-master)(PORT=1521)))(CONNECT_DATA=(SID=apple)(SERVER=DEDICATED)))");
        assertEquals(dict, typicalTnsEntry());
    }

    @Test
    public void testList() throws Exception {
        OraDict dict = parse("NAMES.DIRECTORY_PATH= (LDAP, TNSNAMES, HOSTNAME)");
        assertEquals(dict, dict("NAMES.DIRECTORY_PATH", list("LDAP", "TNSNAMES", "HOSTNAME")));
    }

    @Test
    public void testEscape() throws Exception {
        OraDict dict = parse("BANANA\\#MASTER = " +
            "(A='ba\\'na\\'na')" +
            "(B=\\ \\ q)" +
            "(C= \"one \\\"two\\\" three\"))" +
            "# bananas");
        assertEquals(dict, dict("BANANA#MASTER", dict(
            dict("A", "ba'na'na"),
            dict("B", "  q"),
            dict("C", "one \"two\" three")
        )));
    }

}
