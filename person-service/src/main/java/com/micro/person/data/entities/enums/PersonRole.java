package com.micro.person.data.entities.enums;

/**
 * Ролі персон в системі Pet Clinic.
 * 
 * ВАЖЛИВО: ADMIN не є Person, це системний користувач в Auth Service.
 * В Person Service зберігаються тільки: VETERINARIAN, RECEPTIONIST, OWNER.
 */
public enum PersonRole {
    
    /**
     * Лікар ветеринар.
     * Може переглядати всіх власників, редагувати контакти, створювати записи.
     */
    VETERINARIAN("Лікар ветеринар"),
    
    /**
     * Реєстратор на рецепції.
     * Може створювати нових клієнтів, оновлювати контакти.
     */
    RECEPTIONIST("Реєстратор"),
    
    /**
     * Власник тварини (клієнт клініки).
     * Може переглядати та редагувати тільки свої дані.
     */
    OWNER("Власник тварини");
    
    private final String displayName;
    
    PersonRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

