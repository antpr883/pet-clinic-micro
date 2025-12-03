package com.micro.clinic.statemachine.config;

import com.micro.clinic.data.entities.enums.ClinicCaseStatus;
import com.micro.clinic.statemachine.ClinicCaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * Spring State Machine configuration for Clinic Case workflow.
 * 
 * Defines states, transitions, guards, and actions.
 */
@Slf4j
@Configuration
@EnableStateMachineFactory
public class ClinicCaseStateMachineConfig 
        extends StateMachineConfigurerAdapter<ClinicCaseStatus, ClinicCaseEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<ClinicCaseStatus, ClinicCaseEvent> config) 
            throws Exception {
        config
            .withConfiguration()
            .autoStartup(false)
            .listener(stateMachineListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<ClinicCaseStatus, ClinicCaseEvent> states) 
            throws Exception {
        states
            .withStates()
            .initial(ClinicCaseStatus.REGISTERED)
            .states(EnumSet.allOf(ClinicCaseStatus.class))
            .end(ClinicCaseStatus.DISCHARGED)
            .end(ClinicCaseStatus.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ClinicCaseStatus, ClinicCaseEvent> transitions) 
            throws Exception {
        transitions
            // REGISTERED → INITIAL_CHECKUP_COMPLETED
            .withExternal()
                .source(ClinicCaseStatus.REGISTERED)
                .target(ClinicCaseStatus.INITIAL_CHECKUP_COMPLETED)
                .event(ClinicCaseEvent.INITIAL_CHECKUP_COMPLETED)
                .guard(initialCheckupGuard())
                .and()
            
            // INITIAL_CHECKUP_COMPLETED → DIAGNOSTICS_IN_PROGRESS
            .withExternal()
                .source(ClinicCaseStatus.INITIAL_CHECKUP_COMPLETED)
                .target(ClinicCaseStatus.DIAGNOSTICS_IN_PROGRESS)
                .event(ClinicCaseEvent.START_DIAGNOSTICS)
                .and()
            
            // DIAGNOSTICS_IN_PROGRESS → TREATMENT_PLANNED
            .withExternal()
                .source(ClinicCaseStatus.DIAGNOSTICS_IN_PROGRESS)
                .target(ClinicCaseStatus.TREATMENT_PLANNED)
                .event(ClinicCaseEvent.TREATMENT_PLANNED)
                .guard(treatmentPlannedGuard())
                .and()
            
            // TREATMENT_PLANNED → PROCEDURES_IN_PROGRESS
            .withExternal()
                .source(ClinicCaseStatus.TREATMENT_PLANNED)
                .target(ClinicCaseStatus.PROCEDURES_IN_PROGRESS)
                .event(ClinicCaseEvent.PROCEDURES_STARTED)
                .guard(proceduresStartedGuard())
                .and()
            
            // PROCEDURES_IN_PROGRESS → HOSPITALIZED
            .withExternal()
                .source(ClinicCaseStatus.PROCEDURES_IN_PROGRESS)
                .target(ClinicCaseStatus.HOSPITALIZED)
                .event(ClinicCaseEvent.HOSPITALIZATION_STARTED)
                .and()
            
            // HOSPITALIZED → DISCHARGED
            .withExternal()
                .source(ClinicCaseStatus.HOSPITALIZED)
                .target(ClinicCaseStatus.DISCHARGED)
                .event(ClinicCaseEvent.DISCHARGED)
                .guard(dischargedGuard())
                .and()
            
            // Any state (except DISCHARGED) → CANCELLED
            .withExternal()
                .source(ClinicCaseStatus.REGISTERED)
                .target(ClinicCaseStatus.CANCELLED)
                .event(ClinicCaseEvent.CANCELLED)
                .and()
            .withExternal()
                .source(ClinicCaseStatus.INITIAL_CHECKUP_COMPLETED)
                .target(ClinicCaseStatus.CANCELLED)
                .event(ClinicCaseEvent.CANCELLED)
                .and()
            .withExternal()
                .source(ClinicCaseStatus.DIAGNOSTICS_IN_PROGRESS)
                .target(ClinicCaseStatus.CANCELLED)
                .event(ClinicCaseEvent.CANCELLED)
                .and()
            .withExternal()
                .source(ClinicCaseStatus.TREATMENT_PLANNED)
                .target(ClinicCaseStatus.CANCELLED)
                .event(ClinicCaseEvent.CANCELLED)
                .and()
            .withExternal()
                .source(ClinicCaseStatus.PROCEDURES_IN_PROGRESS)
                .target(ClinicCaseStatus.CANCELLED)
                .event(ClinicCaseEvent.CANCELLED)
                .and()
            .withExternal()
                .source(ClinicCaseStatus.HOSPITALIZED)
                .target(ClinicCaseStatus.CANCELLED)
                .event(ClinicCaseEvent.CANCELLED)
                .guard(cancelledGuard());
    }

    /**
     * Guard: Check if initial checkup is completed (at least one INITIAL visit).
     */
    @Bean
    public Guard<ClinicCaseStatus, ClinicCaseEvent> initialCheckupGuard() {
        return context -> {
            // Guard logic will be implemented in service layer
            // This guard will be called with context containing the case entity
            return true; // Will be overridden in service
        };
    }

    /**
     * Guard: Check if treatment is planned (at least one confirmed diagnosis).
     */
    @Bean
    public Guard<ClinicCaseStatus, ClinicCaseEvent> treatmentPlannedGuard() {
        return context -> {
            // Guard logic will be implemented in service layer
            return true; // Will be overridden in service
        };
    }

    /**
     * Guard: Check if procedures are started (at least one planned procedure).
     */
    @Bean
    public Guard<ClinicCaseStatus, ClinicCaseEvent> proceduresStartedGuard() {
        return context -> {
            // Guard logic will be implemented in service layer
            return true; // Will be overridden in service
        };
    }

    /**
     * Guard: Check if discharge is valid (hospitalization endAt is set).
     */
    @Bean
    public Guard<ClinicCaseStatus, ClinicCaseEvent> dischargedGuard() {
        return context -> {
            // Guard logic will be implemented in service layer
            return true; // Will be overridden in service
        };
    }

    /**
     * Guard: Check if cancellation is valid (not already discharged).
     */
    @Bean
    public Guard<ClinicCaseStatus, ClinicCaseEvent> cancelledGuard() {
        return context -> {
            // Guard logic will be implemented in service layer
            return true; // Will be overridden in service
        };
    }

    /**
     * State Machine listener for logging transitions.
     */
    @Bean
    public StateMachineListener<ClinicCaseStatus, ClinicCaseEvent> stateMachineListener() {
        return new StateMachineListenerAdapter<ClinicCaseStatus, ClinicCaseEvent>() {
            @Override
            public void stateChanged(State<ClinicCaseStatus, ClinicCaseEvent> from, 
                                    State<ClinicCaseStatus, ClinicCaseEvent> to) {
                if (from != null && to != null) {
                    log.info("State Machine transition: {} → {}", from.getId(), to.getId());
                } else if (to != null) {
                    log.info("State Machine initial state: {}", to.getId());
                }
            }

            @Override
            public void stateEntered(State<ClinicCaseStatus, ClinicCaseEvent> state) {
                log.debug("Entered state: {}", state.getId());
            }

            @Override
            public void stateExited(State<ClinicCaseStatus, ClinicCaseEvent> state) {
                log.debug("Exited state: {}", state.getId());
            }
        };
    }
}

