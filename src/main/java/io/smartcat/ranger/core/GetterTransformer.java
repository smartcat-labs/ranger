package io.smartcat.ranger.core;

import java.util.Map;

/**
 * Extracts property value for a given key from given composite value.
 *
 * @param <T> Type this value would evaluate to.
 */
public class GetterTransformer<T> extends Transformer<T> {

    private final String keyName;
    private final Value<?> value;


    /**
     * Constructs getter transformer with specified <code>key</code> and <code>value</code>.
     *
     * @param keyName Name of property for which to attempt get.
     * @param keyType Type of property for which to attempt get.
     * @param value Value from which to attempt get.
     */
    public GetterTransformer(String keyName, Class<T> keyType, Value<?> value) {
        if (keyName == null || keyName.isEmpty()) {
            throw new IllegalArgumentException("keyName cannot be null nor empty.");
        }
        if (keyType == null) {
            throw new IllegalArgumentException("keyType cannot be null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Composite value cannot be null.");
        }
        this.keyName = keyName;
        this.value = value;
    }

    @Override
    public void reset() {
        super.reset();
        value.reset();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void eval() {
        Map<String, Object> v = (Map) value.get();
        val = (T) v.get(keyName);
    }
}
