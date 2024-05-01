package org.scaler.universitysystem.service;

import org.scaler.universitysystem.models.*;
import org.scaler.universitysystem.repository.AdmissionRepository;
import org.scaler.universitysystem.repository.ApplicantRepository;
import org.scaler.universitysystem.repository.ProgramRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SelfAdmissionService implements AdmissionService{

    private AdmissionRepository admissionRepository;

    private ApplicantRepository applicantRepository;
    private ProgramRepository programRepository;

    public SelfAdmissionService(AdmissionRepository admissionRepository, ApplicantRepository applicantRepository, ProgramRepository programRepository) {
        this.admissionRepository = admissionRepository;
        this.applicantRepository = applicantRepository;
        this.programRepository = programRepository;
    }

    @Override
    public Admission createAdmission(Admission admission) {
        Applicant applicant = applicantRepository.findById(admission.getApplicant().getId()).get();
        Program program = programRepository.findById(admission.getProgram().getId()).get();
        admission.setDecision(Decision.WAITLISTED);
        applicant.setApplicationStatus(ApplicationStatus.APPLIED);
        admission.setApplicant(applicant);
        admission.setProgram(program);
        Admission savedAdmission = admissionRepository.save(admission);
        return savedAdmission;
    }

    @Override
    public Admission getAdmissionById(Long id) {

        Optional<Admission> optionalAdmission = admissionRepository.findById(id);
        return optionalAdmission.get();
    }


    @Override
    public Admission updateAdmission(Long id, Admission admission) {
//        Applicant applicant = applicantRepository.findById(admission.getApplicant().getId()).get();
//        if (admission.getDecision() == Decision.ACCEPTED){
//            applicant.setApplicationStatus(ApplicationStatus.ADMITTED);
//        }
        Optional<Admission> existingAdmission = admissionRepository.findById(id);
        if (existingAdmission.isPresent()) {
            Admission updatedAdmission = admissionRepository.save(update(existingAdmission.get(), admission));
            Applicant applicant = applicantRepository.findById(updatedAdmission.getApplicant().getId()).get();
            if (updatedAdmission.getDecision() == Decision.ACCEPTED){
                applicant.setApplicationStatus(ApplicationStatus.ADMITTED);
            }
            applicantRepository.save(applicant);
            return updatedAdmission;
        } else {
            throw new RuntimeException("Admission not found with id: " + id);
        }
    }

    @Override
    public void deleteAdmission(Long id) {

        admissionRepository.deleteById(id);

    }

    @Override
    public List<Admission> getAdmissionsByProgram(Long programId) {
        return admissionRepository.findByProgramId(programId);
    }

    @Override
    public List<Admission> getAdmissionsByApplicant(Long applicantId) {
        return admissionRepository.findByApplicantId(applicantId);
    }

    public Admission update(Admission existingAdmission, Admission updatedAdmission) {
        if(updatedAdmission.getDecision() != null)
            existingAdmission.setDecision(updatedAdmission.getDecision());
        if(updatedAdmission.getDecisionDate() != null){
            existingAdmission.setDecisionDate(updatedAdmission.getDecisionDate());
        }
        return existingAdmission;
    }
}
