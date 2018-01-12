package io.smartcat.ranger.core.arithmetic;

import io.smartcat.ranger.core.Value;

/**
 * Multiplies two values and returns result as {@code Float} type.
 */
public class MultiplicationValueFloat extends Value<Float> {

    @SuppressWarnings("rawtypes")
    private final Value factor1;

    @SuppressWarnings("rawtypes")
    private final Value factor2;

    /**
     * Creates Multiplication value with specified <code>factor1</code> and <code>factor2</code>.
     *
     * @param factor1 Value which will be used as factor1 for this multiplication.
     * @param factor2 Value which will be used as factor2 for this multiplication.
     */
    @SuppressWarnings("rawtypes")
    public MultiplicationValueFloat(Value factor1, Value factor2) {
        this.factor1 = factor1;
        this.factor2 = factor2;
    }

    @Override
    public void reset() {
        super.reset();
        factor1.reset();
        factor2.reset();
    }

    @Override
    protected void eval() {
        val = ((Number) factor1.get()).floatValue() * ((Number) factor2.get()).floatValue();
    }
}
