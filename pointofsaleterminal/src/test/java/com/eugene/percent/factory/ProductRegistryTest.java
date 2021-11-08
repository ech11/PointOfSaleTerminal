package com.eugene.percent.factory;

import static com.eugene.percent.TestingConstants.MAX_SCANS_PER_PRODUCT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.eugene.percent.model.Product;
import com.eugene.percent.model.ProductPrice;

import org.junit.Before;
import org.junit.Test;

public class ProductRegistryTest {
    private Product productA;
    private Product productB;
    private ProductPrice productPriceNoVolumeDiscount;
    private ProductPrice productPriceVolumeDiscount;

    @Before
    public void setup() {
        productA = Product.builder().productCode("A").build();
        productB = Product.builder().productCode("B").build();
        productPriceNoVolumeDiscount = new ProductPrice(10, 1, 10);
        productPriceVolumeDiscount = new ProductPrice(10, 5, 45);

        ProductRegistry.reset();
    }

    @Test(expected = Exception.class)
    public void test_registry_nullProduct() {
        ProductRegistry.register(null, 1);
    }

    @Test(expected = Exception.class)
    public void test_registry_nullPrice() {
        ProductRegistry.register(productA, null);
    }

    @Test
    public void test_registry_goodProduct() {
        ProductRegistry.register(productA, productPriceNoVolumeDiscount);
        assertEquals(productPriceNoVolumeDiscount, ProductRegistry.getPrice(productA));
        assertEquals(1, ProductRegistry.getProducts().size());
    }

    @Test
    public void test_registry_updatePrice() {
        ProductRegistry.register(productA, productPriceNoVolumeDiscount);
        ProductPrice productPrice1 = ProductRegistry.getPrice(productA);
        assertEquals(productPriceNoVolumeDiscount, productPrice1);

        ProductRegistry.register(productA, productPriceVolumeDiscount);
        ProductPrice productPrice2 = ProductRegistry.getPrice(productA);
        assertEquals(productPriceVolumeDiscount, productPrice2);

        assertNotEquals(productPrice1, productPrice2);

        assertEquals(1, ProductRegistry.getProducts().size());
    }

    @Test
    public void test_registry_deregister() {
        ProductRegistry.register(productA, productPriceNoVolumeDiscount);
        assertEquals(1, ProductRegistry.getProducts().size());

        ProductRegistry.deregister(productA);
        assertEquals(0, ProductRegistry.getProducts().size());
    }

    @Test
    public void test_registry_clear() {
        ProductRegistry.register(productA, productPriceNoVolumeDiscount);
        assertEquals(1, ProductRegistry.getProducts().size());

        ProductRegistry.register(productB, productPriceNoVolumeDiscount);
        assertEquals(2, ProductRegistry.getProducts().size());

        ProductRegistry.reset();
        assertEquals(0, ProductRegistry.getProducts().size());
    }

    @Test
    public void test_generateRandomProductScan() {
        assertTrue(ProductRegistry.generateRandomProductScan().isEmpty());

        ProductRegistry.register(productA, productPriceNoVolumeDiscount);
        int n = 100;
        int numProducts = 0;
        for (int i = 0; i < n; ++i) {
            numProducts += ProductRegistry.generateRandomProductScan().size();
        }
        assertTrue(numProducts/n < MAX_SCANS_PER_PRODUCT);
    }
}