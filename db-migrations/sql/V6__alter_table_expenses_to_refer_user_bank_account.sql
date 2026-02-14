ALTER TABLE public.expenses
ADD COLUMN bank_account_id VARCHAR;

ALTER TABLE public.expenses
ADD CONSTRAINT fk_expenses_bank_account
FOREIGN KEY (bank_account_id)
REFERENCES user_bank_account(id);