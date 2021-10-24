package ch.zhaw.integration.beacons.scheduler;

import ch.zhaw.integration.beacons.entities.doctor.Doctor;
import ch.zhaw.integration.beacons.entities.doctor.DoctorRepository;
import ch.zhaw.integration.beacons.entities.trackingdata.TrackingData;
import ch.zhaw.integration.beacons.entities.trackingdata.TrackingDataRepository;
import ch.zhaw.integration.beacons.entities.treatment.Treatment;
import ch.zhaw.integration.beacons.entities.treatment.TreatmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableScheduling
@Component
public class TrackingDataProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackingDataProcessor.class);

    private final DoctorRepository doctorRepository;
    private final TrackingDataRepository trackingDataRepository;
    private final TreatmentRepository treatmentRepository;


    public TrackingDataProcessor(TrackingDataRepository trackingDataRepository, DoctorRepository doctorRepository, TreatmentRepository treatmentRepository) {
        this.trackingDataRepository = trackingDataRepository;
        this.doctorRepository = doctorRepository;
        this.treatmentRepository = treatmentRepository;
    }

    @Scheduled(cron = "${cron.expression.tracking.data.interpretation.schedule}")
    public void processTrackingData() {
        LOGGER.info("processTrackingData() scheduled!!!!!!!!!!!!!!!!");
        List<Doctor> doctorList = doctorRepository.findAll();
        for(Doctor doctor : doctorList) {
            processTrackingDataForDoctor(doctor);
        }
    }

    public void processTrackingDataForDoctor(Doctor doctor) {
        List<TrackingData> trackingDataList = trackingDataRepository.findAllByDoctorAndProcessedFalse(doctor);
        for (TrackingData trackingData : trackingDataList) {
            Treatment treatment = new Treatment();
            treatment.setDoctor(doctor);
            treatment.setPatient(trackingData.getBeacon().getBed().getPatient());
            treatment.setStartTime(trackingData.getTreatmentStartDate());
            treatment.setEndTime(trackingData.getTreatmentEndDate());
            treatmentRepository.save(treatment);
            trackingData.setProcessed(true);
            trackingDataRepository.save(trackingData);
        }
    }
}
