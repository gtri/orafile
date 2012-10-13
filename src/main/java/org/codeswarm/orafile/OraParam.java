package org.codeswarm.orafile;

import java.util.List;

public interface OraParam {

    String asString();

    List<String> asList();

    OraDict asDict();

}
