package io.nayasis.basica.base;

import io.nayasis.basica.base.format.Formatter;
import io.nayasis.basica.exception.Exceptions;
import io.nayasis.basica.exception.unchecked.EncodingException;
import io.nayasis.basica.exception.unchecked.UncheckedClassNotFoundException;
import lombok.experimental.UtilityClass;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * String Handling Utility
 *
 * @author nayasis@gmail.com
 */
@UtilityClass
public class Strings {

	private Pattern    PATTERN_CAMEL = Pattern.compile( "(_[a-zA-Z])" );
	private Pattern    PATTERN_SNAKE = Pattern.compile( "([A-Z])" );
	private Formatter  formatter     = new Formatter();

	/**
	 * get display length applying character's font width. <br>
	 *
	 * if character is CJK, font width can be 0.5 or 2. <br>
	 * this method calculate total display length of string value.
	 *
	 * Full-Width of CJK characters can be set by {@link Characters#fullwidth(double)}.
	 *
	 * @param value value
	 * @return total display length
	 */
	public int getDisplayLength( Object value ) {

		if( value == null ) return 0;

		String val = value.toString();
		if( ! Characters.isFontWidthModified() ) return val.length();

		double result = 0;
		for( int i = 0, iCnt = val.length(); i < iCnt; i++ ) {
			result += Characters.getFontWidth( val.charAt( i ) );
		}

		return (int) Math.round( result );

	}

	/**
	 * get lpad value applying character's display font width
	 *
     * @param value    	source value
     * @param length	padding length
     * @param padChar	padding character
     * @return padding string value
	 */
	public String dplpad( Object value, int length, char padChar ) {
		int adjustLength = ( Characters.fullwidth() == 1 || value == null )	? length
				: value.toString().length() + ( length - getDisplayLength( value ) );
		return lpad( value, adjustLength, padChar );
	}

	/**
	 * get rpad value applying character's display font width
	 *
	 * @param value    	source value
	 * @param length	padding length
	 * @param padChar	padding character
	 * @return padding string value
	 */
	public String dprpad( Object value, int length, char padChar ) {
		int adjustLength = ( Characters.fullwidth() == 1 || value == null )	? length
				: value.toString().length() + ( length - getDisplayLength( value ) );

		return rpad( value, adjustLength, padChar );
	}

	/**
	 * check if string of value is empty.
	 *
	 * @param value value to check
	 * @return true if string of value is empty.
	 */
	public boolean isEmpty( Object value ) {
		return value == null || value.toString().length() == 0;
	}

	/**
	 * check if string of value is not empty.
	 *
	 * @param value value to check
	 * @return true if string of value is not empty.
	 */
	public boolean isNotEmpty( Object value ) {
		return ! isEmpty( value );
	}


	/**
	 * check if string of value is blank.
	 *
	 * @param value value to check
	 * @return true if string of value is blank.
	 */
	public boolean isBlank( Object value ) {
		if( value == null ) return true;
		String val = value.toString();
		return val.length() == 0 || val.trim().length() == 0;
	}

	/**
	 * check if string of value is not blank.
	 *
	 * @param value value to check
	 * @return true if string of value is not blank.
	 */
	public boolean isNotBlank( Object value ) {
		return ! isBlank( value );
	}

	/**
	 * trim string of value.
	 * @param value value
	 * @return	trimmed string
	 */
	public String trim( Object value ) {
		return nvl( value ).trim();
	}

	/**
	 * trim leading whitespace from given value.
	 * @param value value
	 * @return	left trimmed string
	 */
	public String ltrim( Object value ) {
		return nvl( value ).replaceFirst( "^\\s+", "" );
	}

	/**
	 * trim leading whitespace from given value.
	 * @param value value
	 * @return	left trimmed string
	 */
	public String rtrim( Object value ) {
		return nvl( value ).replaceFirst( "\\s+$", "" );
	}

	/**
	 * Check a string is equals to other string.
	 *
	 * it is free from NullPointException.
	 *
	 * @param one    string to compare
	 * @param other  other string to compare
	 * @return true if each are equal.
	 */
	public boolean equals( String one, String other ) {
		if( one == null && other == null ) return true;
		if( one == null && other != null ) return false;
		if( one != null && other == null ) return false;
		return one.equals( other );
	}

    /**
     * Check a string is equals to other string.
     *
     * it is free from NullPointException.
     *
     * @param one    string to compare
     * @param other  other string to compare
     * @return true if each are equal.
     */
    public boolean equalsIgnoreCase( String one, String other ) {
        if( one == null && other == null ) return true;
        if( one == null && other != null ) return false;
        if( one != null && other == null ) return false;
        return one.equalsIgnoreCase( other );
    }

	/**
	 * return formatted string binding parameters.<br><br>
	 *
	 * <p>Format</p>
	 * <table>
	 *   <thead>
	 *     <tr>
	 *       <th>Markup</th>
	 *       <th>Description</th>
	 *       <th>Example</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <th>{}</th><td>index based</td>
	 *       <td><pre>
	 *         Strings.format("{}st, {}nd", 1, 2) -&gt; "1st, 2nd"
	 *       </pre></td>
	 *     </tr>
	 *     <tr>
	 *       <th>{key}</th><td>parameter based</td>
	 *       <td><pre>
	 *         NMap parameter = new NMap( "{'name':'abc', 'age':2}" );
	 *         Strings.format( "PRE {name} POST {age}", parameter ) -&gt; "PRE abc POST 2"
	 *       </pre></td>
	 *     </tr>
	 *     <tr>
	 *       <th>{key:format}</th><td>parameter based with format</td>
	 *       <td><pre>
	 *         NMap parameter = new NMap( "{'name':'abc', 'age':2}" );
	 *         Strings.format( "PRE {name} POST {age:%3d}", parameter ) -&gt; "PRE abc POST __2"
	 *       </pre></td>
	 *     </tr>
	 *     <tr>
	 *       <th>{:format}</th><td>index based with format</td>
	 *       <td><pre>
	 *         Strings.format("{}st, {:%3d}nd", 1, 2) -&gt; "1st, __2nd"
	 *       </pre></td>
	 *     </tr>
	 *   </tbody>
	 *   <caption>format method usage</caption>
	 * </table>
	 *
	 * @param format format string
	 * @param param  binding parameter
	 * @return formatted string
	 */
	public String format( Object format, Object... param ) {
		return formatter.format( format, param );
	}

    /**
     * return left padded string.
     *
     * <pre>
     * {@link Strings#lpad}("AAAAAA", 'Z', 10) ) -&gt; "ZZZZAAAAAA"
     * </pre>
     *
     * @param value    	original value
     * @param length	padding length
     * @param padChar	padding character
     * @return left padded string
     */
    public String lpad( Object value, int length, char padChar ) {

        String text        = nvl( value );
        int    textCharCnt = text.length();
        int    index       = Math.max( length - textCharCnt, 0 );

        char[] result = new char[ length ];

        for( int i = 0; i < index; i++ ) {
            result[ i ] = padChar;
        }

        for( int i = 0, iCnt = Math.min(length, textCharCnt); i < iCnt; i++ ) {
            result[ index + i ] = text.charAt( i );
        }

        return new String( result );

    }

    /**
     *
     * return right padded string.
     *
     * <pre>
     * {@link Strings#rpad}("AAAAAA", 'Z', 10) ) -&gt; "AAAAAAZZZZ"
     * </pre>
     *
	 * @param value    	original value
	 * @param length	padding length
	 * @param padChar	padding character
     * @return right padded string
     */
    public String rpad( Object value, int length, char padChar ) {

        String text  = nvl( value );
        int    index = Math.min( length, text.length() );

        char[] result = new char[ length ];

        for( int i = 0; i < index; i++ ) {
            result[ i ] = text.charAt( i );
        }

        for( int i = index; i < length; i++ ) {
            result[ i ] = padChar;
        }

        return new String( result );

    }


    /**
     * return empty string if input value is null.
     *
     * @param val value to check
     * @return empty string if val is null, itself if val is not null.
     */
    public String nvl( Object val ) {
    	return ( val == null ) ? "" : val.toString();
    }

    /**
     * return replace value if value is null.
     *
     * @param value 		value to check
     * @param replaceValue	substitutive value
     * @return itself if value is not null, replace value if value is not null
     */
    public String nvl( Object value, Object replaceValue ) {
    	return ( value == null ) ? nvl( replaceValue ) : value.toString();
    }

    /**
	 * convert text to camel case
	 *
     * <pre>
     * String text = Strings.toCamel( "unicode_text" );
     * System.out.println( text ); -&gt; "unicodeText""
     * </pre>
     * @param text   text to convert
     * @return camel cased text
     */
    public String toCamel( String text ) {

    	if( isEmpty(text) ) return "";

    	text = text.toLowerCase();
        Matcher matcher = PATTERN_CAMEL.matcher( text );
        StringBuffer sb = new StringBuffer();

        while( matcher.find() ) {
            String r = matcher.group().substring( 1 );
            if( matcher.start() != 0 ) r = r.toUpperCase();
            matcher.appendReplacement( sb, r );
        }

        matcher.appendTail( sb );

        return sb.toString();

    }

    /**
     * convert camel cased text to underscored text
	 *
     * <pre>
     * String text = Strings.snake( "unicodeText" );
     * System.out.println( text ); → "unicode_text"
     * </pre>
     * @param text     text to convert
     * @return underscored text
     */
    public String toSnake( String text ) {

        Matcher matcher = PATTERN_SNAKE.matcher( text );
        StringBuffer sb = new StringBuffer();

        while( matcher.find() ) {
            if( matcher.start() == 0 ) continue;
            String r = matcher.group();
            matcher.appendReplacement( sb, "_" + r.toLowerCase() );
        }

        matcher.appendTail( sb );
        return sb.toString();

    }

    /**
	 * escape string preserving json structure in text.
	 *
     * @param value text
     * @return escaped string
     */
    public String escape( Object value ) {

    	if( isEmpty(value) ) return "";

    	StringBuilder sb = new StringBuilder();

        for( char ch : value.toString().toCharArray() ) {

            switch( ch ) {

                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b");  break;
                case '\f': sb.append("\\f");  break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                case '/':  sb.append("\\/");  break;

                default:
                    if( ch >= '\u0000' && ch <= '\u001F' ) {
                    	sb.append("\\u").append( lpad(Integer.toHexString(ch), 4, '0').toUpperCase() );
                    } else {
                    	sb.append(ch);
                    }
            }

        }

        return sb.toString();

    }

    /**
     * <pre>
     * 1. \\u**** 형식의 문자열을 unicode 문자열로 변경한다.
     *
     * Stringss.unescape( "\uacb0\uc7ac\uae08\uc561\uc624\ub958" );
     *
     * → "결제금액오류"
     *
     * 2. \\n 형식으로 표현된 Sequence 문자열을 원래 Sequence 문자로 변경한다.
     *
     * Strings.unescape( "\\n\\n" ) → "\n\n"
     * </pre>
     *
     * @param param 문자열
     * @return 유니코드 문자열
     */
    public String unescape( Object param ) {

		if( isEmpty(param) ) return "";

        String  srcTxt  = param.toString();
        Pattern pattern = Pattern.compile( "\\\\(b|t|n|f|r|\\\"|\\\'|\\\\)|([u|U][0-9a-fA-F]{4})" );
        Matcher matcher = pattern.matcher( srcTxt );

        StringBuffer sb = new StringBuffer( srcTxt.length() );

        while( matcher.find() ) {

        	String replacedChar = null;

        	if( matcher.start(1) >= 0 ) {
        		replacedChar = getUnescapedSequence( matcher.group(1) );

        	} else if( matcher.start(2) >= 0 ) {
        		replacedChar = getUnescapedUnicodeChar( matcher.group(2) );
        	}

            matcher.appendReplacement( sb, Matcher.quoteReplacement(replacedChar) );

        }

        matcher.appendTail( sb );

        return sb.toString();

    }

    private String getUnescapedUnicodeChar( String escapedString ) {
    	try {
    		String hex = escapedString.substring( 2 );
    		int hexNumber = Integer.parseInt( hex, 16 );
    		return Character.toString( (char) hexNumber );
    	} catch( StringIndexOutOfBoundsException e ) {
    		throw new StringIndexOutOfBoundsException( String.format( "Char to unescape unicode : [%s]", escapedString ) );
    	}
    }

    private String getUnescapedSequence( String escapedString ) {
    	switch( escapedString.charAt(0) ) {
    		case 'b' : return "\b";
    		case 't' : return "\t";
    		case 'n' : return "\n";
    		case 'f' : return "\f";
    		case 'r' : return "\r";
    	}
    	return escapedString;
    }

    /**
     * Join collection's element to single string.
     *
     * <pre>
     * List&lt;String&gt; collection = Arrays.asList( "a", "b", null, "c" );
     * Strings.join( collection, "," );
     * -&gt; "a,b,c"
     * </pre>
     *
     * @param collection 	collection to join
     * @param concater 		concatenation string
     * @param skipEmpty 	skip empty entry
     * @return joined text
     */
    public String join( Collection<?> collection, String concater, boolean skipEmpty ) {

    	if( collection == null || collection.size() == 0 ) return "";

    	StringBuilder sb = new StringBuilder();

    	for( Object e : collection ) {
    		if( skipEmpty && isEmpty(e) ) continue;
    		if( sb.length() > 0 ) sb.append( concater );
    		sb.append( e == null ? null : e.toString() );
    	}
    	return sb.toString();

    }

	/**
	 * Join collection's element to single string.
	 *
	 * <pre>
	 * List&lt;String&gt; collection = Arrays.asList( "a", "b", null, "c" );
	 * Strings.join( collection, "," );
	 * -&gt; "a,b,c"
	 * </pre>
	 *
	 * @param collection 	collection to join
	 * @param concater 		concatenation string
	 * @return joined text
	 */
	public String join( Collection<?> collection, String concater ) {
		return join( collection, concater, true );
	}

	/**
	 * Split string around matches of the given <a href="../util/regex/Pattern.html#sum">regular expression</a>.
	 *
	 * @param value		string value
	 * @param separator	regular expression separator
	 * @return	string array divided by regular expression matcher
	 */
	public List<String> split( Object value, String separator ) {
		return split( value, separator, false );
	}

	/**
	 * Split string around matches of the given <a href="../util/regex/Pattern.html#sum">regular expression</a>.
	 *
	 * @param value				string value
	 * @param separator	        regular expression separator
	 * @param includeSeparator	include separator in result
	 * @return	string array divided by regular expression matcher
	 */
	public List<String> split( Object value, String separator, boolean includeSeparator ) {

		List<String> result = new ArrayList<>();

		if( isEmpty(value) ) return result;

		String val = trim( value );

		if( isEmpty(separator) ) {
			result.add( val );
			return result;
		}

		Pattern pattern = Pattern.compile( separator );
		Matcher matcher = pattern.matcher( val );

		int caret = 0;

		while( matcher.find() ) {
			if( caret != matcher.start() ) {
				result.add( val.substring( caret, matcher.start() ).trim() );
			}
			if( includeSeparator ) {
				result.add( matcher.group() );
			}
			caret = matcher.end();
		}

		if( caret != val.length() ) {
			result.add( val.substring( caret ).trim() );
		}

		return result;

	}

	/**
	 * tokenize text by separator
	 *
	 * @param value     		value to tokenize
	 * @param separator 		separator to tokenize
	 * @return tokenized word list
	 */
	public List<String> tokenize( Object value, String separator ) {
		return tokenize( value, separator, false );
	}

	/**
     * tokenize text by separator
     *
     * @param value     		value to tokenize
     * @param separator 		separator to tokenize
     * @param includeSeparator	include separator in result
     * @return tokenized word list
     */
    public List<String> tokenize( Object value, String separator, boolean includeSeparator ) {

    	List<String> result = new ArrayList<>();

    	if( isEmpty(value) ) return result;

		StringTokenizer tokenizer = new StringTokenizer( value.toString(), separator, includeSeparator );

		while( tokenizer.hasMoreTokens() ) {
			result.add( tokenizer.nextToken() );
		}

    	return result;

    }

    /**
     * change word's first character to upper case
     *
     * @param text text to change
     * @return uncapitalized text
     */
    public String uncapitalize( Object text ) {
		return changeFirstCharacterCase( text, false );
    }

    /**
     * change word's first character to lower case
     *
     * @param text text to change
     * @return capitalized text
     */
    public String capitalize( Object text ) {
    	return changeFirstCharacterCase( text, true );
    }

    private String changeFirstCharacterCase( Object val, boolean capitalize ) {

		if( isEmpty(val) ) return "";

		String string = val.toString();

		char origin  = string.charAt( 0 );
		char changed = capitalize ? Character.toUpperCase( origin ) : Character.toLowerCase( origin );

		if( origin == changed )
			return string;

		char[] array = string.toCharArray();
		array[0] = changed;
		return new String( array, 0, array.length );

	}

    /**
     * Compress multiple space to single space
     *
     * <pre>
     * {@link Strings#compressSpace}( "A     B" ); -&gt; "A B"
     * {@link Strings#compressSpace}( "A    B" );  -&gt; "A B"
     * {@link Strings#compressSpace}( "A   B" );   -&gt; "A B"
     * {@link Strings#compressSpace}( "A  B" );    -&gt; "A B"
     * {@link Strings#compressSpace}( "A B" );     -&gt; "A B"
     * </pre>
     *
     * @param value text value
     * @return text with space compressed
     */
    public String compressSpace( Object value ) {
    	if( isEmpty(value) ) return "";
    	return value.toString().replaceAll( "[ \t]+", " " ).trim();
    }

	/**
	 * Compress multiple space or enter to single space
	 *
	 * <pre>
	 * {@link Strings#compressBlank}( "A     B" );   -&gt; "A B"
	 * {@link Strings#compressBlank}( "A B" );       -&gt; "A B"
	 * {@link Strings#compressBlank}( "A \n\n B" );  -&gt; "A B"
	 * </pre>
	 *
	 * @param value text value
	 * @return text with space or enter compressed
	 */
	public String compressBlank( Object value ) {
		if( isEmpty(value) ) return "";
		return value.toString().replaceAll( "[ \t\n\r]+", " " ).trim();
	}

	/**
	 * Compress multiple enter to single enter
	 *
	 * <pre>
	 * {@link Strings#compressEnter}( "A\n\n\nB" );  -&gt; "A\nB"
	 * </pre>
	 *
	 * @param value text value
	 * @return text with enter compressed
	 */
	public String compressEnter( Object value ) {
		if( isEmpty(value) ) return "";
		return value.toString().replaceAll( " *[\n\r]", "\n" ).replaceAll( "[\n\r]+", "\n" );
	}

    /**
     * encode object to text
     *
     * @param value object to encode
     * @return encoded text
     * @throws UncheckedIOException if I/O exception occurs.
     */
    public String encode( Object value ) throws UncheckedIOException {
		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream( bos )
		) {
			oos.writeObject( value );
			oos.close();
			return DatatypeConverter.printBase64Binary( bos.toByteArray() );
		} catch ( IOException e ) {
			throw new UncheckedIOException( e );
		}
    }

    /**
     * decode text to object
     *
     * @param value text to decode as object
	 * @param <T> This is the type parameter
     * @return decoded object
     * @throws UncheckedIOException if I/O exception occurs.
     * @throws UncheckedClassNotFoundException if class is not found in class loader.
     */
    public <T> T decode( String value ) throws UncheckedIOException {
		if( value == null ) return null;

		byte bytes[] = DatatypeConverter.parseBase64Binary( nvl(value) );

		try (
			ByteArrayInputStream bis = new ByteArrayInputStream( bytes );
			ObjectInputStream ois = new ObjectInputStream( bis )
		) {
			Object val = ois.readObject();
			return val == null ? null : (T) val;
		} catch (IOException e) {
			throw new UncheckedIOException( e );
		} catch ( ClassNotFoundException e) {
			throw new UncheckedClassNotFoundException( e );
		}

    }

	/**
	 * encode URL
	 * @param uri uri to encode
	 * @return encoded URL
	 * @throws EncodingException    if an encoding error occurs.
	 */
    public String encodeUrl( Object uri ) throws EncodingException {
    	try {
			return URLEncoder.encode( nvl(uri), "UTF-8" );
    	} catch( UnsupportedEncodingException e ) {
        	throw new EncodingException( e );
        }
    }

	/**
	 * decode URL
	 * @param uri uri to decode
	 * @return decoded URL
	 * @throws EncodingException	if an decoding error occurs.
	 */
    public String decodeUrl( Object uri ) throws EncodingException {
    	try {
    		return URLDecoder.decode( nvl( uri ), "UTF-8" );
    	} catch( UnsupportedEncodingException e ) {
        	throw new EncodingException( e );
        }
    }

    /**
	 * extract digit characters from word
	 *
	 * @param string word
	 * @return number characters
	 */
	public String toDigit( String string ) {
		if( isEmpty( string ) ) return "";
		return string.replaceAll( "[^0-9]", "" );
	}

	/**
	 * zip text
	 *
	 * @param value text to zip
	 * @return compressed text
	 */
	public String zip( String value ) {
		if( isEmpty(value) ) return "";
        try(
        	ByteArrayOutputStream out  = new ByteArrayOutputStream();
        	GZIPOutputStream      gzip = new GZIPOutputStream( out )
		) {
        	gzip.write( value.getBytes() );
        	gzip.close();
        	return out.toString( StandardCharsets.ISO_8859_1.toString() );
        } catch( IOException e ) {
        	throw new UncheckedIOException( e );
        }
	}

	/**
	 *  unzip text
	 *
	 * @param value text to unzip
	 * @return decompressed text
	 */
	public String unzip( String value ) {
		if( isEmpty(value) ) return "";
        try(
        	ByteArrayInputStream input        = new ByteArrayInputStream( value.getBytes( StandardCharsets.ISO_8859_1 ));
        	GZIPInputStream      gzip         = new GZIPInputStream( input );
        	BufferedReader       bufferReader = new BufferedReader( new InputStreamReader( gzip ) )
		) {
        	StringBuilder sb = new StringBuilder();
        	String line;
        	while( (line = bufferReader.readLine()) != null ) {
        		sb.append( line );
        	}
        	return sb.toString();
        } catch( IOException e ) {
        	throw new UncheckedIOException( e );
        }
	}

	/**
	 * compare string like DBMS's LIKE.
	 *
	 * <pre>
	 * {@link Strings#like}( "ABCDEFG", "%BCD%"   ) -&gt; true
	 * {@link Strings#like}( "ABCDEFG", "%BCD_F%" ) -&gt; true
	 * {@link Strings#like}( "AB_DEFG", "AB.DEFG" ) -&gt; false
	 * </pre>
	 *
	 * @param value   string to compare
	 * @param pattern LIKE pattern ( "_" : single character, "%" : unknown string, "\\_" : '_' character, "\\%" : '%' character )
	 * @return true if value matches with LIKE pattern.
	 */
	public boolean like( Object value, String pattern ) {

		pattern = escapeRegexp( pattern );

		StringBuilder newPattern = new StringBuilder();

		for( int i = 0, iCnt = pattern.length() - 1; i <= iCnt; i++ ) {

			char chCurr = pattern.charAt( i );

			switch( chCurr ) {

				case '\\' :

					char ch2ndNext = ( i == iCnt ) ? ' ' : pattern.charAt( i + 1 );

					if( ch2ndNext == '\\' ) {

						char ch3rdNext = ( i == iCnt - 1 ) ? ' ' : pattern.charAt( i + 2 );

						if( ch3rdNext == '_' || ch3rdNext == '%' ) {
							newPattern.append( ch3rdNext );
							i =+ 3;

						} else {
							newPattern.append( chCurr ).append( ch2ndNext );
							i =+ 2;
						}

					} else {
						newPattern.append( chCurr );
					}

					break;

				case '_' : newPattern.append( '.'    ); break;
				case '%' : newPattern.append( ".*?"  ); break;
				default  : newPattern.append( chCurr );

			}

		}

		return Pattern.compile( newPattern.toString(), Pattern.DOTALL ).matcher( nvl(value) ).matches();

	}

	/**
	 * compare string like DBMS's <b>NOT LIKE</b>.
	 *
	 * <pre>
	 * {@link Strings#notLike}( "ABCDEFG", "%BCD%"   ) -&gt; false
	 * {@link Strings#notLike}( "ABCDEFG", "%BCD_F%" ) -&gt; false
	 * {@link Strings#notLike}( "AB_DEFG", "AB.DEFG" ) -&gt; true
	 *
	 *  </pre>
	 *
	 * @param value   string to compare
	 * @param pattern LIKE pattern ( "_" : single character, "%" : unknown string, "\\_" : '_' character, "\\%" : '%' character )
	 * @return true if value does not match with LIKE pattern.
	 */
	public boolean notLike( Object value , String pattern ) {
		return ! like( value, pattern );
	}

	/**
	 * add <font style="color:red">\</font> character before Regular Expression Keywords <font style="color:blue">([](){}.*+?$^|#\)</font>.
	 *
	 * @param pattern regular expression
	 * @return  escaped pattern
	 */
	public String escapeRegexp( String pattern ) {

		StringBuilder newPattern = new StringBuilder();

		for( char c : nvl( pattern ).toCharArray() ) {
			if( "[](){}.*+?$^|#\\".indexOf( c ) != -1 ) {
				newPattern.append( '\\' );
			}
			newPattern.append( c );
		}

		return newPattern.toString();

	}

	/**
	 * extract words matched by regular expression.
	 *
	 * <pre>
	 *
	 *  String pattern = "#\\{(.+?)}";
	 *  List&lt;String&gt; finded = Strings.capture( "/admkr#{AAAA}note#{BBBB}ananan#{AAAA}sss", pattern );
	 *  System.out.println( finded ); -&gt; ['AAAA','BBBB', 'AAAA']
	 *
	 *  ----------------------------------------------------------------
	 *
	 *  Strings.capturePatterns( "1.2.3.4", "\\." )   -&gt; []
	 *  Strings.capturePatterns( "1.2.3.4", "(\\.)" ) -&gt; ['.', '.', '.']
	 *
	 * </pre>
	 *
	 * @param value   target value to inspect
	 * @param pattern regular expression (only captured pattern (wrapped by (...)) can be extracted)
	 * @return captured words
	 */
	public List<String> capture( Object value, String pattern ) {
		Pattern p = ( pattern == null ) ? null : Pattern.compile( pattern );
		return capture( value, p );
	}

	/**
	 * extract words matched by regular expression.
	 *
	 * <pre>
	 *
	 *  String pattern = "#\\{(.+?)}";
	 *  List&lt;String&gt; finded = Strings.capture( "/admkr#{AAAA}note#{BBBB}ananan#{AAAA}sss", pattern );
	 *  System.out.println( finded ); -&gt; ['AAAA','BBBB', 'AAAA']
	 *
	 *  ----------------------------------------------------------------
	 *
	 *  Strings.capturePatterns( "1.2.3.4", "\\." )   -&gt; []
	 *  Strings.capturePatterns( "1.2.3.4", "(\\.)" ) -&gt; ['.', '.', '.']
	 *
	 * </pre>
	 *
	 * @param value   target value to inspect
	 * @param pattern regular expression (only captured pattern (wrapped by (...)) can be extracted)
	 * @return captured words
	 */
	public List<String> capture( Object value, Pattern pattern ) {
		List<String> result = new ArrayList<>();
		if( isEmpty(value) || isEmpty(pattern) ) return result;
		Matcher matcher = pattern.matcher( value.toString() );
		while( matcher.find() ) {
			for( int i = 1, iCnt = matcher.groupCount(); i <= iCnt; i++ ) {
				result.add( matcher.group(i) );
			}
		}
		return result;
	}

	/**
	 * Converts all of the characters in value to lower case
	 *
	 * @param value value to convert
	 * @return the String, converted to lowercase.
	 */
	public String toLowerCase( Object value ) {
		return nvl(value).toLowerCase();
	}

	/**
	 * Converts all of the characters in value to upper case
	 *
	 * @param value value to convert
	 * @return the String, converted to uppercase.
	 */
	public String toUpperCase( Object value ) {
		return nvl(value).toUpperCase();
	}

	/**
	 * extract upper characters from word
	 *
	 * @param string word
	 * @return upper characters
	 */
	public String extractUppers( String string ) {
		if( isEmpty( string ) ) return "";
		return string.replaceAll( "[^A-Z]", "" );
	}

	/**
	 * extract lower characters from word
	 *
	 * @param string word
	 * @return lower characters
	 */
	public String extractLowers( String string ) {
		if( isEmpty( string ) ) return "";
		return string.replaceAll( "[^a-z]", "" );
	}

	/**
	 * extract digit from word
	 *
	 * @param string word
	 * @return digit characters
	 */
	public String extractDigit( String string ) {
		if( isEmpty( string ) ) return "";
		return string.replaceAll( "[^0-9]", "" );
	}

	/**
	 * Return value to Y or N
	 *
	 * @param value value to convert
	 *   <table summary="rule" border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *     <tr><td>Y</td><td>N</td></tr>
	 *     <tr>
	 *       <td>
     *         <ul>
	 *           <li>y</li>
	 *           <li>yes</li>
	 *           <li>t</li>
	 *           <li>true</li>
     *         </ul>
	 *       </td>
	 *       <td>
	 *         <ul>
	 *           <li>Null or empty</li>
	 *           <li>Not in 'Y' condition</li>
	 *         </ul>
	 *       </td>
	 *     </tr>
	 *   </table>
	 *
	 * @return 'Y' or 'N'
	 */
	public String toYn( Object value ) {
		return toYn( value, false );
	}

	/**
	 * Return value to Y or N
	 *
	 * @param value value to convert
	 *   <table summary="rule" border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *     <tr><td>Y</td><td>N</td></tr>
	 *     <tr>
	 *       <td>
	 *         <ul>
	 *           <li>y</li>
	 *           <li>yes</li>
	 *           <li>t</li>
	 *           <li>true</li>
	 *         </ul>
	 *       </td>
	 *       <td>
	 *         <ul>
	 *           <li>Null or empty</li>
	 *           <li>Not in 'Y' condition</li>
	 *         </ul>
	 *       </td>
	 *     </tr>
	 *   </table>
	 * @param emptyToY return 'N' when value is empty.
	 *
	 * @return 'Y' or 'N'
	 */
	public String toYn( Object value, boolean emptyToY ) {

		if( isEmpty(value) ) {
			return emptyToY ? "Y" : "N";
		}

		if( value instanceof Boolean ) {
			return ((Boolean) value).compareTo( true ) == 0 ? "Y" : "N";

		} else {

			String text = trim( value );

			if( "y".equalsIgnoreCase(text) )    return "Y";
			if( "yes".equalsIgnoreCase(text) )  return "Y";
			if( "t".equalsIgnoreCase(text) )    return "Y";
			if( "true".equalsIgnoreCase(text) ) return "Y";

			return "N";

		}

	}

	/**
	 * Return value to true or false.
	 *
	 * @param value value to convert
	 *   <table summary="rule" border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *     <tr><td>true</td><td>false</td></tr>
	 *     <tr><td><ul>
	 *         <li>y</li>
	 *         <li>yes</li>
	 *         <li>t</li>
	 *         <li>true</li>
	 *     </ul></td><td><ul>
	 *         <li>Null or empty</li>
	 *         <li>Not in 'Y' condition</li>
	 *     </ul></td></tr>
	 *   </table>
	 * @return true if value is positive
	 */
	public boolean toBoolean( Object value ) {
		return toBoolean( value, false );
	}

	/**
	 * Return value to true or false.
	 *
	 * @param value value to convert
	 *   <table summary="rule" border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *     <tr><td>true</td><td>false</td></tr>
	 *     <tr><td><ul>
	 *         <li>y</li>
	 *         <li>yes</li>
	 *         <li>t</li>
	 *         <li>true</li>
	 *     </ul></td><td><ul>
	 *         <li>Null or empty</li>
	 *         <li>Not in 'Y' condition</li>
	 *     </ul></td></tr>
	 *   </table>
	 * @param emptyToTrue return false when value is empty.
	 * @return true if value is positive
	 */
	public boolean toBoolean( Object value, boolean emptyToTrue ) {
		return "Y".equals( toYn( value, emptyToTrue ) );
	}

	/**
	 * clear XSS(cross site script) pattern in text
	 *
	 * @param value target value
	 * @return escaped string
	 */
	public String clearXss( Object value ) {

		if( isEmpty(value) ) return "";

		StringBuilder sb = new StringBuilder();

		for( char ch : value.toString().toCharArray() ) {

			switch( ch ) {
				case '<' :  sb.append("&lt;");   break;
				case '>' :  sb.append("&gt;");   break;
				case '"' :  sb.append("&#34;");  break;
				case '\'':  sb.append("&#39;");  break;
				case '(' :  sb.append("&#40;");  break;
				case ')' :  sb.append("&#41;");  break;
				case '{' :  sb.append("&#123;"); break;
				case '}' :  sb.append("&#125;"); break;
				default:
					sb.append( ch );
			}

		}

		return sb.toString();

	}

	/**
	 * restore XSS(cross site script) pattern in text
	 *
	 * @param value target value
	 * @return unescaped string
	 */
	public String restoreXss( Object value ) {

		if( isEmpty(value) ) return "";

		StringBuilder sb = new StringBuilder();

		char[] chars = value.toString().toCharArray();

		for( int i = 0, limit = chars.length - 1; i <= limit; i++ ) {

			if( chars[i] != '&' ) {
				sb.append( chars[i] );
				continue;
			}

			String code = String.format( "&%c%c%c%c%c"
				,chars[ Math.min(i + 1,limit) ]
				,chars[ Math.min(i + 2,limit) ]
				,chars[ Math.min(i + 3,limit) ]
				,chars[ Math.min(i + 4,limit) ]
				,chars[ Math.min(i + 5,limit) ]
			);

			if( code.startsWith( "&lt;" ) ) {
				sb.append( '<' ); i+=3;
			} else if ( code.startsWith( "&gt;" ) ) {
				sb.append( '>' ); i+=3;
			} else if ( code.startsWith( "&#34;" ) ) {
				sb.append( '"' ); i+=4;
			} else if ( code.startsWith( "&#39;" ) ) {
				sb.append( '\'' ); i+=4;
			} else if ( code.startsWith( "&#40;" ) ) {
				sb.append( '(' ); i+=4;
			} else if ( code.startsWith( "&#41;" ) ) {
				sb.append( ')' ); i+=4;
			} else if ( code.startsWith( "&#123;" ) ) {
				sb.append( '{' ); i+=5;
			} else if ( code.startsWith( "&#125;" ) ) {
				sb.append( '}' ); i+=5;
			} else {
				sb.append( chars[i] );
			}

		}

		return sb.toString();

	}

	/**
	 * apply mask pattern to word
	 *
	 * <pre>
	 * String word = "01031155023";
	 *
	 * Strings.mask( "",                word ) ); -&gt; ""
	 * Strings.mask( "***_****_****",   word ) ); -&gt; "010_3115_5023"
	 * Strings.mask( "***_****_***",    word ) ); -&gt; "010_3115_502"
	 * Strings.mask( "\\****_****_***", word ) ); -&gt; "*010_3115_502"
	 * Strings.mask( "***_****_***\\*", word ) ); -&gt; "010_3115_502*"
	 * Strings.mask( "***_****_***\\",  word ) ); -&gt; "010_3115_502"
	 * </pre>
	 *
	 * @param pattern	mask pattern to apply. only '*' character is substitute with word.
	 *                  if you want to print '*' character itself, set pattern as '\\*'
	 * @param word  word to mask
	 * @return masked text
	 */
	public String mask( String pattern, String word ) {

		if( isEmpty(pattern) || isEmpty(word) ) return "";

		StringBuilder sb = new StringBuilder();

		int k = 0;

		int lastIdxMask = pattern.length() - 1;
		int lastIdxWord = word.length() - 1;

		for( int i = 0; i <= lastIdxMask; i++ ) {

			char curr = pattern.charAt( i );
			char next = ( i == lastIdxMask ) ? '\n' : pattern.charAt( i + 1 );

			if( curr == '\\' ) {
				if( i != lastIdxMask ) sb.append( next );
				i++;
				continue;
			}

			if( curr == '*' ) {
				sb.append( word.charAt( k ) );
				k++;
				if( k > lastIdxWord ) break;
			} else {
				sb.append( curr );
			}

		}

		return sb.toString();
	}


	/**
	 * get similarity between 0 and 1. <br><br>
	 *
	 * 0 is non-matched and 1 is perfect-matched.
	 *
	 * @param one		string
	 * @param another	another string
     * @return similarity
     */
	public double similarity( String one, String another ) {

		String longer = nvl(one), shorter = nvl(another);
		if( longer.length() < shorter.length() ) {
			String temp = longer;
			longer = shorter; shorter = temp;
		}
		int longerLength = longer.length();
		if (longerLength == 0) return 1.0;

		return (longerLength - getLavenshteinDistance(longer, shorter)) / (double) longerLength;

	}

	/**
	 * get Levenshtein distance
	 *
	 * @param source
	 * @param target
     * @return cost
	 * @see <a href="http://rosettacode.org/wiki/Levenshtein_distance#Java">rosettacode</a>
	 * @see <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">wikipedia</a>
     */
	private int getLavenshteinDistance( String source, String target ) {

		source = toLowerCase( source );
		target = toLowerCase( target );

		int[] costs = new int[ target.length() + 1 ];

		for (int i = 0; i <= source.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= target.length(); j++) {
				if (i == 0) {
					costs[j] = j;
				} else {
					if (j > 0) {
						int newValue = costs[j-1];
						if (source.charAt(i - 1) != target.charAt(j - 1))
							newValue = Math.min( Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j-1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if ( i > 0 )
				costs[target.length()] = lastValue;
		}
		return costs[target.length()];
	}

	/**
	 * convert Throwable(Exception or Error) stacktrace to String
	 *
	 * @param e throwable
	 * @return stacktrace
	 */
	public String toString( Throwable e ) {
		return Exceptions.toString( e );
	}

	/**
	 * check if CJK (Chinese, Japanese, Korean) character exists in text.
	 *
	 * @param text text to check
	 * @return true if CJK character exists.
	 */
	public boolean hasCjkCharacter( String text ) {
		if( isNotEmpty(text) ) {
			for( char c : text.toCharArray() )
				if( Characters.isCJK(c) ) return true;
		}
		return false;
	}

	/**
	 * get line string consisted with a given character.
	 * @param ch		line character
	 * @param length	line length
	 * @return line string
	 */
	public String line( char ch, int length ) {
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < length; i++ ) {
			sb.append( ch );
		}
		return sb.toString();
	}

}