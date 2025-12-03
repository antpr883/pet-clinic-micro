package com.micro.clinic.statemachine;

import com.micro.clinic.data.entities.ClinicCaseEntity;
import com.micro.clinic.data.entities.enums.ClinicCaseStatus;
import com.micro.clinic.data.entities.enums.DiagnosisStatus;
import com.micro.clinic.data.entities.enums.ProcedureStatus;
import com.micro.clinic.data.entities.enums.VisitType;
import com.micro.clinic.repository.ClinicCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing Clinic Case State Machine.
 * 
 * Handles state transitions with validation guards.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicCaseStateMachineService {

    private final StateMachineFactory<ClinicCaseStatus, ClinicCaseEvent> stateMachineFactory;
    private final ClinicCaseRepository clinicCaseRepository;

    /**
     * Send event to State Machine and persist state change.
     */
    @Transactional
    public void sendEvent(Long caseId, ClinicCaseEvent event) {
        log.debug("Sending event {} to case ID: {}", event, caseId);
        
        ClinicCaseEntity caseEntity = clinicCaseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalStateException("Clinic case not found: " + caseId));
        
        // Create State Machine instance
        StateMachine<ClinicCaseStatus, ClinicCaseEvent> stateMachine = 
                stateMachineFactory.getStateMachine();
        
        // Restore state from entity
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(access -> {
                    access.resetStateMachine(new DefaultStateMachineContext<>(
                            caseEntity.getStatus(), null, null, null));
                });
        
        // Validate transition with guards
        if (!validateTransition(caseEntity, event, stateMachine)) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s with event %s", 
                            caseEntity.getStatus(), event));
        }
        
        // Send event
        boolean accepted = stateMachine.sendEvent(event);
        
        if (!accepted) {
            throw new IllegalStateException(
                    String.format("Event %s not accepted in state %s", 
                            event, caseEntity.getStatus()));
        }
        
        // Update entity with new state
        ClinicCaseStatus newStatus = stateMachine.getState().getId();
        caseEntity.setStatus(newStatus);
        clinicCaseRepository.save(caseEntity);
        
        log.info("Case ID: {} transitioned from {} to {}", 
                caseId, caseEntity.getStatus(), newStatus);
    }

    /**
     * Validate transition with business rules (guards).
     */
    private boolean validateTransition(ClinicCaseEntity caseEntity, 
                                       ClinicCaseEvent event, 
                                       StateMachine<ClinicCaseStatus, ClinicCaseEvent> stateMachine) {
        // Load relationships if needed
        ClinicCaseEntity entityWithRelations = clinicCaseRepository.findById(caseEntity.getId())
                .orElse(caseEntity);
        
        switch (event) {
            case INITIAL_CHECKUP_COMPLETED:
                // Guard: at least one INITIAL visit
                return entityWithRelations.getVisits().stream()
                        .anyMatch(v -> v.getActive() && v.getVisitType() == VisitType.INITIAL);
            
            case TREATMENT_PLANNED:
                // Guard: at least one confirmed diagnosis
                return entityWithRelations.getDiagnoses().stream()
                        .anyMatch(d -> d.getActive() && d.getStatus() == DiagnosisStatus.CONFIRMED);
            
            case PROCEDURES_STARTED:
                // Guard: at least one planned procedure
                return entityWithRelations.getProcedures().stream()
                        .anyMatch(p -> p.getActive() && p.getStatus() == ProcedureStatus.PLANNED);
            
            case DISCHARGED:
                // Guard: hospitalization endAt is set
                return entityWithRelations.getHospitalization() != null 
                        && entityWithRelations.getHospitalization().getEndAt() != null;
            
            case CANCELLED:
                // Guard: not already discharged
                return entityWithRelations.getStatus() != ClinicCaseStatus.DISCHARGED;
            
            default:
                return true; // Other events don't need special validation
        }
    }
}

