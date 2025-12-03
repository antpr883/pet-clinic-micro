package com.micro.person.data.entities.enums;

/**
 * Тип контакту - маркер для ідентифікації типу контактної інформації.
 * 
 * Використання:
 * - EMAIL - для email адрес
 * - PHONE - для стаціонарних телефонів
 * - MOBILE - для мобільних телефонів
 * - TELEGRAM - для Telegram контактів
 * - WHATSAPP - для WhatsApp контактів
 * - LINKEDIN - для LinkedIn профілів
 * - OTHER - для інших типів контактів
 * 
 * Примітка: label використовується для уточнення призначення (наприклад, "робочий", "особистий")
 */
public enum ContactType {
    
    /**
     * Email адреса
     */
    EMAIL("Email"),
    
    /**
     * Стаціонарний телефон
     */
    PHONE("Телефон"),
    
    /**
     * Мобільний телефон
     */
    MOBILE("Мобільний"),
    
    /**
     * Telegram контакт
     */
    TELEGRAM("Telegram"),
    
    /**
     * WhatsApp контакт
     */
    WHATSAPP("WhatsApp"),
    
    /**
     * LinkedIn профіль
     */
    LINKEDIN("LinkedIn"),
    
    /**
     * Інший тип контакту
     */
    OTHER("Інший");
    
    private final String displayName;
    
    ContactType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

