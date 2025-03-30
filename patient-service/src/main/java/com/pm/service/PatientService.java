package com.pm.service;

import com.pm.dto.PatientRequestDTO;
import com.pm.dto.PatientResponseDTO;
import com.pm.exception.EmailAlreadyExistsException;
import com.pm.exception.PatientNotFoundException;
import com.pm.mapper.PatientMapper;
import com.pm.model.Patient;
import com.pm.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;


    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "A patient with this email already exists: " + patientRequestDTO.getEmail()
            );
        }

        Patient newPatient = patientRepository.save(
                PatientMapper.toModel(patientRequestDTO)
        );
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException(
                    "A patient with this email already exists: " + patientRequestDTO.getEmail()
            );
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());


        try {
            patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for dateOfBirth: " + patientRequestDTO.getDateOfBirth());
        }

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient not found with ID: " + id);
        }
        patientRepository.deleteById(id);
    }
}
