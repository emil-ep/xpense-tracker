ALTER TABLE public.expense_tags
DROP CONSTRAINT fkipodf4lkv9b1hb88wcveont2q;

ALTER TABLE public.expense_tags
ADD CONSTRAINT fk_expense_tags_tag_id
FOREIGN KEY (tag_id)
REFERENCES tag (id)
ON DELETE CASCADE;