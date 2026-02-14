package com.xperia.xpense_tracker.repository.tracker;

import com.xperia.xpense_tracker.models.entities.tracker.RemovedExpense;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RemovedExpensesRepository extends JpaRepository<RemovedExpense, String> {

    Optional<RemovedExpense> findByBankReferenceNo(String bankReferenceNo);

    List<RemovedExpense> findAllByUser(TrackerUser user);
}
