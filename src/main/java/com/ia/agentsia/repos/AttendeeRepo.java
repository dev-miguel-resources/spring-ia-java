package com.ia.agentsia.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ia.agentsia.models.Attendee;

public interface AttendeeRepo extends JpaRepository<Attendee, Long> {

    // SELECT * FROM attendees WHERE qr_code = ?
    Optional<Attendee> findByQrCode(String qrCode);

    // "SELECT COUNT(*) FROM attendees WHERE email = ? LIMIT 1";
    boolean existsByEmail(String email);

}
