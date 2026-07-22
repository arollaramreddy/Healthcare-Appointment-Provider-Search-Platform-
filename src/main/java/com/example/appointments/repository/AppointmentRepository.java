package com.example.appointments.repository;

import com.example.appointments.model.Appointment;
import com.example.appointments.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("""
            select count(a) from Appointment a
            where a.provider.id = :providerId
              and a.status in :statuses
              and a.startTime < :endTime
              and a.endTime > :startTime
              and (:excludeId is null or a.id <> :excludeId)
            """)
    long countConflicts(@Param("providerId") Long providerId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime,
                        @Param("statuses") Collection<AppointmentStatus> statuses,
                        @Param("excludeId") Long excludeId);

    List<Appointment> findByPatientIdOrderByStartTimeDesc(Long patientId);
    List<Appointment> findByProviderIdAndStartTimeBetweenOrderByStartTime(Long providerId,
                                                                          LocalDateTime from,
                                                                          LocalDateTime to);
}
