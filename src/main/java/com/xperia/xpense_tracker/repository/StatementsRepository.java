package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.Statements;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatementsRepository extends JpaRepository<Statements, String> {

    Optional<Statements> findByFileName(String fileName);

}
