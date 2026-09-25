package com.pcbuilder.domain;

import java.util.Objects;

/**
 * Value object describing the warranty that comes with a build.
 * It is immutable on purpose - a WarrantyPlan never changes after it is created,
 * it can only be replaced by a new one. Two plans with the same values are equal.
 */
public final class WarrantyPlan {

    private final int years;
    private final boolean extended;
    private final boolean accidentalDamageCoverage;

    private WarrantyPlan(int years, boolean extended, boolean accidentalDamageCoverage) {
        if (years < 0 || years > 5) {
            throw new IllegalArgumentException("Warranty years must be between 0 and 5, got " + years);
        }
        if (accidentalDamageCoverage && !extended) {
            throw new IllegalArgumentException("Accidental damage coverage is only available on extended warranties");
        }
        this.years = years;
        this.extended = extended;
        this.accidentalDamageCoverage = accidentalDamageCoverage;
    }

    public static WarrantyPlan standard() {
        return new WarrantyPlan(1, false, false);
    }

    public static WarrantyPlan extended(int years) {
        return new WarrantyPlan(years, true, false);
    }

    public static WarrantyPlan extendedWithAccidentalDamage(int years) {
        return new WarrantyPlan(years, true, true);
    }

    public int years() {
        return years;
    }

    public boolean isExtended() {
        return extended;
    }

    public boolean hasAccidentalDamageCoverage() {
        return accidentalDamageCoverage;
    }

    @Override
    public String toString() {
        return years + " year(s)" + (extended ? ", extended" : "") + (accidentalDamageCoverage ? " + accidental damage" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WarrantyPlan)) return false;
        WarrantyPlan that = (WarrantyPlan) o;
        return years == that.years && extended == that.extended
                && accidentalDamageCoverage == that.accidentalDamageCoverage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(years, extended, accidentalDamageCoverage);
    }
}