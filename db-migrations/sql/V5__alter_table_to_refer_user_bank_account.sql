ALTER TABLE removed_expenses
ADD COLUMN bank_account_id VARCHAR;

ALTER TABLE removed_expenses
ADD CONSTRAINT fk_removed_expenses_bank_account
FOREIGN KEY (bank_account_id)
REFERENCES user_bank_account(id);
