package com.xperia.xpense_tracker.repository.tracker;

import com.xperia.xpense_tracker.models.entities.tracker.Statements;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatementsRepository extends JpaRepository<Statements, String> {

    Optional<Statements> findByFileName(String fileName);

}
