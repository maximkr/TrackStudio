-- Увеличение длины всех текстовых полей VARCHAR(2000) до VARCHAR(4000)

ALTER TABLE gr_attachment ALTER COLUMN attachment_description TYPE VARCHAR(4000);
ALTER TABLE gr_fvalue ALTER COLUMN fvalue_value TYPE VARCHAR(4000);
ALTER TABLE gr_message ALTER COLUMN message_description TYPE VARCHAR(4000);
ALTER TABLE gr_task ALTER COLUMN task_description TYPE VARCHAR(4000);
ALTER TABLE gr_template ALTER COLUMN template_description TYPE VARCHAR(4000);
ALTER TABLE gr_udfval ALTER COLUMN udfval_str TYPE VARCHAR(4000);
ALTER TABLE gr_report ALTER COLUMN report_params TYPE VARCHAR(4000);
ALTER TABLE gr_priority ALTER COLUMN priority_description TYPE VARCHAR(4000);
ALTER TABLE gr_longtext ALTER COLUMN longtext_value TYPE VARCHAR(4000);
ALTER TABLE gr_property ALTER COLUMN property_value TYPE VARCHAR(4000);
