
CREATE TABLE gr_archive
(
    archive_taskid character varying(32) NOT NULL,
    archive_task jsonb,
    CONSTRAINT gr_archive_pkey PRIMARY KEY (archive_taskid)
);


