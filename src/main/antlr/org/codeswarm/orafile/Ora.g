/*

ANTLR grammar for Oracle Network Services configuration files.

This grammar can parse entries in an Oracle Network Services configuration
file (tnsnames.ora, listener.ora, sqlnet.ora, cman.ora, ldap.ora...), based
on the Oracle 10g document:

http://docs.oracle.com/cd/E18283_01/network.112/e10835/syntax.htm

This grammar does not strictly conform to the document.  In paricular it does
not enforce that params start at column 0, and that continuation lines do
not start in column 0 -- whitespace is ignored (as are comments).  All other
rules should be observed.

The Oracle "spec" above is a little vague as a standalone language/syntax
definition: I don't see how "NAMES.DIRECTORY_PATH= (TNSNAMES, ONAMES)" is
valid since ',' is not listed as a delimiter.

*/

grammar Ora;

options {
    k = 5;
    output = AST;
}

@parser::header {
    package org.codeswarm.orafile;
}

@lexer::header {
    package org.codeswarm.orafile;
}

@parser::members {

    String unquote(String x) {
        return x.substring(1, x.length() - 1);
    }

    String unescape(String x) {
        return x.replaceAll("\\\\(.)", "$1");
    }

}

file returns [OraNamedParamList params]
    : { $params = Orafile.params(); }
      ( definition { $params.add($definition.named_param); } )*
    ;

definition returns [OraNamedParam named_param]
    : keyword EQUALS param
      { $named_param = Orafile.namedParam($keyword.string, $param.param); }
    ;

param returns [OraParam param]
    : ( value { $param = Orafile.string($value.string); }
      | LEFT_PAREN value_list RIGHT_PAREN { $param = $value_list.strings; }
      | param_list { $param = $param_list.params; }
      )
    ;

param_list returns [OraNamedParamList params]
    : { $params = Orafile.params(); }
      ( LEFT_PAREN definition RIGHT_PAREN { $params.add($definition.named_param); } )+
    ;

unquoted_string returns [String string]
    : UNQUOTED_STRING { $string = unescape($UNQUOTED_STRING.text); }
    ;

quoted_string returns [String string]
    : QUOTED_STRING { $string = unescape(unquote($QUOTED_STRING.text)); }
    ;

keyword returns [String string]
    : unquoted_string { $string = $unquoted_string.string.toUpperCase(); }
    ;

value returns [String string]
    : unquoted_string { $string = $unquoted_string.string; }
    | quoted_string { $string = $quoted_string.string; }
    ;

value_list returns [OraStringList strings]
    : { $strings = Orafile.strings(); }
      v1=value { $strings.add($v1.string); }
      ( COMMA v2=value { $strings.add($v2.string); } )*
    ;

fragment NETWORK_CHARACTER
    : 'A' .. 'Z' | 'a' .. 'z' | '0' .. '9'
    | '<' | '>' | '/' | '.' | ':'| ';' | '-' | '_'
    | '$' | '+' | '*' | '&' | '!' | '%' | '?' | '@' ;

fragment ESCAPED_CHARACTER: '\\' . ;

UNQUOTED_STRING: ( NETWORK_CHARACTER | ESCAPED_CHARACTER )+ ;

QUOTED_STRING
    : '\'' ( '\\\'' | ~( '\'' ) )* '\''
    | '"' ( '\\"' | ~( '"' ) )* '"' ;

LEFT_PAREN: '(';

RIGHT_PAREN: ')';

EQUALS: '='; COMMA: ',';

COMMENT: '#' ( ~( '\n' ) )* { $channel=HIDDEN; };

WHITESPACE: ( '\t' | ' ' ) { $channel=HIDDEN; };

NEWLINE: ( '\r' )? '\n' { $channel=HIDDEN; } ;
