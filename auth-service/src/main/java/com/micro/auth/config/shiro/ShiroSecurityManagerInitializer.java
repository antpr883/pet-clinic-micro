package com.micro.auth.config.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Ініціалізатор SecurityManager для Shiro.
 * 
 * Встановлює SecurityManager в SecurityUtils після повної ініціалізації Spring контексту.
 * 
 * ContextRefreshedEvent викликається після:
 * - Створення всіх bean'ів
 * - Впровадження всіх залежностей
 * - Ініціалізації Spring AOP (включно з AuthorizationAttributeSourceAdvisor)
 * 
 * Це гарантує, що SecurityManager встановлюється після того, як всі компоненти готові,
 * що усуває проблеми з порядком ініціалізації та циклічними залежностями.
 */
@Component
public class ShiroSecurityManagerInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final ApplicationContext applicationContext;

    public ShiroSecurityManagerInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Встановлюємо SecurityManager тільки для root context (не для child contexts)
        if (event.getApplicationContext().getParent() == null) {
            // Отримуємо SecurityManager з ApplicationContext після повної ініціалізації
            // Це уникнення циклічної залежності з Spring AOP
            SecurityManager securityManager = applicationContext.getBean("securityManager", SecurityManager.class);
            SecurityUtils.setSecurityManager(securityManager);
        }
    }
}


