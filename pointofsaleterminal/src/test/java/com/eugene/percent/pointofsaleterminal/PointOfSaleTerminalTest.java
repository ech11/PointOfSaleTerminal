package com.eugene.percent.pointofsaleterminal;

import static com.eugene.percent.TestingConstants.DOUBLE_DELTA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.eugene.percent.exceptions.ScanProductException;
import com.eugene.percent.factory.ProductRegistry;
import com.eugene.percent.model.Product;
import com.eugene.percent.model.ProductPrice;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class PointOfSaleTerminalTest {
    private PointOfSaleTerminal pointOfSaleTerminal;

    private Product productA;
    private Product productB;
    private Product productC;
    private Product productD;
    private ProductPrice productPriceA;
    private ProductPrice productPriceB;
    private ProductPrice productPriceC;
    private ProductPrice productPriceD;

    @Before
    public void setUp() {
        productA = Product.builder().productCode("A").build();
        productB = Product.builder().productCode("B").build();
        productC = Product.builder().productCode("C").build();
        productD = Product.builder().productCode("D").build();

        productPriceA = new ProductPrice(1.25, 3, 3);
        productPriceB = new ProductPrice(4.25, 1, 4.25);
        productPriceC = new ProductPrice(1, 6, 5);
        productPriceD = new ProductPrice(0.75, 1, 0.75);

        setPricing();

        pointOfSaleTerminal = new PointOfSaleTerminal();
    }

    private void setPricing() {
        ProductRegistry.register(productA, productPriceA);
        ProductRegistry.register(productB, productPriceB);
        ProductRegistry.register(productC, productPriceC);
        ProductRegistry.register(productD, productPriceD);
    }

    @Test(expected = Exception.class)
    public void test_scan_nullProduct() throws Exception {
        pointOfSaleTerminal.scan(null);
    }

    @Test(expected = ScanProductException.class)
    public void scan_nonRegisteredProduct() throws Exception {
        Product unregisteredProduct = Product.builder().productCode("unregisteredProduct").build();

        assertFalse(ProductRegistry.getProducts().contains(unregisteredProduct));

        pointOfSaleTerminal.scan(unregisteredProduct);
    }

    @Test
    public void test_scan_multiple() throws Exception {
        int numScans = pointOfSaleTerminal.scan(productA);
        assertEquals(1, numScans);

        numScans = pointOfSaleTerminal.scan(productA);
        assertEquals(2, numScans);
    }

    @Test
    public void test_setPricing() {
        ProductPrice productPrice = ProductRegistry.getPrice(productA);
        assertNotNull(productPrice);
        assertEquals(productPriceA, productPrice);

        productPrice = ProductRegistry.getPrice(productB);
        assertNotNull(productPrice);
        assertEquals(productPriceB, productPrice);

        productPrice = ProductRegistry.getPrice(productC);
        assertNotNull(productPrice);
        assertEquals(productPriceC, productPrice);

        productPrice = ProductRegistry.getPrice(productD);
        assertNotNull(productPrice);
        assertEquals(productPriceD, productPrice);

        assertEquals(4, ProductRegistry.getProducts().size());
    }

    @Test
    public void test_calculateTotal() throws Exception {
        pointOfSaleTerminal.scan(productA);
        BigDecimal price = pointOfSaleTerminal.calculateTotal();
        assertEquals(productPriceA.getPricePerUnit(), price.doubleValue(), DOUBLE_DELTA);

        int numScans = pointOfSaleTerminal.scan(productA);
        assertEquals(2, numScans);
        price = pointOfSaleTerminal.calculateTotal();
        assertEquals(numScans*productPriceA.getPricePerUnit(), price.doubleValue(), DOUBLE_DELTA);

        numScans = pointOfSaleTerminal.scan(productB);
        assertEquals(1, numScans);
        price = pointOfSaleTerminal.calculateTotal();
        assertEquals(2*productPriceA.getPricePerUnit() + productPriceB.getPricePerUnit(),
                price.doubleValue(), DOUBLE_DELTA);

        pointOfSaleTerminal.clear();

        for (int i = 0; i < productPriceA.getNumberOfUnitsInVolume(); ++i) {
            numScans = pointOfSaleTerminal.scan(productA);
        }
        assertEquals(productPriceA.getNumberOfUnitsInVolume(), numScans);
        price = pointOfSaleTerminal.calculateTotal();
        assertEquals(productPriceA.getPricePerVolume(), price.doubleValue(), DOUBLE_DELTA);
    }

    @Test
    public void test_ABCDABA() throws Exception {
        pointOfSaleTerminal.scan(productA);
        pointOfSaleTerminal.scan(productB);
        pointOfSaleTerminal.scan(productC);
        pointOfSaleTerminal.scan(productD);
        pointOfSaleTerminal.scan(productA);
        pointOfSaleTerminal.scan(productB);
        pointOfSaleTerminal.scan(productA);

        BigDecimal price = pointOfSaleTerminal.calculateTotal();
        assertEquals(13.25, price.doubleValue(), DOUBLE_DELTA);
    }

    @Test
    public void test_CCCCCCC() throws Exception {
        pointOfSaleTerminal.scan(productC);
        pointOfSaleTerminal.scan(productC);
        pointOfSaleTerminal.scan(productC);
        pointOfSaleTerminal.scan(productC);
        pointOfSaleTerminal.scan(productC);
        pointOfSaleTerminal.scan(productC);
        pointOfSaleTerminal.scan(productC);

        BigDecimal price = pointOfSaleTerminal.calculateTotal();
        assertEquals(6.0, price.doubleValue(), DOUBLE_DELTA);
    }

    @Test
    public void test_ABCD() throws Exception {
        pointOfSaleTerminal.scan(productA);
        pointOfSaleTerminal.scan(productB);
        pointOfSaleTerminal.scan(productC);
        pointOfSaleTerminal.scan(productD);

        BigDecimal price = pointOfSaleTerminal.calculateTotal();
        assertEquals(7.25, price.doubleValue(), DOUBLE_DELTA);
    }
}