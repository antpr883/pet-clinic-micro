package com.micro.person.data.entities;

import com.micro.person.data.entities.enums.ContactType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Contact entity - окрема таблиця для контактної інформації.
 * Одна персона може мати багато контактів різних типів.
 * 
 * Рефакторена структура:
 * - value (TEXT) - зберігає значення контакту (email, телефон, адреса)
 * - label (VARCHAR) - опис/назва контакту (наприклад, "Робочий телефон")
 * 
 * Приклади:
 * - contactType=EMAIL, value="ivan@example.com", label="Особистий email"
 * - contactType=MOBILE, value="+380501234567", label="Мобільний телефон"
 * - contactType=WORK, value="+380671234567", label="Робочий телефон"
 * - contactType=HOME, value="вул. Хрещатик, 1, Київ", label="Домашня адреса"
 */
@Entity
@Table(name = "contacts", schema = "person_schema", indexes = {
    @Index(name = "idx_contact_person_id", columnList = "person_id"),
    @Index(name = "idx_contact_type", columnList = "contact_type"),
    @Index(name = "idx_contact_value", columnList = "value"),
    @Index(name = "idx_contact_label", columnList = "label")
})
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = "person")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ContactEntity extends BaseActiveEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false, foreignKey = @ForeignKey(name = "fk_contact_person"))
    private PersonEntity person;

    @NotNull(message = "Contact type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false, length = 30)
    private ContactType contactType;

    @NotBlank(message = "Contact value is required")
    @Column(name = "value", nullable = false, length = 1000)
    private String value;

    @Size(max = 100, message = "Label must not exceed 100 characters")
    @Column(name = "label", length = 100)
    private String label;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;
    
    @PrePersist
    public void prePersist() {
        if (isPrimary == null) {
            isPrimary = false;
        }
        // Автоматично генеруємо label якщо не вказано
        if (label == null || label.isBlank()) {
            label = generateDefaultLabel();
        }
    }
    
    /**
     * Генерує label за замовчуванням на основі contactType.
     */
    private String generateDefaultLabel() {
        return switch (contactType) {
            case EMAIL -> "Email";
            case PHONE -> "Телефон";
            case MOBILE -> "Мобільний телефон";
            case TELEGRAM -> "Telegram";
            case WHATSAPP -> "WhatsApp";
            case LINKEDIN -> "LinkedIn";
            case OTHER -> "Інший контакт";
        };
    }
}

