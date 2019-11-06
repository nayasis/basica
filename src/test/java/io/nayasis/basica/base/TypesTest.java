package io.nayasis.basica.base;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertTrue( Types.isInt("-123") );
        Assert.assertTrue( Types.isInt("+123") );
        Assert.assertTrue( Types.isInt("123") );
        Assert.assertFalse( Types.isInt("123.1") );
        Assert.assertFalse( Types.isInt("-1.30") );
    }

    @Test
    public void isPositiveInt() {
        Assert.assertFalse( Types.isPositiveInt("-123") );
        Assert.assertTrue( Types.isPositiveInt("+123") );
        Assert.assertTrue( Types.isPositiveInt("123") );
        Assert.assertFalse( Types.isPositiveInt("123.1") );
        Assert.assertFalse( Types.isPositiveInt("-1.30") );
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
        Assert.assertTrue( Types.isNumeric("1.234") );
        Assert.assertFalse( Types.isNumeric("1.234A") );
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