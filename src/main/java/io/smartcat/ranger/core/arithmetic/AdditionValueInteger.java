package io.smartcat.ranger.core.arithmetic;

import io.smartcat.ranger.core.Value;

/**
 * Adds up two values and returns result as {@code Integer} type.
 */
public class AdditionValueInteger extends Value<Integer> {

    @SuppressWarnings("rawtypes")
    private final Value summand1;

    @SuppressWarnings("rawtypes")
    private final Value summand2;

    /**
     * Creates Addition value with specified <code>summand1</code> and <code>summand2</code>.
     *
     * @param summand1 Value which will be used as summand1 for this addition.
     * @param summand2 Value which will be used as summand2 for this addition.
     */
    @SuppressWarnings("rawtypes")
    public AdditionValueInteger(Value summand1, Value summand2) {
        this.summand1 = summand1;
        this.summand2 = summand2;
    }

    @Override
    public void reset() {
        super.reset();
        summand1.reset();
        summand2.reset();
    }

    @Override
    protected void eval() {
        val = ((Number) summand1.get()).intValue() + ((Number) summand2.get()).intValue();
    }
}
