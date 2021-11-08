package com.eugene.percent;

import com.eugene.percent.exceptions.ScanProductException;
import com.eugene.percent.factory.ProductRegistry;
import com.eugene.percent.model.Product;
import com.eugene.percent.model.ProductPrice;
import com.eugene.percent.pointofsaleterminal.PointOfSaleTerminal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Crude demo of a store.
 * Starts {@value #NUM_TERMINALS} {@link PointOfSaleTerminal}s and runs them for {@value #DEMO_MILLIS}ms.
 */
public class StoreDemo {
    private static final int NUM_TERMINALS = 5;
    private static final int DEMO_MILLIS = 100;
    private static final int POS_MAX_SLEEP_MILLIS = 80;

    private final List<Thread> terminals;

    public StoreDemo() {
        this.terminals = new ArrayList<>();

        for (int i = 1; i <= NUM_TERMINALS; ++i) {
            this.terminals.add(new Thread("Terminal " + i) {
                public void run() {
                    runPos(getName());
                }
            });
        }
    }

    /**
     * Runs store until closing.
     */
    public void start() {
        terminals.forEach(Thread::start);
    }

    /**
     * Closes the store.
     */
    public void stop() {
        terminals.forEach(Thread::interrupt);
    }

    /**
     * Waits for store to close.
     */
    public void join() {
        terminals.forEach(terminal -> {
            try {
                terminal.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     *
     * Runs terminal in perpetuity.
     *
     * @param terminalName terminal id.
     */
    private static void runPos(final String terminalName) {
        PointOfSaleTerminal pointOfSaleTerminal = new PointOfSaleTerminal();

        boolean isInterrupted = false;

        System.out.println("Terminal: " + terminalName + " - starting...");

        int numCustomersServed = 0;
        BigDecimal total = BigDecimal.valueOf(0.0);

        while (true) {
            if (isInterrupted) {
                System.out.println(
                        "Terminal: " + terminalName + " - done. " +
                        "Customers Served: " + numCustomersServed + ". " +
                        "Total price: " + total
                );
                return;
            }

            pointOfSaleTerminal.clear();

            List<Product> productList = ProductRegistry.generateRandomProductScan();
            for (Product product : productList) {
                try {
                    pointOfSaleTerminal.scan(product);
                } catch (ScanProductException e) {
                    System.err.println("Terminal: " + terminalName + " cannot scan " + product);
                    e.printStackTrace();
                }
            }

            BigDecimal totalPrice = pointOfSaleTerminal.calculateTotal();

            ++numCustomersServed;
            total = total.add(totalPrice);
            System.out.println("Terminal: " + terminalName + ", Total Price: " + totalPrice + ", Products: " + productList);

            isInterrupted = Thread.currentThread().isInterrupted();
            try {
                if (!isInterrupted) {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(POS_MAX_SLEEP_MILLIS));
                }
            } catch (InterruptedException e) {
                isInterrupted = true;
            }
        }
    }

    /**
     * Sets {@link ProductPrice}s and {@link ProductPrice}s for a sample store.
     */
    public static void setPricing() {
        Product productA = Product.builder().productCode("A").build();
        Product productB = Product.builder().productCode("B").build();
        Product productC = Product.builder().productCode("C").build();
        Product productD = Product.builder().productCode("D").build();

        ProductPrice productPriceA = new ProductPrice(1.25, 3, 3);
        ProductPrice productPriceB = new ProductPrice(4.25, 1, 4.25);
        ProductPrice productPriceC = new ProductPrice(1, 6, 5);
        ProductPrice productPriceD = new ProductPrice(0.75, 1, 0.75);

        ProductRegistry.register(productA, productPriceA);
        ProductRegistry.register(productB, productPriceB);
        ProductRegistry.register(productC, productPriceC);
        ProductRegistry.register(productD, productPriceD);
    }

    /**
     * Sample main.
     * Expectation is for {@link PointOfSaleTerminal} to be used as a bean.
     *
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        StoreDemo.setPricing();

        StoreDemo storeDemo = new StoreDemo();

        storeDemo.start();
        Thread.sleep(DEMO_MILLIS);
        storeDemo.stop();
        storeDemo.join();
    }
}
