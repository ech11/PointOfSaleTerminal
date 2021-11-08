package com.eugene.percent.factory;

import com.eugene.percent.model.Product;
import com.eugene.percent.model.ProductPrice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Registry of {@link Product}s and their {@link ProductPrice}s.
 *
 * Prices are assumed to be static and updated off-hours when the store is closed.
 * Prices are assumed to be in the same currency.
 *
 * New products can be registered by a manager intra-day.
 *
 * This registry models a singleton - "one per store".
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRegistry {
    private final static int MAX_SCANS_PER_PRODUCT = 3;

    private final static Map<Product, ProductPrice> productMap = new HashMap<>();

    /**
     * Registers new {@link Product} and its pricePerUnit.
     * No-volume discount is used.
     *
     * @param product {@link Product}
     * @param pricePerUnit
     */
    public static void register(@NonNull Product product, double pricePerUnit)  {
        ProductPrice productPrice = new ProductPrice(pricePerUnit, 1, pricePerUnit);

        register(product, productPrice);
    }

    /**
     *
     * Registers new {@link Product} and its {@link ProductPrice}.
     *
     * If product is already registered, its price is updated.
     *
     * @param product {@link Product}
     * @param productPrice {@link ProductPrice}
     */
    public static void register(@NonNull Product product, @NonNull ProductPrice productPrice)  {
        productMap.put(product, productPrice);
    }

    /**
     * De-registers product (for example when sold out).
     *
     * @param product
     * @return last registered {@link ProductPrice}
     */
    public static ProductPrice deregister(@NonNull Product product) {
        return productMap.remove(product);
    }

    /**
     * @param product {@link Product}
     * @return {@link ProductPrice} for a {@link Product}.
     */
    public static ProductPrice getPrice(@NonNull Product product) {
        return productMap.get(product);
    }

    /**
     * @return set of registered {@link Product}s.
     */
    public static Set<Product> getProducts() {
        return productMap.keySet();
    }


    /**
     * Resets registry.
     */
    public static void reset() {
        productMap.clear();
    }

    /**
     * USED for testing only.
     *
     * Produces a random list of {@link Product} to scan.
     * Each {@link Product} can be randomly chosen up to {@value #MAX_SCANS_PER_PRODUCT} times.
     *
     * @return list of {@link Product}s.
     */
    public static List<Product> generateRandomProductScan() {
        List<Product> result = new ArrayList<>();

        Set<Product> productSet = ProductRegistry.getProducts();

        for (Product product : productSet) {
            List<Product> productList =
                    Collections.nCopies(ThreadLocalRandom.current().nextInt(MAX_SCANS_PER_PRODUCT), product);
            result.addAll(productList);
        }

        Collections.shuffle(result);

        return result;
    }
}
