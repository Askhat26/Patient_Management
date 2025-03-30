package com.pm.controller;


import com.pm.dto.PatientRequestDTO;
import com.pm.dto.PatientResponseDTO;
import com.pm.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@Tag(name="Patient",description = "API for managing Patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService){
        this.patientService=patientService;
    }
    @GetMapping
    @Operation(summary = "Get Patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients(){
        List<PatientResponseDTO> patients= patientService.getPatients();
        return ResponseEntity.ok().body(patients);
    }
    @PutMapping("/{id}")
    @Operation(summary = "update a new patient")
    public ResponseEntity<PatientResponseDTO>updatePatient(@PathVariable UUID id,
                                                           @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id,
                patientRequestDTO);

        return ResponseEntity.ok().body(patientResponseDTO);

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Patient")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id){
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

}
