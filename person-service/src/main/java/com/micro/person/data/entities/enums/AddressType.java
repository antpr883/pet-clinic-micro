package com.micro.person.data.entities.enums;

/**
 * Тип адреси - маркер для ідентифікації призначення адреси.
 */
public enum AddressType {
    
    /**
     * Домашня адреса
     */
    HOME("Домашня"),
    
    /**
     * Робоча адреса
     */
    WORK("Робоча"),
    
    /**
     * Тимчасова адреса
     */
    TEMPORARY("Тимчасова"),
    
    /**
     * Поштова адреса (для отримання пошти)
     */
    MAILING("Поштова"),
    
    /**
     * Інший тип адреси
     */
    OTHER("Інша");
    
    private final String displayName;
    
    AddressType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

