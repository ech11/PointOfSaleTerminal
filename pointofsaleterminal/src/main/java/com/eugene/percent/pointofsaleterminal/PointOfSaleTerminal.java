package com.eugene.percent.pointofsaleterminal;

import com.eugene.percent.exceptions.ScanProductException;
import com.eugene.percent.factory.ProductRegistry;
import com.eugene.percent.model.Product;
import com.eugene.percent.model.ProductPrice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;

/**
 * Point of sale system representing a session for a single customer at 1 register.
 *
 * Assumptions:
 * - {@link Product}s are only scanned for addition to the cart
 */
public class PointOfSaleTerminal {
    private final Map<Product, Integer> productsScanned; //aka cart

    public PointOfSaleTerminal() {
        this.productsScanned = new HashMap<>();
    }

    /**
     * Adds product to this pos session.
     *
     * @param product {@link Product}
     * @return number of units of {@link Product} scanned so far.
     * @throws ScanProductException if {@link Product} is not in the {@link ProductRegistry}
     */
    public int scan(@NonNull Product product) throws ScanProductException {
        ProductPrice productPrice = ProductRegistry.getPrice(product);

        if (productPrice == null) {
            throw new ScanProductException("Product (" + product + ") is not in the registry. Get manager to help.");
        }

        int numOfUnits = productsScanned.getOrDefault(product, 0) + 1;

        productsScanned.put(product, numOfUnits);

        return numOfUnits;
    }


    /**
     * Assumes all scanned {@link Product}s are registered.
     *
     * @return total price of this pos session.
     */
    public BigDecimal calculateTotal() {
        BigDecimal result = BigDecimal.valueOf(0.0);

        for (Map.Entry<Product, Integer> entry : productsScanned.entrySet()) {
            Product product = entry.getKey();
            Integer numUnits = entry.getValue();

            ProductPrice productPrice = ProductRegistry.getPrice(product);
            assert productPrice != null;

            result = result.add(BigDecimal.valueOf(productPrice.calculatePrice(numUnits)));
        }

        return result;
    }

    /**
     * Removes all scanned {@link Product}s from the cart.
     */
    public void clear() {
        productsScanned.clear();
    }
}
