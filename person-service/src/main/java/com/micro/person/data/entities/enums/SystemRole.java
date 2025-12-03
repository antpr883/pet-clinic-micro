package com.micro.person.data.entities.enums;

/**
 * Системні ролі (в JWT токенах).
 * Ці ролі не зберігаються в Person Service, а в Auth Service.
 * Використовуються для авторизації та перевірки прав доступу.
 */
public enum SystemRole {
    
    /**
     * Адміністратор системи.
     * Має повний доступ до всіх ресурсів.
     * ВАЖЛИВО: ADMIN не є Person, це системний користувач.
     */
    ADMIN("Адміністратор"),
    
    /**
     * Лікар ветеринар (з Person Service).
     * Може переглядати всіх власників, редагувати контакти.
     */
    VETERINARIAN("Лікар ветеринар"),
    
    /**
     * Реєстратор (з Person Service).
     * Може створювати нових клієнтів, оновлювати контакти.
     */
    RECEPTIONIST("Реєстратор"),
    
    /**
     * Власник тварини (з Person Service).
     * Може переглядати та редагувати тільки свої дані.
     */
    OWNER("Власник тварини"),
    
    /**
     * Гість (неавторизований користувач).
     * Може тільки реєструватися як OWNER.
     */
    GUEST("Гість");
    
    private final String displayName;
    
    SystemRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

