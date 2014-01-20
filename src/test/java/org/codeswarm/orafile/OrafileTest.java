package org.codeswarm.orafile;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.codeswarm.orafile.Orafile.*;
import static org.testng.Assert.assertEquals;

public class OrafileTest {

    @Test
    public void testEmptyString() throws Exception {

        OrafileDict params = parse("");

        assertEquals(params.asList().size(), 0);
    }

    @Test
    public void testStringValue() throws Exception {

        OrafileDict params = parse("A=B");

        assertEquals(params, dict("A", "B"));
    }

    @Test
    public void testListValue() throws Exception {

        OrafileDict params = parse("ABC=(XY, YZ)");

        assertEquals(params, dict("ABC", asList("XY", "YZ")));
    }

    @Test
    public void testTwoStringValues() throws Exception {

        OrafileDict params = parse("ONE = X TWO = Y");

        assertEquals(params, dict(dict("ONE", "X"), dict("TWO", "Y")));
    }

    OrafileDict typicalTnsEntry() {

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

        OrafileDict params = parse("APPLE_MASTER =\n" +
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

        OrafileDict params = parse("APPLE_MASTER=(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)" +
            "(HOST=db-apple-master)(PORT=1521)))(CONNECT_DATA=(SID=apple)(SERVER=DEDICATED)))");

        assertEquals(params, typicalTnsEntry());
    }

    @Test
    public void testList() throws Exception {

        OrafileDict params = parse("NAMES.DIRECTORY_PATH= (LDAP, TNSNAMES, HOSTNAME)");

        assertEquals(params, dict("NAMES.DIRECTORY_PATH", strings("LDAP", "TNSNAMES", "HOSTNAME")));
    }

    @Test
    public void testEscape() throws Exception {

        OrafileDict params = parse("BANANA\\#MASTER = " +
            "(A='ba\\'na\\'na')" +
            "(B=\\ \\ q)" +
            "(C= \"one \\\"two\\\" three\"))" +
            "# bananas");

        assertEquals(params, dict("BANANA#MASTER", dict(
            dict("A", "ba'na'na"),
            dict("B", "  q"),
            dict("C", "one \"two\" three")
        )));
    }

    @Test
    public void testMultipleAddresses() throws Exception {

        OrafileDict params = parse("net_service_name=\n" +
            "     (DESCRIPTION=\n" +
            "      (ADDRESS=one)\n" +
            "      (Address=two)\n" +
            "      (ADDRESS=three)))");

        assertEquals(params, dict("net_service_name", dict(
            dict("DESCRIPTION", dict(
                dict("ADDRESS", "one"),
                dict("Address", "two"),
                dict("ADDRESS", "three")
            ))
        )));

        OrafileVal net_service_name = params.get("net_service_name").get(0);
        OrafileVal description = net_service_name.asNamedParamList().get("description").get(0);
        List<OrafileVal> address = description.asNamedParamList().get("address");

        assertEquals(address, new ArrayList<OrafileVal>(
            asList(string("one"), string("two"), string("three"))));
    }

    OrafileDict tns() throws Exception {
        return parse(IOUtils.toString(getClass().getResourceAsStream("tnsnames.ora")));
    }

    @Test
    public void testTns1() throws Exception {

        Map<String, String> values = tns().get("APPLE_v1.0").get(0)
            .findParamAttrs("address", asList("host", "port", "sid")).get(0);

        assertEquals(values.get("host"), "db-apple-v1-0");
        assertEquals(values.get("port"), "1521");
        assertEquals(values.get("sid"), "apple");
    }

    @Test
    public void testTns2() throws Exception {

        Map<String, String> values = tns().get("apple_master").get(0)
            .findParamAttrs("address", asList("host", "port", "sid")).get(0);

        assertEquals(values.get("host"), "db-apple-master");
        assertEquals(values.get("port"), "1500");
        assertEquals(values.get("sid"), "grape");
    }

    @Test
    public void testTns3() throws Exception {

        List<Map<String, String>> values = tns().get("BANANA_MASTER").get(0)
            .findParamAttrs("address", asList("host", "port", "sid"));

        assertEquals(values.get(0).get("host"), "db-banana-master");
        assertEquals(values.get(0).get("port"), "1521");
        assertEquals(values.get(0).get("sid"), "banana");

        assertEquals(values.get(1).get("host"), "db-banana-master");
        assertEquals(values.get(1).get("port"), "1522");
        assertEquals(values.get(1).get("sid"), "banana");

        assertEquals(values.get(2).get("host"), "db-banana-master-2");
        assertEquals(values.get(2).get("port"), "1522");
        assertEquals(values.get(2).get("sid"), "banana2");
    }

    @Test
    public void testRender() throws Exception {

        String original = IOUtils.toString(getClass().getResourceAsStream("render-test.ora"));
        OrafileDict parsed = parse(original);
        String rendered = new OrafileRenderer().renderFile(parsed);
        assertEquals(rendered, original);
    }

}
