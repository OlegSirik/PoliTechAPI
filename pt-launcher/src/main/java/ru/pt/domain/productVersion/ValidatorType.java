package ru.pt.domain.productVersion;

public enum ValidatorType {
    QUOTE("QUOTE"),
    SAVE("SAVE");
    private final String value;

    ValidatorType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
        
    
}
