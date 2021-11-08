package com.eugene.percent.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product pricing logic.
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductPrice {
    private double pricePerUnit;
    private int numberOfUnitsInVolume;
    private double pricePerVolume;

    public ProductPrice(double pricePerUnit, int numberOfUnitsInVolume, double pricePerVolume) {
        assert pricePerUnit > 0.0;
        assert numberOfUnitsInVolume > 0;
        assert pricePerVolume > 0.0;

        this.pricePerUnit = pricePerUnit;
        this.numberOfUnitsInVolume = numberOfUnitsInVolume;
        this.pricePerVolume = pricePerVolume;
    }

    /**
     * @param numberOfUnits
     * @return price of numberOfUnits of Product, taking into account any volume discounts
     */
    public double calculatePrice(int numberOfUnits) {
        if (numberOfUnits <= 0) {
            return 0.0;
        }

        int numberOfVolumeUnits = (int)(numberOfUnits/this.numberOfUnitsInVolume);
        int numberOfNonVolumeUnits = numberOfUnits % this.numberOfUnitsInVolume;

        return numberOfVolumeUnits * this.pricePerVolume + numberOfNonVolumeUnits * this.pricePerUnit;
    }
}
