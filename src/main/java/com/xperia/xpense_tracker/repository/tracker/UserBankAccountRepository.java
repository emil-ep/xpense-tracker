package com.xperia.xpense_tracker.repository.tracker;

import com.xperia.xpense_tracker.models.entities.tracker.UserBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBankAccountRepository extends JpaRepository<UserBankAccount, String> {
}
