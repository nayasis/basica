package io.nayasis.basica.base;

import io.nayasis.basica.model.NMap;
import io.nayasis.basica.reflection.Reflector;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class StringsTest {

    @Test
    public void format() {

        assertEquals( "{ name : merong, age : 2 }", Strings.format( "{ name : {}, age : {} }", "merong", 2 ) );
        assertEquals( "5K\nUtil\ndesc", Strings.format("{}\n{}\n{}", "5K", "Util", "desc") );

        NMap parameter = new NMap( "{'name':'abc', 'age':2}" );
        assertEquals( "PRE 2 POST", Strings.format( "PRE {age} POST", parameter ) );
        assertEquals( "PRE {age} POST", Strings.format( "PRE \\{age} POST", parameter ) );
        assertEquals( "abc PRE 2 POST", Strings.format( "{name} PRE {age} POST", parameter ) );
        assertEquals( "abc PRE   2 POST", Strings.format( "{name} PRE {age:%3d} POST", parameter ) );

    }

    @Test
    public void formatAllowNullParameter() {
        assertEquals( "badCredentials", Strings.format("badCredentials", null) );
    }

    @Test
    public void formatFromBean() {

        String json = "{'a':1, 'b':2, 'c':'abcd'}";
        Bean param = Reflector.toBeanFrom(json, Bean.class);

        assertEquals( "Bean(a=1, b=2, c=abcd)", Strings.format("{}", param) );
        assertEquals( "1 is 2 or abcd", Strings.format("{a} is {b} or {c}", param) );

        @Data
        class Bean {
            private int    a;
            private int    b;
            private String c;
        }

    }

    @Test
    public void fileParameter() {
        File f = new File( "salamander");
        String text = Strings.format("it is {:%15s}", f );
        assertEquals( "it is      salamander", text );
    }

    @Test
    public void formatFromMap() {
        String json = "{'a':1, 'b':2, 'c':'abcd'}";
        Map param = Reflector.toMapFrom( json );
        assertEquals( "1 is 2 or abcd", Strings.format("{a} is {b} or {c}", param) );
    }



    @Test
    public void changeHangulJosa() {

        assertEquals( "카드템플릿을 등록합니다." , Strings.format( "{}를 등록합니다." , "카드템플릿") );
        assertEquals( "카드를 등록합니다."       , Strings.format( "{}를 등록합니다." , "카드"      ) );
        assertEquals( "카드는 등록됩니다."       , Strings.format( "{}는 등록됩니다." , "카드"      ) );
        assertEquals( "카드템플릿은 등록됩니다." , Strings.format( "{}는 등록됩니다." , "카드템플릿") );
        assertEquals( "카드가 등록됩니다."       , Strings.format( "{}가 등록됩니다." , "카드"      ) );
        assertEquals( "카드템플릿이 등록됩니다." , Strings.format( "{}가 등록됩니다." , "카드템플릿") );

    }

    @Test
    public void unescape() {

        assertEquals( "결재금액오\n\n류", Strings.unescape( "\uacb0\uc7ac\uae08\uc561\uc624\\n\\n\ub958" ) );

        assertEquals( "\\", Strings.unescape( "\\\\" ) );
        assertEquals( "\"", Strings.unescape( "\\\"" ) );
        assertEquals( "\'", Strings.unescape( "\\\'" ) );

    }

    @Test
    public void zip() {

        assertEquals( "ISO-8859-1", StandardCharsets.ISO_8859_1.toString() );

        String testString01 = "정화수는 정주호랑 김선지랑 행복하게 오래오래 살았어요 !";
        String testString02 = "I am a boy !";

        System.out.println( Strings.zip( testString01 ) );
        System.out.println( Strings.zip( testString02 ) );
        System.out.println( Strings.unzip( Strings.zip( testString01 ) ) );
        System.out.println( Strings.unzip( Strings.zip( testString02 ) ) );

        assertEquals( testString01, Strings.unzip( Strings.zip( testString01 ) ) );
        assertEquals( testString02, Strings.unzip( Strings.zip( testString02 ) ) );

    }

    @Test
    public void urlEncodeAndDecode() {

        System.out.println( Strings.encodeUrl( "http://unikys.tistory.com/195?menuId=DP13+DP14" ) );

    }

    @Test
    public void likeTest() {

        Assertions.assertTrue( Strings.like( "ABCD", "_B%" ) );
        Assertions.assertFalse( Strings.like( "ABCD", "_F%" ) );
        Assertions.assertTrue( Strings.like( "[A]B_D", "[_]%" ) );
        Assertions.assertFalse( Strings.like( "A\\ACD", "A\\%" ) );
        Assertions.assertTrue( Strings.like( "A%ACD", "A\\%%" ) );
        Assertions.assertTrue( Strings.like( "AB_D", "%\\_%" ) );
        Assertions.assertTrue( Strings.like( "AB_D", "%_%_%" ) );

    }

    @Test
    public void orOperation() {

        log.debug( Integer.toHexString( 0x00 | 0x01 ) );

        log.debug( Integer.toHexString( 0x10 | 0x01 ) );
        log.debug( Integer.toHexString( 0x10 | 0x02 ) );
        log.debug( Integer.toHexString( ( 0x10 | 0x01 ) & 1 ) );

    }

    @Test
    public void capturePattern() {

        String value = "jdbc:sqlite:./target/test-classes/localDb/#{Merong}#{Nayasis}SimpleLauncherHelloWorld.db";

        List<String> capturedList = Strings.capture( value, "#\\{(.+?)\\}" );

        assertEquals( 2, capturedList.size() );
        assertEquals( Arrays.asList( "Merong", "Nayasis" ), capturedList );

        value = "< Ref id=\"refOrigin2\" />";
        List<String> refIds = Strings.capture( value, "(?i)< *?ref +?id *?= *?['\"](.*?)['\"] *?\\/>" );
        assertEquals( "[refOrigin2]", refIds.toString() );

    }

    @Test
    public void join() {

        List<String> testArray = Arrays.asList( "1", "2", null, "3" );

        String result = Strings.join( testArray, ";" );

        assertEquals("1;2;3", result);

    }

    @Test
    public void tokenize() {

        assertEquals( "[I , m ,  boy || you , re ,  girl]", Strings.tokenize( "I am a boy || you are a girl", "a" ).toString() );
        assertEquals( "[I am a boy ,  you are a girl]", Strings.tokenize( "I am a boy || you are a girl", "||" ).toString() );
        assertEquals( "[I am a boy || ZAyou are a girl]", Strings.tokenize( "I am a boy || ZAyou are a girl", "" ).toString() );
        assertEquals( "[I am a boy || , you are a girl]", Strings.tokenize( "I am a boy || ZAyou are a girl", "ZA" ).toString() );
        assertEquals( "[a, b, c, d]", Strings.tokenize("a b\tc\nd", " \n\t" ).toString() );
        assertEquals( "[kube,  , ,,  , c]", Strings.tokenize("kube , c", " ,", true).toString() );

        log.debug( Strings.tokenize( "I am a boy || ZAyou are a girl", " " ).toString() );
        log.debug( Strings.tokenize( "I am a boy || ZAyou are a girl", "ZA" ).toString() );

    }

    @Test
    public void regexpRemoveEnter() {

        String test =
            "\n" +
                "\n" +
                "\n" +
                "  <col1 id=\"c1\">값1</col1>\n" +
                "  <col2 id=\"c2\" val=\"val2\">값2</col2>\n" +
                "\n" +
                "\n";

        test = test
            .replaceFirst( "^\n*", "" )
            .replaceFirst( "\n*$", "" )
        ;

        log.debug( test );

    }

    @Test
    public void rpad() {

        String pattern = "|{}|";

        int length = 50;

        log.debug( pattern, Strings.rpad("1671287205674218853", length, ' ') ) ;
        log.debug( pattern, Strings.rpad("[199.0, 392.0, 120.0, 70.0]", length, ' ') ) ;
        log.debug( pattern, Strings.rpad("", length, ' ') ) ;

    }

    @Test
    public void sr() {

        log.debug( Strings.trim( "aaaa" ).replaceFirst( "^#\\{", "" ).replaceFirst( "\\}$", "" ) );

    }

    @Test
    public void toBoolean() {

        assertEquals( Strings.toBoolean( "true" ), true );
        assertEquals( Strings.toBoolean( "trUe" ), true );
        assertEquals( Strings.toBoolean( "t" ), true );
        assertEquals( Strings.toBoolean( "T" ), true );
        assertEquals( Strings.toBoolean( "y" ), true );
        assertEquals( Strings.toBoolean( "Y" ), true );
        assertEquals( Strings.toBoolean( "yes" ), true );
        assertEquals( Strings.toBoolean( "yEs" ), true );

        assertEquals( Strings.toBoolean( "false" ), false );
        assertEquals( Strings.toBoolean( "n" ), false );
        assertEquals( Strings.toBoolean( "No" ), false );
        assertEquals( Strings.toBoolean( "another" ), false );

    }

    @Test
    public void regTest() {

        System.out.println( "#{name.value[1]}".replaceAll( "#\\{.+?(\\..+?)?\\}", String.format("#{%s$1}", "?") ) );

    }

    @Test
    public void split() {

        String val = "DP01  +DP02+^DP03|DP40";

        assertEquals( Strings.split( val, "(\\+(\\^)?|\\|)", true ).toString(), "[DP01, +, DP02, +^, DP03, |, DP40]"  );

        log.debug( Strings.split( " ", "," ).toString() );

    }

    @Test
    public void camel() {

        assertEquals( "lndPlus19Yn", Strings.toCamel( "lnd_plus19_yn" ) );
        assertEquals( "lnd_plus19_yn", Strings.toSnake( "lndPlus19Yn" ) );

    }

    @Test
    public void xss() {

        String word01 = "<script>";
        String word02 = "&lt;script&gt;";
        String word03 = "<script>aaa(\"aaa\",'b'){}</script>";
        String word04 = "066&";

        assertEquals( word02, Strings.clearXss( word01 ) );
        assertEquals( word01, Strings.restoreXss( word02 ) );
        assertEquals( word03, Strings.restoreXss( word03 ) );
        assertEquals( word04, Strings.restoreXss( word04 ) );

    }

    @Test
    public void mask() {

        String word = "01031155023";

        assertEquals( "", Strings.mask( "", word ) );
        assertEquals( "010_3115_5023", Strings.mask( "***_****_****", word ) );
        assertEquals( "010_3115_502", Strings.mask( "***_****_***", word ) );
        assertEquals( "*010_3115_502", Strings.mask( "\\****_****_***", word ) );
        assertEquals( "010_3115_502*", Strings.mask( "***_****_***\\*", word ) );
        assertEquals( "010_3115_502", Strings.mask( "***_****_***\\", word ) );

    }

    @Test
    public void displayLength() {

        String value = "control값";

        Characters.fullwidth( 1.0 );
        assertEquals( 8, Strings.getDisplayLength( value ) );

        Characters.fullwidth( 2.0 );
        assertEquals( 9, Strings.getDisplayLength( value ) );

    }

 }