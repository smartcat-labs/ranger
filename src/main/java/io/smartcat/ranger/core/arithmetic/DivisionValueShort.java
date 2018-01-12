package io.smartcat.ranger.core.arithmetic;

import io.smartcat.ranger.core.Value;

/**
 * Divides two values and returns result as {@code Short} type.
 */
public class DivisionValueShort extends Value<Short> {

    @SuppressWarnings("rawtypes")
    private final Value dividend;

    @SuppressWarnings("rawtypes")
    private final Value divisor;

    /**
     * Creates Division value with specified <code>dividend</code> and <code>divisor</code>.
     *
     * @param dividend Value which will be used as dividend for this division.
     * @param divisor Value which will be used as divisor for this division.
     */
    @SuppressWarnings("rawtypes")
    public DivisionValueShort(Value dividend, Value divisor) {
        this.dividend = dividend;
        this.divisor = divisor;
    }

    @Override
    public void reset() {
        super.reset();
        dividend.reset();
        divisor.reset();
    }

    @Override
    protected void eval() {
        val = (short) (((Number) dividend.get()).shortValue() / ((Number) divisor.get()).shortValue());
    }
}
