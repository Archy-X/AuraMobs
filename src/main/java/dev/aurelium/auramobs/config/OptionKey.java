package dev.aurelium.auramobs.config;

import java.util.Objects;

public class OptionKey {

    private final String key;

    public OptionKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionKey optionKey = (OptionKey) o;
        return Objects.equals(key, optionKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
