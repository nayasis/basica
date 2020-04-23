package io.nayasis.basica.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypesTest {

    @Test
    public void isMap() {
    }

    @Test
    public void isMap1() {
    }

    @Test
    public void isCollection() {
    }

    @Test
    public void isCollection1() {
    }

    @Test
    public void isArray() {
    }

    @Test
    public void isArray1() {
    }

    @Test
    public void isArrayOrCollection() {
    }

    @Test
    public void isArrayOrCollection1() {
    }

    @Test
    public void isBoolean() {
    }

    @Test
    public void isBoolean1() {
    }

    @Test
    public void isInt() {
    }

    @Test
    public void isInt1() {
    }

    @Test
    public void isShort() {
    }

    @Test
    public void isShort1() {
    }

    @Test
    public void isByte() {
    }

    @Test
    public void isByte1() {
    }

    @Test
    public void isIntLike() {
    }

    @Test
    public void isIntLike1() {
    }

    @Test
    public void isInt2() {
        assertTrue( Types.isInt("-123") );
        assertTrue( Types.isInt("+123") );
        assertTrue( Types.isInt("123") );
        assertFalse( Types.isInt("123.1") );
        assertFalse( Types.isInt("-1.30") );
    }

    @Test
    public void isPositiveInt() {
        assertFalse( Types.isPositiveInt("-123") );
        assertTrue( Types.isPositiveInt("+123") );
        assertTrue( Types.isPositiveInt("123") );
        assertFalse( Types.isPositiveInt("123.1") );
        assertFalse( Types.isPositiveInt("-1.30") );
    }

    @Test
    public void isLong() {
    }

    @Test
    public void isLong1() {
    }

    @Test
    public void isFloat() {
    }

    @Test
    public void isFloat1() {
    }

    @Test
    public void isDouble() {
    }

    @Test
    public void isDouble1() {
    }

    @Test
    public void isBigDecimal() {
    }

    @Test
    public void isBigDecimal1() {
    }

    @Test
    public void isBigInteger() {
    }

    @Test
    public void isBigInteger1() {
    }

    @Test
    public void isChar() {
    }

    @Test
    public void isChar1() {
    }

    @Test
    public void isString() {
    }

    @Test
    public void isString1() {
    }

    @Test
    public void isNumeric() {
        assertTrue( Types.isNumeric("1.234") );
        assertFalse( Types.isNumeric("1.234A") );
    }

    @Test
    public void isPrimitive() {
    }

    @Test
    public void isPrimitive1() {
    }

    @Test
    public void isNotPrimitive() {
    }

    @Test
    public void toList() {


        
        
    }

    @Test
    public void toCollection() {
    }

    @Test
    public void toArray() {

        List<String> list = new ArrayList<>();
        list.add( "1" );
        list.add( "2" );
        list.add( "3" );

        String[] array = Types.toArray( list, String.class );

        Assertions.assertEquals( "[1, 2, 3]", Arrays.toString(array) );

    }

    @Test
    public void toString1() {
    }

    @Test
    public void toInt() {
    }

    @Test
    public void toLong() {
    }

    @Test
    public void toFloat() {
    }

    @Test
    public void toDouble() {
    }

    @Test
    public void toBoolean() {
    }

    @Test
    public void toByte() {
    }

    @Test
    public void toShort() {
    }

    @Test
    public void toChar() {
    }

}