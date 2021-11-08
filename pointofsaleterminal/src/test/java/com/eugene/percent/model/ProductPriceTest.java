package com.eugene.percent.model;

import static com.eugene.percent.TestingConstants.DOUBLE_DELTA;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProductPriceTest {
    @Test(expected = AssertionError.class)
    public void test_invalid_pricePerUnit() {
        new ProductPrice(-1, 1, 1);
    }

    @Test(expected = AssertionError.class)
    public void test_invalid_numberOfUnitsInVolume() {
        new ProductPrice(1, 0, 1);
    }

    @Test(expected = AssertionError.class)
    public void test_invalid_pricePerVolume() {
        new ProductPrice(1, 1, 0);
    }

    @Test
    public void test_calculatePrice_nonPositiveQuantity() {
        ProductPrice productPrice = new ProductPrice(1, 1, 1);

        double price = productPrice.calculatePrice(0);
        assertEquals(0.0, price, DOUBLE_DELTA);

        price = productPrice.calculatePrice(-1);
        assertEquals(0.0, price, DOUBLE_DELTA);
    }

    @Test
    public void test_calculatePrice_positiveQuantity() {
        ProductPrice productPrice = new ProductPrice(10, 5, 45);

        //non-volume discount
        double price = productPrice.calculatePrice(1);
        assertEquals(10.0, price, DOUBLE_DELTA);

        price = productPrice.calculatePrice(4);
        assertEquals(40.0, price, DOUBLE_DELTA);

        //volume discount
        price = productPrice.calculatePrice(5);
        assertEquals(45.0, price, DOUBLE_DELTA);

        price = productPrice.calculatePrice(11);
        assertEquals(100.0, price, DOUBLE_DELTA);
    }
}