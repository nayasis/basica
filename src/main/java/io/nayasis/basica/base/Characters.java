package io.nayasis.basica.base;

import lombok.experimental.UtilityClass;

import java.lang.Character.UnicodeBlock;
import java.util.HashSet;
import java.util.Set;

/**
 * Character Utility
 * 
 */
@UtilityClass
public class Characters {

	private Set<UnicodeBlock> CHINESE = new HashSet<UnicodeBlock>() {{
		add( UnicodeBlock.CJK_COMPATIBILITY );
		add( UnicodeBlock.CJK_COMPATIBILITY_FORMS );
		add( UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS );
		add( UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT );
		add( UnicodeBlock.CJK_RADICALS_SUPPLEMENT );
		add( UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION );
		add( UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS );
		add( UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A );
		add( UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B );
		add( UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C );
		add( UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D );
		add( UnicodeBlock.KANGXI_RADICALS );
		add( UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS );
	}};

	private Set<UnicodeBlock> KOREAN = new HashSet<UnicodeBlock>() {{
		add( UnicodeBlock.HANGUL_COMPATIBILITY_JAMO );
		add( UnicodeBlock.HANGUL_JAMO );
		add( UnicodeBlock.HANGUL_JAMO_EXTENDED_A );
		add( UnicodeBlock.HANGUL_JAMO_EXTENDED_B );
		add( UnicodeBlock.HANGUL_SYLLABLES );
	}};

	private Set<UnicodeBlock> JAPANESE = new HashSet<UnicodeBlock>() {{
		add( UnicodeBlock.HIRAGANA );
		add( UnicodeBlock.KATAKANA );
		add( UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS );
		add( UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION );
	}};

	private Set<UnicodeBlock> CJK = new HashSet<UnicodeBlock>() {{
		addAll( CHINESE  );
		addAll( KOREAN   );
		addAll( JAPANESE );
		add( UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS );
	}};

	public char NULL_CHAR = '\0';

	/** Hangul Chosung */
	private char[] HANGUL_1ST = new char[] { 'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ' };

	/** Hangul Joongsung */
	private char[] HANGUL_2ND = new char[] { 'ㅏ','ㅐ','ㅑ','ㅒ','ㅓ','ㅔ','ㅕ','ㅖ','ㅗ','ㅘ','ㅙ','ㅚ','ㅛ','ㅜ','ㅝ','ㅞ','ㅟ','ㅠ','ㅡ','ㅢ','ㅣ' };

	/** Hangul Jongsung */
	private char[] HANGUL_3RD = new char[] { NULL_CHAR,'ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ','ㄽ','ㄾ','ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ' };


	/** font width of Full-width character */
	private double fullwidth = 1;

	/** font width of Half-width character */
	private double halfwidth = 1;

	/**
	 * set font width of Full-width character
	 * 
	 * @param width console font width to print
	 */
	public void fullwidth( double width ) {
		fullwidth = width;
	}

	/**
	 * get font width of Full-width
	 * 
	 * @return console font width to print
	 */
	public double fullwidth() {
		return fullwidth;
	}

	/**
	 * set font width of Half-width character
	 *
	 * @param width console font width to print
	 */
	public void halfwidth( double width ) {
		halfwidth = width;
	}

	/**
	 * get font width of Half-width
	 *
	 * @return console font width to print
	 */
	public double halfwidth() {
		return halfwidth;
	}

	public boolean isFontWidthModified() {
		return fullwidth != 1.0 || halfwidth != 1.0;
	}

	/**
	 * resolve Hangul character to Chosung, Joongsung, Jonsung
	 * 
     * <pre>
     * Characters.resolveKorean( '롱' ); -> [ 'ㄹ','ㅗ','ㅇ']
     * Characters.resolveKorean( '수' ); -> ['ㅅ','ㅜ','\0' ]
     * Characters.resolveKorean( 'H'  ); -> null
     * </pre>
	 * 
	 * @param ch character to resolve
	 * @return resolved character array (null if it can not be resolved.)
	 */
	public char[] resolveKorean(char ch ) {
		
		// if it can not be resolved
		if( ch < 0xAC00 || ch > 0xD79F ) return null;
		
		ch -= 0xAC00;

    	int idx3rd = ch % 28;
    	int idx2nd = ( (ch - idx3rd) / 28 ) % 21;
    	int idx1st = ((ch - idx3rd) / 28) / 21;

    	char[] result = new char[3];

    	result[0] = HANGUL_1ST[ idx1st ];
    	result[1] = HANGUL_2ND[ idx2nd ];
    	result[2] = HANGUL_3RD[ idx3rd ];

    	return result;		
		
	}
	
	/**
	 * check if character has Hangul Jonsung.
	 * 
	 * <pre>
	 * Characters.hasHangulJongsung( 'H'  ) -> false
	 * Characters.hasHangulJongsung( '수' ) -> false
	 * Characters.hasHangulJongsung( '롱' ) -> true
	 * </pre>
	 * 
	 * @param ch character to check
	 * @return true if character has Hangul Jonsung.
	 */
	public boolean hasHangulJongsung( char ch ) {
		
		char[] result = resolveKorean( ch );
		
		if( result == null ) return false;
		
		return result[2] != NULL_CHAR;
		
	}

	/**
	 * check if character is half-width
	 * 
	 * @param ch character to check
	 * @return true if character is half-width
	 * 
	 * @see <a href="http://unicode.org/reports/tr11">http://unicode.org/reports/tr11</a>
	 * @see <a href="http://unicode.org/charts/PDF/UFF00.pdf">http://unicode.org/charts/PDF/UFF00.pdf</a>
	 *
	 */
	public boolean isHalfWidth( char ch ) {

		if( ch < 0x0020 ) return true;  // special character

		if( 0x0020 <= ch && ch <= 0x007F ) return true;  // ASCII (Latin characters, symbols, punctuation,numbers)
		
		// FF61 ~ FF64 : Halfwidth CJK punctuation
		// FF65 ~ FF9F : Halfwidth Katakanana variants
		// FFA0 ~ FFDC : Halfwidth Hangul variants
		if( 0xFF61 <= ch && ch <= 0xFFDC ) return true;
		
		// FFE8 ~ FFEE : Halfwidth symbol variants
		if( 0xFFE8 <= ch && ch <= 0xFFEE ) return true;
		
		return false;
		
	}

	/**
	 * get font width to print
	 * 
	 * @param ch character to check
	 * @return font width to print
	 */
	public double getFontWidth( char ch ) {
		if( isHalfWidth( ch ) ) return halfwidth;
		if( isCJK( ch ) ) return fullwidth;
		return 1;
	}

	/**
	 * check if character is korean
	 *
	 * @param ch character
	 * @return true if character is korean
	 */
	public boolean isKorean( char ch ) {
		if( KOREAN.contains(UnicodeBlock.of(ch)) ) return true;
		return 0xFFA0 <= ch && ch <= 0xFFDC;

	}

	/**
	 * check if character is japanese
	 *
	 * @param ch character
	 * @return true if character is japanese
	 */
	public boolean isJapanese( char ch ) {
		if( JAPANESE.contains(UnicodeBlock.of(ch)) ) return true;
		return 0xFF65 <= ch && ch <= 0xFF9F;

	}

	/**
	 * check if character is chinese
	 * 
	 * @param ch character
	 * @return true if character is chinese
	 */
	public boolean isChinese( char ch ) {
		if( CHINESE.contains(UnicodeBlock.of(ch)) ) return true;
		return 0xFF65 <= ch && ch <= 0xFF9F;
	}

	/**
	 * check if character is chinese or japanese or korean
	 *
	 * @param ch character
	 * @return true if character is chinese or japanese or korean
	 */
	public boolean isCJK( char ch ) {
		return CJK.contains( UnicodeBlock.of(ch) );
	}

}