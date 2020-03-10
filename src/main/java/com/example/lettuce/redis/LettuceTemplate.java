package com.example.lettuce.redis;

public class LettuceTemplate<K, V> {
    private LettuceValueOperations<K, V> valueOperations;

    public void setLettuceConnection(LettuceConnection<K, V> lettuceConnection) {
        valueOperations = new LettuceValueOperations<>(lettuceConnection);
    }

    public LettuceValueOperations<K, V> getValueOperations() {
        return valueOperations;
    }
}
