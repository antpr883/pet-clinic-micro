package com.micro.person.data.dto.mapper;

import com.micro.person.data.entities.AddressEntity;
import com.micro.person.data.entities.ContactEntity;
import com.micro.person.data.entities.PersonEntity;
import com.micro.person.data.entities.enums.AddressType;
import com.micro.person.data.dto.person.*;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ContactMapper.class, AddressMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PersonMapper extends BaseMapper<
    PersonEntity, 
    PersonDto, 
    PersonPreviewDto, 
    PersonDto> {  // PersonDto використовується і для request

    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Override
    PersonDto toDto(PersonEntity entity);

    @Override
    PersonPreviewDto toPreviewDto(PersonEntity entity);
    
    @AfterMapping
    default void mapToPreviewDto(@MappingTarget PersonPreviewDto dto, PersonEntity entity) {
        // Заповнюємо email та phone з контактів
        if (entity.getContacts() != null && !entity.getContacts().isEmpty()) {
            entity.getContacts().stream()
                    .filter(c -> c.getContactType() != null)
                    .forEach(contact -> {
                        if (contact.getContactType().name().contains("EMAIL") && dto.getEmail() == null) {
                            dto.setEmail(contact.getValue());
                        }
                        if ((contact.getContactType().name().contains("MOBILE") || 
                             contact.getContactType().name().contains("PHONE")) && dto.getPhone() == null) {
                            dto.setPhone(contact.getValue());
                        }
                    });
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    PersonEntity toEntity(PersonDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    void updateEntityFromDto(PersonDto dto, @MappingTarget PersonEntity entity);

    @Override
    List<PersonDto> toDtoList(List<PersonEntity> entities);

    @Override
    List<PersonPreviewDto> toPreviewDtoList(List<PersonEntity> entities);

    @AfterMapping
    default void setPersonInContacts(@MappingTarget PersonEntity entity) {
        if (entity.getContacts() != null) {
            entity.getContacts().forEach(contact -> contact.setPerson(entity));
        }
        if (entity.getAddresses() != null) {
            entity.getAddresses().forEach(address -> address.setPerson(entity));
        }
    }
    
    // ========== PersonFullDto Mapping ==========
    
    // MapStruct автоматично використовує ContactMapper.toDtoSet() та AddressMapper.toDtoSet()
    // через uses = {ContactMapper.class, AddressMapper.class}
    PersonFullDto toFullDto(PersonEntity entity);
    
    List<PersonFullDto> toFullDtoList(List<PersonEntity> entities);
    
    // ========== PersonFullExtendedDto Mapping ==========
    
    // MapStruct автоматично використовує ContactMapper.toDtoSet() та AddressMapper.toDtoSet()
    // через uses = {ContactMapper.class, AddressMapper.class}
    PersonFullExtendedDto toFullExtendedDto(PersonEntity entity);
    
    List<PersonFullExtendedDto> toFullExtendedDtoList(List<PersonEntity> entities);
    
    // ========== PersonPreviewExtendedDto Mapping ==========
    
    @Mapping(target = "primaryPhone", ignore = true)
    @Mapping(target = "city", ignore = true)
    PersonPreviewExtendedDto toPreviewExtendedDto(PersonEntity entity);
    
    @AfterMapping
    default void mapToPreviewExtendedDto(@MappingTarget PersonPreviewExtendedDto dto, PersonEntity entity) {
        // Заповнюємо primaryPhone з primary контакту
        if (entity.getContacts() != null && !entity.getContacts().isEmpty()) {
            entity.getContacts().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsPrimary()) && 
                               (c.getContactType().name().contains("MOBILE") || 
                                c.getContactType().name().contains("PHONE")))
                    .findFirst()
                    .ifPresent(contact -> dto.setPrimaryPhone(contact.getValue()));
        }
        
        // Заповнюємо city з primary адреси (HOME тип)
        if (entity.getAddresses() != null && !entity.getAddresses().isEmpty()) {
            entity.getAddresses().stream()
                    .filter(a -> a.getAddressType() == AddressType.HOME)
                    .findFirst()
                    .ifPresent(address -> dto.setCity(address.getCity()));
        }
    }
    
    List<PersonPreviewExtendedDto> toPreviewExtendedDtoList(List<PersonEntity> entities);
    
    // ========== PersonFullPreviewDto Mapping ==========
    
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "primaryPhone", ignore = true)
    @Mapping(target = "primaryEmail", ignore = true)
    @Mapping(target = "primaryAddress", ignore = true)
    PersonFullPreviewDto toFullPreviewDto(PersonEntity entity);
    
    @AfterMapping
    default void mapToFullPreviewDto(@MappingTarget PersonFullPreviewDto dto, PersonEntity entity) {
        // Формуємо fullName з firstName + lastName
        String fullName = "";
        if (entity.getFirstName() != null) {
            fullName = entity.getFirstName();
        }
        if (entity.getLastName() != null) {
            fullName = fullName.isEmpty() ? entity.getLastName() : fullName + " " + entity.getLastName();
        }
        if (entity.getMiddleName() != null && !entity.getMiddleName().isEmpty()) {
            fullName = fullName + " " + entity.getMiddleName();
        }
        dto.setFullName(fullName.trim());
        
        // Заповнюємо primaryPhone з primary контакту типу MOBILE/PHONE
        if (entity.getContacts() != null && !entity.getContacts().isEmpty()) {
            entity.getContacts().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsPrimary()) && 
                               (c.getContactType().name().contains("MOBILE") || 
                                c.getContactType().name().contains("PHONE")))
                    .findFirst()
                    .ifPresent(contact -> dto.setPrimaryPhone(contact.getValue()));
        }
        
        // Заповнюємо primaryEmail з primary контакту типу EMAIL
        if (entity.getContacts() != null && !entity.getContacts().isEmpty()) {
            entity.getContacts().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsPrimary()) && 
                               c.getContactType().name().contains("EMAIL"))
                    .findFirst()
                    .ifPresent(contact -> dto.setPrimaryEmail(contact.getValue()));
        }
        
        // Заповнюємо primaryAddress з primary адреси або HOME типу
        if (entity.getAddresses() != null && !entity.getAddresses().isEmpty()) {
            AddressEntity primaryAddress = entity.getAddresses().stream()
                    .filter(a -> a.getAddressType() == AddressType.HOME)
                    .findFirst()
                    .orElse(entity.getAddresses().stream()
                            .findFirst()
                            .orElse(null));
            
            if (primaryAddress != null) {
                StringBuilder addressBuilder = new StringBuilder();
                if (primaryAddress.getCity() != null) {
                    addressBuilder.append(primaryAddress.getCity());
                }
                if (primaryAddress.getStreet() != null) {
                    if (addressBuilder.length() > 0) {
                        addressBuilder.append(", ");
                    }
                    addressBuilder.append(primaryAddress.getStreet());
                }
                if (primaryAddress.getBuilding() != null) {
                    if (addressBuilder.length() > 0) {
                        addressBuilder.append(" ");
                    }
                    addressBuilder.append(primaryAddress.getBuilding());
                }
                if (primaryAddress.getApartment() != null) {
                    addressBuilder.append(", кв. ").append(primaryAddress.getApartment());
                }
                dto.setPrimaryAddress(addressBuilder.toString().trim());
            }
        }
    }
    
    List<PersonFullPreviewDto> toFullPreviewDtoList(List<PersonEntity> entities);
    
    // ========== PersonBasicDto Mapping ==========
    
    PersonBasicDto toBasicDto(PersonEntity entity);
    
    List<PersonBasicDto> toBasicDtoList(List<PersonEntity> entities);
    
    // ========== PersonCreateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    PersonEntity fromCreateDto(PersonCreateDto dto);
    
    // ========== PersonUpdateDto Mapping ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contacts", ignore = true) // Обробляємо вручну через @AfterMapping
    @Mapping(target = "addresses", ignore = true) // Обробляємо вручну через @AfterMapping
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateFromUpdateDto(PersonUpdateDto dto, @MappingTarget PersonEntity entity);
    
    /**
     * Обробка contacts та addresses при оновленні з PersonUpdateDto.
     * Якщо contacts/addresses передано - замінюємо всі існуючі (replace strategy).
     */
    @AfterMapping
    default void updateContactsAndAddressesFromUpdateDto(
            PersonUpdateDto dto, 
            @MappingTarget PersonEntity entity,
            @Context ContactMapper contactMapper,
            @Context AddressMapper addressMapper) {
        
        // Оновлюємо контакти якщо передано
        if (dto.getContacts() != null) {
            // Видаляємо всі існуючі контакти
            entity.getContacts().clear();
            // Додаємо нові контакти
            if (!dto.getContacts().isEmpty()) {
                dto.getContacts().forEach(contactUpdateDto -> {
                    ContactEntity contactEntity = contactMapper.fromUpdateDto(contactUpdateDto);
                    entity.addContact(contactEntity);
                });
            }
        }
        
        // Оновлюємо адреси якщо передано
        if (dto.getAddresses() != null) {
            // Видаляємо всі існуючі адреси
            entity.getAddresses().clear();
            // Додаємо нові адреси
            if (!dto.getAddresses().isEmpty()) {
                dto.getAddresses().forEach(addressUpdateDto -> {
                    AddressEntity addressEntity = addressMapper.fromUpdateDto(addressUpdateDto);
                    entity.addAddress(addressEntity);
                });
            }
        }
    }
}
