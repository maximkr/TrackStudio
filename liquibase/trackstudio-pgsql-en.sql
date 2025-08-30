CREATE TABLE gr_attachment (        attachment_id       VARCHAR(32) NOT NULL,        attachment_task     VARCHAR(32),	    attachment_message     VARCHAR(32),        attachment_user     VARCHAR(32) ,        attachment_name     VARCHAR(200) NOT NULL,        attachment_description VARCHAR(2000) ,        CONSTRAINT xpkgr_attachment            PRIMARY KEY (attachment_id)) ;

CREATE TABLE gr_bookmark (       bookmark_id               VARCHAR(32) NOT NULL,       bookmark_name             VARCHAR(200) NOT NULL,       bookmark_createdate       TIMESTAMP NOT NULL,       bookmark_filter           VARCHAR(32) ,       bookmark_task             VARCHAR(32) ,       bookmark_user             VARCHAR(32) ,       bookmark_owner            VARCHAR(32) NOT NULL,       CONSTRAINT xpkgr_bookmark              PRIMARY KEY (bookmark_id)) ;

CREATE TABLE gr_category (       category_id               VARCHAR(32) NOT NULL,       category_name             VARCHAR(200) NOT NULL,       category_action           VARCHAR(200) ,       category_budget           VARCHAR(200) ,       category_preferences      VARCHAR(200) ,       category_description      VARCHAR(200) ,       category_workflow         VARCHAR(32) NOT NULL,       category_task             VARCHAR(32) NOT NULL,       category_icon             VARCHAR(200) ,       category_handler_required INTEGER ,       category_group_handler    INTEGER ,       category_cr_trigger       VARCHAR(32) ,       category_upd_trigger      VARCHAR(32) ,       category_template         VARCHAR(32) ,       CONSTRAINT xpkgr_category              PRIMARY KEY (category_id)) ;

CREATE TABLE gr_filter (       filter_id            VARCHAR(32) NOT NULL,       filter_name          VARCHAR(200) NOT NULL ,       filter_description   VARCHAR(200) ,       filter_preferences      VARCHAR(200) ,       filter_priv          INTEGER ,       filter_task          VARCHAR(32) ,       filter_user          VARCHAR(32) ,       filter_owner         VARCHAR(32) NOT NULL,       CONSTRAINT xpkgr_filter              PRIMARY KEY (filter_id)) ;

CREATE TABLE gr_fvalue (       fvalue_id            VARCHAR(32) NOT NULL,       fvalue_filter        VARCHAR(32) NOT NULL,       fvalue_key           VARCHAR(200) ,       fvalue_value         VARCHAR(2000) ,       CONSTRAINT xpkgr_fvalue              PRIMARY KEY (fvalue_id)) ;

CREATE TABLE gr_message (       message_id           VARCHAR(32) NOT NULL,       message_description  VARCHAR(2000) ,       message_submitter    VARCHAR(32) NOT NULL,       message_task         VARCHAR(32) NOT NULL,       message_time         TIMESTAMP NOT NULL,       message_mstatus      VARCHAR(32) NOT NULL,       message_hrs          BIGINT ,       message_deadline     TIMESTAMP ,       message_budget       BIGINT ,       message_handler      VARCHAR(32) NOT NULL,       message_resolution   VARCHAR(32) ,       message_longtext     VARCHAR(32) ,       message_priority     VARCHAR(32) ,       CONSTRAINT xpkgr_message              PRIMARY KEY (message_id)) ;

CREATE TABLE gr_mprstatus (       mprstatus_id         VARCHAR(32) NOT NULL,       mprstatus_type       VARCHAR(200) ,       mprstatus_mstatus    VARCHAR(32) NOT NULL,       mprstatus_prstatus   VARCHAR(32) NOT NULL,       CONSTRAINT xpkgr_mprstatus              PRIMARY KEY (mprstatus_id)) ;

CREATE TABLE gr_mstatus (       mstatus_id           VARCHAR(32) NOT NULL,       mstatus_name         VARCHAR(200) NOT NULL,       mstatus_description  VARCHAR(200) ,       mstatus_preferences      VARCHAR(200) ,       mstatus_action       VARCHAR(200) ,              mstatus_workflow     VARCHAR(32) NOT NULL,       mstatus_trigger      VARCHAR(32) ,       CONSTRAINT xpkgr_mstatus                PRIMARY KEY (mstatus_id)) ;

CREATE TABLE gr_prstatus (       prstatus_id          VARCHAR(32) NOT NULL,       prstatus_name        VARCHAR(200) NOT NULL,       prstatus_description VARCHAR(200) ,       prstatus_preferences      VARCHAR(200) ,       prstatus_user        VARCHAR(32) NOT NULL,       CONSTRAINT xpkgr_prstatus              PRIMARY KEY (prstatus_id)) ;

CREATE TABLE gr_resolution (       resolution_id        VARCHAR(32) NOT NULL,       resolution_name      VARCHAR(200) NOT NULL,       resolution_mstatus   VARCHAR(32) NOT NULL,       resolution_isdefault INTEGER ,       CONSTRAINT xpkgr_resolution              PRIMARY KEY (resolution_id)) ;

CREATE TABLE gr_rolestatus (       rolestatus_id        VARCHAR(32) NOT NULL,       rolestatus_prstatus  VARCHAR(32) NOT NULL,       rolestatus_role      VARCHAR(32) NOT NULL,       CONSTRAINT xpkgr_rolestatus              PRIMARY KEY (rolestatus_id)) ;

CREATE TABLE gr_status (       status_id            VARCHAR(32) NOT NULL,       status_name          VARCHAR(200) NOT NULL,       status_isstart         INTEGER ,       status_isfinish         INTEGER ,       status_workflow      VARCHAR(32) NOT NULL,       status_color         VARCHAR(200) NOT NULL ,       CONSTRAINT xpkgr_status              PRIMARY KEY (status_id)) ;

CREATE TABLE gr_subscription (       subscription_id        VARCHAR(32) NOT NULL,       subscription_name      VARCHAR(200) ,       subscription_user      VARCHAR(32) NOT NULL,       subscription_filter    VARCHAR(32) NOT NULL,       subscription_task      VARCHAR(32) NOT NULL,       subscription_startdate TIMESTAMP ,       subscription_stopdate  TIMESTAMP ,       subscription_nextrun   TIMESTAMP ,       subscription_interval  INTEGER ,       subscription_template  VARCHAR(200) ,       CONSTRAINT xpkgr_subscription              PRIMARY KEY (subscription_id)) ;

CREATE TABLE gr_notification (       notification_id        VARCHAR(32) NOT NULL,       notification_name      VARCHAR(200) ,       notification_user      VARCHAR(32) NOT NULL,       notification_filter    VARCHAR(32) NOT NULL,       notification_task      VARCHAR(32) NOT NULL,       notification_template VARCHAR(200) ,       notification_condition VARCHAR(32) ,       CONSTRAINT xpkgr_notification              PRIMARY KEY (notification_id)) ;

CREATE TABLE gr_task (       task_id              VARCHAR(32) NOT NULL,       task_shortname       VARCHAR(200) ,       task_name            VARCHAR(200) NOT NULL,       task_submitdate      TIMESTAMP NOT NULL,       task_updatedate      TIMESTAMP NOT NULL,       task_closedate       TIMESTAMP ,       task_description     VARCHAR(2000) ,       task_abudget         BIGINT ,       task_budget          BIGINT ,       task_deadline        TIMESTAMP ,       task_category        VARCHAR(32) NOT NULL,       task_status          VARCHAR(32) NOT NULL,       task_resolution      VARCHAR(32) ,       task_priority        VARCHAR(32) ,       task_submitter       VARCHAR(32) NOT NULL,       task_handler         VARCHAR(32) NOT NULL,       task_parent          VARCHAR(32) ,       task_longtext        VARCHAR(32) ,       task_number          VARCHAR(32) ,       CONSTRAINT xpkgr_task              PRIMARY KEY (task_id)) ;

CREATE TABLE gr_template (       template_id              VARCHAR(32) NOT NULL,       template_name            VARCHAR(200) NOT NULL,       template_description     VARCHAR(2000) ,       template_owner           VARCHAR(32) NOT NULL,       template_user            VARCHAR(32) ,       template_task            VARCHAR(32) NOT NULL,       template_folder            VARCHAR(200) ,       template_active          INTEGER ,       CONSTRAINT xpkgr_template              PRIMARY KEY (template_id)) ;

CREATE TABLE gr_transition (       transition_id        VARCHAR(32) NOT NULL,       transition_start     VARCHAR(32) NOT NULL,       transition_finish    VARCHAR(32) NOT NULL,       transition_mstatus   VARCHAR(32) NOT NULL,       CONSTRAINT xpkgr_transition              PRIMARY KEY (transition_id)) ;

CREATE TABLE gr_udf (       udf_id               VARCHAR(32) NOT NULL,       udf_caption          VARCHAR(200) ,       udf_referencedbycaption	VARCHAR(200) ,       udf_order            INTEGER ,       udf_def              VARCHAR(200) ,       udf_required         INTEGER ,       udf_htmlview         INTEGER ,       udf_lookuponly       INTEGER ,       udf_cachevalues      INTEGER ,       udf_type             INTEGER ,       udf_udfsource        VARCHAR(32) NOT NULL,       udf_script                       VARCHAR(200) ,       udf_lookupscript                 VARCHAR(200) ,       udf_initialtask                  VARCHAR(32) ,       udf_initialuser                  VARCHAR(32) ,       CONSTRAINT xpkgr_udf              PRIMARY KEY (udf_id)) ;

CREATE TABLE gr_udflist (       udflist_id           VARCHAR(32) NOT NULL,       udflist_val          VARCHAR(200) ,       udflist_udf          VARCHAR(32) NOT NULL,       CONSTRAINT xpkgr_udflist              PRIMARY KEY (udflist_id)) ;

CREATE TABLE gr_udfsource (       udfsource_id         VARCHAR(32) NOT NULL,       udfsource_task       VARCHAR(32) ,       udfsource_user       VARCHAR(32) ,       udfsource_workflow   VARCHAR(32) ,       CONSTRAINT xpkgr_udfsource              PRIMARY KEY (udfsource_id)) ;

CREATE TABLE gr_udfval (       udfval_id            VARCHAR(32) NOT NULL,       udfval_str           VARCHAR(2000) ,       udfval_num           NUMERIC ,       udfval_dat           TIMESTAMP ,       udfval_udflist       VARCHAR(32) ,       udfval_task          VARCHAR(32) ,       udfval_user          VARCHAR(32) ,       udfval_udf           VARCHAR(32) NOT NULL,       udfval_udfsource     VARCHAR(32) NOT NULL,       udfval_longtext      VARCHAR(32) ,       CONSTRAINT xpkgr_udfval              PRIMARY KEY (udfval_id)) ;

CREATE TABLE gr_user (       user_id              VARCHAR(32) NOT NULL,       user_login           VARCHAR(200) NOT NULL,       user_password        VARCHAR(200) ,       user_name            VARCHAR(200) NOT NULL,       user_tel             VARCHAR(200) ,       user_email           VARCHAR(200) ,       user_active          INTEGER ,       user_preferences     VARCHAR(200) ,       user_prstatus        VARCHAR(32) NOT NULL,       user_locale          VARCHAR(200) ,       user_timezone        VARCHAR(200) ,       user_child_allowed   INTEGER ,       user_manager         VARCHAR(32) ,       user_expiredate      TIMESTAMP ,       user_lastlogon       TIMESTAMP ,       user_passchanged TIMESTAMP ,       user_company         VARCHAR(200) ,       user_template        VARCHAR(200) ,       user_default_project VARCHAR(32) ,       CONSTRAINT xpkgr_user              PRIMARY KEY (user_id)) ;

CREATE TABLE gr_usersource (       usersource_id        VARCHAR(32) NOT NULL,       usersource_user      VARCHAR(32) ,       usersource_prstatus  VARCHAR(32) ,       CONSTRAINT xpkgr_usersource              PRIMARY KEY (usersource_id)) ;

CREATE TABLE gr_workflow (       workflow_id          VARCHAR(32) NOT NULL,       workflow_name        VARCHAR(200) NOT NULL,       workflow_task        VARCHAR(32) NOT NULL,       CONSTRAINT xpkgr_workflow              PRIMARY KEY (workflow_id)) ;

CREATE TABLE gr_report (       report_id            VARCHAR(32) NOT NULL,       report_name          VARCHAR(200) NOT NULL,       report_preferences   VARCHAR(200) ,       report_rtype         VARCHAR(32) NOT NULL,       report_priv          INTEGER ,       report_filter        VARCHAR(32) NOT NULL,       report_task          VARCHAR(32) NOT NULL,       report_owner         VARCHAR(32) NOT NULL,       report_params         VARCHAR(2000) ,       CONSTRAINT xpkgr_report              PRIMARY KEY (report_id)) ;

CREATE TABLE gr_priority (        priority_id             VARCHAR(32) NOT NULL,        priority_name           VARCHAR(200) NOT NULL,        priority_order          INTEGER NOT NULL,        priority_workflow       VARCHAR(32) NOT NULL,        priority_description    VARCHAR(2000) ,        priority_def            INTEGER ,        CONSTRAINT xpkgr_priority              PRIMARY KEY (priority_id)) ;

CREATE TABLE gr_catrelation (        catrelation_id          VARCHAR(32) NOT NULL,        catrelation_category    VARCHAR(32) NOT NULL,        catrelation_child       VARCHAR(32) NOT NULL,        CONSTRAINT xpkgr_catrelation                        PRIMARY KEY(catrelation_id)) ;

CREATE TABLE gr_cprstatus (        cprstatus_id VARCHAR(32) NOT NULL,        cprstatus_type VARCHAR(200) ,        cprstatus_category VARCHAR(32) NOT NULL,        cprstatus_prstatus VARCHAR(32) NOT NULL,        CONSTRAINT xpkgr_cprstatus                        PRIMARY KEY(cprstatus_id)) ;

CREATE TABLE gr_uprstatus (        uprstatus_id VARCHAR(32) NOT NULL,        uprstatus_type VARCHAR(200) ,        uprstatus_udf VARCHAR(32) NOT NULL,        uprstatus_prstatus VARCHAR(32) NOT NULL,        CONSTRAINT xpkgr_uprstatus                        PRIMARY KEY(uprstatus_id)) ;

CREATE TABLE gr_umstatus (        umstatus_id VARCHAR(32) NOT NULL,        umstatus_type VARCHAR(200) ,        umstatus_udf VARCHAR(32) NOT NULL,        umstatus_mstatus VARCHAR(32) NOT NULL,        CONSTRAINT xpkgr_umstatus                        PRIMARY KEY(umstatus_id)) ;

CREATE TABLE gr_mailimport (        mailimport_id               VARCHAR(32) NOT NULL,        mailimport_name             VARCHAR(200) NOT NULL ,        mailimport_category         VARCHAR(32) NOT NULL,        mailimport_task             VARCHAR(32) NOT NULL,        mailimport_owner            VARCHAR(32) ,        mailimport_mstatus          VARCHAR(32) ,        mailimport_keywords         VARCHAR(200) ,        mailimport_search_in        INTEGER NOT NULL,        mailimport_order            INTEGER ,        mailimport_active           INTEGER ,        mailimport_domain           VARCHAR(200) ,        mailimport_importUnknown    INTEGER NOT NULL,        CONSTRAINT xpkgr_mailimport                        PRIMARY KEY(mailimport_id)) ;

CREATE TABLE gr_longtext (        longtext_id           VARCHAR(32) NOT NULL,        longtext_reference    VARCHAR(32),        longtext_order        INTEGER NOT NULL,        longtext_value        VARCHAR(2000) NOT NULL,        CONSTRAINT xpkgr_longtext                        PRIMARY KEY(longtext_id)) ;

CREATE TABLE gr_currentfilter (        currentfilter_id           VARCHAR(32) NOT NULL,        currentfilter_task         VARCHAR(32) ,        currentfilter_user         VARCHAR(32) ,        currentfilter_owner        VARCHAR(32) ,        currentfilter_fil       VARCHAR(32) NOT NULL,        CONSTRAINT xpkgr_currentfil                        PRIMARY KEY(currentfilter_id)) ;

CREATE TABLE gr_acl (        acl_id           VARCHAR(32) NOT NULL,        acl_task         VARCHAR(32) ,        acl_to_user      VARCHAR(32) ,        acl_usersource   VARCHAR(32) NOT NULL,        acl_owner        VARCHAR(32) NOT NULL,        acl_override     INTEGER ,        acl_prstatus     VARCHAR(32) ,        CONSTRAINT xpkgr_acl                        PRIMARY KEY(acl_id)) ;

CREATE TABLE gr_registration (        registration_id           VARCHAR(32) NOT NULL,        registration_name           VARCHAR(200) NOT NULL,        registration_user           VARCHAR(32) ,        registration_prstatus           VARCHAR(32) ,        registration_task           VARCHAR(32) ,        registration_category           VARCHAR(32) ,        registration_child_allowed           INTEGER ,        registration_expire_days           INTEGER ,        registration_priv           INTEGER ,        CONSTRAINT xpkgr_registration                        PRIMARY KEY(registration_id)) ;

CREATE TABLE gr_property (        property_id VARCHAR(32) NOT NULL,        property_name VARCHAR(200) NOT NULL,        property_value VARCHAR(2000) NOT NULL,        CONSTRAINT xpkgr_property                   PRIMARY KEY(property_id)) ;

CREATE TABLE gr_trigger (       trigger_id               VARCHAR(32) NOT NULL,       trigger_before           VARCHAR(200) ,       trigger_insteadof        VARCHAR(200) ,       trigger_after            VARCHAR(200) ,       CONSTRAINT xpkgr_trigger              PRIMARY KEY (trigger_id)) ;

CREATE INDEX icategory_1 ON gr_category(       category_workflow);

CREATE INDEX icategory_2 ON gr_category(       category_task);

CREATE INDEX icategory_3 ON gr_category(       category_cr_trigger);

CREATE INDEX icategory_4 ON gr_category(       category_upd_trigger);

CREATE INDEX icategory_5 ON gr_category(       category_template);

CREATE INDEX ifilter_1 ON gr_filter(       filter_task);

CREATE INDEX ifilter_2 ON gr_filter(       filter_owner);

CREATE INDEX ifilter_3 ON gr_filter(       filter_priv);

CREATE INDEX ifilter_5 ON gr_filter(       filter_user);

CREATE INDEX ifvalue_1 ON gr_fvalue(       fvalue_filter);

CREATE INDEX imessage_2 ON gr_message(       message_handler);

CREATE INDEX imessage_3 ON gr_message(       message_resolution);

CREATE INDEX imessage_4 ON gr_message(       message_mstatus);

CREATE INDEX imessage_5 ON gr_message(       message_hrs);

CREATE INDEX imessage_6 ON gr_message(       message_time);

CREATE INDEX imessage_7 ON gr_message(       message_priority);

CREATE INDEX imessage_8 ON gr_message(       message_deadline);

CREATE INDEX imessage_9 ON gr_message(       message_budget);

CREATE INDEX imessage_10 ON gr_message(       message_submitter);

CREATE INDEX imessage_11 ON gr_message(       message_task);

CREATE INDEX imprstatus_1 ON gr_mprstatus(       mprstatus_type);

CREATE INDEX imprstatus_2 ON gr_mprstatus(       mprstatus_mstatus);

CREATE INDEX imprstatus_3 ON gr_mprstatus(       mprstatus_prstatus);

CREATE INDEX imstatus_1 ON gr_mstatus(       mstatus_workflow);

CREATE INDEX imstatus_2 ON gr_mstatus(       mstatus_trigger);

CREATE INDEX iprstatus_1 ON gr_prstatus(       prstatus_user);

CREATE INDEX ireport_1 ON gr_report(       report_filter);

CREATE INDEX ireport_2 ON gr_report(       report_task);

CREATE INDEX ireport_3 ON gr_report(       report_owner);

CREATE INDEX ireport_4 ON gr_report(       report_priv);

CREATE INDEX iresolution_1 ON gr_resolution(       resolution_mstatus);

CREATE INDEX irolestatus_1 ON gr_rolestatus(       rolestatus_prstatus);

CREATE INDEX irolestatus_2 ON gr_rolestatus(       rolestatus_role);

CREATE INDEX istatus_1 ON gr_status(       status_workflow);

CREATE INDEX istatus_2 ON gr_status(       status_isfinish);

CREATE INDEX isubscription_1 ON gr_subscription(       subscription_task);

CREATE INDEX isubscription_2 ON gr_subscription(       subscription_user);

CREATE INDEX isubscription_3 ON gr_subscription(       subscription_filter);

CREATE INDEX itask_1 ON gr_task(       task_parent);

CREATE INDEX itask_2 ON gr_task(       task_shortname);

CREATE INDEX itask_3 ON gr_task(       task_priority);

CREATE INDEX itask_4 ON gr_task(        task_submitter);

CREATE INDEX itask_5 ON gr_task(        task_handler);

CREATE INDEX itask_6 ON gr_task(        task_resolution);

CREATE INDEX itask_7 ON gr_task(        task_status);

CREATE INDEX itask_8 ON gr_task(        task_category);

CREATE UNIQUE INDEX itask_9 ON gr_task(        task_number);

CREATE INDEX itask_10 ON gr_task(        task_submitdate);

CREATE INDEX itask_11 ON gr_task(        task_updatedate);

CREATE INDEX itask_12 ON gr_task(        task_closedate);

CREATE INDEX itask_13 ON gr_task(        task_budget);

CREATE INDEX itask_14 ON gr_task(        task_deadline);

CREATE UNIQUE INDEX itransition_1 ON gr_transition(       transition_start               ,       transition_finish              ,       transition_mstatus);

CREATE INDEX itransition_2 ON gr_transition(       transition_start);

CREATE INDEX itransition_3 ON gr_transition(       transition_finish);

CREATE INDEX itransition_4 ON gr_transition(       transition_mstatus);

CREATE INDEX iudf_1 ON gr_udf(       udf_udfsource);

CREATE INDEX iudflist_1 ON gr_udflist(       udflist_udf);

CREATE INDEX iudfsource_1 ON gr_udfsource(       udfsource_task);

CREATE INDEX iudfsource_2 ON gr_udfsource(       udfsource_user);

CREATE INDEX iudfsource_3 ON gr_udfsource(       udfsource_workflow);

CREATE INDEX iudfval_1 ON gr_udfval(       udfval_udfsource);

CREATE INDEX iudfval_2 ON gr_udfval(       udfval_udflist);

CREATE INDEX iudfval_3 ON gr_udfval(       udfval_udf);

CREATE INDEX iudfval_4 ON gr_udfval(       udfval_task);

CREATE INDEX iudfval_5 ON gr_udfval(       udfval_user);

CREATE INDEX iudfval_6 ON gr_udfval(       udfval_num);

CREATE UNIQUE INDEX iuser_1 ON gr_user(       user_login);

CREATE INDEX iuser_2 ON gr_user(       user_manager);

CREATE INDEX iuser_3 ON gr_user(        user_prstatus);

CREATE INDEX iuser_5 ON gr_user(        user_default_project);

CREATE INDEX iuser_4 ON gr_user(        user_name);

CREATE INDEX iuser_6 ON gr_user(        user_tel);

CREATE INDEX iuser_7 ON gr_user(        user_email);

CREATE INDEX iuser_8 ON gr_user(        user_locale);

CREATE INDEX iuser_9 ON gr_user(        user_timezone);

CREATE INDEX iuser_11 ON gr_user(        user_child_allowed);

CREATE INDEX iuser_12 ON gr_user(        user_expiredate);

CREATE INDEX iuser_13 ON gr_user(        user_company);

CREATE INDEX iuser_15 ON gr_user(        user_lastlogon);

CREATE INDEX iuser_16 ON gr_user(        user_passchanged);

CREATE INDEX iuser_17 ON gr_user(        user_preferences);

CREATE INDEX iusersource_1 ON gr_usersource(       usersource_user);

CREATE INDEX iusersource_2 ON gr_usersource(      usersource_prstatus);

CREATE INDEX iworkflow_1 ON gr_workflow(       workflow_task);

CREATE INDEX ipriority_1 ON gr_priority(       priority_order);

CREATE INDEX ipriority_2 ON gr_priority(       priority_workflow);

CREATE INDEX icatrelation_1 ON gr_catrelation(       catrelation_category);

CREATE INDEX icatrelation_2 ON gr_catrelation(       catrelation_child);

CREATE INDEX icprstatus_1 ON gr_cprstatus(       cprstatus_category);

CREATE INDEX icprstatus_2 ON gr_cprstatus(       cprstatus_prstatus);

CREATE INDEX iuprstatus_1 ON gr_uprstatus(       uprstatus_udf);

CREATE INDEX iuprstatus_2 ON gr_uprstatus(       uprstatus_prstatus);

CREATE INDEX iumtatus_1 ON gr_umstatus(       umstatus_udf);

CREATE INDEX iumtatus_2 ON gr_umstatus(       umstatus_mstatus);

CREATE INDEX inotification_1 ON gr_notification(       notification_task);

CREATE INDEX inotification_2 ON gr_notification(       notification_user);

CREATE INDEX inotification_3 ON gr_notification(       notification_filter);

CREATE INDEX imailimport_2 ON gr_mailimport(       mailimport_category);

CREATE INDEX imailimport_4 ON gr_mailimport(       mailimport_task);

CREATE INDEX imailimport_5 ON gr_mailimport(       mailimport_owner);

CREATE INDEX imailimport_6 ON gr_mailimport(       mailimport_mstatus);

CREATE INDEX ilongtext_1 ON gr_longtext(       longtext_reference);

CREATE INDEX ilongtext_2 ON gr_longtext(       longtext_order);

CREATE INDEX icurrentfilter_1 ON gr_currentfilter(       currentfilter_task);

CREATE INDEX icurrentfilter_2 ON gr_currentfilter(       currentfilter_user);

CREATE INDEX icurrentfilter_3 ON gr_currentfilter(       currentfilter_fil);

CREATE INDEX icurrentfilter_4 ON gr_currentfilter(       currentfilter_owner);

CREATE INDEX iacl_1 ON gr_acl(       acl_task);

CREATE INDEX iacl_2 ON gr_acl(       acl_usersource);

CREATE INDEX iacl_3 ON gr_acl(       acl_prstatus);

CREATE INDEX iacl_4 ON gr_acl(       acl_owner);

CREATE INDEX iacl_5 ON gr_acl(       acl_to_user);

CREATE INDEX iregistration_1 ON gr_registration(       registration_user);

CREATE INDEX iregistration_2 ON gr_registration(       registration_prstatus);

CREATE INDEX iregistration_3 ON gr_registration(       registration_task);

CREATE INDEX iregistration_4 ON gr_registration(       registration_category);

CREATE INDEX iattachment_1 ON gr_attachment(       attachment_task);

CREATE INDEX iproperty_1 ON gr_property(       property_name);

CREATE INDEX itrigger_1 ON gr_trigger(       trigger_before);

CREATE INDEX itrigger_2 ON gr_trigger(       trigger_insteadof);

CREATE INDEX itrigger_3 ON gr_trigger(       trigger_after);

CREATE INDEX itemplate_1 ON gr_template(       template_name);

CREATE INDEX itemplate_2 ON gr_template(       template_owner);

CREATE INDEX itemplate_3 ON gr_template(       template_user);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('1', 'Project', 'Create project', 'YMhms', 'TS', 'Project', '1', '1', 'default_folder2.png', 0, 0, NULL, NULL, NULL);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('4028808a19512fa50119515f006f0008', 'List of requirements', 'Create list of requirements', '', 'TS', 'List of requirements', '1', '1', 'default_folder.png', 0, 0, NULL, NULL, NULL);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('4028808a19512fa5011951659a9e002a', 'List of bugs', 'Create list of bugs', '', 'TS', 'List of bugs', '1', '1', 'default_folder.png', 0, 0, NULL, NULL, NULL);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('4028808a1951e21b01195245ff4200c1', 'Bug', 'Create a bug', 'h', 'TV', 'Bug report', '4028808a194731fd0119476e1efe0003', '1', 'default_bug.png', 1, 0, NULL, NULL, NULL);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('4028808a1951e21b0119524644b800e0', 'Requirement', 'Create Requirement', 'h', 'TV', 'Requirement', '4028808a192e43e801192e527cee013f', '1', 'default_component.png', 0, 0, NULL, NULL, NULL);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('4028808a1951e21b011952475ca000ff', 'Test suite', 'Create test suite', 'h', 'TV', 'Test suite', '4028808a1947f5220119492e773d03c7', '1', 'default_task.png', 0, 0, NULL, NULL, NULL);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('4028808a1951e21b011952b4384d024d', 'List of changes', 'Create list of changes', '', 'TS', 'List of changes', '1', '1', 'default_folder.png', 0, 0, NULL, NULL, NULL);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('4028808a1951e21b011952bddb38032c', 'Change', 'Create change', 'h', 'TV', 'Change in project', '4028808a1951e21b011952b524ff0286', '1', 'default_feature.png', 0, 0, NULL, NULL, NULL);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('4028808a1951e21b011952cefb8f036a', 'Version', 'Create version', 'YMhms', 'TS', 'Version', '1', '1', 'default_folder2.png', 0, 0, NULL, NULL, NULL);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('4028808a19512fa5011951687d82004b', 'List of tests', 'Create list of tests', '', 'TSDV', 'List of tests', '1', '1', 'document.png', 0, 0, NULL, NULL, NULL);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('4028808a1951e21b01195243b62d00a1', 'Test case', 'Create test case', 'Ds', 'TD', 'Test case', '4028808a194731fd011947ab7301002f', '1', 'document.png', 0, 0, NULL, NULL, '4028808a1951e21b0119524438ab00c0');

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('ff8080812b58a7f4012b58ad7cb5003f', 'File container', 'Create a file container', '', 'VB', 'File container', '1', '1', 'filebox.png', 0, 0, NULL, NULL, NULL);

insert into gr_category (category_id, category_name, category_action, category_budget, category_preferences, category_description, category_workflow, category_task, category_icon, category_handler_required, category_group_handler, category_cr_trigger, category_upd_trigger, category_template) values ('ff8080812bd3d88d012bd3e22ff90051', 'Dashboard', 'Add dashboard', '', 'VD', 'Dashboard', '4028808a192e43e801192e527cee013f', '1', 'default_task.png', 0, 0, NULL, NULL, NULL);

insert into gr_filter (filter_id, filter_name, filter_description, filter_preferences, filter_priv, filter_task, filter_user, filter_owner) values ('0', 'All users', 'List of all users', 'T', 0, NULL, '1', '1');

insert into gr_filter (filter_id, filter_name, filter_description, filter_preferences, filter_priv, filter_task, filter_user, filter_owner) values ('1', 'All tasks', 'Return list of all tasks', 'T', 0, '1', '1', '1');

insert into gr_filter (filter_id, filter_name, filter_description, filter_preferences, filter_priv, filter_task, filter_user, filter_owner) values ('4028808a1934fdc7011935080447004e', 'My tasks', 'Return list of my tasks', 'T', 0, '1', '1', '1');

insert into gr_filter (filter_id, filter_name, filter_description, filter_preferences, filter_priv, filter_task, filter_user, filter_owner) values ('4028808a1953022d0119535b5f600159', 'My tasks (deep search)', 'Return list of my tasks (deep search)', '', 0, '1', '1', '1');

insert into gr_filter (filter_id, filter_name, filter_description, filter_preferences, filter_priv, filter_task, filter_user, filter_owner) values ('4028808a1953022d0119535da2c901cc', 'Open tasks', 'Return list of open tasks', 'T', 0, '1', '1', '1');

insert into gr_filter (filter_id, filter_name, filter_description, filter_preferences, filter_priv, filter_task, filter_user, filter_owner) values ('4028808a1953022d011953601937020f', 'Open tasks (deep search)', 'Return list of open tasks (deep search)', '', 0, '1', '1', '1');

insert into gr_filter (filter_id, filter_name, filter_description, filter_preferences, filter_priv, filter_task, filter_user, filter_owner) values ('4028808a1953022d01195368dd4c0291', 'List of requirements', 'Return list of requirements', 'T', 0, '1', '1', '1');

insert into gr_filter (filter_id, filter_name, filter_description, filter_preferences, filter_priv, filter_task, filter_user, filter_owner) values ('4028808a1953022d0119536fd4be02a6', 'List of requirements (deep search)', 'Return list of requirements (deep search)', '', 0, '1', '1', '1');

insert into gr_filter (filter_id, filter_name, filter_description, filter_preferences, filter_priv, filter_task, filter_user, filter_owner) values ('4028808a1953022d0119537e664c0335', 'All tasks (deep search)', 'Return list of all tasks (deep search)', '', 0, '1', '1', '1');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df5742003b', '4028808a1953022d0119535b5f600159', '_handler', '');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df574a003c', '4028808a1953022d0119535b5f600159', 'display', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df5752003d', '4028808a1953022d0119535b5f600159', 'display', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df575a003e', '4028808a1953022d0119535b5f600159', 'display', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df5763003f', '4028808a1953022d0119535b5f600159', 'display', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df576b0040', '4028808a1953022d0119535b5f600159', 'display', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df57740041', '4028808a1953022d0119535b5f600159', 'display', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df57a50042', '4028808a1953022d0119535b5f600159', 'display', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df57df0043', '4028808a1953022d0119535b5f600159', 'display', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df57f00044', '4028808a1953022d0119535b5f600159', 'handler', 'CurrentUserID');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df57f80045', '4028808a1953022d0119535b5f600159', 'onpage', '50');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df58000046', '4028808a1953022d0119535b5f600159', 'sortorder', ';;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df58090047', '4028808a1953022d0119535b5f600159', 'subtask', '1');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df58120048', '4028808a1953022d0119535b5f600159', 'use', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df581a0049', '4028808a1953022d0119535b5f600159', 'use', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df582a004a', '4028808a1953022d0119535b5f600159', 'use', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df5833004b', '4028808a1953022d0119535b5f600159', 'use', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df5846004c', '4028808a1953022d0119535b5f600159', 'use', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df5854004d', '4028808a1953022d0119535b5f600159', 'use', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df585c004e', '4028808a1953022d0119535b5f600159', 'use', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3df5864004f', '4028808a1953022d0119535b5f600159', 'use', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd5004d', '4028808a1934fdc7011935080447004e', '_handler', '');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd5004e', '4028808a1934fdc7011935080447004e', 'ba_updated_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd6004f', '4028808a1934fdc7011935080447004e', 'category', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd60050', '4028808a1934fdc7011935080447004e', 'display', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd60051', '4028808a1934fdc7011935080447004e', 'display', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd60052', '4028808a1934fdc7011935080447004e', 'display', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd70053', '4028808a1934fdc7011935080447004e', 'display', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd70054', '4028808a1934fdc7011935080447004e', 'display', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd70055', '4028808a1934fdc7011935080447004e', 'display', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd80056', '4028808a1934fdc7011935080447004e', 'display', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd80057', '4028808a1934fdc7011935080447004e', 'display', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd80058', '4028808a1934fdc7011935080447004e', 'el_updated_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd80059', '4028808a1934fdc7011935080447004e', 'handler', 'CurrentUserID');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd9005a', '4028808a1934fdc7011935080447004e', 'interval_updated_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd9005b', '4028808a1934fdc7011935080447004e', 'message_view', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd9005c', '4028808a1934fdc7011935080447004e', 'onpage', '50');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dd9005d', '4028808a1934fdc7011935080447004e', 'priority', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dda005e', '4028808a1934fdc7011935080447004e', 'resolution', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dda005f', '4028808a1934fdc7011935080447004e', 'sortorder', ';;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dda0060', '4028808a1934fdc7011935080447004e', 'status', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4ddb0061', '4028808a1934fdc7011935080447004e', 'submitter', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4ddb0062', '4028808a1934fdc7011935080447004e', 'taskname', '_eq_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4ddb0063', '4028808a1934fdc7011935080447004e', 'tasknumber', '_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4ddc0064', '4028808a1934fdc7011935080447004e', 'use', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4ddc0065', '4028808a1934fdc7011935080447004e', 'use', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4ddc0066', '4028808a1934fdc7011935080447004e', 'use', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4ddd0067', '4028808a1934fdc7011935080447004e', 'use', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4ddd0068', '4028808a1934fdc7011935080447004e', 'use', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4ddd0069', '4028808a1934fdc7011935080447004e', 'use', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4ddd006a', '4028808a1934fdc7011935080447004e', 'use', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e41f4dde006b', '4028808a1934fdc7011935080447004e', 'use', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83600d9', '4028808a1953022d01195368dd4c0291', 'UDF4028808a1953022d011953651fd60242', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83600da', '4028808a1953022d01195368dd4c0291', '_category', '');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83700db', '4028808a1953022d01195368dd4c0291', 'abudget', '_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83700dc', '4028808a1953022d01195368dd4c0291', 'ba_submit_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83700dd', '4028808a1953022d01195368dd4c0291', 'ba_updated_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83700de', '4028808a1953022d01195368dd4c0291', 'category', '4028808a1951e21b0119524644b800e0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83800df', '4028808a1953022d01195368dd4c0291', 'display', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83800e0', '4028808a1953022d01195368dd4c0291', 'display', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83800e1', '4028808a1953022d01195368dd4c0291', 'display', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83900e2', '4028808a1953022d01195368dd4c0291', 'display', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83900e3', '4028808a1953022d01195368dd4c0291', 'display', 'submit_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83900e4', '4028808a1953022d01195368dd4c0291', 'display', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83900e5', '4028808a1953022d01195368dd4c0291', 'display', 'abudget');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83a00e6', '4028808a1953022d01195368dd4c0291', 'display', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83a00e7', '4028808a1953022d01195368dd4c0291', 'display', 'fullpath');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83a00e8', '4028808a1953022d01195368dd4c0291', 'display', 'UDF4028808a1953022d011953651fd60242');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83a00e9', '4028808a1953022d01195368dd4c0291', 'el_submit_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83b00ea', '4028808a1953022d01195368dd4c0291', 'el_updated_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83b00eb', '4028808a1953022d01195368dd4c0291', 'handler', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83b00ec', '4028808a1953022d01195368dd4c0291', 'interval_submit_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83b00ed', '4028808a1953022d01195368dd4c0291', 'interval_updated_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83c00ee', '4028808a1953022d01195368dd4c0291', 'message_view', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83c00ef', '4028808a1953022d01195368dd4c0291', 'sortorder', ';;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83c00f0', '4028808a1953022d01195368dd4c0291', 'status', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83d00f1', '4028808a1953022d01195368dd4c0291', 'submitter', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83d00f2', '4028808a1953022d01195368dd4c0291', 'taskname', '_eq_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83d00f3', '4028808a1953022d01195368dd4c0291', 'tasknumber', '_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83d00f4', '4028808a1953022d01195368dd4c0291', 'use', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83e00f5', '4028808a1953022d01195368dd4c0291', 'use', 'fullpath');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83e00f6', '4028808a1953022d01195368dd4c0291', 'use', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83e00f7', '4028808a1953022d01195368dd4c0291', 'use', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83f00f8', '4028808a1953022d01195368dd4c0291', 'use', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83f00f9', '4028808a1953022d01195368dd4c0291', 'use', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83f00fa', '4028808a1953022d01195368dd4c0291', 'use', 'submit_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b83f00fb', '4028808a1953022d01195368dd4c0291', 'use', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b84000fc', '4028808a1953022d01195368dd4c0291', 'use', 'abudget');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e421b84000fd', '4028808a1953022d01195368dd4c0291', 'use', 'UDF4028808a1953022d011953651fd60242');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3400fe', '4028808a1953022d0119536fd4be02a6', 'UDF4028808a1953022d011953651fd60242', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3400ff', '4028808a1953022d0119536fd4be02a6', '_category', '');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e340100', '4028808a1953022d0119536fd4be02a6', 'abudget', '_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e340101', '4028808a1953022d0119536fd4be02a6', 'ba_submit_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e350102', '4028808a1953022d0119536fd4be02a6', 'ba_updated_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e350103', '4028808a1953022d0119536fd4be02a6', 'category', '4028808a1951e21b0119524644b800e0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e350104', '4028808a1953022d0119536fd4be02a6', 'display', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e350105', '4028808a1953022d0119536fd4be02a6', 'display', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e350106', '4028808a1953022d0119536fd4be02a6', 'display', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e360107', '4028808a1953022d0119536fd4be02a6', 'display', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e360108', '4028808a1953022d0119536fd4be02a6', 'display', 'submit_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e360109', '4028808a1953022d0119536fd4be02a6', 'display', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e36010a', '4028808a1953022d0119536fd4be02a6', 'display', 'abudget');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e37010b', '4028808a1953022d0119536fd4be02a6', 'display', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e37010c', '4028808a1953022d0119536fd4be02a6', 'display', 'fullpath');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e37010d', '4028808a1953022d0119536fd4be02a6', 'display', 'UDF4028808a1953022d011953651fd60242');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e37010e', '4028808a1953022d0119536fd4be02a6', 'el_submit_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e38010f', '4028808a1953022d0119536fd4be02a6', 'el_updated_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e380110', '4028808a1953022d0119536fd4be02a6', 'handler', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e380111', '4028808a1953022d0119536fd4be02a6', 'interval_submit_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e380112', '4028808a1953022d0119536fd4be02a6', 'interval_updated_date', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e390113', '4028808a1953022d0119536fd4be02a6', 'message_view', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e390114', '4028808a1953022d0119536fd4be02a6', 'sortorder', ';;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e390115', '4028808a1953022d0119536fd4be02a6', 'status', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e390116', '4028808a1953022d0119536fd4be02a6', 'submitter', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3a0117', '4028808a1953022d0119536fd4be02a6', 'subtask', '1');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3a0118', '4028808a1953022d0119536fd4be02a6', 'taskname', '_eq_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3a0119', '4028808a1953022d0119536fd4be02a6', 'tasknumber', '_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3a011a', '4028808a1953022d0119536fd4be02a6', 'use', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3b011b', '4028808a1953022d0119536fd4be02a6', 'use', 'fullpath');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3b011c', '4028808a1953022d0119536fd4be02a6', 'use', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3b011d', '4028808a1953022d0119536fd4be02a6', 'use', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3c011e', '4028808a1953022d0119536fd4be02a6', 'use', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3c011f', '4028808a1953022d0119536fd4be02a6', 'use', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3c0120', '4028808a1953022d0119536fd4be02a6', 'use', 'submit_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3c0121', '4028808a1953022d0119536fd4be02a6', 'use', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3d0122', '4028808a1953022d0119536fd4be02a6', 'use', 'abudget');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e4221e3d0123', '4028808a1953022d0119536fd4be02a6', 'use', 'UDF4028808a1953022d011953651fd60242');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e42fb8770124', '0', 'display', 'prstatus');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e42fb8770125', '0', 'display', 'login');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e42fb8770126', '0', 'display', 'name');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e42fb8780127', '0', 'login', '_eq_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e42fb8780128', '0', 'name', '_eq_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e42fb8780129', '0', 'prstatus', '0');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e42fb878012a', '0', 'sortorder', ';;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e42fb879012b', '0', 'use', 'login');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e42fb879012c', '0', 'use', 'name');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('402880e420e2d0740120e42fb879012d', '0', 'use', 'prstatus');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3850004', '1', 'display', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a38d0005', '1', 'display', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3960006', '1', 'display', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a39e0007', '1', 'display', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3a60008', '1', 'display', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3ae0009', '1', 'display', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3b7000a', '1', 'display', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3bf000b', '1', 'display', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3c7000c', '1', 'messagecount', '_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3d0000d', '1', 'sortorder', ';;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3d8000e', '1', 'use', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3e0000f', '1', 'use', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3e90010', '1', 'use', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3f10011', '1', 'use', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a3f90012', '1', 'use', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a4020013', '1', 'use', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a40a0014', '1', 'use', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a4120015', '1', 'use', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58a8a4230016', '1', 'word', NULL);

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addbd30067', '4028808a1953022d0119537e664c0335', 'category', '_1');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addbdb0068', '4028808a1953022d0119537e664c0335', 'display', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addbe40069', '4028808a1953022d0119537e664c0335', 'display', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addbec006a', '4028808a1953022d0119537e664c0335', 'display', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addbf4006b', '4028808a1953022d0119537e664c0335', 'display', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addbfd006c', '4028808a1953022d0119537e664c0335', 'display', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc05006d', '4028808a1953022d0119537e664c0335', 'display', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc0d006e', '4028808a1953022d0119537e664c0335', 'display', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc15006f', '4028808a1953022d0119537e664c0335', 'display', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc1e0070', '4028808a1953022d0119537e664c0335', 'messagecount', '_');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc260071', '4028808a1953022d0119537e664c0335', 'onpage', '50');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc2f0072', '4028808a1953022d0119537e664c0335', 'sortorder', ';;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc370073', '4028808a1953022d0119537e664c0335', 'subtask', '1');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc400074', '4028808a1953022d0119537e664c0335', 'use', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc470075', '4028808a1953022d0119537e664c0335', 'use', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc500076', '4028808a1953022d0119537e664c0335', 'use', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc580077', '4028808a1953022d0119537e664c0335', 'use', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc600078', '4028808a1953022d0119537e664c0335', 'use', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc690079', '4028808a1953022d0119537e664c0335', 'use', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc72007a', '4028808a1953022d0119537e664c0335', 'use', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc7b007b', '4028808a1953022d0119537e664c0335', 'use', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812b58a7f4012b58addc82007c', '4028808a1953022d0119537e664c0335', 'word', NULL);

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4200003', '4028808a1953022d0119535da2c901cc', '_handler', '');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4490004', '4028808a1953022d0119535da2c901cc', 'display', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4510005', '4028808a1953022d0119535da2c901cc', 'display', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab45c0006', '4028808a1953022d0119535da2c901cc', 'display', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4620007', '4028808a1953022d0119535da2c901cc', 'display', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab46a0008', '4028808a1953022d0119535da2c901cc', 'display', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4720009', '4028808a1953022d0119535da2c901cc', 'display', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab47b000a', '4028808a1953022d0119535da2c901cc', 'display', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab483000b', '4028808a1953022d0119535da2c901cc', 'display', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab48b000c', '4028808a1953022d0119535da2c901cc', 'onpage', '50');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab494000d', '4028808a1953022d0119535da2c901cc', 'sortorder', ';;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab49c000e', '4028808a1953022d0119535da2c901cc', 'status', '_4028808a193230e301193272f9bf005a');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4a4000f', '4028808a1953022d0119535da2c901cc', 'status', '_4028808a1951e21b011952b5250e0290');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4ad0010', '4028808a1953022d0119535da2c901cc', 'status', '_4028808a1947f52201194889f529010b');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4b50011', '4028808a1953022d0119535da2c901cc', 'status', '_4028808a194731fd0119477770db000d');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4be0012', '4028808a1953022d0119535da2c901cc', 'status', '_4028808a1947f52201194932895a03e7');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4c70013', '4028808a1953022d0119535da2c901cc', 'status', '_4028808a1934592501193488ab250058');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4d80014', '4028808a1953022d0119535da2c901cc', 'status', '_4028808a19345925011934885fc40056');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab4e80015', '4028808a1953022d0119535da2c901cc', 'use', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab51a0016', '4028808a1953022d0119535da2c901cc', 'use', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab52a0017', '4028808a1953022d0119535da2c901cc', 'use', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab5330018', '4028808a1953022d0119535da2c901cc', 'use', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab5430019', '4028808a1953022d0119535da2c901cc', 'use', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab54b001a', '4028808a1953022d0119535da2c901cc', 'use', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab55c001b', '4028808a1953022d0119535da2c901cc', 'use', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dab56d001c', '4028808a1953022d0119535da2c901cc', 'use', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc13001d', '4028808a1953022d011953601937020f', '_handler', '');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc1b001e', '4028808a1953022d011953601937020f', 'display', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc23001f', '4028808a1953022d011953601937020f', 'display', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc2b0020', '4028808a1953022d011953601937020f', 'display', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc340021', '4028808a1953022d011953601937020f', 'display', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc3c0022', '4028808a1953022d011953601937020f', 'display', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc440023', '4028808a1953022d011953601937020f', 'display', 'updated_date');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc4d0024', '4028808a1953022d011953601937020f', 'display', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc550025', '4028808a1953022d011953601937020f', 'display', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc5d0026', '4028808a1953022d011953601937020f', 'onpage', '50');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc660027', '4028808a1953022d011953601937020f', 'sortorder', ';;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc6e0028', '4028808a1953022d011953601937020f', 'status', '_4028808a193230e301193271d0c00059');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc760029', '4028808a1953022d011953601937020f', 'status', '_4028808a193230e301193272f9bf005a');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc7e002a', '4028808a1953022d011953601937020f', 'status', '_2');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc87002b', '4028808a1953022d011953601937020f', 'status', '_4028808a1951e21b011952b5250e0290');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc8f002c', '4028808a1953022d011953601937020f', 'status', '_4028808a1947f52201194889f529010b');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacc9c002d', '4028808a1953022d011953601937020f', 'status', '_4028808a194731fd0119477770db000d');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacca8002e', '4028808a1953022d011953601937020f', 'status', '_4028808a1947f52201194932895a03e7');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3daccda002f', '4028808a1953022d011953601937020f', 'status', '_4028808a1934592501193488ab250058');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacd1c0030', '4028808a1953022d011953601937020f', 'status', '_4028808a19345925011934885fc40056');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacd350031', '4028808a1953022d011953601937020f', 'subtask', '1');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacd3d0032', '4028808a1953022d011953601937020f', 'use', 'category');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacd460033', '4028808a1953022d011953601937020f', 'use', 'handler');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacd4e0034', '4028808a1953022d011953601937020f', 'use', 'priority');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacd560035', '4028808a1953022d011953601937020f', 'use', 'status');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacd5f0036', '4028808a1953022d011953601937020f', 'use', 'submitter');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacd670037', '4028808a1953022d011953601937020f', 'use', 'taskname');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacd6f0038', '4028808a1953022d011953601937020f', 'use', 'tasknumber');

insert into gr_fvalue (fvalue_id, fvalue_filter, fvalue_key, fvalue_value) values ('ff8080812bd3d88d012bd3dacd770039', '4028808a1953022d011953601937020f', 'use', 'updated_date');

insert into gr_message (message_id, message_description, message_submitter, message_task, message_time, message_mstatus, message_hrs, message_deadline, message_budget, message_handler, message_resolution, message_longtext, message_priority) values ('ff8080812bd3d88d012bd3e4a91b0078', '', '1', '4028808a1953022d0119537bdcc2032e', to_timestamp('2010-10-22 16:23:19', 'YYYY-MM-DD HH24:MI:SS'), '4028808a1947f5220119480a35cb003d', 0, NULL, NULL, '1', NULL, NULL, NULL);

insert into gr_message (message_id, message_description, message_submitter, message_task, message_time, message_mstatus, message_hrs, message_deadline, message_budget, message_handler, message_resolution, message_longtext, message_priority) values ('ff8080812c9d426b012c9d45d6310034', '<table  cellpadding=4><tr><th align="right">Приоритет</th><td><strike>Normal</strike></td><td>High</td></tr>
<tr><th align="right">Описание</th><td collspan="2"><SPAN TITLE="i=0">Incorrect login after registration</SPAN><INS STYLE="background:#E6FFE6;" TITLE="i=34">. Add new text</INS><SPAN TITLE="i=48">&lt;br /&gt;</SPAN></td></tr>
</table>', '4028808a192e43e801192e48f4fd0002', '4028808a1953022d0119537bdcc2032e', to_timestamp('2010-11-30 17:53:08', 'YYYY-MM-DD HH24:MI:SS'), 'ff8080812c9d426b012c9d44a9180002', 0, NULL, NULL, 'nullusersource', NULL, NULL, NULL);

insert into gr_message (message_id, message_description, message_submitter, message_task, message_time, message_mstatus, message_hrs, message_deadline, message_budget, message_handler, message_resolution, message_longtext, message_priority) values ('ff8080812ca7a2bb012ca7b03a590033', '<table  cellpadding=4><SPAN TITLE="i=0"><strong>PRELIMINARY REQUIREMENTS:</strong><br /><br />All tasks for designers and developers on work on page the Login should be finished. <br /><br /><strong>PLAN OF ACTION:</strong><br /><br />Open page the Login. <br /><br /><strong>THE CHECK PLAN:</strong><br /><br />- A window the Login openly<br />- The window name - the Login<br />- The company logo is displayed in the right top corner <br />- On the form of 2 fields - the Name and the Password<br />- The button the Login is accessible<br />- The link has forgotten the password - is accessible</SPAN><DEL STYLE="background:#FFE6E6;" TITLE="i=556">&nbsp; <br />
<div>- The link for autoregistation</div></DEL></table>', '1', '4028808a1953022d0119531c0e5500c8', to_timestamp('2010-12-02 18:25:33', 'YYYY-MM-DD HH24:MI:SS'), 'ff8080812ca7a2bb012ca7adc4e60002', 0, NULL, NULL, 'nullusersource', NULL, NULL, NULL);

insert into gr_message (message_id, message_description, message_submitter, message_task, message_time, message_mstatus, message_hrs, message_deadline, message_budget, message_handler, message_resolution, message_longtext, message_priority) values ('ff8080812ca7a2bb012ca7b1c8fb0034', '<table  cellpadding=4><SPAN TITLE="i=0"><strong>PRELIMINARY REQUIREMENTS:</strong><br /><br />All tasks for designers and developers on work on page the Login should be finished. <br /><br /><strong>PLAN OF ACTION:</strong><br /><br />Open page the Login. <br /><br /><strong>THE CHECK PLAN:</strong><br /><br />- A window the Login openly<br />- The window name - the Login<br />- The company logo is displayed in the right top corner <br />- On the form of 2 fields - the Name and the Password<br />- The button the Login is accessible<br />- The link has forgotten the password - is accessible</SPAN><INS STYLE="background:#E6FFE6;" TITLE="i=556">
<div>- And add link for anonumoysly user</div></INS></table>', '1', '4028808a1953022d0119531c0e5500c8', to_timestamp('2010-12-02 18:27:15', 'YYYY-MM-DD HH24:MI:SS'), 'ff8080812ca7a2bb012ca7adc4e60002', 0, NULL, NULL, 'nullusersource', NULL, NULL, NULL);

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866ea5053e', 'V', '4028808a1947f5220119480c50000054', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866eb5053f', 'A', '4028808a1947f5220119480c50000054', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866eb50540', 'B', '4028808a1947f5220119480c50000054', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866ec50541', 'V', '4028808a1947f5220119480c50000054', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866ec50542', 'A', '4028808a1947f5220119480c50000054', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866ed40543', 'B', '4028808a1947f5220119480c50000054', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866ee40544', 'V', '4028808a1947f5220119480c50000054', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866ee40545', 'A', '4028808a1947f5220119480c50000054', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866ee40546', 'B', '4028808a1947f5220119480c50000054', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866ee40547', 'V', '4028808a1947f5220119480c50000054', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866ef30548', 'A', '4028808a1947f5220119480c50000054', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949866ef30549', 'B', '4028808a1947f5220119480c50000054', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f2b054d', 'V', '4028808a1947f5220119480a35cb003d', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f2b054e', 'A', '4028808a1947f5220119480a35cb003d', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f2b054f', 'B', '4028808a1947f5220119480a35cb003d', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f3b0550', 'V', '4028808a1947f5220119480a35cb003d', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f3b0551', 'A', '4028808a1947f5220119480a35cb003d', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f3b0552', 'B', '4028808a1947f5220119480a35cb003d', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f4a0553', 'V', '4028808a1947f5220119480a35cb003d', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f4a0554', 'A', '4028808a1947f5220119480a35cb003d', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f4a0555', 'B', '4028808a1947f5220119480a35cb003d', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f5a0556', 'V', '4028808a1947f5220119480a35cb003d', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f5a0557', 'A', '4028808a1947f5220119480a35cb003d', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949869f5a0558', 'B', '4028808a1947f5220119480a35cb003d', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e116055c', 'V', '4028808a1947f522011948124d73007f', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e116055d', 'A', '4028808a1947f522011948124d73007f', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e116055e', 'B', '4028808a1947f522011948124d73007f', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e135055f', 'V', '4028808a1947f522011948124d73007f', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e1350560', 'A', '4028808a1947f522011948124d73007f', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e1350561', 'B', '4028808a1947f522011948124d73007f', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e1350562', 'V', '4028808a1947f522011948124d73007f', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e1450563', 'A', '4028808a1947f522011948124d73007f', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e1450564', 'B', '4028808a1947f522011948124d73007f', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e1550565', 'V', '4028808a1947f522011948124d73007f', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e1550566', 'A', '4028808a1947f522011948124d73007f', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194986e1550567', 'B', '4028808a1947f522011948124d73007f', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194987b4d10569', 'V', '4028808a1947f52201194813946a0093', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194987b4d1056a', 'V', '4028808a1947f52201194813946a0093', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194987b4e1056c', 'V', '4028808a1947f52201194813946a0093', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194987b4f1056d', 'V', '4028808a1947f52201194813946a0093', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194987b4f1056e', 'V', '4028808a1947f52201194813946a0093', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194987b4f1056f', 'A', '4028808a1947f52201194813946a0093', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194987b5000570', 'B', '4028808a1947f52201194813946a0093', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194987b5000571', 'V', '4028808a1947f52201194813946a0093', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194987b5000572', 'A', '4028808a1947f52201194813946a0093', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f52201194987b5100573', 'B', '4028808a1947f52201194813946a0093', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498807470575', 'V', '4028808a1947f5220119481110ad0069', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498807470576', 'V', '4028808a1947f5220119481110ad0069', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498807560578', 'V', '4028808a1947f5220119481110ad0069', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498807660579', 'V', '4028808a1947f5220119481110ad0069', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949880766057a', 'V', '4028808a1947f5220119481110ad0069', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949880766057b', 'A', '4028808a1947f5220119481110ad0069', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949880776057c', 'B', '4028808a1947f5220119481110ad0069', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949880776057d', 'V', '4028808a1947f5220119481110ad0069', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949880785057e', 'A', '4028808a1947f5220119481110ad0069', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949880785057f', 'B', '4028808a1947f5220119481110ad0069', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498c5b3f05ea', 'V', '4028808a1934933b011934dbdacc0554', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498c5b3f05eb', 'V', '4028808a1934933b011934dbdacc0554', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498c5b4f05ed', 'V', '4028808a1934933b011934dbdacc0554', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498c5b4f05ee', 'V', '4028808a1934933b011934dbdacc0554', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498c5b5f05ef', 'V', '4028808a1934933b011934dbdacc0554', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498c5b5f05f0', 'A', '4028808a1934933b011934dbdacc0554', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498c5b5f05f1', 'B', '4028808a1934933b011934dbdacc0554', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498c5b5f05f2', 'V', '4028808a1934933b011934dbdacc0554', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498c5b6e05f3', 'A', '4028808a1934933b011934dbdacc0554', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498c5b6e05f4', 'B', '4028808a1934933b011934dbdacc0554', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498cf5d90602', 'V', '4028808a1934933b011934dda4420568', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498cf5d90603', 'V', '4028808a1934933b011934dda4420568', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498cf5e90605', 'V', '4028808a1934933b011934dda4420568', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498cf5f90606', 'V', '4028808a1934933b011934dda4420568', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498cf6080607', 'V', '4028808a1934933b011934dda4420568', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498cf6080608', 'A', '4028808a1934933b011934dda4420568', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498cf6080609', 'B', '4028808a1934933b011934dda4420568', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498cf608060a', 'V', '4028808a1934933b011934dda4420568', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498cf608060b', 'A', '4028808a1934933b011934dda4420568', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498cf618060c', 'B', '4028808a1934933b011934dda4420568', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498d3265060e', 'V', '4028808a1934933b011934de39bb057b', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498d3265060f', 'V', '4028808a1934933b011934de39bb057b', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498d32850611', 'V', '4028808a1934933b011934de39bb057b', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498d32850612', 'V', '4028808a1934933b011934de39bb057b', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498d32850613', 'V', '4028808a1934933b011934de39bb057b', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498d32940614', 'A', '4028808a1934933b011934de39bb057b', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498d32940615', 'B', '4028808a1934933b011934de39bb057b', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498d32a40616', 'V', '4028808a1934933b011934de39bb057b', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498d32a40617', 'A', '4028808a1934933b011934de39bb057b', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f5220119498d32a40618', 'B', '4028808a1934933b011934de39bb057b', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d25f0641', 'V', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d25f0642', 'PROCESS_SH', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d25f0643', 'BE_HANDLER_SH', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d26e0644', 'V', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d26e0645', 'PROCESS_SH', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d26e0646', 'BE_HANDLER_SH', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d27e064a', 'V', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d27e064b', 'PROCESS_SH', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d28e064c', 'BE_HANDLER_SH', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d28e064d', 'V', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d28e064e', 'PROCESS_SH', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d28e064f', 'BE_HANDLER_SH', '4028808a1934933b011934987cf30002', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d29d0650', 'V', '4028808a1934933b011934987cf30002', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d29d0651', 'A', '4028808a1934933b011934987cf30002', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d29d0652', 'B', '4028808a1934933b011934987cf30002', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d29d0653', 'V', '4028808a1934933b011934987cf30002', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d29d0654', 'A', '4028808a1934933b011934987cf30002', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a4d2ad0655', 'B', '4028808a1934933b011934987cf30002', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a7c60c0677', 'V', '4028808a1934933b011934a229e10029', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a7c60c0678', 'V', '4028808a1934933b011934a229e10029', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a7c61b067a', 'V', '4028808a1934933b011934a229e10029', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a7c61b067b', 'A', '4028808a1934933b011934a229e10029', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a7c61b067c', 'V', '4028808a1934933b011934a229e10029', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a7c62b067d', 'V', '4028808a1934933b011934a229e10029', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a7c62b067e', 'A', '4028808a1934933b011934a229e10029', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a7c62b067f', 'B', '4028808a1934933b011934a229e10029', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a7c63b0680', 'V', '4028808a1934933b011934a229e10029', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a7c63b0681', 'A', '4028808a1934933b011934a229e10029', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a7c63b0682', 'B', '4028808a1934933b011934a229e10029', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a844b20684', 'V', '4028808a1934933b011934d8a5440518', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a844b20685', 'A', '4028808a1934933b011934d8a5440518', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a844b20686', 'B', '4028808a1934933b011934d8a5440518', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a844c10687', 'V', '4028808a1934933b011934d8a5440518', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a844c10689', 'V', '4028808a1934933b011934d8a5440518', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a844d1068a', 'V', '4028808a1934933b011934d8a5440518', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a844e1068b', 'A', '4028808a1934933b011934d8a5440518', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a844e1068c', 'V', '4028808a1934933b011934d8a5440518', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a844e1068d', 'A', '4028808a1934933b011934d8a5440518', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a844e1068e', 'B', '4028808a1934933b011934d8a5440518', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a844f0068f', 'V', '4028808a1934933b011934d8a5440518', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a845000690', 'A', '4028808a1934933b011934d8a5440518', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a845000691', 'B', '4028808a1934933b011934d8a5440518', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf000693', 'V', '4028808a1934933b011934d96884052c', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf000694', 'A', '4028808a1934933b011934d96884052c', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b525d902da', 'B', '4028808a1951e21b011952b5259b02cd', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf000695', 'V', '4028808a1934933b011934d96884052c', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf100697', 'V', '4028808a1934933b011934d96884052c', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf100698', 'V', '4028808a1934933b011934d96884052c', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf1f0699', 'A', '4028808a1934933b011934d96884052c', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf1f069a', 'B', '4028808a1934933b011934d96884052c', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf1f069b', 'V', '4028808a1934933b011934d96884052c', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf2f069c', 'A', '4028808a1934933b011934d96884052c', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf2f069d', 'B', '4028808a1934933b011934d96884052c', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf2f069e', 'V', '4028808a1934933b011934d96884052c', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf2f069f', 'A', '4028808a1934933b011934d96884052c', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a8cf3e06a0', 'B', '4028808a1934933b011934d96884052c', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a957f606a2', 'V', '4028808a1934933b011934db3e4e0540', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a957f606a3', 'A', '4028808a1934933b011934db3e4e0540', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a957f606a4', 'V', '4028808a1934933b011934db3e4e0540', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a9580606a6', 'V', '4028808a1934933b011934db3e4e0540', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a9580606a7', 'V', '4028808a1934933b011934db3e4e0540', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a9580606a8', 'A', '4028808a1934933b011934db3e4e0540', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a9581606a9', 'V', '4028808a1934933b011934db3e4e0540', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a9581606aa', 'A', '4028808a1934933b011934db3e4e0540', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a9581606ab', 'B', '4028808a1934933b011934db3e4e0540', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a9581606ac', 'V', '4028808a1934933b011934db3e4e0540', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a9582506ad', 'A', '4028808a1934933b011934db3e4e0540', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949a9582506ae', 'B', '4028808a1934933b011934db3e4e0540', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab527e06b2', 'V', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab527e06b3', 'PROCESS_SH', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab527e06b4', 'BE_HANDLER_SH', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab528e06b5', 'V', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab528e06b6', 'PROCESS_SH', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab528e06b7', 'BE_HANDLER_SH', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab529e06bb', 'V', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab529e06bc', 'PROCESS_SH', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab52ad06bd', 'BE_HANDLER_SH', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab52ad06be', 'V', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab52ad06bf', 'PROCESS_SH', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab52ad06c0', 'BE_HANDLER_SH', '4028808a1947f5220119492e776b03e0', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab52ad06c1', 'V', '4028808a1947f5220119492e776b03e0', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab52ad06c2', 'A', '4028808a1947f5220119492e776b03e0', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab52bd06c3', 'B', '4028808a1947f5220119492e776b03e0', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab52bd06c4', 'V', '4028808a1947f5220119492e776b03e0', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab52bd06c5', 'A', '4028808a1947f5220119492e776b03e0', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ab52bd06c6', 'B', '4028808a1947f5220119492e776b03e0', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc7fc06ca', 'V', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc7fc06cb', 'PROCESS_SH', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc7fc06cc', 'BE_HANDLER_SH', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc7fc06cd', 'V', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc7fc06ce', 'PROCESS_SH', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc80c06cf', 'BE_HANDLER_SH', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc80c06d3', 'V', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc80c06d4', 'PROCESS_SH', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc81c06d5', 'BE_HANDLER_SH', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc81c06d6', 'V', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc82b06d7', 'PROCESS_SH', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc82b06d8', 'BE_HANDLER_SH', '4028808a1947f5220119492e774c03ce', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc82b06d9', 'V', '4028808a1947f5220119492e774c03ce', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc82b06da', 'A', '4028808a1947f5220119492e774c03ce', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc82b06db', 'B', '4028808a1947f5220119492e774c03ce', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc82b06dc', 'V', '4028808a1947f5220119492e774c03ce', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc83b06dd', 'A', '4028808a1947f5220119492e774c03ce', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949abc83b06de', 'B', '4028808a1947f5220119492e774c03ce', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac22de06e2', 'V', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac22de06e3', 'PROCESS_SH', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac22de06e4', 'BE_HANDLER_SH', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac22ee06e5', 'V', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac22ee06e6', 'PROCESS_SH', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac22ee06e7', 'BE_HANDLER_SH', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac22fd06eb', 'V', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac22fd06ec', 'PROCESS_SH', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac22fd06ed', 'BE_HANDLER_SH', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac22fd06ee', 'V', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac22fd06ef', 'PROCESS_SH', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac230d06f0', 'BE_HANDLER_SH', '4028808a1947f5220119492e775c03d8', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac230d06f1', 'V', '4028808a1947f5220119492e775c03d8', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac230d06f2', 'A', '4028808a1947f5220119492e775c03d8', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac230d06f3', 'B', '4028808a1947f5220119492e775c03d8', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac230d06f4', 'V', '4028808a1947f5220119492e775c03d8', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac231d06f5', 'A', '4028808a1947f5220119492e775c03d8', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ac231d06f6', 'B', '4028808a1947f5220119492e775c03d8', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949acd7b70710', 'V', '4028808a1947f5220119493553860403', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949acd7b70711', 'V', '4028808a1947f5220119493553860403', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949acd7c70713', 'V', '4028808a1947f5220119493553860403', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949acd7d60714', 'V', '4028808a1947f5220119493553860403', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949acd7d60715', 'V', '4028808a1947f5220119493553860403', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949acd7d60716', 'A', '4028808a1947f5220119493553860403', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949acd7d60717', 'B', '4028808a1947f5220119493553860403', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949acd7e60718', 'V', '4028808a1947f5220119493553860403', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949acd7e60719', 'A', '4028808a1947f5220119493553860403', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949acd7e6071a', 'B', '4028808a1947f5220119493553860403', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b53071e', 'V', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b53071f', 'PROCESS_SH', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b630720', 'BE_HANDLER_SH', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b630721', 'V', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b630722', 'PROCESS_SH', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b630723', 'BE_HANDLER_SH', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b720727', 'V', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b720728', 'PROCESS_SH', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b720729', 'BE_HANDLER_SH', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b72072a', 'V', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b72072b', 'PROCESS_SH', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b82072c', 'BE_HANDLER_SH', '4028808a1947f52201194933183c03e8', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b82072d', 'V', '4028808a1947f52201194933183c03e8', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b82072e', 'A', '4028808a1947f52201194933183c03e8', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b82072f', 'B', '4028808a1947f52201194933183c03e8', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b910730', 'V', '4028808a1947f52201194933183c03e8', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b910731', 'A', '4028808a1947f52201194933183c03e8', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ad3b910732', 'B', '4028808a1947f52201194933183c03e8', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2b50736', 'V', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2b50737', 'PROCESS_SH', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2b50738', 'BE_HANDLER_SH', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2b50739', 'V', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2b5073a', 'PROCESS_SH', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2c5073b', 'BE_HANDLER_SH', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2d5073f', 'V', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2d50740', 'PROCESS_SH', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2d50741', 'BE_HANDLER_SH', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2d50742', 'V', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2e40743', 'PROCESS_SH', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2e40744', 'BE_HANDLER_SH', '4028808a1947f5220119488c003c010c', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2e40745', 'V', '4028808a1947f5220119488c003c010c', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2e40746', 'A', '4028808a1947f5220119488c003c010c', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2f40747', 'B', '4028808a1947f5220119488c003c010c', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2f40748', 'V', '4028808a1947f5220119488c003c010c', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2f40749', 'A', '4028808a1947f5220119488c003c010c', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949adb2f4074a', 'B', '4028808a1947f5220119488c003c010c', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae055a074e', 'V', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae055a074f', 'PROCESS_SH', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae055a0750', 'BE_HANDLER_SH', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae055a0751', 'V', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae05690752', 'PROCESS_SH', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae05690753', 'BE_HANDLER_SH', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae05790757', 'V', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae05790758', 'PROCESS_SH', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae05790759', 'BE_HANDLER_SH', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae0579075a', 'V', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae0579075b', 'PROCESS_SH', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae0589075c', 'BE_HANDLER_SH', '4028808a1947f5220119489805a00123', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae0589075d', 'V', '4028808a1947f5220119489805a00123', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae0589075e', 'A', '4028808a1947f5220119489805a00123', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae0589075f', 'B', '4028808a1947f5220119489805a00123', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae05890760', 'V', '4028808a1947f5220119489805a00123', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae05980761', 'A', '4028808a1947f5220119489805a00123', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae05980762', 'B', '4028808a1947f5220119489805a00123', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44280766', 'V', '4028808a1947f52201194899df520139', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44280767', 'PROCESS_SH', '4028808a1947f52201194899df520139', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44280768', 'BE_HANDLER_SH', '4028808a1947f52201194899df520139', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44370769', 'V', '4028808a1947f52201194899df520139', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae4437076a', 'PROCESS_SH', '4028808a1947f52201194899df520139', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae4447076b', 'BE_HANDLER_SH', '4028808a1947f52201194899df520139', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae4447076f', 'V', '4028808a1947f52201194899df520139', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44570770', 'PROCESS_SH', '4028808a1947f52201194899df520139', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44570771', 'BE_HANDLER_SH', '4028808a1947f52201194899df520139', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44570772', 'V', '4028808a1947f52201194899df520139', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44570773', 'PROCESS_SH', '4028808a1947f52201194899df520139', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44570774', 'BE_HANDLER_SH', '4028808a1947f52201194899df520139', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44660775', 'V', '4028808a1947f52201194899df520139', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44660776', 'A', '4028808a1947f52201194899df520139', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44660777', 'B', '4028808a1947f52201194899df520139', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44660778', 'V', '4028808a1947f52201194899df520139', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae44660779', 'A', '4028808a1947f52201194899df520139', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae4466077a', 'B', '4028808a1947f52201194899df520139', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae91ac077c', 'V', '4028808a1947f5220119489a584b014d', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae91ac077d', 'V', '4028808a1947f5220119489a584b014d', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae91bb077f', 'V', '4028808a1947f5220119489a584b014d', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae91bb0780', 'V', '4028808a1947f5220119489a584b014d', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae91cb0781', 'V', '4028808a1947f5220119489a584b014d', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae91cb0782', 'A', '4028808a1947f5220119489a584b014d', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae91cb0783', 'B', '4028808a1947f5220119489a584b014d', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae91cb0784', 'V', '4028808a1947f5220119489a584b014d', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae91cb0785', 'A', '4028808a1947f5220119489a584b014d', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949ae91da0786', 'B', '4028808a1947f5220119489a584b014d', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d0a078a', 'V', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d1a078b', 'PROCESS_SH', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d1a078c', 'BE_HANDLER_SH', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d1a078d', 'V', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d1a078e', 'PROCESS_SH', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d1a078f', 'BE_HANDLER_SH', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d290793', 'V', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d390794', 'PROCESS_SH', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d390795', 'BE_HANDLER_SH', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d390796', 'V', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d390797', 'PROCESS_SH', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d390798', 'BE_HANDLER_SH', '4028808a1947f5220119489b99a40162', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d490799', 'V', '4028808a1947f5220119489b99a40162', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d49079a', 'A', '4028808a1947f5220119489b99a40162', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d49079b', 'B', '4028808a1947f5220119489b99a40162', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d49079c', 'V', '4028808a1947f5220119489b99a40162', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d49079d', 'A', '4028808a1947f5220119489b99a40162', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949af5d58079e', 'B', '4028808a1947f5220119489b99a40162', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b6696c07a8', 'V', '4028808a1934933b011934a02854001f', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b6697c07a9', 'V', '4028808a1934933b011934a02854001f', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b6697c07aa', 'A', '4028808a1934933b011934a02854001f', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b6698b07ac', 'V', '4028808a1934933b011934a02854001f', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b6698b07ad', 'A', '4028808a1934933b011934a02854001f', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b6698b07ae', 'B', '4028808a1934933b011934a02854001f', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b6698b07af', 'V', '4028808a1934933b011934a02854001f', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b6699b07b0', 'V', '4028808a1934933b011934a02854001f', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b6699b07b1', 'A', '4028808a1934933b011934a02854001f', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b6699b07b2', 'B', '4028808a1934933b011934a02854001f', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b669aa07b3', 'V', '4028808a1934933b011934a02854001f', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b669aa07b4', 'A', '4028808a1934933b011934a02854001f', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1947f522011949b669ba07b5', 'B', '4028808a1934933b011934a02854001f', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694910124', 'V', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694910125', 'A', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694a10126', 'B', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694a10127', 'V', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694a10128', 'A', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694b10129', 'B', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694b1012a', 'V', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694c0012b', 'A', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694c0012c', 'B', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694c0012d', 'V', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694d0012e', 'A', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694d0012f', 'B', '4028808a193230e3011932762dd1005b', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694d00130', 'V', '4028808a193230e3011932762dd1005b', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694df0131', 'A', '4028808a193230e3011932762dd1005b', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694df0132', 'B', '4028808a193230e3011932762dd1005b', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694df0133', 'V', '4028808a193230e3011932762dd1005b', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694ef0134', 'A', '4028808a193230e3011932762dd1005b', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b0119526694ef0135', 'B', '4028808a193230e3011932762dd1005b', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c96d0136', 'V', '4028808a193230e301193279870e0062', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c96d0137', 'A', '4028808a193230e301193279870e0062', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c97c0138', 'B', '4028808a193230e301193279870e0062', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c97c0139', 'V', '4028808a193230e301193279870e0062', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c97c013a', 'A', '4028808a193230e301193279870e0062', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c97c013b', 'B', '4028808a193230e301193279870e0062', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c98c013c', 'V', '4028808a193230e301193279870e0062', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c98c013d', 'A', '4028808a193230e301193279870e0062', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c98c013e', 'B', '4028808a193230e301193279870e0062', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c98c013f', 'V', '4028808a193230e301193279870e0062', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c99b0140', 'A', '4028808a193230e301193279870e0062', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c99b0141', 'B', '4028808a193230e301193279870e0062', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c9ab0142', 'V', '4028808a193230e301193279870e0062', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c9ab0143', 'A', '4028808a193230e301193279870e0062', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c9bb0144', 'B', '4028808a193230e301193279870e0062', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c9bb0145', 'V', '4028808a193230e301193279870e0062', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c9bb0146', 'A', '4028808a193230e301193279870e0062', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266c9bb0147', 'B', '4028808a193230e301193279870e0062', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef540148', 'V', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef540149', 'A', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef63014a', 'B', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef63014b', 'V', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef63014c', 'A', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef63014d', 'B', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef73014e', 'V', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef73014f', 'A', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef730150', 'B', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef830151', 'V', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef830152', 'A', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef830153', 'B', '4028808a193230e3011932b99a310102', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef920154', 'V', '4028808a193230e3011932b99a310102', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef920155', 'A', '4028808a193230e3011932b99a310102', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266ef920156', 'B', '4028808a193230e3011932b99a310102', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266efa20157', 'V', '4028808a193230e3011932b99a310102', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266efa20158', 'A', '4028808a193230e3011932b99a310102', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b01195266efa20159', 'B', '4028808a193230e3011932b99a310102', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952a858b00242', 'V', '4028808a1934933b0119349ef13b0013', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952a858b00243', 'A', '4028808a1934933b0119349ef13b0013', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952a858c00244', 'B', '4028808a1934933b0119349ef13b0013', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952a858c00245', 'V', '4028808a1934933b0119349ef13b0013', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952a858c00246', 'A', '4028808a1934933b0119349ef13b0013', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952a858cf0247', 'V', '4028808a1934933b0119349ef13b0013', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952a858df0248', 'A', '4028808a1934933b0119349ef13b0013', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952a858df0249', 'B', '4028808a1934933b0119349ef13b0013', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952a858df024a', 'V', '4028808a1934933b0119349ef13b0013', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952a858df024b', 'A', '4028808a1934933b0119349ef13b0013', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952a858ef024c', 'B', '4028808a1934933b0119349ef13b0013', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d0298', 'V', '4028808a1951e21b011952b5250e0292', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d0299', 'A', '4028808a1951e21b011952b5250e0292', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d029a', 'B', '4028808a1951e21b011952b5250e0292', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d029b', 'V', '4028808a1951e21b011952b5250e0292', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d029c', 'A', '4028808a1951e21b011952b5250e0292', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d029d', 'B', '4028808a1951e21b011952b5250e0292', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d029e', 'V', '4028808a1951e21b011952b5250e0292', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d029f', 'A', '4028808a1951e21b011952b5250e0292', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d02a0', 'B', '4028808a1951e21b011952b5250e0292', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d02a1', 'V', '4028808a1951e21b011952b5250e0292', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d02a2', 'A', '4028808a1951e21b011952b5250e0292', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5252d02a3', 'B', '4028808a1951e21b011952b5250e0292', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5254d02a6', 'V', '4028808a1951e21b011952b5253d02a4', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5254d02a7', 'A', '4028808a1951e21b011952b5253d02a4', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5254d02a8', 'B', '4028808a1951e21b011952b5253d02a4', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5254d02a9', 'V', '4028808a1951e21b011952b5253d02a4', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5254d02aa', 'A', '4028808a1951e21b011952b5253d02a4', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5254d02ab', 'B', '4028808a1951e21b011952b5253d02a4', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5254d02ac', 'V', '4028808a1951e21b011952b5253d02a4', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5254d02ad', 'A', '4028808a1951e21b011952b5253d02a4', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5254d02ae', 'B', '4028808a1951e21b011952b5253d02a4', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5255c02af', 'V', '4028808a1951e21b011952b5253d02a4', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5255c02b0', 'A', '4028808a1951e21b011952b5253d02a4', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5255c02b1', 'B', '4028808a1951e21b011952b5253d02a4', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5256c02b5', 'V', '4028808a1951e21b011952b5255c02b2', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5256c02b6', 'V', '4028808a1951e21b011952b5255c02b2', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5256c02b7', 'V', '4028808a1951e21b011952b5255c02b2', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5256c02b8', 'V', '4028808a1951e21b011952b5255c02b2', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5256c02b9', 'V', '4028808a1951e21b011952b5255c02b2', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5256c02ba', 'A', '4028808a1951e21b011952b5255c02b2', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5256c02bb', 'B', '4028808a1951e21b011952b5255c02b2', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5256c02bc', 'V', '4028808a1951e21b011952b5255c02b2', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5257c02bd', 'A', '4028808a1951e21b011952b5255c02b2', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5257c02be', 'B', '4028808a1951e21b011952b5255c02b2', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5258b02c1', 'V', '4028808a1951e21b011952b5258b02bf', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5258b02c2', 'A', '4028808a1951e21b011952b5258b02bf', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5258b02c3', 'B', '4028808a1951e21b011952b5258b02bf', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5258b02c4', 'V', '4028808a1951e21b011952b5258b02bf', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5258b02c5', 'A', '4028808a1951e21b011952b5258b02bf', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5258b02c6', 'B', '4028808a1951e21b011952b5258b02bf', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5259b02c7', 'V', '4028808a1951e21b011952b5258b02bf', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5259b02c8', 'A', '4028808a1951e21b011952b5258b02bf', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5259b02c9', 'B', '4028808a1951e21b011952b5258b02bf', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5259b02ca', 'V', '4028808a1951e21b011952b5258b02bf', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5259b02cb', 'A', '4028808a1951e21b011952b5258b02bf', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b5259b02cc', 'B', '4028808a1951e21b011952b5258b02bf', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b525d902d4', 'V', '4028808a1951e21b011952b5259b02cd', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b525d902d5', 'V', '4028808a1951e21b011952b5259b02cd', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b525d902d6', 'V', '4028808a1951e21b011952b5259b02cd', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b525d902d7', 'V', '4028808a1951e21b011952b5259b02cd', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b525d902d8', 'V', '4028808a1951e21b011952b5259b02cd', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b525d902d9', 'A', '4028808a1951e21b011952b5259b02cd', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b525d902db', 'V', '4028808a1951e21b011952b5259b02cd', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b525d902dc', 'A', '4028808a1951e21b011952b5259b02cd', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1951e21b011952b525d902dd', 'B', '4028808a1951e21b011952b5259b02cd', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ef00de', 'V', '4028808a1947f5220119480c50000054', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ef00df', 'A', '4028808a1947f5220119480c50000054', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ef00e0', 'B', '4028808a1947f5220119480c50000054', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ef00e1', 'V', '4028808a1947f5220119480a35cb003d', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ef00e2', 'A', '4028808a1947f5220119480a35cb003d', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ef00e3', 'B', '4028808a1947f5220119480a35cb003d', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ef00e4', 'V', '4028808a1947f522011948124d73007f', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ef00e5', 'A', '4028808a1947f522011948124d73007f', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ef00e6', 'B', '4028808a1947f522011948124d73007f', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ef00e7', 'V', '4028808a1947f52201194813946a0093', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00e8', 'V', '4028808a1947f5220119481110ad0069', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00e9', 'V', '4028808a1934933b011934dbdacc0554', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00ea', 'V', '4028808a1934933b011934dda4420568', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00eb', 'V', '4028808a1934933b011934de39bb057b', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00ec', 'V', '4028808a1934933b011934987cf30002', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00ed', 'PROCESS_SH', '4028808a1934933b011934987cf30002', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00ee', 'BE_HANDLER_SH', '4028808a1934933b011934987cf30002', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00ef', 'V', '4028808a1934933b011934a229e10029', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00f0', 'V', '4028808a1934933b011934d8a5440518', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00f1', 'A', '4028808a1934933b011934d8a5440518', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00f2', 'V', '4028808a1934933b011934d96884052c', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00f3', 'A', '4028808a1934933b011934d96884052c', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00f4', 'B', '4028808a1934933b011934d96884052c', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00f5', 'V', '4028808a1934933b011934db3e4e0540', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00f6', 'A', '4028808a1934933b011934db3e4e0540', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed65ff00f7', 'V', '4028808a1947f5220119492e776b03e0', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e00f8', 'PROCESS_SH', '4028808a1947f5220119492e776b03e0', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e00f9', 'BE_HANDLER_SH', '4028808a1947f5220119492e776b03e0', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e00fa', 'V', '4028808a1947f5220119492e774c03ce', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e00fb', 'PROCESS_SH', '4028808a1947f5220119492e774c03ce', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e00fc', 'BE_HANDLER_SH', '4028808a1947f5220119492e774c03ce', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e00fd', 'V', '4028808a1947f5220119492e775c03d8', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e00fe', 'PROCESS_SH', '4028808a1947f5220119492e775c03d8', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e00ff', 'BE_HANDLER_SH', '4028808a1947f5220119492e775c03d8', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e0100', 'V', '4028808a1947f5220119493553860403', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e0101', 'V', '4028808a1947f52201194933183c03e8', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e0102', 'PROCESS_SH', '4028808a1947f52201194933183c03e8', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e0103', 'BE_HANDLER_SH', '4028808a1947f52201194933183c03e8', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e0104', 'V', '4028808a1947f5220119488c003c010c', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed660e0105', 'PROCESS_SH', '4028808a1947f5220119488c003c010c', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e0106', 'BE_HANDLER_SH', '4028808a1947f5220119488c003c010c', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e0107', 'V', '4028808a1947f5220119489805a00123', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e0108', 'PROCESS_SH', '4028808a1947f5220119489805a00123', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e0109', 'BE_HANDLER_SH', '4028808a1947f5220119489805a00123', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e010a', 'V', '4028808a1947f52201194899df520139', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e010b', 'PROCESS_SH', '4028808a1947f52201194899df520139', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e010c', 'BE_HANDLER_SH', '4028808a1947f52201194899df520139', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e010d', 'V', '4028808a1947f5220119489a584b014d', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e010e', 'V', '4028808a1947f5220119489b99a40162', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e010f', 'PROCESS_SH', '4028808a1947f5220119489b99a40162', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e0110', 'BE_HANDLER_SH', '4028808a1947f5220119489b99a40162', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e0111', 'V', '4028808a1934933b011934a02854001f', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e0112', 'V', '4028808a193230e3011932762dd1005b', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e0113', 'A', '4028808a193230e3011932762dd1005b', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed661e0114', 'B', '4028808a193230e3011932762dd1005b', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e0115', 'V', '4028808a193230e301193279870e0062', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e0116', 'A', '4028808a193230e301193279870e0062', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e0117', 'B', '4028808a193230e301193279870e0062', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e0118', 'V', '4028808a193230e3011932b99a310102', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e0119', 'A', '4028808a193230e3011932b99a310102', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e011a', 'B', '4028808a193230e3011932b99a310102', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e011b', 'V', '4028808a1951e21b011952b5250e0292', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e011c', 'A', '4028808a1951e21b011952b5250e0292', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e011d', 'B', '4028808a1951e21b011952b5250e0292', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e011e', 'V', '4028808a1951e21b011952b5253d02a4', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e011f', 'A', '4028808a1951e21b011952b5253d02a4', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e0120', 'B', '4028808a1951e21b011952b5253d02a4', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e0121', 'V', '4028808a1951e21b011952b5255c02b2', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed662e0122', 'V', '4028808a1951e21b011952b5258b02bf', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed663d0123', 'A', '4028808a1951e21b011952b5258b02bf', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed663d0124', 'B', '4028808a1951e21b011952b5258b02bf', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('4028808a1952eada011952ed663d0125', 'V', '4028808a1951e21b011952b5259b02cd', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df39440006', 'V', '402880e620999032012099df38c70005', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df39730007', 'A', '402880e620999032012099df38c70005', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df39920008', 'B', '402880e620999032012099df38c70005', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df39b10009', 'V', '402880e620999032012099df38c70005', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df39d1000a', 'A', '402880e620999032012099df38c70005', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df39f0000b', 'B', '402880e620999032012099df38c70005', '4028808a1934933b011934c1e26c020c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3a0f000c', 'V', '402880e620999032012099df38c70005', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3a4e000d', 'A', '402880e620999032012099df38c70005', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3a5d000e', 'B', '402880e620999032012099df38c70005', '4028808a1952eada011952ed6563009a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3a6d000f', 'V', '402880e620999032012099df38c70005', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3a7c0010', 'A', '402880e620999032012099df38c70005', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3a8c0011', 'B', '402880e620999032012099df38c70005', '4028808a193230e3011932be7da8010a');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3a9c0012', 'V', '402880e620999032012099df38c70005', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3aab0013', 'A', '402880e620999032012099df38c70005', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3abb0014', 'B', '402880e620999032012099df38c70005', '4028808a1934933b011934c2214a02a4');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3abb0015', 'V', '402880e620999032012099df38c70005', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3acb0016', 'A', '402880e620999032012099df38c70005', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3ada0017', 'B', '402880e620999032012099df38c70005', '4028808a1934933b011934c25dc6033c');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3aea0018', 'V', '402880e620999032012099df38c70005', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3af90019', 'A', '402880e620999032012099df38c70005', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('402880e620999032012099df3af9001a', 'B', '402880e620999032012099df38c70005', '4028808a1934933b011934c5ea5803ee');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('ff8080812c9d426b012c9d4536f3001b', 'V', 'ff8080812c9d426b012c9d44a9180002', '5');

insert into gr_mprstatus (mprstatus_id, mprstatus_type, mprstatus_mstatus, mprstatus_prstatus) values ('ff8080812ca7a2bb012ca7afe211001b', 'V', 'ff8080812ca7a2bb012ca7adc4e60002', '5');

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a193230e3011932762dd1005b', 'Start process', 'Start process', 'T', 'Start process', '1', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a193230e301193279870e0062', 'Resolve', 'Resolve', 'T', 'Resolve', '1', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a193230e3011932b99a310102', 'Note', 'Note', 'T', 'Add a Note', '1', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1934933b011934987cf30002', 'Note', 'Note for this requirement', 'T', 'Add a Note', '4028808a192e43e801192e527cee013f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1934933b0119349ef13b0013', 'Analyse', 'Analyse requirement', 'T', 'Analyse requirement', '4028808a192e43e801192e527cee013f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1934933b011934a02854001f', 'Write technical specification', 'Write technical specification', 'T', 'Write technical specification', '4028808a192e43e801192e527cee013f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1934933b011934a229e10029', 'Approve', 'Approve requirement', 'T', 'Approve requirement', '4028808a192e43e801192e527cee013f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1934933b011934d8a5440518', 'Start development', 'Start development', 'T', 'Start development', '4028808a192e43e801192e527cee013f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1934933b011934d96884052c', 'Start testing', 'Start testing', 'T', 'Start testing', '4028808a192e43e801192e527cee013f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1934933b011934db3e4e0540', 'Accept', 'Accept requirement', 'T', 'Accept requirement', '4028808a192e43e801192e527cee013f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1934933b011934dbdacc0554', 'Close', 'Close requirement', 'T', 'Close requirement', '4028808a192e43e801192e527cee013f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1934933b011934dda4420568', 'Pause', 'Pause requirement', 'T', 'Pause requirement', '4028808a192e43e801192e527cee013f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1934933b011934de39bb057b', 'Decline', 'Decline requirement', 'T', 'Decline requirement', '4028808a192e43e801192e527cee013f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f5220119480a35cb003d', 'Note', 'Note for this bug', 'T', 'Add a Note', '4028808a194731fd0119476e1efe0003', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f5220119480c50000054', 'Start process', 'Start process', 'T', 'Start process', '4028808a194731fd0119476e1efe0003', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f5220119481110ad0069', 'Pause', 'Pause this bug', 'T', 'Pause this bug', '4028808a194731fd0119476e1efe0003', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f522011948124d73007f', 'Resolve', 'Resolve this bug', 'T', 'Resolve this bug', '4028808a194731fd0119476e1efe0003', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f52201194813946a0093', 'Close', 'Close this bug', 'T', 'Close this bug', '4028808a194731fd0119476e1efe0003', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f5220119488c003c010c', 'Note', 'Note for this test case', 'T', 'Add a Note', '4028808a194731fd011947ab7301002f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f5220119489805a00123', 'Start process', 'Start process', 'T', 'Start process', '4028808a194731fd011947ab7301002f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f52201194899df520139', 'Ready', 'Ready', 'T', 'Ready', '4028808a194731fd011947ab7301002f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f5220119489a584b014d', 'Out of date', 'Out of date', 'T', 'Out of date', '4028808a194731fd011947ab7301002f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f5220119489b99a40162', 'Improve', 'Improve this test case', 'T', 'Improve this test case', '4028808a194731fd011947ab7301002f', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f5220119492e774c03ce', 'Start process', 'Start process', 'T', 'Start process', '4028808a1947f5220119492e773d03c7', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f5220119492e775c03d8', 'Ready', 'Ready', 'T', 'Ready', '4028808a1947f5220119492e773d03c7', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f5220119492e776b03e0', 'Note', 'Note for this test suite', 'T', 'Add a Note', '4028808a1947f5220119492e773d03c7', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f52201194933183c03e8', 'Improve', 'Improve this test suite', 'T', 'Improve this test suite', '4028808a1947f5220119492e773d03c7', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1947f5220119493553860403', 'Out of date', 'Out of date', 'T', 'Out of date', '4028808a1947f5220119492e773d03c7', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1951e21b011952b5250e0292', 'Note', 'Note for this change', 'T', 'Add a Note', '4028808a1951e21b011952b524ff0286', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1951e21b011952b5253d02a4', 'Start process', 'Start process', 'T', 'Start process', '4028808a1951e21b011952b524ff0286', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1951e21b011952b5255c02b2', 'Pause', 'Pause change', 'T', 'Pause change', '4028808a1951e21b011952b524ff0286', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1951e21b011952b5258b02bf', 'Resolve', 'Resolve change', 'T', 'Resolve change', '4028808a1951e21b011952b524ff0286', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('4028808a1951e21b011952b5259b02cd', 'Close', 'Close change', 'T', 'Close change', '4028808a1951e21b011952b524ff0286', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('402880e620999032012099df38c70005', 'Reopen', 'Reopen change', 'T', 'Reopen change', '4028808a1951e21b011952b524ff0286', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('ff8080812c9d426b012c9d44a9180002', '*', '', '', '*', '4028808a194731fd0119476e1efe0003', NULL);

insert into gr_mstatus (mstatus_id, mstatus_name, mstatus_description, mstatus_preferences, mstatus_action, mstatus_workflow, mstatus_trigger) values ('ff8080812ca7a2bb012ca7adc4e60002', '*', '', '', '*', '4028808a194731fd011947ab7301002f', NULL);

insert into gr_prstatus (prstatus_id, prstatus_name, prstatus_description, prstatus_preferences, prstatus_user) values ('4028808a193230e3011932be7da8010a', 'Manager', '', '', '1');

insert into gr_prstatus (prstatus_id, prstatus_name, prstatus_description, prstatus_preferences, prstatus_user) values ('4028808a1934933b011934c1e26c020c', 'Analitics', '', '', '1');

insert into gr_prstatus (prstatus_id, prstatus_name, prstatus_description, prstatus_preferences, prstatus_user) values ('4028808a1934933b011934c2214a02a4', 'Developer', '', '', '1');

insert into gr_prstatus (prstatus_id, prstatus_name, prstatus_description, prstatus_preferences, prstatus_user) values ('4028808a1934933b011934c25dc6033c', 'Tester', '', '', '1');

insert into gr_prstatus (prstatus_id, prstatus_name, prstatus_description, prstatus_preferences, prstatus_user) values ('4028808a1934933b011934c5ea5803ee', 'Tech writer', '', '', '1');

insert into gr_prstatus (prstatus_id, prstatus_name, prstatus_description, prstatus_preferences, prstatus_user) values ('4028808a1952eada011952ed6563009a', 'External Customer', '', '', '1');

insert into gr_prstatus (prstatus_id, prstatus_name, prstatus_description, prstatus_preferences, prstatus_user) values ('5', 'Administrator', '', 'T', '1');

insert into gr_prstatus (prstatus_id, prstatus_name, prstatus_description, prstatus_preferences, prstatus_user) values ('ff8080812c34f3f1012c34f7df880002', 'anonymous role', NULL, '', '1');

insert into gr_resolution (resolution_id, resolution_name, resolution_mstatus, resolution_isdefault) values ('4028808a1947f52201194813f61200a6', 'Fixed', '4028808a1947f52201194813946a0093', 1);

insert into gr_resolution (resolution_id, resolution_name, resolution_mstatus, resolution_isdefault) values ('4028808a1947f5220119481415fe00a7', 'Not a bug', '4028808a1947f52201194813946a0093', 0);

insert into gr_resolution (resolution_id, resolution_name, resolution_mstatus, resolution_isdefault) values ('4028808a1947f522011948142ca300a8', 'Duplicate', '4028808a1947f52201194813946a0093', 0);

insert into gr_resolution (resolution_id, resolution_name, resolution_mstatus, resolution_isdefault) values ('4028808a1951e21b011952b5259b02ce', 'Fixed', '4028808a1951e21b011952b5259b02cd', 1);

insert into gr_resolution (resolution_id, resolution_name, resolution_mstatus, resolution_isdefault) values ('4028808a1951e21b011952b5259b02cf', 'Not a bug', '4028808a1951e21b011952b5259b02cd', 0);

insert into gr_resolution (resolution_id, resolution_name, resolution_mstatus, resolution_isdefault) values ('4028808a1951e21b011952b5259b02d0', 'Duplicate', '4028808a1951e21b011952b5259b02cd', 0);

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('297e234c0134e1ea010134e2065e0006', '5', 'editTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('297e234c0134e1ea010134e2065e0007', '5', 'useChat');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('297e234c0134e1ea010134e2065e0008', '4028808a193230e3011932be7da8010a', 'useChat');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('297e234c0134e1ea010134e2065e0009', '4028808a1934933b011934c1e26c020c', 'useChat');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('297e234c0134e1ea010134e2065e0010', '4028808a1934933b011934c2214a02a4', 'useChat');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('297e234c0134e1ea010134e2065e0011', '4028808a1934933b011934c25dc6033c', 'useChat');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('297e234c0134e1ea010134e2065e0012', '4028808a1934933b011934c5ea5803ee', 'useChat');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('297e234c0134e1ea010134e2065e0013', '4028808a1952eada011952ed6563009a', 'useChat');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0072', '5', 'createTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0059', '5', 'manageTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0060', '4028808a193230e3011932be7da8010a', 'createTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0061', '4028808a193230e3011932be7da8010a', 'manageTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0062', '4028808a1934933b011934c1e26c020c', 'createTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0063', '4028808a1934933b011934c1e26c020c', 'manageTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0064', '4028808a1934933b011934c2214a02a4', 'createTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0065', '4028808a1934933b011934c2214a02a4', 'manageTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0066', '4028808a1934933b011934c25dc6033c', 'createTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0067', '4028808a1934933b011934c25dc6033c', 'manageTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0068', '4028808a1934933b011934c5ea5803ee', 'createTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0069', '4028808a1934933b011934c5ea5803ee', 'manageTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0070', '4028808a1952eada011952ed6563009a', 'createTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0071', '4028808a1952eada011952ed6563009a', 'manageTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7dd70122', '4028808a193230e3011932be7da8010a', 'createUser');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7dd70128', '4028808a193230e3011932be7da8010a', 'cutCopyPasteTask');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7dd70129', '4028808a193230e3011932be7da8010a', 'cutPasteUser');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7dd7012d', '4028808a193230e3011932be7da8010a', 'deleteOperations');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7dd7013c', '4028808a193230e3011932be7da8010a', 'deleteUser');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60151', '4028808a193230e3011932be7da8010a', 'editTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60152', '4028808a193230e3011932be7da8010a', 'editTaskAlias');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60153', '4028808a193230e3011932be7da8010a', 'editTaskBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60156', '4028808a193230e3011932be7da8010a', 'editTaskDeadline');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60157', '4028808a193230e3011932be7da8010a', 'editTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60159', '4028808a193230e3011932be7da8010a', 'editTaskHandler');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de6015b', '4028808a193230e3011932be7da8010a', 'editTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de6015f', '4028808a193230e3011932be7da8010a', 'editUserActive');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60160', '4028808a193230e3011932be7da8010a', 'editUserChildren');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60161', '4028808a193230e3011932be7da8010a', 'editUserChildrenPassword');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60162', '4028808a193230e3011932be7da8010a', 'editUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60165', '4028808a193230e3011932be7da8010a', 'editUserDefaultProject');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60166', '4028808a193230e3011932be7da8010a', 'editUserEmail');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7de60167', '4028808a193230e3011932be7da8010a', 'editUserEmailType');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7df60168', '4028808a193230e3011932be7da8010a', 'editUserExpireDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7df6016a', '4028808a193230e3011932be7da8010a', 'editUserHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7df6016b', '4028808a193230e3011932be7da8010a', 'editUserLicensed');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7df6016c', '4028808a193230e3011932be7da8010a', 'editUserLocale');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7df6016f', '4028808a193230e3011932be7da8010a', 'editUserPasswordHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7df60170', '4028808a193230e3011932be7da8010a', 'editUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7df60171', '4028808a193230e3011932be7da8010a', 'editUserStatus');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7df60172', '4028808a193230e3011932be7da8010a', 'editUserTimezone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7e060181', '4028808a193230e3011932be7da8010a', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7e06018a', '4028808a193230e3011932be7da8010a', 'viewTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7e06018b', '4028808a193230e3011932be7da8010a', 'viewTaskCloseDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7e06018f', '4028808a193230e3011932be7da8010a', 'viewTaskLastUpdated');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7e060190', '4028808a193230e3011932be7da8010a', 'viewTaskResolution');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a193230e3011932be7e060191', '4028808a193230e3011932be7da8010a', 'viewTaskSubmitDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934eef56c060c', '4028808a1934933b011934c1e26c020c', 'editUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934eef56c060d', '4028808a1934933b011934c1e26c020c', 'editUserDefaultProject');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934eef56c060e', '4028808a1934933b011934c1e26c020c', 'editUserEmail');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934eef56c060f', '4028808a1934933b011934c1e26c020c', 'editUserEmailType');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934eef56c0610', '4028808a1934933b011934c1e26c020c', 'editUserLocale');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934eef57c0613', '4028808a1934933b011934c1e26c020c', 'editUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934eef57c0614', '4028808a1934933b011934c1e26c020c', 'editUserTimezone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934eef57c0616', '4028808a1934933b011934c1e26c020c', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934eef57c0617', '4028808a1934933b011934c1e26c020c', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f0f870063c', '4028808a1934933b011934c1e26c020c', 'editUserHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f0f870063d', '4028808a1934933b011934c1e26c020c', 'editUserPasswordHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f17977064f', '4028808a1934933b011934c2214a02a4', 'editUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f179770650', '4028808a1934933b011934c2214a02a4', 'editUserDefaultProject');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f179770651', '4028808a1934933b011934c2214a02a4', 'editUserEmail');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f179770652', '4028808a1934933b011934c2214a02a4', 'editUserEmailType');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f179770653', '4028808a1934933b011934c2214a02a4', 'editUserLocale');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f179770656', '4028808a1934933b011934c2214a02a4', 'editUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f179770657', '4028808a1934933b011934c2214a02a4', 'editUserTimezone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f179770659', '4028808a1934933b011934c2214a02a4', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f17987065a', '4028808a1934933b011934c2214a02a4', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f574c106e1', '4028808a1934933b011934c2214a02a4', 'editUserHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f574c106e2', '4028808a1934933b011934c2214a02a4', 'editUserPasswordHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f5c48706f4', '4028808a1934933b011934c25dc6033c', 'editUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f5c48706f5', '4028808a1934933b011934c25dc6033c', 'editUserDefaultProject');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f5c48706f6', '4028808a1934933b011934c25dc6033c', 'editUserEmail');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f5c48706f7', '4028808a1934933b011934c25dc6033c', 'editUserEmailType');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f5c48706f8', '4028808a1934933b011934c25dc6033c', 'editUserLocale');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f5c48706fb', '4028808a1934933b011934c25dc6033c', 'editUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f5c48706fc', '4028808a1934933b011934c25dc6033c', 'editUserTimezone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f5c48706fe', '4028808a1934933b011934c25dc6033c', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f5c48706ff', '4028808a1934933b011934c25dc6033c', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f7cfc80725', '4028808a1934933b011934c25dc6033c', 'editUserHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f7cfc80726', '4028808a1934933b011934c25dc6033c', 'editUserPasswordHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f8200b0738', '4028808a1934933b011934c5ea5803ee', 'editUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f8200b0739', '4028808a1934933b011934c5ea5803ee', 'editUserDefaultProject');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f8200b073a', '4028808a1934933b011934c5ea5803ee', 'editUserEmail');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f8200b073b', '4028808a1934933b011934c5ea5803ee', 'editUserEmailType');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f8200b073c', '4028808a1934933b011934c5ea5803ee', 'editUserLocale');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f8200b073f', '4028808a1934933b011934c5ea5803ee', 'editUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f8200b0740', '4028808a1934933b011934c5ea5803ee', 'editUserTimezone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f8200b0742', '4028808a1934933b011934c5ea5803ee', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f8200b0743', '4028808a1934933b011934c5ea5803ee', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f8a96f076c', '4028808a1934933b011934c5ea5803ee', 'editUserHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934933b011934f8a96f076d', '4028808a1934933b011934c5ea5803ee', 'editUserPasswordHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c012c', '4028808a1934933b011934c5ea5803ee', 'editTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c012d', '4028808a1934933b011934c5ea5803ee', 'editTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c012e', '4028808a1934933b011934c5ea5803ee', 'editTaskHandler');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c0130', '4028808a1934933b011934c5ea5803ee', 'editTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c0132', '4028808a1934933b011934c5ea5803ee', 'viewTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c0133', '4028808a1934933b011934c5ea5803ee', 'viewTaskBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c0134', '4028808a1934933b011934c5ea5803ee', 'viewTaskCloseDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c0135', '4028808a1934933b011934c5ea5803ee', 'viewTaskDeadline');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c0136', '4028808a1934933b011934c5ea5803ee', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c0137', '4028808a1934933b011934c5ea5803ee', 'viewTaskLastUpdated');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c0138', '4028808a1934933b011934c5ea5803ee', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c0139', '4028808a1934933b011934c5ea5803ee', 'viewTaskResolution');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193511d72c013a', '4028808a1934933b011934c5ea5803ee', 'viewTaskSubmitDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312b9015f', '4028808a1934933b011934c25dc6033c', 'editTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312b90160', '4028808a1934933b011934c25dc6033c', 'editTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312b90161', '4028808a1934933b011934c25dc6033c', 'editTaskHandler');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312b90163', '4028808a1934933b011934c25dc6033c', 'editTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312c90165', '4028808a1934933b011934c25dc6033c', 'viewTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312c90166', '4028808a1934933b011934c25dc6033c', 'viewTaskBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312c90167', '4028808a1934933b011934c25dc6033c', 'viewTaskCloseDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312c90168', '4028808a1934933b011934c25dc6033c', 'viewTaskDeadline');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312c90169', '4028808a1934933b011934c25dc6033c', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312c9016a', '4028808a1934933b011934c25dc6033c', 'viewTaskLastUpdated');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312c9016b', '4028808a1934933b011934c25dc6033c', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312c9016c', '4028808a1934933b011934c25dc6033c', 'viewTaskResolution');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351312c9016d', '4028808a1934933b011934c25dc6033c', 'viewTaskSubmitDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c016f', '4028808a1934933b011934c2214a02a4', 'editTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c0170', '4028808a1934933b011934c2214a02a4', 'editTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c0171', '4028808a1934933b011934c2214a02a4', 'editTaskHandler');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c0173', '4028808a1934933b011934c2214a02a4', 'editTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c0175', '4028808a1934933b011934c2214a02a4', 'viewTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c0176', '4028808a1934933b011934c2214a02a4', 'viewTaskBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c0177', '4028808a1934933b011934c2214a02a4', 'viewTaskCloseDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c0178', '4028808a1934933b011934c2214a02a4', 'viewTaskDeadline');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c0179', '4028808a1934933b011934c2214a02a4', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c017a', '4028808a1934933b011934c2214a02a4', 'viewTaskLastUpdated');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c017b', '4028808a1934933b011934c2214a02a4', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c017c', '4028808a1934933b011934c2214a02a4', 'viewTaskResolution');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513753c017d', '4028808a1934933b011934c2214a02a4', 'viewTaskSubmitDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e888017f', '4028808a1934933b011934c1e26c020c', 'editTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e8880180', '4028808a1934933b011934c1e26c020c', 'editTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e8970181', '4028808a1934933b011934c1e26c020c', 'editTaskHandler');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e8970183', '4028808a1934933b011934c1e26c020c', 'editTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e8970185', '4028808a1934933b011934c1e26c020c', 'viewTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e8970186', '4028808a1934933b011934c1e26c020c', 'viewTaskBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e8970187', '4028808a1934933b011934c1e26c020c', 'viewTaskCloseDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e8970188', '4028808a1934933b011934c1e26c020c', 'viewTaskDeadline');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e8970189', '4028808a1934933b011934c1e26c020c', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e897018a', '4028808a1934933b011934c1e26c020c', 'viewTaskLastUpdated');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e897018b', '4028808a1934933b011934c1e26c020c', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e897018c', '4028808a1934933b011934c1e26c020c', 'viewTaskResolution');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc701193513e897018d', '4028808a1934933b011934c1e26c020c', 'viewTaskSubmitDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351b9c7401fa', '4028808a1934933b011934c1e26c020c', 'createNotification');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351b9c93020e', '4028808a1934933b011934c1e26c020c', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351bd2880216', '4028808a1934933b011934c2214a02a4', 'createNotification');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351bd288022a', '4028808a1934933b011934c2214a02a4', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351bf8ec0232', '4028808a1934933b011934c25dc6033c', 'createNotification');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351bf8fc0246', '4028808a1934933b011934c25dc6033c', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351c351a024f', '4028808a1934933b011934c5ea5803ee', 'createNotification');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1934fdc70119351c352a0265', '4028808a1934933b011934c5ea5803ee', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed6582009e', '4028808a1952eada011952ed6563009a', 'createNotification');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed659100b1', '4028808a1952eada011952ed6563009a', 'editTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed659100b2', '4028808a1952eada011952ed6563009a', 'editTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed659100b5', '4028808a1952eada011952ed6563009a', 'editTaskHandler');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed659100b7', '4028808a1952eada011952ed6563009a', 'editTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed659100b9', '4028808a1952eada011952ed6563009a', 'editUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed659100ba', '4028808a1952eada011952ed6563009a', 'editUserDefaultProject');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed659100bb', '4028808a1952eada011952ed6563009a', 'editUserEmail');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed659100bc', '4028808a1952eada011952ed6563009a', 'editUserEmailType');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed659100bf', '4028808a1952eada011952ed6563009a', 'editUserHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed659100c0', '4028808a1952eada011952ed6563009a', 'editUserLocale');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed659100c3', '4028808a1952eada011952ed6563009a', 'editUserPasswordHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100c4', '4028808a1952eada011952ed6563009a', 'editUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100c5', '4028808a1952eada011952ed6563009a', 'editUserTimezone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100c9', '4028808a1952eada011952ed6563009a', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100cd', '4028808a1952eada011952ed6563009a', 'viewTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100ce', '4028808a1952eada011952ed6563009a', 'viewTaskBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100cf', '4028808a1952eada011952ed6563009a', 'viewTaskCloseDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100d0', '4028808a1952eada011952ed6563009a', 'viewTaskDeadline');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100d1', '4028808a1952eada011952ed6563009a', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100d3', '4028808a1952eada011952ed6563009a', 'viewTaskLastUpdated');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100d4', '4028808a1952eada011952ed6563009a', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100d5', '4028808a1952eada011952ed6563009a', 'viewTaskResolution');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100d6', '4028808a1952eada011952ed6563009a', 'viewTaskSubmitDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100da', '4028808a1952eada011952ed6563009a', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028808a1952eada011952ed65a100dc', '4028808a1952eada011952ed6563009a', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3090001', '5', 'manageTaskTemplates');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3380002', '5', 'viewUserAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3380003', '5', 'createUserAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3380004', '5', 'manageUserAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3480005', '5', 'manageEmailSchedules');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3860006', '4028808a193230e3011932be7da8010a', 'manageEmailSchedules');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3860007', '4028808a1934933b011934c1e26c020c', 'manageEmailSchedules');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3860008', '4028808a1934933b011934c2214a02a4', 'manageEmailSchedules');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3860009', '4028808a1934933b011934c25dc6033c', 'manageEmailSchedules');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec386000a', '4028808a1934933b011934c5ea5803ee', 'manageEmailSchedules');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec386000b', '4028808a1952eada011952ed6563009a', 'manageEmailSchedules');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec386000c', '5', 'manageEmailSchedules');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3a6000d', '4028808a193230e3011932be7da8010a', 'manageRegistrations');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3a6000e', '5', 'manageRegistrations');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3d40010', '4028808a193230e3011932be7da8010a', 'manageTaskPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3d40012', '4028808a1934933b011934c1e26c020c', 'manageTaskPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3d40014', '4028808a1934933b011934c2214a02a4', 'manageTaskPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3d40016', '4028808a1934933b011934c25dc6033c', 'manageTaskPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3d40018', '4028808a1934933b011934c5ea5803ee', 'manageTaskPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3d4001a', '4028808a1952eada011952ed6563009a', 'manageTaskPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec3d4001c', '5', 'manageTaskPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec413001e', '4028808a193230e3011932be7da8010a', 'manageUserPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4130020', '4028808a1934933b011934c1e26c020c', 'manageUserPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4130022', '4028808a1934933b011934c2214a02a4', 'manageUserPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4130024', '4028808a1934933b011934c25dc6033c', 'manageUserPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4130026', '4028808a1934933b011934c5ea5803ee', 'manageUserPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4130028', '4028808a1952eada011952ed6563009a', 'manageUserPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec413002a', '5', 'manageUserPublicFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec471002d', '4028808a193230e3011932be7da8010a', 'manageWorkflows');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec471002e', '5', 'manageWorkflows');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4a00031', '4028808a193230e3011932be7da8010a', 'manageCategories');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4a00032', '5', 'manageCategories');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4af0033', '4028808a193230e3011932be7da8010a', 'manageRoles');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4af0034', '5', 'manageRoles');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0035', '4028808a193230e3011932be7da8010a', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0036', '4028808a193230e3011932be7da8010a', 'managePrivateReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0037', '4028808a193230e3011932be7da8010a', 'managePublicReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0038', '4028808a1934933b011934c1e26c020c', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0039', '4028808a1934933b011934c1e26c020c', 'managePrivateReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de003a', '4028808a1934933b011934c1e26c020c', 'managePublicReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de003b', '4028808a1934933b011934c2214a02a4', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de003c', '4028808a1934933b011934c2214a02a4', 'managePrivateReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de003d', '4028808a1934933b011934c2214a02a4', 'managePublicReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de003e', '4028808a1934933b011934c25dc6033c', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de003f', '4028808a1934933b011934c25dc6033c', 'managePrivateReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0040', '4028808a1934933b011934c25dc6033c', 'managePublicReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0041', '4028808a1934933b011934c5ea5803ee', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0042', '4028808a1934933b011934c5ea5803ee', 'managePrivateReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0043', '4028808a1934933b011934c5ea5803ee', 'managePublicReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0044', '4028808a1952eada011952ed6563009a', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0045', '4028808a1952eada011952ed6563009a', 'managePrivateReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0046', '4028808a1952eada011952ed6563009a', 'managePublicReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0047', '5', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0048', '5', 'managePrivateReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec4de0049', '5', 'managePublicReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec50d004a', '4028808a193230e3011932be7da8010a', 'manageEmailImportRules');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec50d004b', '5', 'manageEmailImportRules');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec52c004e', '4028808a193230e3011932be7da8010a', 'manageTaskUDFs');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec52c004f', '5', 'manageTaskUDFs');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec53c0052', '4028808a193230e3011932be7da8010a', 'manageUserUDFs');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec53c0053', '5', 'manageUserUDFs');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec55b0054', '4028808a193230e3011932be7da8010a', 'manageTaskACLs');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec55b0055', '4028808a1934933b011934c5ea5803ee', 'manageTaskACLs');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec55b0056', '5', 'manageTaskACLs');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0057', '4028808a193230e3011932be7da8010a', 'manageUserACLs');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec56b0058', '5', 'manageUserACLs');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a0059', '4028808a193230e3011932be7da8010a', 'manageTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a005a', '4028808a193230e3011932be7da8010a', 'viewTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a005b', '4028808a193230e3011932be7da8010a', 'createTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a005c', '5', 'manageTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a005d', '5', 'viewTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a005e', '5', 'createTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a005f', '4028808a1934933b011934c1e26c020c', 'viewTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a0060', '4028808a1934933b011934c1e26c020c', 'createTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a0061', '4028808a1934933b011934c2214a02a4', 'viewTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a0062', '4028808a1934933b011934c2214a02a4', 'createTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a0063', '4028808a1934933b011934c25dc6033c', 'viewTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a0064', '4028808a1934933b011934c25dc6033c', 'createTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a0065', '4028808a1934933b011934c5ea5803ee', 'viewTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a0066', '4028808a1934933b011934c5ea5803ee', 'createTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a0067', '4028808a1952eada011952ed6563009a', 'viewTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec59a0068', '4028808a1952eada011952ed6563009a', 'createTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec5a90069', '5', 'bulkProcessingTask');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec5e8006a', '4028808a193230e3011932be7da8010a', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec5e8006b', '4028808a1934933b011934c1e26c020c', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec5e8006c', '4028808a1934933b011934c2214a02a4', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec5e8006d', '4028808a1934933b011934c25dc6033c', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec5e8006e', '4028808a1934933b011934c5ea5803ee', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec5e8006f', '4028808a1952eada011952ed6563009a', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec5e80070', '5', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec5f70071', '4028808a193230e3011932be7da8010a', 'viewTaskDeadline');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec5f70072', '5', 'viewTaskDeadline');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6070073', '4028808a193230e3011932be7da8010a', 'viewTaskBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6070074', '5', 'viewTaskBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6170075', '4028808a193230e3011932be7da8010a', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6170076', '4028808a1934933b011934c1e26c020c', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6170077', '4028808a1934933b011934c2214a02a4', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6170078', '4028808a1934933b011934c25dc6033c', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6170079', '4028808a1934933b011934c5ea5803ee', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec617007a', '4028808a1952eada011952ed6563009a', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec617007b', '5', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec626007c', '4028808a193230e3011932be7da8010a', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec626007d', '4028808a1934933b011934c1e26c020c', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec626007e', '4028808a1934933b011934c2214a02a4', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec626007f', '4028808a1934933b011934c25dc6033c', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6260080', '4028808a1934933b011934c5ea5803ee', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6260081', '4028808a1952eada011952ed6563009a', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6260082', '5', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6360083', '4028808a193230e3011932be7da8010a', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6360084', '4028808a1934933b011934c1e26c020c', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6360085', '4028808a1934933b011934c2214a02a4', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6360086', '4028808a1934933b011934c25dc6033c', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6360087', '4028808a1934933b011934c5ea5803ee', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6360088', '4028808a1952eada011952ed6563009a', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec6360089', '5', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec89702c8', '4028808a193230e3011932be7da8010a', 'viewSCMReferences');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a004ebc011a004ec89702c9', '5', 'viewSCMReferences');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a006148011a00614e1b0001', '5', 'viewSCMBrowser');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('402881861a006148011a00614e1b0002', '5', 'viewSCMReferences');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028930007eb62990107eb62a7bf0075', '5', 'cutPasteUser');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('4028930007eb62990107eb62a7d30078', '5', 'cutCopyPasteTask');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6cc30002', '5', 'editUserActive');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6cc30003', '5', 'editUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6cc30004', '5', 'editUserDefaultProject');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6cc30005', '5', 'editUserEmail');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6cc30006', '5', 'editUserExpireDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6cc30008', '5', 'editUserLocale');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6cc30009', '5', 'editUserTimezone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6cc3000a', '5', 'editUserEmailType');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6cc3000b', '5', 'editUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6cd7000d', '5', 'editUserStatus');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6d780042', '5', 'editUserChildren');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6d780043', '5', 'editUserChildrenPassword');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df0004c', '5', 'viewTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df0004d', '5', 'editTaskAlias');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df0004e', '5', 'editTaskBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df0004f', '5', 'viewTaskCloseDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df00050', '5', 'editTaskDeadline');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df00051', '5', 'editTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df00052', '5', 'editTaskHandler');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df00053', '5', 'viewTaskLastUpdated');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df00055', '5', 'editTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df00056', '5', 'viewTaskResolution');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df00057', '5', 'viewTaskSubmitDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a6df0094c', '5', 'editTaskActualBudget');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('40289689fc796a4f00fc796a70b700f6', '5', 'editUserLicensed');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec3d4000f', '4028808a193230e3011932be7da8010a', 'manageTaskPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec3d40011', '4028808a1934933b011934c1e26c020c', 'manageTaskPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec3d40013', '4028808a1934933b011934c2214a02a4', 'manageTaskPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec3d40015', '4028808a1934933b011934c25dc6033c', 'manageTaskPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec3d40017', '4028808a1934933b011934c5ea5803ee', 'manageTaskPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec3d40019', '4028808a1952eada011952ed6563009a', 'manageTaskPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec3d4001b', '5', 'manageTaskPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec413001d', '4028808a193230e3011932be7da8010a', 'manageUserPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec413001f', '4028808a1934933b011934c1e26c020c', 'manageUserPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec4130021', '4028808a1934933b011934c2214a02a4', 'manageUserPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec4130023', '4028808a1934933b011934c25dc6033c', 'manageUserPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec4130025', '4028808a1934933b011934c5ea5803ee', 'manageUserPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec4130027', '4028808a1952eada011952ed6563009a', 'manageUserPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('502881861a004ebc011a004ec4130029', '5', 'manageUserPrivateFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec3d4000f', '4028808a193230e3011932be7da8010a', 'viewFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec3d40011', '4028808a1934933b011934c1e26c020c', 'viewFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec3d40013', '4028808a1934933b011934c2214a02a4', 'viewFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec3d40015', '4028808a1934933b011934c25dc6033c', 'viewFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec3d40017', '4028808a1934933b011934c5ea5803ee', 'viewFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec3d40019', '4028808a1952eada011952ed6563009a', 'viewFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec3d4001b', '5', 'viewFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec413001d', '4028808a193230e3011932be7da8010a', 'viewUserFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec413001f', '4028808a1934933b011934c1e26c020c', 'viewUserFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec4130021', '4028808a1934933b011934c2214a02a4', 'viewUserFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec4130023', '4028808a1934933b011934c25dc6033c', 'viewUserFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec4130025', '4028808a1934933b011934c5ea5803ee', 'viewUserFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec4130027', '4028808a1952eada011952ed6563009a', 'viewUserFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('602881861a004ebc011a004ec4130029', '5', 'viewUserFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('B1865C5BAF948874E0303BD58EAC7288', '5', 'createUser');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('B1865C5BAF958874E0303BD58EAC7288', '5', 'editUserHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('B1865C5BAF968874E0303BD58EAC7288', '5', 'deleteUser');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('B1865C5BAF998874E0303BD58EAC7288', '5', 'editUserPasswordHimself');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('B1865C5BAFAB8874E0303BD58EAC7288', '5', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('B1865C5BAFB28874E0303BD58EAC7288', '5', 'deleteOperations');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e09e0022', 'ff8080812c34f3f1012c34f7df880002', 'viewUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e09f0023', 'ff8080812c34f3f1012c34f7df880002', 'viewUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e09f0025', 'ff8080812c34f3f1012c34f7df880002', 'viewSCMBrowser');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e0a00029', 'ff8080812c34f3f1012c34f7df880002', 'editUserActive');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e0a1002a', 'ff8080812c34f3f1012c34f7df880002', 'editUserCompany');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e0a1002b', 'ff8080812c34f3f1012c34f7df880002', 'editUserDefaultProject');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e0a1002c', 'ff8080812c34f3f1012c34f7df880002', 'editUserEmail');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e0a1002d', 'ff8080812c34f3f1012c34f7df880002', 'editUserExpireDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e0a1002e', 'ff8080812c34f3f1012c34f7df880002', 'editUserLocale');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e0a2002f', 'ff8080812c34f3f1012c34f7df880002', 'editUserTimezone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e0a30030', 'ff8080812c34f3f1012c34f7df880002', 'editUserEmailType');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e0a30031', 'ff8080812c34f3f1012c34f7df880002', 'editUserPhone');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e0a40032', 'ff8080812c34f3f1012c34f7df880002', 'editUserStatus');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34f7e0a90041', 'ff8080812c34f3f1012c34f7df880002', 'editUserLicensed');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fa413400cf', 'ff8080812c34f3f1012c34f7df880002', 'viewUserFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fa413500d0', 'ff8080812c34f3f1012c34f7df880002', 'viewUserAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34faca4100d1', 'ff8080812c34f3f1012c34f7df880002', 'viewTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34faca4100d2', 'ff8080812c34f3f1012c34f7df880002', 'createTaskAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34faca4100d3', 'ff8080812c34f3f1012c34f7df880002', 'createTaskMessageAttachments');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34faca4100d4', 'ff8080812c34f3f1012c34f7df880002', 'viewFilters');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34faca4100d5', 'ff8080812c34f3f1012c34f7df880002', 'viewReports');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fb4f3a00e4', 'ff8080812c34f3f1012c34f7df880002', 'editTaskAlias');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fb4f3a00e5', 'ff8080812c34f3f1012c34f7df880002', 'viewTaskResolution');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fb4f3a00e6', 'ff8080812c34f3f1012c34f7df880002', 'viewTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fb4f3a00e7', 'ff8080812c34f3f1012c34f7df880002', 'editTaskPriority');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fb4f3b00e8', 'ff8080812c34f3f1012c34f7df880002', 'editTaskHandler');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fb4f3b00e9', 'ff8080812c34f3f1012c34f7df880002', 'viewTaskSubmitDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fb4f3b00ea', 'ff8080812c34f3f1012c34f7df880002', 'viewTaskLastUpdated');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fb4f3b00eb', 'ff8080812c34f3f1012c34f7df880002', 'viewTaskCloseDate');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fb4f3b00ec', 'ff8080812c34f3f1012c34f7df880002', 'viewTaskDescription');

insert into gr_rolestatus (rolestatus_id, rolestatus_prstatus, rolestatus_role) values ('ff8080812c34f3f1012c34fb4f3c00ed', 'ff8080812c34f3f1012c34f7df880002', 'editTaskDescription');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('2', 'New', 1, 0, '1', '#FFFFFF');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a193230e301193271d0c00059', 'In process', 0, 0, '1', '#0000ff');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a193230e301193272f9bf005a', 'Resolved', 0, 1, '1', '#00ff00');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a193230e3011932e16ed901b9', 'New', 1, 0, '4028808a192e43e801192e527cee013f', '#ff0000');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1934592501193487403b0050', 'Analysis', 0, 0, '4028808a192e43e801192e527cee013f', '#ff06a4');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a19345925011934875f5b0051', 'Technical specification', 0, 0, '4028808a192e43e801192e527cee013f', '#aa00aa');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a193459250119348782e10052', 'Approved', 0, 0, '4028808a192e43e801192e527cee013f', '#00ff00');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a19345925011934879d9d0053', 'Development', 0, 0, '4028808a192e43e801192e527cee013f', '#0000ff');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1934592501193487b5d80054', 'Testing', 0, 0, '4028808a192e43e801192e527cee013f', '#00ddff');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1934592501193487d2680055', 'Accepted', 0, 0, '4028808a192e43e801192e527cee013f', '#ffff00');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a19345925011934885fc40056', 'Closed', 0, 1, '4028808a192e43e801192e527cee013f', '#c7c7c7');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a19345925011934887ffe0057', 'Paused', 0, 0, '4028808a192e43e801192e527cee013f', '#d79000');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1934592501193488ab250058', 'Declined', 0, 1, '4028808a192e43e801192e527cee013f', '#666666');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a194731fd01194774999f000a', 'New', 1, 0, '4028808a194731fd0119476e1efe0003', '#ff0000');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a194731fd011947757181000b', 'In process', 0, 0, '4028808a194731fd0119476e1efe0003', '#0000ff');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a194731fd01194775e00a000c', 'Resolved', 0, 0, '4028808a194731fd0119476e1efe0003', '#00ff00');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a194731fd0119477770db000d', 'Closed', 0, 1, '4028808a194731fd0119476e1efe0003', '#c7c7c7');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1947f52201194810bacd0068', 'Paused', 0, 0, '4028808a194731fd0119476e1efe0003', '#cc00ff');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1947f5220119488766120108', 'New', 1, 0, '4028808a194731fd011947ab7301002f', '#ff0000');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1947f522011948883cac0109', 'In process', 0, 0, '4028808a194731fd011947ab7301002f', '#0000ff');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1947f522011948892ca9010a', 'Ready', 0, 0, '4028808a194731fd011947ab7301002f', '#00ff00');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1947f52201194889f529010b', 'Outdated', 0, 1, '4028808a194731fd011947ab7301002f', '#c7c7c7');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1947f5220119492e774c03cb', 'New', 1, 0, '4028808a1947f5220119492e773d03c7', '#ff0000');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1947f5220119492e774c03cc', 'In process', 0, 0, '4028808a1947f5220119492e773d03c7', '#0000ff');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1947f5220119492e774c03cd', 'Ready', 0, 0, '4028808a1947f5220119492e773d03c7', '#00ff00');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1947f52201194932895a03e7', 'Outdated', 0, 1, '4028808a1947f5220119492e773d03c7', '#c7c7c7');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1951e21b011952b5250e028d', 'New', 1, 0, '4028808a1951e21b011952b524ff0286', '#ff0000');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1951e21b011952b5250e028e', 'In process', 0, 0, '4028808a1951e21b011952b524ff0286', '#0000ff');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1951e21b011952b5250e028f', 'Resolved', 0, 0, '4028808a1951e21b011952b524ff0286', '#00ff00');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1951e21b011952b5250e0290', 'Closed', 0, 1, '4028808a1951e21b011952b524ff0286', '#c7c7c7');

insert into gr_status (status_id, status_name, status_isstart, status_isfinish, status_workflow, status_color) values ('4028808a1951e21b011952b5250e0291', 'Paused', 0, 0, '4028808a1951e21b011952b524ff0286', '#cc00ff');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('1', '', 'Projects', to_timestamp('2004-05-12 17:30:53', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:37:02', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '1', '2', NULL, '2', '1', '1', NULL, NULL, '1');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a192e43e801192e4dc70f013a', '', 'My project', to_timestamp('2008-04-08 17:50:59', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:38:44', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'The project of working out of the program Library', 0, 0, NULL, '1', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '1', NULL, '26');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a19512fa5011951d9cbdb0070', '', 'Errors / bugs', to_timestamp('2008-04-15 15:30:38', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:49:05', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'We write here<br />', 0, 0, NULL, '4028808a19512fa5011951659a9e002a', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d0ed0e039e', NULL, '95');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b01195229b82f0002', '', 'Requirements', to_timestamp('2008-04-15 16:57:56', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:42:03', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '4028808a19512fa50119515f006f0008', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d0ed0e039e', NULL, '96');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b0119522a19990003', '', 'Test documentation', to_timestamp('2008-04-15 16:58:20', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-27 00:21:38', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '4028808a19512fa5011951687d82004b', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d0ed0e039e', NULL, '97');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b0119526efcca0160', '', 'Business requirement', to_timestamp('2008-04-15 18:13:35', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:45:07', 'YYYY-MM-DD HH24:MI:SS'), NULL, '&nbsp;', 0, 0, NULL, '4028808a19512fa50119515f006f0008', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b01195229b82f0002', NULL, '101');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b0119526f2be90161', '', 'Nonfunctional requirements', to_timestamp('2008-04-15 18:13:47', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:44:05', 'YYYY-MM-DD HH24:MI:SS'), NULL, '&nbsp;', 0, 0, NULL, '4028808a19512fa50119515f006f0008', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b01195229b82f0002', NULL, '102');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b0119526f56e10162', '', 'User requirements', to_timestamp('2008-04-15 18:13:58', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:43:13', 'YYYY-MM-DD HH24:MI:SS'), NULL, '&nbsp;', 0, 0, NULL, '4028808a19512fa50119515f006f0008', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b01195229b82f0002', NULL, '103');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b0119526f85440163', '', 'Functional requirements', to_timestamp('2008-04-15 18:14:10', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:41:40', 'YYYY-MM-DD HH24:MI:SS'), NULL, '&nbsp;', 0, 0, NULL, '4028808a19512fa50119515f006f0008', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b01195229b82f0002', NULL, '104');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b011952bf5b500368', '', 'Completions (It is necessary to make)', to_timestamp('2008-04-15 19:41:22', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-27 00:19:21', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '4028808a1951e21b011952b4384d024d', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d0ed0e039e', NULL, '107');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b011952d0ed0e039e', '', 'Version 1.0', to_timestamp('2008-04-15 20:00:34', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:38:59', 'YYYY-MM-DD HH24:MI:SS'), NULL, '&nbsp;', 0, 0, NULL, '4028808a1951e21b011952cefb8f036a', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a192e43e801192e4dc70f013a', NULL, '108');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b011952d15e75039f', '', 'Version 1.1', to_timestamp('2008-04-15 20:01:03', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:39:18', 'YYYY-MM-DD HH24:MI:SS'), NULL, '&nbsp;', 0, 0, NULL, '4028808a1951e21b011952cefb8f036a', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a192e43e801192e4dc70f013a', NULL, '109');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b011952d1e6ef03a0', '', 'Errors / bugs', to_timestamp('2008-04-15 20:01:38', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:49:18', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '4028808a19512fa5011951659a9e002a', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d15e75039f', NULL, '110');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b011952d1e7d903a1', '', 'Requirements', to_timestamp('2008-04-15 20:01:38', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:40:24', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '4028808a19512fa50119515f006f0008', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d15e75039f', NULL, '111');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b011952d1e8f203a2', '', 'Business requirement', to_timestamp('2008-04-15 20:01:38', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:44:58', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '4028808a19512fa50119515f006f0008', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d1e7d903a1', NULL, '112');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b011952d1ea2b03a3', '', 'Nonfunctional requirements', to_timestamp('2008-04-15 20:01:38', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:43:51', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '4028808a19512fa50119515f006f0008', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d1e7d903a1', NULL, '113');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b011952d1eb3503a4', '', 'User requirements', to_timestamp('2008-04-15 20:01:39', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:42:50', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '4028808a19512fa50119515f006f0008', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d1e7d903a1', NULL, '114');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b011952d1ec4e03a5', '', 'Functional requirements', to_timestamp('2008-04-15 20:01:39', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:41:23', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '4028808a19512fa50119515f006f0008', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d1e7d903a1', NULL, '115');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b011952d1ed5703a6', '', 'Test documentation', to_timestamp('2008-04-15 20:01:39', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-27 00:21:55', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '4028808a19512fa5011951687d82004b', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d15e75039f', NULL, '116');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1951e21b011952d1ee9003a7', '', 'Completions (It is necessary to make)', to_timestamp('2008-04-15 20:01:39', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-27 00:19:46', 'YYYY-MM-DD HH24:MI:SS'), NULL, '', 0, 0, NULL, '4028808a1951e21b011952b4384d024d', '2', NULL, '2', '1', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b011952d15e75039f', NULL, '117');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1953022d0119531abec300c6', '', 'Identification of the users', to_timestamp('2008-04-15 21:21:11', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:47:58', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'Analyse system and write the thoughts<br />', 0, 0, NULL, '4028808a1951e21b0119524644b800e0', '4028808a193230e3011932e16ed901b9', NULL, '4028808a192e43e801192e54c17f0141', '4028808a192e43e801192e48f4fd0002', '4028808a1934933b011934c8266d04a8', '4028808a1951e21b0119526f56e10162', NULL, '119');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1953022d01195344252600c9', '', 'List of new registrations', to_timestamp('2008-04-15 22:06:25', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-27 00:20:42', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'It is necessary to think over as better to make, make for this purpose separate task and there on the writer translate <br />', 0, 0, NULL, '4028808a1951e21b011952bddb38032c', '4028808a1951e21b011952b5250e028d', NULL, '4028808a1951e21b011952b524ff0288', '4028808a192e43e801192e48f4fd0002', '4028808a1934933b011934c8266d04a8', '4028808a1951e21b011952bf5b500368', NULL, '121');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1953022d01195346e62a00cb', '', 'List of new registrations', to_timestamp('2008-04-15 22:09:25', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-26 23:47:09', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'In 3 columns on 20 pieces + page by page. Describe please a language of science))', 0, 0, NULL, '4028808a1951e21b0119524644b800e0', '4028808a193459250119348782e10052', NULL, '4028808a192e43e801192e572ee60143', '4028808a1934933b011934c2e27703d4', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b0119526f85440163', NULL, '122');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1953022d0119534c205d00ce', '', 'List of new registrations', to_timestamp('2008-04-15 22:15:08', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2009-04-27 00:25:56', 'YYYY-MM-DD HH24:MI:SS'), NULL, '<strong>PRELIMINARY REQUIREMENTS:</strong><br /><br />The page should be handed over.<br /><strong><br />PLAN OF ACTIONS:</strong><br /><br />Open page with the list.<br /><br /><strong>THE CHECK PLAN:</strong><br /><br />Should see: <br />-list in 3 columns<br />-20 users in a column<br />-slajder in the bottom of page<br />', 0, 0, NULL, '4028808a1951e21b01195243b62d00a1', '4028808a1947f522011948892ca9010a', NULL, '4028808a1947f5220119486b728e0105', '4028808a1934933b011934c2e27703d4', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b0119522a19990003', NULL, '123');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('ff8080812b58a7f4012b58adcf150066', NULL, 'File Storage', to_timestamp('2010-09-28 18:10:07', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2010-09-28 18:10:07', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL, 0, 0, NULL, 'ff8080812b58a7f4012b58ad7cb5003f', '2', NULL, '2', '1', 'nullusersource', '4028808a1951e21b011952d15e75039f', NULL, '127');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('ff8080812b58a7f4012b58adf2b3007d', NULL, 'File Storage', to_timestamp('2010-09-28 18:10:16', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2010-09-28 18:10:16', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL, 0, 0, NULL, 'ff8080812b58a7f4012b58ad7cb5003f', '2', NULL, '2', '1', 'nullusersource', '4028808a1951e21b011952d0ed0e039e', NULL, '128');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1953022d0119537bdcc2032e', '', 'Incorrect login', to_timestamp('2008-04-15 23:07:16', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2010-11-30 17:53:08', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'Incorrect login after registration. Add new text<br />', 0, 0, NULL, '4028808a1951e21b01195245ff4200c1', '4028808a194731fd01194775e00a000c', NULL, '4028808a194731fd0119476ef0860006', '4028808a1934933b011934ca3b3404af', '1', '4028808a19512fa5011951d9cbdb0070', NULL, '124');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('ff8080812bd3d88d012bd3e2f5320076', NULL, 'Dashboard', to_timestamp('2010-10-22 16:21:27', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2010-10-22 17:09:04', 'YYYY-MM-DD HH24:MI:SS'), NULL, '<div style="color: #000000; font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 11px; background-image: initial; background-attachment: initial; background-origin: initial; background-clip: initial; background-color: #ffffff; margin: 8px;">
<p><strong><span style="font-size: small;">My tasks:</span></strong></p>
<p>Current tasks for ##26</p>
<p>#26{filter:All tasks}</p>
<p>Open tasks (deep search) for ##26</p>
<p>#26{filter:Open tasks (deep search)}</p>
<p>My tasks (deep search) for ##26</p>
<p>#26{filter:My tasks (deep search)}</p>
<p>&nbsp;</p>
<p><strong><span style="font-size: small;">Other links:</span></strong></p>
<p><strong><span style="font-size: x-small;">Version 1.0</span></strong></p>
<p>Bugs for version 1.0 #95</p>
<p>Test documentation&nbsp;for&nbsp;version 1.0 #97</p>
<p>File Storage&nbsp;for&nbsp;version 1.0 #128</p>
<p><strong><span style="font-size: x-small;">Version 1.1</span></strong></p>
<p><strong><span style="font-size: x-small;"><span style="font-weight: normal; font-size: 11px;">Bugs&nbsp;for&nbsp;version 1.1 #110</span></span></strong></p>
<div>Test documentation&nbsp;for&nbsp;version 1.1 #116</div>
<div><br /></div>
<div>File Storage&nbsp;for&nbsp;version 1.1 #127</div>
<div><span>Broken requirement :&nbsp;</span>#124{udf:Broken requirement}</div>
</div>', 0, 0, NULL, 'ff8080812bd3d88d012bd3e22ff90051', '4028808a193230e3011932e16ed901b9', NULL, '4028808a193230e3011932ed095e01bf', '1', 'nullusersource', '4028808a192e43e801192e4dc70f013a', NULL, '131');

insert into gr_task (task_id, task_shortname, task_name, task_submitdate, task_updatedate, task_closedate, task_description, task_abudget, task_budget, task_deadline, task_category, task_status, task_resolution, task_priority, task_submitter, task_handler, task_parent, task_longtext, task_number) values ('4028808a1953022d0119531c0e5500c8', '', 'Check of page a login', to_timestamp('2008-04-15 21:22:37', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2010-12-02 18:27:15', 'YYYY-MM-DD HH24:MI:SS'), NULL, '<strong>PRELIMINARY REQUIREMENTS:</strong><br /><br />All tasks for designers and developers on work on page the Login should be finished. <br /><br /><strong>PLAN OF ACTION:</strong><br /><br />Open page the Login. <br /><br /><strong>THE CHECK PLAN:</strong><br /><br />- A window the Login openly<br />- The window name - the Login<br />- The company logo is displayed in the right top corner <br />- On the form of 2 fields - the Name and the Password<br />- The button the Login is accessible<br />- The link has forgotten the password - is accessible
<div>- And add link for anonumoysly user</div>', 0, 0, NULL, '4028808a1951e21b01195243b62d00a1', '4028808a1947f5220119488766120108', NULL, '4028808a1947f5220119486b728e0105', '4028808a192e43e801192e48f4fd0002', '4028808a192e43e801192e4f4ad1013b', '4028808a1951e21b0119522a19990003', NULL, '120');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a193230e301193276558d005f', '2', '4028808a193230e301193271d0c00059', '4028808a193230e3011932762dd1005b');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a193230e301193276f6af0061', '4028808a193230e301193272f9bf005a', '4028808a193230e301193271d0c00059', '4028808a193230e3011932762dd1005b');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a193230e30119327b2f110066', '4028808a193230e301193271d0c00059', '4028808a193230e301193272f9bf005a', '4028808a193230e301193279870e0062');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a193230e30119327b3f8b0067', '2', '4028808a193230e301193272f9bf005a', '4028808a193230e301193279870e0062');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a193230e3011932ba89f00106', '2', '2', '4028808a193230e3011932b99a310102');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934989a6e0009', '4028808a1934592501193487403b0050', '4028808a1934592501193487403b0050', '4028808a1934933b011934987cf30002');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b01193498a971000a', '4028808a193230e3011932e16ed901b9', '4028808a193230e3011932e16ed901b9', '4028808a1934933b011934987cf30002');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b01193498b885000b', '4028808a1934592501193488ab250058', '4028808a1934592501193488ab250058', '4028808a1934933b011934987cf30002');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b01193498ca09000c', '4028808a19345925011934887ffe0057', '4028808a19345925011934887ffe0057', '4028808a1934933b011934987cf30002');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b01193498dca7000d', '4028808a1934592501193487d2680055', '4028808a1934592501193487d2680055', '4028808a1934933b011934987cf30002');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b01193498ee79000e', '4028808a19345925011934879d9d0053', '4028808a19345925011934879d9d0053', '4028808a1934933b011934987cf30002');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b01193498fc25000f', '4028808a19345925011934885fc40056', '4028808a19345925011934885fc40056', '4028808a1934933b011934987cf30002');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934990e070010', '4028808a1934592501193487b5d80054', '4028808a1934592501193487b5d80054', '4028808a1934933b011934987cf30002');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b0119349921030011', '4028808a19345925011934875f5b0051', '4028808a19345925011934875f5b0051', '4028808a1934933b011934987cf30002');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934992e030012', '4028808a193459250119348782e10052', '4028808a193459250119348782e10052', '4028808a1934933b011934987cf30002');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b0119349f0734001a', '4028808a193230e3011932e16ed901b9', '4028808a1934592501193487403b0050', '4028808a1934933b0119349ef13b0013');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b0119349f18c8001b', '4028808a19345925011934887ffe0057', '4028808a1934592501193487403b0050', '4028808a1934933b0119349ef13b0013');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b0119349f2f7d001c', '4028808a19345925011934875f5b0051', '4028808a1934592501193487403b0050', '4028808a1934933b0119349ef13b0013');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b0119349f9d89001e', '4028808a19345925011934879d9d0053', '4028808a1934592501193487403b0050', '4028808a1934933b0119349ef13b0013');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934a059a50026', '4028808a1934592501193487403b0050', '4028808a19345925011934875f5b0051', '4028808a1934933b011934a02854001f');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934a08ad70027', '4028808a19345925011934879d9d0053', '4028808a19345925011934875f5b0051', '4028808a1934933b011934a02854001f');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934a1d3e10028', '4028808a19345925011934887ffe0057', '4028808a19345925011934875f5b0051', '4028808a1934933b011934a02854001f');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934a262f20030', '4028808a19345925011934875f5b0051', '4028808a193459250119348782e10052', '4028808a1934933b011934a229e10029');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934d8cf03052b', '4028808a193459250119348782e10052', '4028808a19345925011934879d9d0053', '4028808a1934933b011934d8a5440518');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934dae011053f', '4028808a19345925011934879d9d0053', '4028808a1934592501193487b5d80054', '4028808a1934933b011934d96884052c');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934db8c3f0553', '4028808a1934592501193487b5d80054', '4028808a1934592501193487d2680055', '4028808a1934933b011934db3e4e0540');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934dc236e0567', '4028808a1934592501193487d2680055', '4028808a19345925011934885fc40056', '4028808a1934933b011934dbdacc0554');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e115f8058e', '4028808a193230e3011932e16ed901b9', '4028808a19345925011934875f5b0051', '4028808a1934933b011934a02854001f');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e199a0058f', '4028808a193230e3011932e16ed901b9', '4028808a193459250119348782e10052', '4028808a1934933b011934a229e10029');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e1b6bd0590', '4028808a19345925011934887ffe0057', '4028808a193459250119348782e10052', '4028808a1934933b011934a229e10029');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e235240591', '4028808a1934592501193487b5d80054', '4028808a19345925011934879d9d0053', '4028808a1934933b011934d8a5440518');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e244b40592', '4028808a19345925011934887ffe0057', '4028808a19345925011934879d9d0053', '4028808a1934933b011934d8a5440518');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e319c70593', '4028808a1934592501193487d2680055', '4028808a1934592501193487b5d80054', '4028808a1934933b011934d96884052c');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e36d460594', '4028808a19345925011934887ffe0057', '4028808a1934592501193487b5d80054', '4028808a1934933b011934d96884052c');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e3d98d0595', '4028808a19345925011934879d9d0053', '4028808a1934592501193487d2680055', '4028808a1934933b011934db3e4e0540');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e3fdee0596', '4028808a19345925011934887ffe0057', '4028808a1934592501193487d2680055', '4028808a1934933b011934db3e4e0540');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e4b18e0597', '4028808a193459250119348782e10052', '4028808a19345925011934885fc40056', '4028808a1934933b011934dbdacc0554');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e51b640599', '4028808a19345925011934887ffe0057', '4028808a19345925011934885fc40056', '4028808a1934933b011934dbdacc0554');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e54c57059a', '4028808a1934592501193487403b0050', '4028808a19345925011934887ffe0057', '4028808a1934933b011934dda4420568');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e55a90059b', '4028808a193230e3011932e16ed901b9', '4028808a19345925011934887ffe0057', '4028808a1934933b011934dda4420568');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e58578059d', '4028808a19345925011934879d9d0053', '4028808a19345925011934887ffe0057', '4028808a1934933b011934dda4420568');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e5a545059e', '4028808a1934592501193487b5d80054', '4028808a19345925011934887ffe0057', '4028808a1934933b011934dda4420568');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e5b523059f', '4028808a19345925011934875f5b0051', '4028808a19345925011934887ffe0057', '4028808a1934933b011934dda4420568');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e5cd7d05a0', '4028808a193459250119348782e10052', '4028808a19345925011934887ffe0057', '4028808a1934933b011934dda4420568');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e66a6905a3', '4028808a193230e3011932e16ed901b9', '4028808a1934592501193488ab250058', '4028808a1934933b011934de39bb057b');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1934933b011934e6a44605a4', '4028808a193459250119348782e10052', '4028808a1934592501193488ab250058', '4028808a1934933b011934de39bb057b');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119480aaa010050', '4028808a194731fd011947757181000b', '4028808a194731fd011947757181000b', '4028808a1947f5220119480a35cb003d');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119480abed10051', '4028808a194731fd0119477770db000d', '4028808a194731fd0119477770db000d', '4028808a1947f5220119480a35cb003d');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119480ad4da0052', '4028808a194731fd01194775e00a000c', '4028808a194731fd01194775e00a000c', '4028808a1947f5220119480a35cb003d');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119480ae8bf0053', '4028808a194731fd01194774999f000a', '4028808a194731fd01194774999f000a', '4028808a1947f5220119480a35cb003d');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119481006bf0067', '4028808a194731fd01194774999f000a', '4028808a194731fd011947757181000b', '4028808a1947f5220119480c50000054');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f52201194811695c007c', '4028808a194731fd011947757181000b', '4028808a1947f52201194810bacd0068', '4028808a1947f5220119481110ad0069');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f52201194811c1cd007e', '4028808a194731fd01194774999f000a', '4028808a1947f52201194810bacd0068', '4028808a1947f5220119481110ad0069');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f522011948126a9f0092', '4028808a194731fd011947757181000b', '4028808a194731fd01194775e00a000c', '4028808a1947f522011948124d73007f');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f52201194814716c00a9', '4028808a194731fd01194774999f000a', '4028808a194731fd0119477770db000d', '4028808a1947f52201194813946a0093');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f522011948148a6300aa', '4028808a194731fd01194775e00a000c', '4028808a194731fd0119477770db000d', '4028808a1947f52201194813946a0093');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f52201194814a99300ab', '4028808a1947f52201194810bacd0068', '4028808a194731fd0119477770db000d', '4028808a1947f52201194813946a0093');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119481598f400ac', '4028808a1947f52201194810bacd0068', '4028808a1947f52201194810bacd0068', '4028808a1947f5220119480a35cb003d');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119488c26bf011f', '4028808a1947f522011948883cac0109', '4028808a1947f522011948883cac0109', '4028808a1947f5220119488c003c010c');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119488c3d350120', '4028808a1947f522011948892ca9010a', '4028808a1947f522011948892ca9010a', '4028808a1947f5220119488c003c010c');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119488c4dcf0121', '4028808a1947f5220119488766120108', '4028808a1947f5220119488766120108', '4028808a1947f5220119488c003c010c');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119488c5bc90122', '4028808a1947f52201194889f529010b', '4028808a1947f52201194889f529010b', '4028808a1947f5220119488c003c010c');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119489838390136', '4028808a1947f5220119488766120108', '4028808a1947f522011948883cac0109', '4028808a1947f5220119489805a00123');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f52201194899fc30014c', '4028808a1947f522011948883cac0109', '4028808a1947f522011948892ca9010a', '4028808a1947f52201194899df520139');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119489a85f20161', '4028808a1947f522011948892ca9010a', '4028808a1947f52201194889f529010b', '4028808a1947f5220119489a584b014d');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119489bbad80175', '4028808a1947f522011948892ca9010a', '4028808a1947f522011948883cac0109', '4028808a1947f5220119489b99a40162');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119489bd0a20176', '4028808a1947f52201194889f529010b', '4028808a1947f522011948883cac0109', '4028808a1947f5220119489b99a40162');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f522011948a4de430178', '4028808a1947f5220119488766120108', '4028808a1947f522011948892ca9010a', '4028808a1947f52201194899df520139');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119492e774c03cf', '4028808a1947f5220119492e774c03cb', '4028808a1947f5220119492e774c03cc', '4028808a1947f5220119492e774c03ce');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119492e775c03d9', '4028808a1947f5220119492e774c03cc', '4028808a1947f5220119492e774c03cd', '4028808a1947f5220119492e775c03d8');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119492e776b03da', '4028808a1947f5220119492e774c03cb', '4028808a1947f5220119492e774c03cd', '4028808a1947f5220119492e775c03d8');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119492e777b03e1', '4028808a1947f5220119492e774c03cb', '4028808a1947f5220119492e774c03cb', '4028808a1947f5220119492e776b03e0');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f52201194933424a0401', '4028808a1947f5220119492e774c03cd', '4028808a1947f5220119492e774c03cc', '4028808a1947f52201194933183c03e8');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119493357770402', '4028808a1947f52201194932895a03e7', '4028808a1947f5220119492e774c03cc', '4028808a1947f52201194933183c03e8');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f522011949358005041d', '4028808a1947f5220119492e774c03cd', '4028808a1947f52201194932895a03e7', '4028808a1947f5220119493553860403');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f5220119493650d2041e', '4028808a1947f5220119492e774c03cc', '4028808a1947f5220119492e774c03cc', '4028808a1947f5220119492e776b03e0');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f522011949366081041f', '4028808a1947f5220119492e774c03cd', '4028808a1947f5220119492e774c03cd', '4028808a1947f5220119492e776b03e0');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1947f52201194936712b0420', '4028808a1947f52201194932895a03e7', '4028808a1947f52201194932895a03e7', '4028808a1947f5220119492e776b03e0');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b01195267a323015a', '4028808a193230e301193271d0c00059', '4028808a193230e301193271d0c00059', '4028808a193230e3011932b99a310102');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b01195267b0df015b', '4028808a193230e301193272f9bf005a', '2', '4028808a193230e3011932b99a310102');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b5251e0293', '4028808a1951e21b011952b5250e028e', '4028808a1951e21b011952b5250e028e', '4028808a1951e21b011952b5250e0292');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b5251e0294', '4028808a1951e21b011952b5250e0290', '4028808a1951e21b011952b5250e0290', '4028808a1951e21b011952b5250e0292');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b5251e0295', '4028808a1951e21b011952b5250e028f', '4028808a1951e21b011952b5250e028f', '4028808a1951e21b011952b5250e0292');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b5251e0296', '4028808a1951e21b011952b5250e028d', '4028808a1951e21b011952b5250e028d', '4028808a1951e21b011952b5250e0292');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b5252d0297', '4028808a1951e21b011952b5250e0291', '4028808a1951e21b011952b5250e0291', '4028808a1951e21b011952b5250e0292');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b5254d02a5', '4028808a1951e21b011952b5250e028d', '4028808a1951e21b011952b5250e028e', '4028808a1951e21b011952b5253d02a4');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b5255c02b3', '4028808a1951e21b011952b5250e028e', '4028808a1951e21b011952b5250e0291', '4028808a1951e21b011952b5255c02b2');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b5255c02b4', '4028808a1951e21b011952b5250e028d', '4028808a1951e21b011952b5250e0291', '4028808a1951e21b011952b5255c02b2');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b5258b02c0', '4028808a1951e21b011952b5250e028e', '4028808a1951e21b011952b5250e028f', '4028808a1951e21b011952b5258b02bf');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b525ca02d1', '4028808a1951e21b011952b5250e028d', '4028808a1951e21b011952b5250e0290', '4028808a1951e21b011952b5259b02cd');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b525ca02d2', '4028808a1951e21b011952b5250e028f', '4028808a1951e21b011952b5250e0290', '4028808a1951e21b011952b5259b02cd');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('4028808a1951e21b011952b525ca02d3', '4028808a1951e21b011952b5250e0291', '4028808a1951e21b011952b5250e0290', '4028808a1951e21b011952b5259b02cd');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402880e620999032012099dd82ca0002', '4028808a1951e21b011952b5250e028d', '4028808a1951e21b011952b5250e028f', '4028808a1951e21b011952b5258b02bf');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402880e620999032012099ddaa950003', '4028808a1951e21b011952b5250e0291', '4028808a1951e21b011952b5250e028f', '4028808a1951e21b011952b5258b02bf');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402880e620999032012099de4e570004', '4028808a1951e21b011952b5250e028e', '4028808a1951e21b011952b5250e0290', '4028808a1951e21b011952b5259b02cd');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402880e620999032012099e01973001b', '4028808a1951e21b011952b5250e028f', '4028808a1951e21b011952b5250e028e', '402880e620999032012099df38c70005');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402880e620999032012099e01973001c', '4028808a1951e21b011952b5250e0290', '4028808a1951e21b011952b5250e028e', '402880e620999032012099df38c70005');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402880e620999032012099e01982001d', '4028808a1951e21b011952b5250e0291', '4028808a1951e21b011952b5250e028e', '402880e620999032012099df38c70005');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402880e62099903201209a081960001e', '4028808a194731fd011947757181000b', '4028808a194731fd0119477770db000d', '4028808a1947f52201194813946a0093');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402880e62099903201209a09a04f001f', '4028808a194731fd01194774999f000a', '4028808a194731fd01194775e00a000c', '4028808a1947f522011948124d73007f');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402880e62099903201209a09b0f80020', '4028808a1947f52201194810bacd0068', '4028808a194731fd01194775e00a000c', '4028808a1947f522011948124d73007f');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402880e62099903201209a186d200021', '4028808a1947f5220119492e774c03cc', '4028808a1947f52201194932895a03e7', '4028808a1947f5220119493553860403');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402880e62099903201209a186d200022', '4028808a1947f5220119492e774c03cb', '4028808a1947f52201194932895a03e7', '4028808a1947f5220119493553860403');

insert into gr_transition (transition_id, transition_start, transition_finish, transition_mstatus) values ('402881861a04e77a011a053ded7e0006', '4028808a1947f52201194810bacd0068', '4028808a194731fd011947757181000b', '4028808a1947f5220119480c50000054');

insert into gr_udf (udf_id, udf_caption, udf_referencedbycaption, udf_order, udf_def, udf_required, udf_htmlview, udf_lookuponly, udf_cachevalues, udf_type, udf_udfsource, udf_script, udf_lookupscript, udf_initialtask, udf_initialuser) values ('4028808a193230e3011932efdc6401ce', 'Requirement version', NULL, 0, '', 0, 0, 0, 1, 0, '4028808a193230e3011932ee97dd01c0', NULL, NULL, NULL, NULL);

insert into gr_udf (udf_id, udf_caption, udf_referencedbycaption, udf_order, udf_def, udf_required, udf_htmlview, udf_lookuponly, udf_cachevalues, udf_type, udf_udfsource, udf_script, udf_lookupscript, udf_initialtask, udf_initialuser) values ('4028808a193230e3011932f2098501da', 'Requirement source', NULL, 0, '', 0, 0, 0, 1, 0, '4028808a193230e3011932ee97dd01c0', NULL, NULL, NULL, NULL);

insert into gr_udf (udf_id, udf_caption, udf_referencedbycaption, udf_order, udf_def, udf_required, udf_htmlview, udf_lookuponly, udf_cachevalues, udf_type, udf_udfsource, udf_script, udf_lookupscript, udf_initialtask, udf_initialuser) values ('4028808a1934fdc7011935002e6b0003', 'Technical specification', NULL, 0, '', 0, 0, 0, 1, 5, '4028808a193230e3011932ee97dd01c0', NULL, NULL, NULL, NULL);

insert into gr_udf (udf_id, udf_caption, udf_referencedbycaption, udf_order, udf_def, udf_required, udf_htmlview, udf_lookuponly, udf_cachevalues, udf_type, udf_udfsource, udf_script, udf_lookupscript, udf_initialtask, udf_initialuser) values ('4028808a1947f52201194803e5f30022', 'Replicate a bug?', NULL, 0, '4028808a1947f522011948046aa3003b', 0, 0, 0, 1, 3, '4028808a1947f52201194803e5f30021', NULL, NULL, NULL, NULL);

insert into gr_udf (udf_id, udf_caption, udf_referencedbycaption, udf_order, udf_def, udf_required, udf_htmlview, udf_lookuponly, udf_cachevalues, udf_type, udf_udfsource, udf_script, udf_lookupscript, udf_initialtask, udf_initialuser) values ('4028808a1947f52201194818b51900ad', 'Broken requirement', 'Bugs for this requirement', 0, '', 0, 0, 0, 1, 7, '4028808a1947f52201194803e5f30021', NULL, NULL, '4028808a1951e21b01195229b82f0002', NULL);

insert into gr_udf (udf_id, udf_caption, udf_referencedbycaption, udf_order, udf_def, udf_required, udf_htmlview, udf_lookuponly, udf_cachevalues, udf_type, udf_udfsource, udf_script, udf_lookupscript, udf_initialtask, udf_initialuser) values ('4028808a1947f5220119482b654700c6', 'Test case, which detected a bug', 'Bugs, found with this test case', 0, '', 0, 0, 0, 1, 7, '4028808a1947f52201194803e5f30021', NULL, NULL, '4028808a1951e21b0119522a19990003', NULL);

insert into gr_udf (udf_id, udf_caption, udf_referencedbycaption, udf_order, udf_def, udf_required, udf_htmlview, udf_lookuponly, udf_cachevalues, udf_type, udf_udfsource, udf_script, udf_lookupscript, udf_initialtask, udf_initialuser) values ('4028808a1951e21b011952b525f902df', 'Test case for testing', 'Use in change', 0, '', 0, 0, 0, 1, 7, '4028808a1951e21b011952b525f902de', NULL, NULL, '4028808a1951e21b0119522a19990003', NULL);

insert into gr_udf (udf_id, udf_caption, udf_referencedbycaption, udf_order, udf_def, udf_required, udf_htmlview, udf_lookuponly, udf_cachevalues, udf_type, udf_udfsource, udf_script, udf_lookupscript, udf_initialtask, udf_initialuser) values ('4028808a1951e21b011952b5261802f8', 'Conform with requirement', 'Use in change', 0, '', 0, 0, 0, 1, 7, '4028808a1951e21b011952b525f902de', NULL, NULL, '4028808a1951e21b01195229b82f0002', NULL);

insert into gr_udf (udf_id, udf_caption, udf_referencedbycaption, udf_order, udf_def, udf_required, udf_htmlview, udf_lookuponly, udf_cachevalues, udf_type, udf_udfsource, udf_script, udf_lookupscript, udf_initialtask, udf_initialuser) values ('4028808a1953022d0119534f1be900d1', 'Created for requirement', 'Test case for testing', 0, '', 0, 0, 0, 1, 7, '4028808a1953022d0119534f1bda00d0', NULL, NULL, '1', NULL);

insert into gr_udf (udf_id, udf_caption, udf_referencedbycaption, udf_order, udf_def, udf_required, udf_htmlview, udf_lookuponly, udf_cachevalues, udf_type, udf_udfsource, udf_script, udf_lookupscript, udf_initialtask, udf_initialuser) values ('4028808a1953022d01195350a40000ef', 'Created for requirement', 'Test suite for testing', 0, '', 0, 0, 0, 1, 7, '4028808a1953022d01195350a40000ee', NULL, NULL, '1', NULL);

insert into gr_udf (udf_id, udf_caption, udf_referencedbycaption, udf_order, udf_def, udf_required, udf_htmlview, udf_lookuponly, udf_cachevalues, udf_type, udf_udfsource, udf_script, udf_lookupscript, udf_initialtask, udf_initialuser) values ('4028808a1953022d011953651fd60242', 'Test case for testing', 'Used in requirement', 0, '', 0, 0, 0, 1, 7, '4028808a193230e3011932ee97dd01c0', NULL, NULL, '1', NULL);

insert into gr_udflist (udflist_id, udflist_val, udflist_udf) values ('4028808a1947f522011948046aa3003b', 'Yes', '4028808a1947f52201194803e5f30022');

insert into gr_udflist (udflist_id, udflist_val, udflist_udf) values ('4028808a1947f522011948047b5d003c', 'No', '4028808a1947f52201194803e5f30022');

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('4028808a193230e3011932ee97dd01c0', NULL, NULL, '4028808a192e43e801192e527cee013f');

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('4028808a1947f52201194803e5f30021', NULL, NULL, '4028808a194731fd0119476e1efe0003');

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('4028808a1951e21b011952b525f902de', NULL, NULL, '4028808a1951e21b011952b524ff0286');

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('4028808a1953022d0119531abf3000c7', '4028808a1953022d0119531abec300c6', NULL, NULL);

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('4028808a1953022d0119534425a300ca', '4028808a1953022d01195344252600c9', NULL, NULL);

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('4028808a1953022d01195346e69800cc', '4028808a1953022d01195346e62a00cb', NULL, NULL);

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('4028808a1953022d0119534f1bda00d0', NULL, NULL, '4028808a194731fd011947ab7301002f');

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('4028808a1953022d01195350a40000ee', NULL, NULL, '4028808a1947f5220119492e773d03c7');

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('4028808a1953022d0119537bdd3f032f', '4028808a1953022d0119537bdcc2032e', NULL, NULL);

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('402880e420e2d0740120e41b015d0009', '4028808a1953022d0119534c205d00ce', NULL, NULL);

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('402880e420e2d0740120e41c8a14000a', '4028808a1953022d0119531c0e5500c8', NULL, NULL);

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('40289689fc796a4f00fc796b09ab0102', '1', NULL, NULL);

insert into gr_udfsource (udfsource_id, udfsource_task, udfsource_user, udfsource_workflow) values ('ff8080812bd3d88d012bd3e2f6a10077', 'ff8080812bd3d88d012bd3e2f5320076', NULL, NULL);

insert into gr_udfval (udfval_id, udfval_str, udfval_num, udfval_dat, udfval_udflist, udfval_task, udfval_user, udfval_udf, udfval_udfsource, udfval_longtext) values ('402880e420e2d0740120e3f77ed50002', '1.0', NULL, NULL, NULL, NULL, NULL, '4028808a193230e3011932efdc6401ce', '4028808a1953022d01195346e69800cc', NULL);

insert into gr_udfval (udfval_id, udfval_str, udfval_num, udfval_dat, udfval_udflist, udfval_task, udfval_user, udfval_udf, udfval_udfsource, udfval_longtext) values ('402880e420e2d0740120e3f77eeb0003', '-3 columns
-20 users in column
-slider in footer', NULL, NULL, NULL, NULL, NULL, '4028808a1934fdc7011935002e6b0003', '4028808a1953022d01195346e69800cc', NULL);

insert into gr_udfval (udfval_id, udfval_str, udfval_num, udfval_dat, udfval_udflist, udfval_task, udfval_user, udfval_udf, udfval_udfsource, udfval_longtext) values ('402880e420e2d0740120e3f77ef90004', NULL, NULL, NULL, NULL, '4028808a1953022d0119534c205d00ce', NULL, '4028808a1953022d011953651fd60242', '4028808a1953022d01195346e69800cc', NULL);

insert into gr_udfval (udfval_id, udfval_str, udfval_num, udfval_dat, udfval_udflist, udfval_task, udfval_user, udfval_udf, udfval_udfsource, udfval_longtext) values ('402880e420e2d0740120e3f840540005', NULL, NULL, NULL, NULL, '4028808a1953022d0119531c0e5500c8', NULL, '4028808a1953022d011953651fd60242', '4028808a1953022d0119531abf3000c7', NULL);

insert into gr_udfval (udfval_id, udfval_str, udfval_num, udfval_dat, udfval_udflist, udfval_task, udfval_user, udfval_udf, udfval_udfsource, udfval_longtext) values ('ff8080812c9d426b012c9d45dc580035', NULL, NULL, NULL, NULL, '4028808a1953022d0119531abec300c6', NULL, '4028808a1947f52201194818b51900ad', '4028808a1953022d0119537bdd3f032f', NULL);

insert into gr_udfval (udfval_id, udfval_str, udfval_num, udfval_dat, udfval_udflist, udfval_task, udfval_user, udfval_udf, udfval_udfsource, udfval_longtext) values ('ff8080812c9d426b012c9d45dccb0036', NULL, NULL, NULL, NULL, '4028808a1953022d0119531c0e5500c8', NULL, '4028808a1947f5220119482b654700c6', '4028808a1953022d0119537bdd3f032f', NULL);

insert into gr_udfval (udfval_id, udfval_str, udfval_num, udfval_dat, udfval_udflist, udfval_task, udfval_user, udfval_udf, udfval_udfsource, udfval_longtext) values ('ff8080812c9d426b012c9d45dced0037', NULL, NULL, NULL, '4028808a1947f522011948046aa3003b', NULL, NULL, '4028808a1947f52201194803e5f30022', '4028808a1953022d0119537bdd3f032f', NULL);

insert into gr_user (user_id, user_login, user_password, user_name, user_tel, user_email, user_active, user_preferences, user_prstatus, user_locale, user_timezone, user_child_allowed, user_manager, user_expiredate, user_lastlogon, user_passchanged, user_company, user_template, user_default_project) values ('4028808a1934933b011934c2e27703d4', 'analyst', '05d5c5dfb743a5bd8fd7494fdc9bdb00202cb962ac59075b964b07152d234b70f58dadd1b8b8e68724e7d0cb88d9dcd3', 'Ivan Analiticov', '', '', 1, NULL, '4028808a1934933b011934c1e26c020c', 'ru', NULL, NULL, '1', NULL, to_timestamp('2008-04-15 22:06:32', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2008-05-19 12:35:33', 'YYYY-MM-DD HH24:MI:SS'), '', 'default_html.ftl', NULL);

insert into gr_user (user_id, user_login, user_password, user_name, user_tel, user_email, user_active, user_preferences, user_prstatus, user_locale, user_timezone, user_child_allowed, user_manager, user_expiredate, user_lastlogon, user_passchanged, user_company, user_template, user_default_project) values ('4028808a1934933b011934c336e003d5', 'developer', '5e8edd851d2fdfbd7415232c67367cc3202cb962ac59075b964b07152d234b70e3e4c14c7840ffc350e8922c3aa22a06', 'Stepan Developerov', '', '', 1, NULL, '4028808a1934933b011934c2214a02a4', 'ru', NULL, NULL, '1', NULL, to_timestamp('2008-04-15 23:08:34', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2008-05-19 12:37:41', 'YYYY-MM-DD HH24:MI:SS'), '', 'default_html.ftl', NULL);

insert into gr_user (user_id, user_login, user_password, user_name, user_tel, user_email, user_active, user_preferences, user_prstatus, user_locale, user_timezone, user_child_allowed, user_manager, user_expiredate, user_lastlogon, user_passchanged, user_company, user_template, user_default_project) values ('4028808a1934933b011934c65e400486', 'writer', 'a82feee3cc1af8bcabda979e8775ef0f202cb962ac59075b964b07152d234b707bc3ca68769437ce986455407dab2a1f', 'Dmitry Writerov', '', '', 1, NULL, '4028808a1934933b011934c5ea5803ee', 'ru', NULL, NULL, '1', NULL, to_timestamp('2008-04-15 23:05:35', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2008-05-19 12:38:01', 'YYYY-MM-DD HH24:MI:SS'), '', 'default_html.ftl', NULL);

insert into gr_user (user_id, user_login, user_password, user_name, user_tel, user_email, user_active, user_preferences, user_prstatus, user_locale, user_timezone, user_child_allowed, user_manager, user_expiredate, user_lastlogon, user_passchanged, user_company, user_template, user_default_project) values ('4028808a1934933b011934ca3b3404af', 'tester', 'bf3453193f289cd54c84cb6f5d3728c0202cb962ac59075b964b07152d234b702c674005a2f3c3132e4cdf1b680ec405', 'Maxim Testerov', '', '', 1, NULL, '4028808a1934933b011934c25dc6033c', 'ru', NULL, NULL, '1', NULL, to_timestamp('2008-04-15 23:06:04', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2008-05-19 12:39:45', 'YYYY-MM-DD HH24:MI:SS'), '', 'default_html.ftl', NULL);

insert into gr_user (user_id, user_login, user_password, user_name, user_tel, user_email, user_active, user_preferences, user_prstatus, user_locale, user_timezone, user_child_allowed, user_manager, user_expiredate, user_lastlogon, user_passchanged, user_company, user_template, user_default_project) values ('ff8080812c34f3f1012c34fbce9300ee', 'anonymous', '63a9f0ea7bb98050796b649e854818457d15d0222cd7365a007923a896f35201', 'anonymous', '', '', 1, NULL, 'ff8080812c34f3f1012c34f7df880002', 'en', 'America/New_York', NULL, '1', NULL, to_timestamp('2010-11-10 11:52:36', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2010-11-10 11:51:47', 'YYYY-MM-DD HH24:MI:SS'), '', 'default_html.ftl', '1');

insert into gr_user (user_id, user_login, user_password, user_name, user_tel, user_email, user_active, user_preferences, user_prstatus, user_locale, user_timezone, user_child_allowed, user_manager, user_expiredate, user_lastlogon, user_passchanged, user_company, user_template, user_default_project) values ('4028808a192e43e801192e48f4fd0002', 'manager', '1d0258c2440a8d19e716292b231e3190202cb962ac59075b964b07152d234b70202cb962ac59075b964b07152d234b70202cb962ac59075b964b07152d234b70fbd20304dfadf13c00d8bd6ee378fdb5', 'Sergey Managerov', '', '', 1, NULL, '4028808a193230e3011932be7da8010a', 'ru', NULL, NULL, '1', NULL, to_timestamp('2010-11-30 17:53:22', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2008-05-19 12:34:46', 'YYYY-MM-DD HH24:MI:SS'), '', 'default_html.ftl', '1');

insert into gr_user (user_id, user_login, user_password, user_name, user_tel, user_email, user_active, user_preferences, user_prstatus, user_locale, user_timezone, user_child_allowed, user_manager, user_expiredate, user_lastlogon, user_passchanged, user_company, user_template, user_default_project) values ('1', 'root', '63a9f0ea7bb98050796b649e8548184563a9f0ea7bb98050796b649e85481845', 'Administrator', '', '', 1, NULL, '5', 'en', 'America/New_York', NULL, NULL, NULL, to_timestamp('2010-12-02 18:11:15', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2008-04-09 22:02:36', 'YYYY-MM-DD HH24:MI:SS'), '', 'default_html.ftl', 'ff8080812bd3d88d012bd3e2f5320076');

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('1', '1', NULL);

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('4028808a192e43e801192e4f4ad1013b', '4028808a192e43e801192e48f4fd0002', NULL);

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('4028808a1934933b011934c4afe403ec', NULL, '4028808a1934933b011934c1e26c020c');

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('4028808a1934933b011934c8266d04a8', '4028808a1934933b011934c2e27703d4', NULL);

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('4028808a1934933b011934c8527e04ab', '4028808a1934933b011934c336e003d5', NULL);

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('4028808a1934933b011934c85daa04ad', '4028808a1934933b011934c65e400486', NULL);

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('4028808a1934933b011934ca7ffd04b0', '4028808a1934933b011934ca3b3404af', NULL);

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('4028808a1934933b011934e808f605a5', NULL, '4028808a193230e3011932be7da8010a');

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('4028808a1934933b011934e9a46605a7', NULL, '4028808a1934933b011934c2214a02a4');

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('4028808a1934933b011934e9f26705ab', NULL, '4028808a1934933b011934c25dc6033c');

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('4028808a1934933b011934ea005205ad', NULL, '4028808a1934933b011934c5ea5803ee');

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('4028808a1953022d01195302c2900002', NULL, '4028808a1952eada011952ed6563009a');

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('402881850abc2f71010abc42d1580005', NULL, '5');

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('nullusersource', NULL, NULL);

insert into gr_usersource (usersource_id, usersource_user, usersource_prstatus) values ('ff8080812c34f3f1012c34fc18ad00ef', NULL, 'ff8080812c34f3f1012c34f7df880002');

insert into gr_workflow (workflow_id, workflow_name, workflow_task) values ('1', 'Folder', '1');

insert into gr_workflow (workflow_id, workflow_name, workflow_task) values ('4028808a192e43e801192e527cee013f', 'Requirement', '1');

insert into gr_workflow (workflow_id, workflow_name, workflow_task) values ('4028808a194731fd0119476e1efe0003', 'Bug', '1');

insert into gr_workflow (workflow_id, workflow_name, workflow_task) values ('4028808a194731fd011947ab7301002f', 'Test case', '1');

insert into gr_workflow (workflow_id, workflow_name, workflow_task) values ('4028808a1947f5220119492e773d03c7', 'Test suite', '1');

insert into gr_workflow (workflow_id, workflow_name, workflow_task) values ('4028808a1951e21b011952b524ff0286', 'Change', '1');

insert into gr_report (report_id, report_name, report_preferences, report_rtype, report_priv, report_filter, report_task, report_owner, report_params) values ('4028808a1953022d0119536f2dc002a5', 'List of requirements', 'T', 'List', 0, '4028808a1953022d01195368dd4c0291', '1', '1', '');

insert into gr_report (report_id, report_name, report_preferences, report_rtype, report_priv, report_filter, report_task, report_owner, report_params) values ('4028808a1953022d01195370aabb02c6', 'List of requirements (deep search)', 'T', 'List', 0, '4028808a1953022d0119536fd4be02a6', '1', '1', '');

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('2', 'Normal', 2, '1', 'Normal priority', 1);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a192e43e801192e53847b0140', 'Key', 1, '4028808a192e43e801192e527cee013f', 'Key requirement', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a192e43e801192e54c17f0141', 'Required', 2, '4028808a192e43e801192e527cee013f', 'Required requirement', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a192e43e801192e5595790142', 'Recommended', 3, '4028808a192e43e801192e527cee013f', 'Recommended requirement', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a192e43e801192e572ee60143', 'Optative', 4, '4028808a192e43e801192e527cee013f', 'Optative requirement', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a192e43e801192e5a6e420144', 'Possible', 5, '4028808a192e43e801192e527cee013f', 'Possible requirement', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a193230e30119327103fa0057', 'High', 1, '1', 'High priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a193230e30119327129c20058', 'Low', 3, '1', 'Low priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a193230e3011932ed095e01bf', 'None', 6, '4028808a192e43e801192e527cee013f', 'None', 1);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a194731fd0119476e6eb40004', 'Low', 5, '4028808a194731fd0119476e1efe0003', 'Low priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a194731fd0119476ebc380005', 'Normal', 4, '4028808a194731fd0119476e1efe0003', 'Normal priority', 1);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a194731fd0119476ef0860006', 'High', 3, '4028808a194731fd0119476e1efe0003', 'High priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a194731fd0119476f674d0008', 'Immediate', 1, '4028808a194731fd0119476e1efe0003', 'Immediate priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1947f5220119486ab6a00102', 'Immediate', 1, '4028808a194731fd011947ab7301002f', 'Immediate priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1947f5220119486afb2b0103', 'Very high', 2, '4028808a194731fd011947ab7301002f', 'Very high priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1947f5220119486b2d470104', 'High', 3, '4028808a194731fd011947ab7301002f', 'High priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1947f5220119486b728e0105', 'Normal', 4, '4028808a194731fd011947ab7301002f', 'Normal priority', 1);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1947f5220119486bc35d0106', 'Low', 5, '4028808a194731fd011947ab7301002f', 'Low priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1947f5220119486c3d020107', 'Very low', 6, '4028808a194731fd011947ab7301002f', 'Very low priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1947f5220119492e773d03c8', 'Normal', 2, '4028808a1947f5220119492e773d03c7', 'Normal priority', 1);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1947f5220119492e773d03c9', 'High', 1, '4028808a1947f5220119492e773d03c7', 'High priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1947f5220119492e773d03ca', 'Low', 3, '4028808a1947f5220119492e773d03c7', 'Low priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1951e21b011952b524ff0287', 'Low', 5, '4028808a1951e21b011952b524ff0286', 'Low priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1951e21b011952b524ff0288', 'Normal', 4, '4028808a1951e21b011952b524ff0286', 'Normal priority', 1);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1951e21b011952b5250e0289', 'High', 3, '4028808a1951e21b011952b524ff0286', 'High priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1951e21b011952b5250e028a', 'Very high', 2, '4028808a1951e21b011952b524ff0286', 'Very high priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1951e21b011952b5250e028b', 'Immediate', 1, '4028808a1951e21b011952b524ff0286', 'Immediate priority', 0);

insert into gr_priority (priority_id, priority_name, priority_order, priority_workflow, priority_description, priority_def) values ('4028808a1951e21b011952b5250e028c', 'Very low', 6, '4028808a1951e21b011952b524ff0286', 'Very low priority', 0);

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a19512fa50119515fc5650029', '4028808a19512fa50119515f006f0008', '4028808a19512fa50119515f006f0008');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a19512fa501195165d3cf004a', '4028808a19512fa5011951659a9e002a', '4028808a19512fa5011951659a9e002a');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b011952552f81011f', '4028808a19512fa5011951659a9e002a', '4028808a1951e21b01195245ff4200c1');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b01195260340f0120', '4028808a19512fa5011951687d82004b', '4028808a1951e21b011952475ca000ff');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b0119526064180121', '4028808a19512fa5011951687d82004b', '4028808a1951e21b01195243b62d00a1');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b01195260aac60122', '4028808a19512fa50119515f006f0008', '4028808a1951e21b0119524644b800e0');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b0119526103f20123', '4028808a1951e21b011952475ca000ff', '4028808a1951e21b01195243b62d00a1');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b0119526a93c3015c', '4028808a19512fa5011951687d82004b', '4028808a19512fa5011951687d82004b');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b011952be59af0365', '4028808a1951e21b011952b4384d024d', '4028808a1951e21b011952bddb38032c');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b011952be64000366', '4028808a1951e21b011952b4384d024d', '4028808a1951e21b011952b4384d024d');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b011952cfbd1a0397', '1', '4028808a1951e21b011952cefb8f036a');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b011952d04d820399', '4028808a1951e21b011952cefb8f036a', '4028808a1951e21b011952b4384d024d');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b011952d0565c039a', '4028808a1951e21b011952cefb8f036a', '4028808a19512fa5011951659a9e002a');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b011952d05f55039b', '4028808a1951e21b011952cefb8f036a', '4028808a19512fa5011951687d82004b');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b011952d068ea039c', '4028808a1951e21b011952cefb8f036a', '4028808a19512fa50119515f006f0008');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028808a1951e21b011952d07a9d039d', '4028808a1951e21b011952cefb8f036a', '1');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('4028969bffaff0a700ffaff81e570002', '1', '1');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('ff8080812b58a7f4012b58ad9f800063', 'ff8080812b58a7f4012b58ad7cb5003f', 'ff8080812b58a7f4012b58ad7cb5003f');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('ff8080812b58a7f4012b58ad9fab0064', '1', 'ff8080812b58a7f4012b58ad7cb5003f');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('ff8080812b58a7f4012b58ad9fc40065', '4028808a1951e21b011952cefb8f036a', 'ff8080812b58a7f4012b58ad7cb5003f');

insert into gr_catrelation (catrelation_id, catrelation_category, catrelation_child) values ('ff8080812bd3d88d012bd3e253fa0075', '1', 'ff8080812bd3d88d012bd3e22ff90051');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1947f522011948c1846e02cb', 'V', '1', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1947f522011948c1846e02cc', 'V', '1', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1947f522011948c1846e02ce', 'V', '1', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1947f522011948c1846e02cf', 'V', '1', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1947f522011948c1847d02d0', 'C', '1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1947f522011948c1847d02d1', 'V', '1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1947f522011948c1847d02d2', 'E', '1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1947f522011948c1847d02d3', 'H', '1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1947f522011948c1847d02d4', 'D', '1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1947f522011948c1847d02d5', 'V', '1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1947f522011948c1847d02d6', 'H', '1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d29101d6', 'C', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d29101d7', 'V', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d29101d8', 'E', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d29101d9', 'H', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d29101da', 'C', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d29101db', 'V', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2a001dc', 'E', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2a001dd', 'H', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2a001de', 'C', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2a001df', 'V', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2a001e0', 'E', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2a001e1', 'H', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2b001e2', 'C', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2b001e3', 'V', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2b001e4', 'E', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2b001e5', 'H', '4028808a19512fa5011951659a9e002a', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2bf01e6', 'C', '4028808a19512fa5011951659a9e002a', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2bf01e7', 'V', '4028808a19512fa5011951659a9e002a', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2bf01e8', 'E', '4028808a19512fa5011951659a9e002a', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2bf01e9', 'H', '4028808a19512fa5011951659a9e002a', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2bf01ea', 'D', '4028808a19512fa5011951659a9e002a', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2bf01eb', 'C', '4028808a19512fa5011951659a9e002a', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2cf01ec', 'V', '4028808a19512fa5011951659a9e002a', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2cf01ed', 'E', '4028808a19512fa5011951659a9e002a', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2cf01ee', 'H', '4028808a19512fa5011951659a9e002a', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a2d2cf01ef', 'D', '4028808a19512fa5011951659a9e002a', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3143d01f0', 'C', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3144d01f1', 'V', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3144d01f2', 'E', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3144d01f3', 'H', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3144d01f4', 'C', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3144d01f5', 'V', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3144d01f6', 'E', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3144d01f7', 'H', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3145c01f8', 'C', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3145c01f9', 'V', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3145c01fa', 'E', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3145c01fb', 'H', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3146c01fc', 'C', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3146c01fd', 'V', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3146c01fe', 'E', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3146c01ff', 'H', '4028808a1951e21b01195245ff4200c1', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3146c0200', 'C', '4028808a1951e21b01195245ff4200c1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3147c0201', 'V', '4028808a1951e21b01195245ff4200c1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3147c0202', 'E', '4028808a1951e21b01195245ff4200c1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3147c0203', 'H', '4028808a1951e21b01195245ff4200c1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3147c0204', 'D', '4028808a1951e21b01195245ff4200c1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3147c0205', 'C', '4028808a1951e21b01195245ff4200c1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3148b0206', 'V', '4028808a1951e21b01195245ff4200c1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3148b0207', 'E', '4028808a1951e21b01195245ff4200c1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3148b0208', 'H', '4028808a1951e21b01195245ff4200c1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1951e21b011952a3148b0209', 'D', '4028808a1951e21b01195245ff4200c1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1952eada011952ed66da014b', 'V', '1', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1952eada011952ed66f9015c', 'C', '4028808a19512fa5011951659a9e002a', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1952eada011952ed66f9015d', 'V', '4028808a19512fa5011951659a9e002a', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1952eada011952ed66f9015e', 'E', '4028808a19512fa5011951659a9e002a', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1952eada011952ed6708015f', 'H', '4028808a19512fa5011951659a9e002a', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1952eada011952ed67080160', 'C', '4028808a1951e21b01195245ff4200c1', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1952eada011952ed67080161', 'V', '4028808a1951e21b01195245ff4200c1', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1952eada011952ed67080162', 'E', '4028808a1951e21b01195245ff4200c1', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1952eada011952ed67080163', 'H', '4028808a1951e21b01195245ff4200c1', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042b570004', 'C', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042b570005', 'V', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042b670006', 'E', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042b670007', 'H', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042b770008', 'C', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042b860009', 'V', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042b86000a', 'E', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042b86000b', 'H', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042ba5000c', 'C', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042ba5000d', 'V', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042ba5000e', 'E', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042ba5000f', 'H', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042bb50010', 'C', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042bb50011', 'V', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042bb50012', 'E', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042bb50013', 'H', '4028808a1951e21b0119524644b800e0', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042bc50014', 'C', '4028808a1951e21b0119524644b800e0', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042bc50015', 'V', '4028808a1951e21b0119524644b800e0', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042bc50016', 'E', '4028808a1951e21b0119524644b800e0', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042bc50017', 'H', '4028808a1951e21b0119524644b800e0', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042bd40018', 'D', '4028808a1951e21b0119524644b800e0', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042be40019', 'C', '4028808a1951e21b0119524644b800e0', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042be4001a', 'V', '4028808a1951e21b0119524644b800e0', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042be4001b', 'E', '4028808a1951e21b0119524644b800e0', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042be4001c', 'H', '4028808a1951e21b0119524644b800e0', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953042be4001d', 'D', '4028808a1951e21b0119524644b800e0', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f4c001e', 'C', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f5c001f', 'V', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f5c0020', 'E', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f5c0021', 'H', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f5c0022', 'C', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f5c0023', 'V', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f6c0024', 'E', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f6c0025', 'H', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f7b0026', 'C', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f7b0027', 'V', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f7b0028', 'E', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f7b0029', 'H', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f8b002a', 'C', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f8b002b', 'V', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f8b002c', 'E', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f9b002d', 'H', '4028808a1951e21b011952475ca000ff', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f9b002e', 'C', '4028808a1951e21b011952475ca000ff', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f9b002f', 'V', '4028808a1951e21b011952475ca000ff', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051f9b0030', 'E', '4028808a1951e21b011952475ca000ff', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051faa0031', 'H', '4028808a1951e21b011952475ca000ff', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051faa0032', 'D', '4028808a1951e21b011952475ca000ff', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051fba0033', 'C', '4028808a1951e21b011952475ca000ff', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051fba0034', 'V', '4028808a1951e21b011952475ca000ff', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051fba0035', 'E', '4028808a1951e21b011952475ca000ff', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051fba0036', 'H', '4028808a1951e21b011952475ca000ff', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d011953051fba0037', 'D', '4028808a1951e21b011952475ca000ff', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305465c0038', 'C', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305466c0039', 'V', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305466c003a', 'E', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305466c003b', 'H', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305467c003c', 'C', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305467c003d', 'V', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305467c003e', 'E', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305467c003f', 'H', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305468b0040', 'C', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305469b0041', 'V', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305469b0042', 'E', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305469b0043', 'H', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305469b0044', 'C', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305469b0045', 'V', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305469b0046', 'E', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d0119530546ab0047', 'H', '4028808a19512fa50119515f006f0008', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d0119530546ba0048', 'C', '4028808a19512fa50119515f006f0008', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d0119530546ba0049', 'V', '4028808a19512fa50119515f006f0008', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d0119530546ba004a', 'E', '4028808a19512fa50119515f006f0008', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d0119530546ba004b', 'H', '4028808a19512fa50119515f006f0008', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d0119530546ba004c', 'D', '4028808a19512fa50119515f006f0008', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d0119530546ca004d', 'C', '4028808a19512fa50119515f006f0008', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d0119530546ca004e', 'V', '4028808a19512fa50119515f006f0008', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d0119530546ca004f', 'E', '4028808a19512fa50119515f006f0008', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d0119530546ca0050', 'H', '4028808a19512fa50119515f006f0008', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d0119530546ca0051', 'D', '4028808a19512fa50119515f006f0008', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bbfa0052', 'C', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bbfa0053', 'V', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bbfa0054', 'E', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc090055', 'H', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc090056', 'C', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc090057', 'V', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc090058', 'E', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc090059', 'H', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc19005a', 'C', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc29005b', 'V', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc29005c', 'E', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc29005d', 'H', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc29005e', 'C', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc29005f', 'V', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc290060', 'E', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc380061', 'H', '4028808a19512fa5011951687d82004b', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc380062', 'C', '4028808a19512fa5011951687d82004b', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc380063', 'V', '4028808a19512fa5011951687d82004b', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc380064', 'E', '4028808a19512fa5011951687d82004b', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc480065', 'H', '4028808a19512fa5011951687d82004b', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc480066', 'D', '4028808a19512fa5011951687d82004b', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc580067', 'C', '4028808a19512fa5011951687d82004b', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc580068', 'V', '4028808a19512fa5011951687d82004b', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc580069', 'E', '4028808a19512fa5011951687d82004b', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc58006a', 'H', '4028808a19512fa5011951687d82004b', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305bc58006b', 'D', '4028808a19512fa5011951687d82004b', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ebe3006c', 'C', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ebe3006d', 'V', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ebe3006e', 'E', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ebe3006f', 'H', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ebf30070', 'C', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ebf30071', 'V', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ebf30072', 'E', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ebf30073', 'H', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec030074', 'C', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec030075', 'V', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec120076', 'E', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec120077', 'H', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec120078', 'C', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec120079', 'V', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec12007a', 'E', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec22007b', 'H', '4028808a1951e21b011952b4384d024d', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec22007c', 'C', '4028808a1951e21b011952b4384d024d', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec22007d', 'V', '4028808a1951e21b011952b4384d024d', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec32007e', 'E', '4028808a1951e21b011952b4384d024d', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec32007f', 'H', '4028808a1951e21b011952b4384d024d', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec320080', 'D', '4028808a1951e21b011952b4384d024d', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec320081', 'C', '4028808a1951e21b011952b4384d024d', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec320082', 'V', '4028808a1951e21b011952b4384d024d', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec410083', 'E', '4028808a1951e21b011952b4384d024d', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec410084', 'H', '4028808a1951e21b011952b4384d024d', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195305ec410085', 'D', '4028808a1951e21b011952b4384d024d', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306adfb0086', 'C', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae0b0087', 'V', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae0b0088', 'E', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae0b0089', 'H', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae0b008a', 'C', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae0b008b', 'V', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae0b008c', 'E', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae1a008d', 'H', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae2a008e', 'C', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae2a008f', 'V', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae2a0090', 'E', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae2a0091', 'H', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae2a0092', 'C', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae2a0093', 'V', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae390094', 'E', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae390095', 'H', '4028808a1951e21b01195243b62d00a1', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae490096', 'C', '4028808a1951e21b01195243b62d00a1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae490097', 'V', '4028808a1951e21b01195243b62d00a1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae490098', 'E', '4028808a1951e21b01195243b62d00a1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae490099', 'H', '4028808a1951e21b01195243b62d00a1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae49009a', 'D', '4028808a1951e21b01195243b62d00a1', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae59009b', 'C', '4028808a1951e21b01195243b62d00a1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae59009c', 'V', '4028808a1951e21b01195243b62d00a1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae59009d', 'E', '4028808a1951e21b01195243b62d00a1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae59009e', 'H', '4028808a1951e21b01195243b62d00a1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306ae59009f', 'D', '4028808a1951e21b01195243b62d00a1', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e03600a0', 'C', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e03600a1', 'V', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e04600a2', 'E', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e04600a3', 'H', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e04600a4', 'C', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e04600a5', 'V', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e04600a6', 'E', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e04600a7', 'H', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e06500a8', 'C', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e06500a9', 'V', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e06500aa', 'E', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e06500ab', 'H', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e07500ac', 'C', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e07500ad', 'V', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e07500ae', 'E', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e07500af', 'H', '4028808a1951e21b011952bddb38032c', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e08400b0', 'C', '4028808a1951e21b011952bddb38032c', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e08400b1', 'V', '4028808a1951e21b011952bddb38032c', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e08400b2', 'E', '4028808a1951e21b011952bddb38032c', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e08400b3', 'H', '4028808a1951e21b011952bddb38032c', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e08400b4', 'D', '4028808a1951e21b011952bddb38032c', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e09400b5', 'C', '4028808a1951e21b011952bddb38032c', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e09400b6', 'V', '4028808a1951e21b011952bddb38032c', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e09400b7', 'E', '4028808a1951e21b011952bddb38032c', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e09400b8', 'H', '4028808a1951e21b011952bddb38032c', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('4028808a1953022d01195306e09400b9', 'D', '4028808a1951e21b011952bddb38032c', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa72959006a', 'V', '4028808a1951e21b011952cefb8f036a', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa72988006e', 'C', '4028808a1951e21b011952cefb8f036a', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa72997006f', 'V', '4028808a1951e21b011952cefb8f036a', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa729a70070', 'E', '4028808a1951e21b011952cefb8f036a', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa729a70071', 'H', '4028808a1951e21b011952cefb8f036a', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa729b70072', 'D', '4028808a1951e21b011952cefb8f036a', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa729d60074', 'V', '4028808a1951e21b011952cefb8f036a', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa729f50079', 'V', '4028808a1951e21b011952cefb8f036a', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa72a05007d', 'C', '4028808a1951e21b011952cefb8f036a', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa72a14007e', 'V', '4028808a1951e21b011952cefb8f036a', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa72a14007f', 'E', '4028808a1951e21b011952cefb8f036a', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa72a140080', 'H', '4028808a1951e21b011952cefb8f036a', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa72a140081', 'D', '4028808a1951e21b011952cefb8f036a', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa72a240083', 'V', '4028808a1951e21b011952cefb8f036a', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('402880e62099903201209aa72a530088', 'V', '4028808a1951e21b011952cefb8f036a', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7cf40040', 'C', 'ff8080812b58a7f4012b58ad7cb5003f', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7d050041', 'V', 'ff8080812b58a7f4012b58ad7cb5003f', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7d160042', 'E', 'ff8080812b58a7f4012b58ad7cb5003f', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7d2e0043', 'H', 'ff8080812b58a7f4012b58ad7cb5003f', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7d500044', 'D', 'ff8080812b58a7f4012b58ad7cb5003f', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7d830045', 'C', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7d920046', 'V', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7da30047', 'E', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7dbb0048', 'H', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7dcc0049', 'D', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7de8004a', 'C', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7df6004b', 'V', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7e06004c', 'E', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7e17004d', 'H', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7e2b004e', 'D', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7e53004f', 'C', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7e630050', 'V', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7e6a0051', 'E', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7e7a0052', 'H', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7e830053', 'D', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7e930054', 'C', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7e9c0055', 'V', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7ea40056', 'E', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7eac0057', 'H', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7eb40058', 'D', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7ec30059', 'C', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7ecd005a', 'V', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7ede005b', 'E', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7eee005c', 'H', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7ef7005d', 'D', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7f12005e', 'C', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7f20005f', 'V', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7f290060', 'E', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7f3a0061', 'H', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812b58a7f4012b58ad7f480062', 'D', 'ff8080812b58a7f4012b58ad7cb5003f', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e230dc0052', 'C', 'ff8080812bd3d88d012bd3e22ff90051', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e231280053', 'V', 'ff8080812bd3d88d012bd3e22ff90051', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e231380054', 'E', 'ff8080812bd3d88d012bd3e22ff90051', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e231490055', 'H', 'ff8080812bd3d88d012bd3e22ff90051', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e2315a0056', 'D', 'ff8080812bd3d88d012bd3e22ff90051', '5');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e231840057', 'C', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e231940058', 'V', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e231a70059', 'E', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e231b5005a', 'H', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e231bd005b', 'D', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c1e26c020c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e231d9005c', 'C', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e231e8005d', 'V', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e231f7005e', 'E', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e23200005f', 'H', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232100060', 'D', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c2214a02a4');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232210061', 'C', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e2322c0062', 'V', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e2323a0063', 'E', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232530064', 'H', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e2325b0065', 'D', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1952eada011952ed6563009a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232780066', 'C', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e2328d0067', 'V', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232960068', 'E', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232a60069', 'H', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232b6006a', 'D', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a193230e3011932be7da8010a');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232d3006b', 'C', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232e0006c', 'V', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232e8006d', 'E', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232f0006e', 'H', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e232fa006f', 'D', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c5ea5803ee');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e2330a0070', 'C', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e233120071', 'V', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e2332b0072', 'E', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e233330073', 'H', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812bd3d88d012bd3e2333d0074', 'D', 'ff8080812bd3d88d012bd3e22ff90051', '4028808a1934933b011934c25dc6033c');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9e934008f', 'V', '4028808a1951e21b01195245ff4200c1', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9e9c10094', 'V', '4028808a1951e21b011952bddb38032c', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9ea4e0099', 'V', 'ff8080812bd3d88d012bd3e22ff90051', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9eadc009e', 'V', 'ff8080812b58a7f4012b58ad7cb5003f', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9eb5800a3', 'V', '4028808a19512fa5011951659a9e002a', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9ec0800a8', 'V', '4028808a1951e21b011952b4384d024d', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9ec8a00ad', 'V', '4028808a19512fa50119515f006f0008', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9ed6b00b2', 'V', '4028808a19512fa5011951687d82004b', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9edd600b7', 'V', '1', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9ee3200bc', 'V', '4028808a1951e21b0119524644b800e0', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9ee6c00c1', 'V', '4028808a1951e21b01195243b62d00a1', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9eeae00c6', 'V', '4028808a1951e21b011952475ca000ff', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_cprstatus (cprstatus_id, cprstatus_type, cprstatus_category, cprstatus_prstatus) values ('ff8080812c34f3f1012c34f9eef100cb', 'V', '4028808a1951e21b011952cefb8f036a', 'ff8080812c34f3f1012c34f7df880002');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb27018f', 'V', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb270190', 'STATUS_VIEW_ALL', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb270191', 'STATUS_EDIT_ALL', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb270192', 'V', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb270193', 'STATUS_VIEW_ALL', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb270194', 'STATUS_EDIT_ALL', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb370195', 'V', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb370196', 'E', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb370197', 'STATUS_VIEW_ALL', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb370198', 'STATUS_EDIT_ALL', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb370199', 'V', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb37019a', 'STATUS_VIEW_ALL', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb37019b', 'STATUS_EDIT_ALL', '4028808a193230e3011932efdc6401ce', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb46019c', 'V', '4028808a193230e3011932efdc6401ce', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb46019d', 'E', '4028808a193230e3011932efdc6401ce', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb46019e', 'STATUS_VIEW_ALL', '4028808a193230e3011932efdc6401ce', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb46019f', 'STATUS_EDIT_ALL', '4028808a193230e3011932efdc6401ce', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb5601a0', 'V', '4028808a193230e3011932efdc6401ce', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb5601a1', 'E', '4028808a193230e3011932efdc6401ce', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb5601a2', 'STATUS_VIEW_ALL', '4028808a193230e3011932efdc6401ce', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193514cb5601a3', 'STATUS_EDIT_ALL', '4028808a193230e3011932efdc6401ce', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e29201ad', 'V', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e29201ae', 'E', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e29201af', 'STATUS_VIEW_ALL', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e29201b0', 'STATUS_EDIT_ALL', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2a201b1', 'V', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2a201b2', 'E', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2a201b3', 'STATUS_VIEW_ALL', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2a201b4', 'STATUS_EDIT_ALL', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2a201b5', 'V', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2a201b6', 'E', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2a201b7', 'STATUS_VIEW_ALL', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2a201b8', 'STATUS_EDIT_ALL', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2b201b9', 'V', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2b201ba', 'E', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2b201bb', 'STATUS_VIEW_ALL', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2b201bc', 'STATUS_EDIT_ALL', '4028808a193230e3011932f2098501da', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2b201bd', 'V', '4028808a193230e3011932f2098501da', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2b201be', 'E', '4028808a193230e3011932f2098501da', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2b201bf', 'STATUS_VIEW_ALL', '4028808a193230e3011932f2098501da', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2b201c0', 'STATUS_EDIT_ALL', '4028808a193230e3011932f2098501da', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2c101c1', 'V', '4028808a193230e3011932f2098501da', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2c101c2', 'E', '4028808a193230e3011932f2098501da', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2c101c3', 'STATUS_VIEW_ALL', '4028808a193230e3011932f2098501da', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc701193515e2c101c4', 'STATUS_EDIT_ALL', '4028808a193230e3011932f2098501da', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c6801cc', 'V', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c6801cd', 'E', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c6801ce', 'STATUS_VIEW_ALL', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c7801cf', 'V', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c7801d0', 'E', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c7801d1', 'STATUS_VIEW_ALL', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c7801d2', 'STATUS_EDIT_ALL', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c8701d3', 'V', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c8701d4', 'E', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c8701d5', 'STATUS_VIEW_ALL', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c8701d6', 'STATUS_EDIT_ALL', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c9701d7', 'V', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c9701d8', 'E', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c9701d9', 'STATUS_VIEW_ALL', '4028808a1934fdc7011935002e6b0003', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c9701da', 'V', '4028808a1934fdc7011935002e6b0003', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c9701db', 'E', '4028808a1934fdc7011935002e6b0003', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c9701dc', 'STATUS_VIEW_ALL', '4028808a1934fdc7011935002e6b0003', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164c9701dd', 'STATUS_EDIT_ALL', '4028808a1934fdc7011935002e6b0003', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164ca701de', 'V', '4028808a1934fdc7011935002e6b0003', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164ca701df', 'E', '4028808a1934fdc7011935002e6b0003', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164ca701e0', 'STATUS_VIEW_ALL', '4028808a1934fdc7011935002e6b0003', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1934fdc7011935164ca701e1', 'STATUS_EDIT_ALL', '4028808a1934fdc7011935002e6b0003', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6020023', 'V', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6020024', 'E', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6020025', 'STATUS_VIEW_ALL', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6020026', 'STATUS_EDIT_ALL', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6310027', 'V', '4028808a1947f52201194803e5f30022', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6310028', 'E', '4028808a1947f52201194803e5f30022', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6410029', 'STATUS_VIEW_ALL', '4028808a1947f52201194803e5f30022', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e641002a', 'STATUS_EDIT_ALL', '4028808a1947f52201194803e5f30022', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e641002b', 'V', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e641002c', 'E', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e641002d', 'STATUS_VIEW_ALL', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e641002e', 'STATUS_EDIT_ALL', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e641002f', 'V', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6410030', 'E', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6500031', 'STATUS_VIEW_ALL', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6500032', 'STATUS_EDIT_ALL', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6500033', 'V', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6500034', 'E', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6500035', 'STATUS_VIEW_ALL', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6500036', 'STATUS_EDIT_ALL', '4028808a1947f52201194803e5f30022', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6500037', 'V', '4028808a1947f52201194803e5f30022', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6500038', 'E', '4028808a1947f52201194803e5f30022', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e6500039', 'STATUS_VIEW_ALL', '4028808a1947f52201194803e5f30022', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194803e650003a', 'STATUS_EDIT_ALL', '4028808a1947f52201194803e5f30022', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b51900ae', 'V', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b51900af', 'E', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b51900b0', 'STATUS_VIEW_ALL', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b51900b1', 'STATUS_EDIT_ALL', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b51900b2', 'V', '4028808a1947f52201194818b51900ad', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b52800b3', 'E', '4028808a1947f52201194818b51900ad', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b52800b4', 'STATUS_VIEW_ALL', '4028808a1947f52201194818b51900ad', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b52800b5', 'STATUS_EDIT_ALL', '4028808a1947f52201194818b51900ad', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b52800b6', 'V', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b52800b7', 'E', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b52800b8', 'STATUS_VIEW_ALL', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b52800b9', 'STATUS_EDIT_ALL', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b52800ba', 'V', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b52800bb', 'E', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b52800bc', 'STATUS_VIEW_ALL', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b52800bd', 'STATUS_EDIT_ALL', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b53800be', 'V', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b53800bf', 'E', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b53800c0', 'STATUS_VIEW_ALL', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b53800c1', 'STATUS_EDIT_ALL', '4028808a1947f52201194818b51900ad', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b53800c2', 'V', '4028808a1947f52201194818b51900ad', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b53800c3', 'E', '4028808a1947f52201194818b51900ad', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b53800c4', 'STATUS_VIEW_ALL', '4028808a1947f52201194818b51900ad', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f52201194818b53800c5', 'STATUS_EDIT_ALL', '4028808a1947f52201194818b51900ad', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b654700c7', 'V', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b654700c8', 'E', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b654700c9', 'STATUS_VIEW_ALL', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b654700ca', 'STATUS_EDIT_ALL', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b654700cb', 'V', '4028808a1947f5220119482b654700c6', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b654700cc', 'E', '4028808a1947f5220119482b654700c6', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b654700cd', 'STATUS_VIEW_ALL', '4028808a1947f5220119482b654700c6', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b654700ce', 'STATUS_EDIT_ALL', '4028808a1947f5220119482b654700c6', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b655600cf', 'V', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b655600d0', 'E', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b655600d1', 'STATUS_VIEW_ALL', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b655600d2', 'STATUS_EDIT_ALL', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b655600d3', 'V', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b655600d4', 'E', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b655600d5', 'STATUS_VIEW_ALL', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b655600d6', 'STATUS_EDIT_ALL', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b655600d7', 'V', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b655600d8', 'E', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b656600d9', 'STATUS_VIEW_ALL', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b656600da', 'STATUS_EDIT_ALL', '4028808a1947f5220119482b654700c6', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b656600db', 'V', '4028808a1947f5220119482b654700c6', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b656600dc', 'E', '4028808a1947f5220119482b654700c6', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b656600dd', 'STATUS_VIEW_ALL', '4028808a1947f5220119482b654700c6', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1947f5220119482b656600de', 'STATUS_EDIT_ALL', '4028808a1947f5220119482b654700c6', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b525f902e0', 'V', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802e1', 'E', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802e2', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802e3', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802e4', 'V', '4028808a1951e21b011952b525f902df', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802e5', 'E', '4028808a1951e21b011952b525f902df', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802e6', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b525f902df', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802e7', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b525f902df', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802e8', 'V', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802e9', 'E', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802ea', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802eb', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802ec', 'V', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802ed', 'E', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802ee', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802ef', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802f0', 'V', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5260802f1', 'E', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5261802f2', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5261802f3', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b525f902df', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5261802f4', 'V', '4028808a1951e21b011952b525f902df', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5261802f5', 'E', '4028808a1951e21b011952b525f902df', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5261802f6', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b525f902df', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5261802f7', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b525f902df', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5262702f9', 'V', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5262702fa', 'E', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5262702fb', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5262702fc', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5262702fd', 'V', '4028808a1951e21b011952b5261802f8', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5262702fe', 'E', '4028808a1951e21b011952b5261802f8', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b5262702ff', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b5261802f8', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b526270300', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b5261802f8', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b526270301', 'V', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b526270302', 'E', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b526270303', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b526270304', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b526370305', 'V', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b526370306', 'E', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b526370307', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b526370308', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b526370309', 'V', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b52637030a', 'E', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b52637030b', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b52637030c', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b5261802f8', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b52637030d', 'V', '4028808a1951e21b011952b5261802f8', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b52647030e', 'E', '4028808a1951e21b011952b5261802f8', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b52647030f', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b5261802f8', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1951e21b011952b526470310', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b5261802f8', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed666c0126', 'V', '4028808a193230e3011932efdc6401ce', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed666c0127', 'STATUS_VIEW_ALL', '4028808a193230e3011932efdc6401ce', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed666c0128', 'STATUS_EDIT_ALL', '4028808a193230e3011932efdc6401ce', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed667c012a', 'V', '4028808a193230e3011932f2098501da', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed667c012b', 'E', '4028808a193230e3011932f2098501da', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed667c012c', 'STATUS_VIEW_ALL', '4028808a193230e3011932f2098501da', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed667c012d', 'STATUS_EDIT_ALL', '4028808a193230e3011932f2098501da', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed667c012f', 'V', '4028808a1934fdc7011935002e6b0003', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed668b0130', 'E', '4028808a1934fdc7011935002e6b0003', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed668b0131', 'STATUS_VIEW_ALL', '4028808a1934fdc7011935002e6b0003', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed668b0133', 'V', '4028808a1947f52201194803e5f30022', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed668b0134', 'E', '4028808a1947f52201194803e5f30022', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed668b0135', 'STATUS_VIEW_ALL', '4028808a1947f52201194803e5f30022', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed668b0136', 'STATUS_EDIT_ALL', '4028808a1947f52201194803e5f30022', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed669b0137', 'V', '4028808a1947f52201194818b51900ad', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed669b0138', 'E', '4028808a1947f52201194818b51900ad', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed669b0139', 'STATUS_VIEW_ALL', '4028808a1947f52201194818b51900ad', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed669b013a', 'STATUS_EDIT_ALL', '4028808a1947f52201194818b51900ad', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed669b013b', 'V', '4028808a1947f5220119482b654700c6', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed669b013c', 'E', '4028808a1947f5220119482b654700c6', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed669b013d', 'STATUS_VIEW_ALL', '4028808a1947f5220119482b654700c6', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed66ab013e', 'STATUS_EDIT_ALL', '4028808a1947f5220119482b654700c6', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed66ba0143', 'V', '4028808a1951e21b011952b525f902df', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed66ba0144', 'E', '4028808a1951e21b011952b525f902df', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed66ba0145', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b525f902df', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed66ba0146', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b525f902df', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed66ba0147', 'V', '4028808a1951e21b011952b5261802f8', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed66ba0148', 'E', '4028808a1951e21b011952b5261802f8', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed66ca0149', 'STATUS_VIEW_ALL', '4028808a1951e21b011952b5261802f8', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1952eada011952ed66ca014a', 'STATUS_EDIT_ALL', '4028808a1951e21b011952b5261802f8', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1be900d2', 'V', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1be900d3', 'E', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1be900d4', 'STATUS_VIEW_ALL', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1be900d5', 'STATUS_EDIT_ALL', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1bf900d6', 'V', '4028808a1953022d0119534f1be900d1', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1bf900d7', 'E', '4028808a1953022d0119534f1be900d1', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1bf900d8', 'STATUS_VIEW_ALL', '4028808a1953022d0119534f1be900d1', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1bf900d9', 'STATUS_EDIT_ALL', '4028808a1953022d0119534f1be900d1', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c0900da', 'V', '4028808a1953022d0119534f1be900d1', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c0900db', 'E', '4028808a1953022d0119534f1be900d1', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c0900dc', 'STATUS_VIEW_ALL', '4028808a1953022d0119534f1be900d1', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c0900dd', 'STATUS_EDIT_ALL', '4028808a1953022d0119534f1be900d1', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c0900de', 'V', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c0900df', 'E', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c1800e0', 'STATUS_VIEW_ALL', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c1800e1', 'STATUS_EDIT_ALL', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c1800e2', 'V', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c1800e3', 'E', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c1800e4', 'STATUS_VIEW_ALL', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c1800e5', 'STATUS_EDIT_ALL', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c2800e6', 'V', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c2800e7', 'E', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c2800e8', 'STATUS_VIEW_ALL', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c3700e9', 'STATUS_EDIT_ALL', '4028808a1953022d0119534f1be900d1', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c3700ea', 'V', '4028808a1953022d0119534f1be900d1', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c3700eb', 'E', '4028808a1953022d0119534f1be900d1', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c3700ec', 'STATUS_VIEW_ALL', '4028808a1953022d0119534f1be900d1', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119534f1c3700ed', 'STATUS_EDIT_ALL', '4028808a1953022d0119534f1be900d1', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a40000f0', 'V', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a40000f1', 'E', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a40000f2', 'STATUS_VIEW_ALL', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a40000f3', 'STATUS_EDIT_ALL', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a41000f4', 'V', '4028808a1953022d01195350a40000ef', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a41000f5', 'E', '4028808a1953022d01195350a40000ef', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a41000f6', 'STATUS_VIEW_ALL', '4028808a1953022d01195350a40000ef', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a41000f7', 'STATUS_EDIT_ALL', '4028808a1953022d01195350a40000ef', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a41000f8', 'V', '4028808a1953022d01195350a40000ef', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a42000f9', 'E', '4028808a1953022d01195350a40000ef', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a42000fa', 'STATUS_VIEW_ALL', '4028808a1953022d01195350a40000ef', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a42000fb', 'STATUS_EDIT_ALL', '4028808a1953022d01195350a40000ef', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a42000fc', 'V', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a42000fd', 'E', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a42000fe', 'STATUS_VIEW_ALL', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a42000ff', 'STATUS_EDIT_ALL', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a42f0100', 'V', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a42f0101', 'E', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a42f0102', 'STATUS_VIEW_ALL', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a42f0103', 'STATUS_EDIT_ALL', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a43f0104', 'V', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a43f0105', 'E', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a43f0106', 'STATUS_VIEW_ALL', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a43f0107', 'STATUS_EDIT_ALL', '4028808a1953022d01195350a40000ef', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a43f0108', 'V', '4028808a1953022d01195350a40000ef', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a44e0109', 'E', '4028808a1953022d01195350a40000ef', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a44e010a', 'STATUS_VIEW_ALL', '4028808a1953022d01195350a40000ef', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d01195350a44e010b', 'STATUS_EDIT_ALL', '4028808a1953022d01195350a40000ef', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953651fe60243', 'V', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953651fe60244', 'E', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953651fe60245', 'STATUS_VIEW_ALL', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953651fe60246', 'STATUS_EDIT_ALL', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c1e26c020c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953651ff50247', 'V', '4028808a1953022d011953651fd60242', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953651ff50248', 'E', '4028808a1953022d011953651fd60242', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953651ff50249', 'STATUS_VIEW_ALL', '4028808a1953022d011953651fd60242', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953651ff5024a', 'STATUS_EDIT_ALL', '4028808a1953022d011953651fd60242', '4028808a1952eada011952ed6563009a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953651ff5024b', 'V', '4028808a1953022d011953651fd60242', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953651ff5024c', 'E', '4028808a1953022d011953651fd60242', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953651ff5024d', 'STATUS_VIEW_ALL', '4028808a1953022d011953651fd60242', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953652005024e', 'STATUS_EDIT_ALL', '4028808a1953022d011953651fd60242', '4028808a193230e3011932be7da8010a');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953652005024f', 'V', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119536520050250', 'E', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119536520050251', 'STATUS_VIEW_ALL', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119536520050252', 'STATUS_EDIT_ALL', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c2214a02a4');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119536520150253', 'V', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119536520150254', 'E', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119536520150255', 'STATUS_VIEW_ALL', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119536520150256', 'STATUS_EDIT_ALL', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c25dc6033c');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119536520150257', 'V', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119536520150258', 'E', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d0119536520150259', 'STATUS_VIEW_ALL', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953652024025a', 'STATUS_EDIT_ALL', '4028808a1953022d011953651fd60242', '4028808a1934933b011934c5ea5803ee');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953652024025b', 'V', '4028808a1953022d011953651fd60242', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953652024025c', 'E', '4028808a1953022d011953651fd60242', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953652024025d', 'STATUS_VIEW_ALL', '4028808a1953022d011953651fd60242', '5');

insert into gr_uprstatus (uprstatus_id, uprstatus_type, uprstatus_udf, uprstatus_prstatus) values ('4028808a1953022d011953652024025e', 'STATUS_EDIT_ALL', '4028808a1953022d011953651fd60242', '5');

insert into gr_currentfilter (currentfilter_id, currentfilter_task, currentfilter_user, currentfilter_owner, currentfilter_fil) values ('1', '1', NULL, '1', '1');

insert into gr_currentfilter (currentfilter_id, currentfilter_task, currentfilter_user, currentfilter_owner, currentfilter_fil) values ('4028808a1934fdc70119350a942a0076', '4028808a192e43e801192e4dc70f013a', NULL, '4028808a1934933b011934c2e27703d4', '1');

insert into gr_currentfilter (currentfilter_id, currentfilter_task, currentfilter_user, currentfilter_owner, currentfilter_fil) values ('4028808a1947f52201194999dd320626', '4028808a192e43e801192e4dc70f013a', NULL, '4028808a1934933b011934ca3b3404af', '1');

insert into gr_currentfilter (currentfilter_id, currentfilter_task, currentfilter_user, currentfilter_owner, currentfilter_fil) values ('4028808a1947f5220119499e5fdb062e', '1', NULL, '4028808a192e43e801192e48f4fd0002', '1');

insert into gr_currentfilter (currentfilter_id, currentfilter_task, currentfilter_user, currentfilter_owner, currentfilter_fil) values ('4028808a1947f522011949b5074c07a6', '4028808a192e43e801192e4dc70f013a', NULL, '4028808a1934933b011934c65e400486', '1');

insert into gr_currentfilter (currentfilter_id, currentfilter_task, currentfilter_user, currentfilter_owner, currentfilter_fil) values ('4028808a1953022d0119535502c70128', '4028808a1951e21b011952d0ed0e039e', NULL, '4028808a192e43e801192e48f4fd0002', '1');

insert into gr_currentfilter (currentfilter_id, currentfilter_task, currentfilter_user, currentfilter_owner, currentfilter_fil) values ('4028808a1953022d011953776c590324', '1', NULL, '4028808a1934933b011934c65e400486', '1');

insert into gr_currentfilter (currentfilter_id, currentfilter_task, currentfilter_user, currentfilter_owner, currentfilter_fil) values ('4028808a1953022d01195379f71a032d', '4028808a1951e21b0119526f85440163', NULL, '4028808a1934933b011934c65e400486', '4028808a1953022d011953601937020f');

insert into gr_currentfilter (currentfilter_id, currentfilter_task, currentfilter_user, currentfilter_owner, currentfilter_fil) values ('402880e420f346090120f37cae750002', '4028808a192e43e801192e4dc70f013a', NULL, '1', '1');

insert into gr_acl (acl_id, acl_task, acl_to_user, acl_usersource, acl_owner, acl_override, acl_prstatus) values ('1', '1', NULL, '1', '1', NULL, '5');

insert into gr_acl (acl_id, acl_task, acl_to_user, acl_usersource, acl_owner, acl_override, acl_prstatus) values ('4028808a1934933b011934e808f605a6', '4028808a192e43e801192e4dc70f013a', NULL, '4028808a1934933b011934e808f605a5', '1', NULL, NULL);

insert into gr_acl (acl_id, acl_task, acl_to_user, acl_usersource, acl_owner, acl_override, acl_prstatus) values ('4028808a19512fa50119515ada7b0003', '1', NULL, '4028808a1934933b011934c4afe403ec', '1', NULL, '4028808a1934933b011934c1e26c020c');

insert into gr_acl (acl_id, acl_task, acl_to_user, acl_usersource, acl_owner, acl_override, acl_prstatus) values ('4028808a19512fa50119515aeaa80004', '1', NULL, '4028808a1934933b011934e808f605a5', '1', NULL, '4028808a193230e3011932be7da8010a');

insert into gr_acl (acl_id, acl_task, acl_to_user, acl_usersource, acl_owner, acl_override, acl_prstatus) values ('4028808a19512fa50119515af5660005', '1', NULL, '4028808a1934933b011934e9a46605a7', '1', NULL, '4028808a1934933b011934c2214a02a4');

insert into gr_acl (acl_id, acl_task, acl_to_user, acl_usersource, acl_owner, acl_override, acl_prstatus) values ('4028808a19512fa50119515b3f310006', '1', NULL, '4028808a1934933b011934e9f26705ab', '1', NULL, '4028808a1934933b011934c25dc6033c');

insert into gr_acl (acl_id, acl_task, acl_to_user, acl_usersource, acl_owner, acl_override, acl_prstatus) values ('4028808a19512fa50119515b667f0007', '1', NULL, '4028808a1934933b011934ea005205ad', '1', NULL, '4028808a1934933b011934c5ea5803ee');

insert into gr_acl (acl_id, acl_task, acl_to_user, acl_usersource, acl_owner, acl_override, acl_prstatus) values ('4028808a1953022d01195302c2900003', '1', NULL, '4028808a1953022d01195302c2900002', '1', NULL, '4028808a1952eada011952ed6563009a');

insert into gr_acl (acl_id, acl_task, acl_to_user, acl_usersource, acl_owner, acl_override, acl_prstatus) values ('ff8080812c34f3f1012c34fc18fb00f0', '1', NULL, 'ff8080812c34f3f1012c34fc18ad00ef', '1', NULL, 'ff8080812c34f3f1012c34f7df880002');

insert into gr_registration (registration_id, registration_name, registration_user, registration_prstatus, registration_task, registration_category, registration_child_allowed, registration_expire_days, registration_priv) values ('4028808a1952eada011952ff7ae70171', 'External Customers', '1', '4028808a1952eada011952ed6563009a', '1', NULL, NULL, NULL, 0);

insert into gr_property (property_id, property_name, property_value) values ('297e234c0134e1ea010134e206680007', 'addEditTaskActualBudgetRole', 'done');

insert into gr_property (property_id, property_name, property_value) values ('4028969b00cce74b0100cce767d70063', 'validateMprstatuses', 'done');

insert into gr_property (property_id, property_name, property_value) values ('4028969b00cce74b0100cce7689f0069', 'validateRolesPermissions', 'done');

insert into gr_property (property_id, property_name, property_value) values ('4028969b00cce74b0100cce76da100b9', 'dbVersion', '4.0');

insert into gr_property (property_id, property_name, property_value) values ('ff8080812b58a7f4012b58a819aa0001', 'UPLOADNAMES', 'fixed');

insert into gr_property (property_id, property_name, property_value) values ('ff8080812b58a7f4012b58a81a310002', 'trackstudio.validatePrimaryKey', 'true');

insert into gr_property (property_id, property_name, property_value) values ('4028969b01b901b90101b901d6580001', 'TASK_NUMBER', '131');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('4028808a1951e21b0119524438ab00c0', NULL, 0, '<strong>PRELIMINARY REQUIREMENTS:</strong><br /><br />Here specify what conditions should be executed prior to the beginning of testing.<br /><br /><em><span style="text-decoration: underline;">Example:</span> All tasks for designers and developers on work on page the Login should be finished.</em><br />&nbsp;<br /><strong><br />PLAN OF ACTIONS:</strong><br /><br />Here paint on steps all actions which are necessary for executing for maintenance of necessary testing<br /><br /><em><span style="text-decoration: underline;">Example:</span> Open page the Login.</em><br />&nbsp;<br /><br /><strong>THE CHECK PLAN:</strong><br /><br />Here write that should turn out as a result of performance of a plan of action.<br /><br /><em><span style="text-decoration: underline;">Example:</span> <br />- A window the Login openly<br />- The window name - the Login<br />- The company logo is displayed in the right top corner <br />- On the form of 2 fields - the Name and the Password<br />- The button the Login is accessible<br />- The link has forgotten the password - is accessible<br /></em>&nbsp;<br /><br /><strong>COMMENTS:</strong><br /><br />If there are what comments on performance of tests write them here.<br />');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000bf17874010bf17a48ec0007', '4028969b01b901b90101b914a8670010', 1, 'exists)>
<@headerline name=titles.subtask.PRIORITY value=addval.taskViewFactory.inEmailText(item).getPriority()/>
</#if>
<#if (filter.SUBMITTER && item.getSubmitter()?exists)>
<@headerline name=titles.subtask.SUBMITTER value=addval.taskViewFactory.inEmailText(item).getSubmitter()/>
</#if>
<#if (filter.SUBMITTERSTATUS && item.getSubmitter()?exists)>
<@headerline name=titles.subtask.SUBMITTERSTATUS value=addval.taskViewFactory.inEmailText(item).getSubmitterPrstatuses()/>
</#if>
<#if (filter.HANDLER && (item.getHandlerUserId()?exists || item.getHandlerGroupId()?exists))>
<@headerline name=titles.subtask.HANDLER value=addval.taskViewFactory.inEmailText(item).getHandler()/>
</#if>
<#if (filter.HANDLERSTATUS && (item.getHandlerUserId()?exists || item.getHandlerGroupId()?exists))>
<@headerline name=titles.subtask.HANDLERSTATUS value=addval.taskViewFactory.inEmailText(item).getHandlerPrstatuses()/>
</#if>
<#if (filter.SUBMITDATE && item.getSubmitdate()?exists)>
<@headerline name=titles.subtask.SUBMITDATE value=addval.taskViewFactory.inEmailText(item).getSubmitdate()/>
</#if>
<#if (filter.UPDATEDATE && item.getUpdatedate()?exists)>
<@headerline name=titles.subtask.UPDATEDATE value=addval.taskViewFactory.inEmailText(item).getUpdatedate()/>
</#if>
<#if (filter.CLOSEDATE && item.getClosedate()?exists)>
<@headerline name=titles.subtask.CLOSEDATE value=addval.taskViewFactory.inEmailText(item).getClosedate()/>
</#if>
<#if (filter.DEADLINE && item.getDeadline()?exists)>
<@headerline name=titles.subtask.DEADLINE value=addval.taskViewFactory.inEmailText(item).getDeadline()/>
</#if>
<#if filter.BUDGET>
<@headerline name=titles.subtask.BUDGET value=addval.taskViewFactory.inEmailText(item).getBudget()/>
</#if>
<#if filter.ABUDGET>
<@headerline name=titles.subtask.ABUDGET value=addval.taskViewFactory.inEmailText(item).getActualBudget()/>
</#if>
<#if filter.CHILDCOUNT && item.getTotalChildrenCount()?exists>
<@headerline name=titles.subtask.SUBTASKS value=');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000bf17874010bf17a48f60008', '4028969b01b901b90101b914a8670010', 2, 'item.getTotalChildrenCount().toString()/>
</#if>
<#if filter.MESSAGECOUNT && item.getMessageCount()?exists>
<@headerline name=titles.subtask.MESSAGES value=item.getMessageCount().toString()/>
</#if>
<#if ((titles.subtask.udfCaption?exists) && subudf[item.getId()]?exists)>
<#assign m = 0/>
<#list titles.subtask.udfCaption as y>
<#if (subudf[item.getId()]?size -m > 0)>
<#if (subudf[item.getId()][m]?exists)><@headerline name=y value=addval.taskViewFactory.inEmailTextList(item).getUDFValueView(subudf[item.getId()][m]).getValue(item)/></#if>
</#if>
<#assign m = m +1/>
</#list>
</#if>
<#if filter.DESCRIPTION>
<@line1/>
${item.getTextDescription()?default("")}
</#if>
<#if filter.MESSAGEVIEW>
<#if submsg[item.getId()]?exists>
<@line2/>
${titles.messages.MESSAGES}
<#list submsg[item.getId()] as m>
<@message msg=m/>
</#list>
</#if></#if>
</#macro>
<#-- *********************************-->
<#macro message msg>
<@line2/>
<@headerline name=titles.messages.DATE value=addval.df.parse(msg.getTime())/>
<@headerline name=titles.messages.AUTHOR value=addval.taskViewFactory.inEmailText(task).getUserView(msg.getSubmitter()).getName()/>
<@headerline name=titles.messages.MESSAGETYPE value=msg.getMstatus().getName()/>
<#if msg.getResolution()?exists>
<@headerline name=titles.messages.RESOLUTION value=msg.getResolution().getName()/>
</#if>
<#if msg.getHandlerUserId()?exists || msg.getHandlerGroupId()?exists>
<#if msg.getHandlerUserId()?exists>
    <@headerline name=titles.messages.HANDLER value=addval.taskViewFactory.inEmailText(task).getUserView(msg.getHandlerUser()).getName()/>
</#if>
<#if msg.getHandlerGroupId()?exists>
    <@headerline name=titles.messages.HANDLER value=msg.getHandlerGroup().getEncodeName()/>
</#if>
</#if>
<#if msg.getPriority()?exists>
<@headerline name=titles.messages.PRIORITY value=msg.getPriority().getName()/>
</#if>
<#if msg.getDeadline()?exists>
<@headerline name=titles.messages.DEADLINE value=addval.df.parse(msg.get');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000bf17874010bf17a49000009', '4028969b01b901b90101b914a8670010', 3, 'Deadline())/>
</#if>
<#if msg.getBudget()?exists>
<@headerline name=titles.messages.BUDGET value=addval.hf.format(msg.getBudget().floatValue(), titles.FORMAT)/>
</#if>
<#if msg.getHrs()?exists>
<@headerline name=titles.messages.ABUDGET value=addval.hf.format(msg.getHrs().floatValue(), titles.FORMAT)/>
</#if>
<@line2/>
<#if msg.getTextDescription()?exists>${msg.getTextDescription()}</#if>
<@line2/>
</#macro>
<#macro subtasks>
<#list items as i>
<@line1/>
<@subtasksheader item=i/>
</#list>
</#macro>
<#-- end macros begin message text-->
<#if (addval.availableMstatusList?exists) && !addval.availableMstatusList.isEmpty()>
*#=====#* REPLY BELOW THIS LINE *#=====#*
*!=====!* REPLY ABOVE THIS LINE *!=====!*
</#if>
${addval.filterInfo}
<@line1/>
${addval.fullreason}
<@line1/>
${addval.taskViewFactory.inEmailText(task).getName()}
${addval.tasklink}
<@line1/>
<@headerline name=titles.TASKNUMBER value=addval.taskViewFactory.inEmailText(task).getNumber()/>
<@headerline name=titles.PATH value=addval.taskViewFactory.inEmailText(task).getFullPath()/>
<@headerline name=titles.TASKNAME value=addval.taskViewFactory.inEmailText(task).getName()/>
<#if (task.getShortname()?exists)>
<@headerline name=titles.ALIAS value=task.getShortname()/>
</#if>
<#if (task.getCategory()?exists)>
<@headerline name=titles.CATEGORY value=addval.taskViewFactory.inEmailText(task).getCategory()/>
</#if>
<#if (task.getStatus()?exists) >
<@headerline name=titles.STATUS value=addval.taskViewFactory.inEmailText(task).getStatus()/>
</#if>
<#if (task.getResolution()?exists)>
<@headerline name=titles.RESOLUTION value=addval.taskViewFactory.inEmailText(task).getResolution()/>
</#if>
<#if (task.getPriority()?exists)>
<@headerline name=titles.PRIORITY value=addval.taskViewFactory.inEmailText(task).getPriority()/>
</#if>
<#if (task.getSubmitter()?exists)>
<@headerline name=titles.SUBMITTER value=addval.taskViewFactory.inEmailText(task).getSubmitter()/>
</#if>
<#if task');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000bf17874010bf17a4900000a', '4028969b01b901b90101b914a8670010', 4, '.getHandlerUserId()?exists || task.getHandlerGroupId()?exists>
<@headerline name=titles.HANDLER value=addval.taskViewFactory.inEmailText(task).getHandler()/>
</#if>
<#if (task.getSubmitdate()?exists)>
<@headerline name=titles.SUBMITDATE value=addval.taskViewFactory.inEmailText(task).getSubmitdate()/>
</#if>
<#if (task.getUpdatedate()?exists)>
<@headerline name=titles.UPDATEDATE value=addval.taskViewFactory.inEmailText(task).getUpdatedate()/>
</#if>
<#if (task.getClosedate()?exists)>
<@headerline name=titles.CLOSEDATE value=addval.taskViewFactory.inEmailText(task).getClosedate()/>
</#if>
<#if (task.getDeadline()?exists)>
<@headerline name=titles.DEADLINE value=addval.taskViewFactory.inEmailText(task).getDeadline()/>
</#if>
<#if (task.getBudget()?exists)>
<@headerline name=titles.BUDGET value=addval.taskViewFactory.inEmailText(task).getBudget()/>
</#if>
<#if (task.getActualBudget()?exists && (task.getActualBudget().doubleValue() > 0))>
<@headerline name=titles.ABUDGET value=addval.taskViewFactory.inEmailText(task).getActualBudget()/>
</#if>
<#if (udfs?exists)>
<#list udfs as y>
<#if (y?exists && addval.taskViewFactory.inEmailText(task).getUDFValueView(y).getValue(task).length()>0)>
<@headerline name=y.getCaption() value=addval.taskViewFactory.inEmailText(task).getUDFValueView(y).getValue(task)/>
</#if>
</#list>
</#if>
<#-- ref by task begin -->
<#if (addval.refTaskUdfs?exists && !addval.refTaskUdfs.isEmpty())>
<@line1/>
<@getSpace str1=titles.REFBYTASKS str2=""/>
<#list addval.refTaskUdfs as u>
<#if (u?exists)>
<@headerline name=u.getCaption() value=addval.taskViewFactory.inEmailText(task).getReferencedTasks(addval.refTasks.get(u))/>
</#if>
</#list>
<@line1/>
</#if>
<#-- ref by user begin -->
<#if (addval.refUserUdfs?exists && !addval.refUserUdfs.isEmpty())>
<@line1/>
<@getSpace str1=titles.REFBYUSERS str2=""/>
<#list addval.refUserUdfs as u>
<#if (u?exists)>
<@headerline name=u.getCaption() value=addval.taskViewFactory.inE');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000bf17874010bf17a490a000b', '4028969b01b901b90101b914a8670010', 5, 'mailText(task).getReferencedUsers(addval.refUsers.get(u))/>
</#if>
</#list>
<@line1/>
</#if>
<#-- scm begin -->
<#if (addval.scmReferences?exists && !addval.scmReferences.isEmpty())>
<@commits/>
</#if>
<#if task.getTextDescription()?exists>
<@line1/>
<@getSpace str1=titles.DESCRIPTION str2=""/>
${task.getTextDescription()?default("")}
<@line1/>
</#if>
<#-- subtasks begin -->
<#if items?exists>
<@subtasks/>
</#if>
<#-- attachments begin -->
<#if (atts?exists && atts.size() > 0)>
<@attachments/>
</#if>
<#-- messages begin -->
<#if msglist?exists && !msglist.isEmpty()>
<@line2/>
${titles.messages.MESSAGES}
<#list msglist as m>
<@message msg=m/>
</#list>
<@line2/>
</#if>

<#--BEGIN ATTACHMENT`S MACROS#-->
<#macro attachments>
${titles.attachments.ATTACHMENTS}
<#list atts as i>
<@attline att=i/>
</#list>
</#macro>
<#macro attline att>
<@line1/>
<@headerline name=titles.attachments.FILE value=att.getName()/>
<@headerline name=titles.attachments.SIZE value=att.getSize().toString()/>
<@headerline name=titles.attachments.LAST_MODIFIED value=att.getLastModifiedString()/>
<@headerline name=titles.attachments.OWNER value=att.getUserName()/>
<@headerline name=titles.attachments.DESCRIPTION value=att.getDecodeDescription()/>
</#macro>
<#--END ATTACHMENT`S MACROS#-->

<#--BEGIN COMMITS`S MACROS#-->
<#macro commits>
${titles.REFSCM}
<#list addval.scmReferences as c>
<@commline comm=c/>
</#list>
</#macro>

<#macro commline comm>
<@line1/>
<#if addval.isSVN>
${titles.scm.REVISION}                             ${comm.revision}
</#if>
<@headerline name=titles.scm.AUTHOR value=comm.author/>
<@headerline name=titles.scm.DATE value=comm.date/>
${titles.scm.COMMENT}
${comm.message}
${titles.scm.CHANGED_PATH}
<#list comm.changedPath as path>
<#if addval.isSVN>${path.value} ${path.key}
<#else>${path}</#if>
</#list>
</#macro>
<#--END COMMITS`S MACROS#-->');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce0121d0014', '4028969b01b901b90101b914701d0003', 10, 'MSTATUS"][i].checked == true) {
			 mstatus = el["MSTATUS"][i].value;
			 break;
		     }
	    var priority = el["PRIORITY"] ? (el["PRIORITY"].value == "" ? " " : el["PRIORITY"].value) : " ";
	    var deadline = el["DEADLINE"] ? (el["DEADLINE"].value == "" ? " " : el["DEADLINE"].value) : " ";
	    var budgetHrs = el["BUDGET_HRS"] ? (el["BUDGET_HRS"].value == "" ? "00" : el["BUDGET_HRS"].value) : " ";
	    var budgetMns = el["BUDGET_MNS"] ? (el["BUDGET_MNS"].value == "" ? "00" : el["BUDGET_MNS"].value) : " ";
	    var budgetSec = el["BUDGET_SEC"] ? (el["BUDGET_SEC"].value == "" ? "00" : el["BUDGET_SEC"].value) : " ";
	    var hours = "00";
	    var min = "00";
	    var sec = "00";
	    var handler = " ";
	    var resolution = " ";
	    for (var i = 0; i < el.length; i++) {
		if (el[i].name.indexOf("HRS") == 0 && el[i].disabled == false && el[i].value != "")
		    hours = el[i].value;
		if (el[i].name.indexOf("MNS") == 0 && el[i].disabled == false && el[i].value != "")
		    min = el[i].value;
		if (el[i].name.indexOf("SEC") == 0 && el[i].disabled == false && el[i].value != "")
		    sec = el[i].value;
		if (el[i].name.indexOf("HANDLER") == 0 && el[i].disabled == false)
		    handler = el[i].value;
		if (el[i].name.indexOf("RESOLUTION") == 0 && el[i].disabled == false)
		    resolution = el[i].value;
	    }
	    el["subject"].value = "***DO_NOT_EDIT***F2|" + taskId + "|" + userId + "|" + mstatus + "|" + hours + "|" + min + "|" + sec + "|" + priority + "|" + deadline + "|" + budgetHrs + "|" + budgetMns + "|" + budgetSec + "|" + handler + "|" + resolution + "***DO_NOT_EDIT***";
	}
	</script>

	<form onSubmit="makeSubject(); return true;" name="formX" action="mailto:${addval.mailFrom}">

	  <div class="general">
	<div id="checkJS"><font color="red">Please enable JavaScript to submit this form</font></div>
	<script>
	document.getElementById("checkJS").style.display=''none'';
	</script>
	<table class="general" id="messageType" cellpadding');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce011ff000b', '4028969b01b901b90101b914701d0003', 1, 'text-decoration: none;
		}


	TABLE.udf {
	background-color: transparent !important;
	padding: 0px 0px 0px 0px !important;
	margin:  0px 0px 0px 0px !important;
	border-left: 0px !important;
	border-right: 0px !important;
	border-top: 0px !important;
	}

	TABLE.udf TD{
	vertical-align: top;
	padding: 0px 0px 0px 0px !important;
	margin:  0px 0px 0px 0px !important;
	}

	.iconized {
	padding-left: 4px;
	padding-right: 4px;
	margin-left: 2px;
	margin-right: 2px;
	text-align: center;
	font-family: Tahoma, Verdana;
	font-size: 12px;
	font-weight: bold;
	color: #5E88BF;
	border: 1px #5E88BF outset;
	background: #FFFFFF;
	}
	.controls{
	text-align: right;
	margin-top: 4px;
	margin-bottom: 4px;

	}

	SELECT  {
		font-family: Tahoma, Arial, sans-serif;
		font-size: 11px;
		font-weight: bold;
			vertical-align: top;
		}

	INPUT {
		font-family: Tahoma, Arial, sans-serif;
		font-size: 11px;
		font-weight: bold;
		vertical-align: middle;
	}

	</style>
	</head>
	  <body>
	</#compress>
	    <#nested>
	  </body>
	  </html>
	</#macro>


	<#macro header title>
	<br>
	<div class=cap>${title?html}</div>
	<table class="general" cellpadding=4 cellspacing=1 border=0>
	<#nested>
	</table>
	</#macro>

	<#macro headerline name value>
	<tr>
	<th width="38%">${name?html}</th>
	<td width="62%">${value}</td>
	</tr>
	</#macro>

	<#macro headerline2 name value>
	<tr>
	<th width="38%">${name?html}</th>
	<td width="62%">${value}</td>
	</tr>
	</#macro>

	<#macro description value>
	<tr>
	<td>${value}</td>
	</tr>
	</#macro>

	<#macro th name>
	<th>${name}</th>
	</#macro>

	<#macro wideTh name>
	<th width="100%">${name}</th>
	</#macro>

	<#macro shortTh name>
	<th width="5%">${name}</th>
	</#macro>

	<#macro attheader>
	<tr class="wide">
	    <@th name=titles.attachments.FILE/>
	    <@th name=titles.attachments.SIZE/>
	    <@th name=titles.attachments.LAST_MODIFIED/>
	    <@th name=titles.atta');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce011ff000c', '4028969b01b901b90101b914701d0003', 2, 'chments.OWNER/>
	    <@th name=titles.attachments.DESCRIPTION/>
	</tr>
	</#macro>

	<#macro commitheader>
	<tr class="wide">
	    <#if addval.isSVN>
		<@th name=titles.scm.REVISION/>
	    </#if>
	    <@th name=titles.scm.AUTHOR/>
	    <@th name=titles.scm.DATE/>
	    <@th name=titles.scm.COMMENT/>
	</tr>
	</#macro>


	<#macro subtasksheader>
	<tr class="wide">
	<#if filter.TASKNUMBER>
	<@shortTh name=titles.subtask.TASKNUMBER/>
	</#if>
	<#if filter.FULLPATH>
	<@wideTh name=titles.subtask.FULLPATH/>
	</#if>
	<#if filter.NAME>
	<@wideTh name=titles.subtask.NAME/>
	</#if>
	<#if filter.ALIAS>
	<@shortTh name=titles.subtask.ALIAS/>
	</#if>
	<#if filter.CATEGORY>
	<@shortTh name=titles.subtask.CATEGORY/>
	</#if>
	<#if filter.STATUS>
	<@shortTh name=titles.subtask.STATUS/>
	</#if>
	<#if filter.RESOLUTION>
	<@shortTh name=titles.subtask.RESOLUTION/>
	</#if>
	<#if filter.PRIORITY>
	<@shortTh name=titles.subtask.PRIORITY/>
	</#if>
	<#if filter.SUBMITTER>
	<@shortTh name=titles.subtask.SUBMITTER/>
	</#if>
	<#if filter.SUBMITTERSTATUS>
	<@shortTh name=titles.subtask.SUBMITTERSTATUS/>
	</#if>
	<#if filter.HANDLER>
	<@shortTh name=titles.subtask.HANDLER/>
	</#if>
	<#if filter.HANDLERSTATUS>
	<@shortTh name=titles.subtask.HANDLERSTATUS/>
	</#if>
	<#if filter.SUBMITDATE>
	<@shortTh name=titles.subtask.SUBMITDATE/>
	</#if>

	<#if filter.UPDATEDATE>
	<@shortTh name=titles.subtask.UPDATEDATE/>
	</#if>

	<#if filter.CLOSEDATE>
	<@shortTh name=titles.subtask.CLOSEDATE/>
	</#if>

	<#if filter.DEADLINE>
	<@shortTh name=titles.subtask.DEADLINE/>
	</#if>

	<#if filter.BUDGET>
	<@shortTh name=titles.subtask.BUDGET/>
	</#if>
	<#if filter.ABUDGET>
	<@shortTh name=titles.subtask.ABUDGET/>
	</#if>

	<#if filter.CHILDCOUNT>
	<@shortTh name=titles.subtask.SUBTASKS/>
	</#if>

	<#if filter.MESSAGECOUNT>
	<@shortTh name=titles.subtask.MESSAGES/>
	</#if>
	<#if (titles.subtask.udfCaption?exists)>
	<#list titles.subtas');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce01209000d', '4028969b01b901b90101b914701d0003', 3, 'k.udfCaption as y>
	<@shortTh name=y/>
	</#list>
	</#if>
	</tr>
	</#macro>

	<#macro td value>
	<td>${value}</td>
	</#macro>


	<#macro attline att>
	<#compress>
	<tr>
	    <@td value=attlink[att.getId()]/>
	    <@td value=att.getSize()/>
	    <@td value=att.getLastModifiedString()/>
	    <@td value=att.getUserName()/>

	<#if att.getDescription()?exists>
	    <@td value=att.getDescription()/>
	<#else>
	    <@td value=" "/>
	</#if>

	</tr>
	</#compress>
	</#macro>

	<#macro commitline comm>
	<#compress>
	<tr class="line0">
	    <#if addval.isSVN>
		<@td value=comm.revision/>
	    </#if>
	    <@td value=comm.author/>
	    <@td value=comm.date/>
	    <td>
	    <table class="general" cellpadding="4" cellspacing="1" border=0>
		<tr>
		    <td>${comm.message}</td>
		</tr>
	    <tr class="wide">
		<@th name=titles.scm.CHANGED_PATH/>
	    </tr>
	    <tr><td>
		<table border=0>
		<#list comm.changedPath as path>
		<tr>
		    <#if addval.isSVN>
			<td>${path.value} ${path.key}</td>
		    <#else>
			<td>${path}</td>
		    </#if>
		</tr>
		</#list>
		</table>
	    </td></tr>
	    </table>
	    </td>

	</#compress>
	</#macro>

	<#macro subline item>
	<#compress>
	<tr>

	<#if filter.TASKNUMBER>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getNumber()/>
	</#if>
	<#if filter.FULLPATH>
	<@td value=addval.taskViewFactory.inEmailHTMLList(item).getRelativePath(task.getId())/>
	</#if>
	<#if filter.NAME>
	<@td value=addval.taskViewFactory.inEmailHTMLList(item).getName()/>
	</#if>
	<#if filter.ALIAS>
	<#if item.getShortname()?exists>
	<@td value=item.getShortname()/>
	<#else>
	<@td value=" "/>
	</#if>
	</#if>
	<#if filter.CATEGORY>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getCategory()/>
	</#if>

	<#if filter.STATUS>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getStatus()/>
	</#if>

	<#if filter.RESOLUTION>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getResol');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce01213000e', '4028969b01b901b90101b914701d0003', 4, 'ution()/>
	</#if>

	<#if filter.PRIORITY>
	<#if item.getPriority()?exists>
	<@td value=addval.taskViewFactory.inEmailHTMLList(item).getPriority()/>
	<#else>
	<@td value=" "/>
	</#if>
	</#if>

	<#if filter.SUBMITTER>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getSubmitter()/>
	</#if>

	<#if filter.SUBMITTERSTATUS>
	<#if item.getSubmitter()?exists>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getSubmitterPrstatuses()/>
	<#else>
	<@td value=" "/>
	</#if>
	</#if>

	<#if filter.HANDLER>
	<#if item.getHandlerUserId()?exists || item.getHandlerGroupId()?exists>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getHandler()/>
	<#else>
	<@td value=" "/>
	</#if>
	</#if>

	<#if filter.HANDLERSTATUS>
	<#if item.getHandlerUserId()?exists || item.getHandlerGroupId()?exists>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getHandlerPrstatuses()/>
	<#else>
	<@td value=" "/>
	</#if>
	</#if>


	<#if filter.SUBMITDATE>
	<#if item.getSubmitdate()?exists>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getSubmitdate()/>
	<#else>
	<@td value=" "/>
	</#if>
	</#if>

	<#if filter.UPDATEDATE>
	<#if item.getUpdatedate()?exists>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getUpdatedate()/>
	<#else>
	<@td value=" "/>
	</#if>
	</#if>

	<#if filter.DEADLINE>
	<#if item.getDeadline()?exists>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getDeadline()/>
	<#else>
	<@td value=" "/>
	</#if>
	</#if>

	<#if filter.CLOSEDATE>
	<#if item.getClosedate()?exists>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getClosedate()/>
	<#else>
	<@td value=" "/>
	</#if>
	</#if>

	<#if filter.BUDGET>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getBudget()/>
	</#if>

	<#if filter.ABUDGET>
	<@td value=addval.taskViewFactory.inEmailHTML(item).getActualBudget()/>
	</#if>

	<#if filter.CHILDCOUNT>
	<@td value=item.getTotalChildrenCount()/>
	</#if>

	<#if filter.MESSAGECOUNT>
	<@td');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce01213000f', '4028969b01b901b90101b914701d0003', 5, 'value=item.getMessageCount()/>
	</#if>

	<#if (subudf[item.getId()]?exists)>
	<#list subudf[item.getId()] as y>
	<#if (y?exists)><@td value=addval.taskViewFactory.inEmailHTMLList(item).getUDFValueView(y).getValue(item)/><#else><@td value=" "/></#if>
	</#list>
	</#if>
	</#compress>

	<#if (filter.DESCRIPTION && item.getDescription()?exists)>
	<tr><td colspan="40">
	${addval.taskViewFactory.inEmailHTML(item).getDescription()}
	</td></tr>
	</#if>

	<#if filter.MESSAGEVIEW>
	<#if submsg[item.getId()]?exists>
	<tr><td colspan="40">
	<#list submsg[item.getId()] as m>
	<@message msg=m/>
	</#list>
	</td></tr>
	</#if>
	</#if>
	</tr>
	</#macro>

	<#macro message msg>
	<#compress>
	<div class="general">
	<table class="general" cellpadding="4" cellspacing="1" border=0>
	<#if msg.getTime()?exists>
	<COLGROUP>
	<COL width="20%">
	<COL width="80%">
	</COLGROUP>
	<tr>
	<@th name=titles.messages.DATE/>
	<td>${addval.df.parse(msg.getTime())}</td>
	</tr>
	</#if>
	<#if msg.getSubmitter()?exists>
	<tr>
	<@th name=titles.messages.AUTHOR/>
	<td>${addval.taskViewFactory.inEmailHTML(task).getUserView(msg.getSubmitter()).getName()}</td>
	</tr>
	</#if>
	<#if msg.getMstatus()?exists>
	<tr>
	<@th name=titles.messages.MESSAGETYPE/>
	<td>
	${msg.getMstatus().getName()}
	<#if (msg.getMstatus().getDescription()?exists && !(msg.getMstatus().getDescription()==""))>
	  |  ${msg.getMstatus().getDescription()}
	</#if>
	</td>
	</tr>
	</#if>
	<#if msg.getResolution()?exists>
	<tr>
	<@th name=titles.messages.RESOLUTION/>
	<@td value=msg.getResolution().getName()/>
	</tr>
	</#if>
	<#if msg.getHandlerUserId()?exists || msg.getHandlerGroupId()?exists>
	<tr>
	<@th name=titles.messages.HANDLER/>
	<#if msg.getHandlerUserId()?exists>
	    <@td value=addval.taskViewFactory.inEmailHTML(task).getUserView(msg.getHandlerUser()).getName()/>
	</#if>
	<#if msg.getHandlerGroupId()?exists>
	    <@td value=msg.getHandlerGroup().getEncodeName()/>
	</#i');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce012130010', '4028969b01b901b90101b914701d0003', 6, 'f>
	</tr>
	</#if>
	<#if msg.getPriority()?exists>
	<tr>
	<@th name=titles.messages.PRIORITY/>
	<@td value=msg.getPriority().getName()/>
	</tr>
	</#if>
	<#if msg.getDeadline()?exists>
	<tr>
	<@th name=titles.messages.DEADLINE/>
	<@td value=addval.df.parse(msg.getDeadline())/>
	</tr>
	</#if>
	<#if msg.getBudget()?exists>
	<tr>
	<@th name=titles.messages.BUDGET/>
	<@td value=addval.hf.format(msg.getBudget().floatValue(), titles.FORMAT)/>
	</tr>
	</#if>
	<#if msg.getHrs()?exists>
	<tr>
	<@th name=titles.messages.ABUDGET/>
	<@td value=addval.hf.format(msg.getHrs().floatValue(), titles.FORMAT)/>
	</tr>
	</#if>
	<tr>
	<td colspan=2>
	</#compress>
	<#if msg.getWikiParsedDescription()?exists>
	${msg.getWikiParsedDescription()}
	<#else>

	</#if>
	</td>
	</tr>
	</table>
	</div>
	</#macro>

	<#macro subtasks>
	<#compress>
	<table class="general" cellpadding=4 cellspacing=1 border=0>
	<@subtasksheader/>
	</#compress>
	<#list items as i>
	<@subline item=i/>
	</#list>

	</table>
	</#macro>

	<#macro attachments>
	<#compress>
	<table class="general" cellpadding="4" cellspacing="1">
	<@attheader/>
	</#compress>
	<#list atts as i>
	<@attline att=i/>
	</#list>
	</table>
	</#macro>

	<#macro commits>
	<#compress>
	<table class="general" cellpadding="4" cellspacing="1" border=0>
	<@commitheader/>
	</#compress>
	<#list addval.scmReferences as c>
	<@commitline comm=c/>
	</#list>
	</table>
	</#macro>


	</#compress>
	<@page title=task.getName() charset=addval.charset>
	<#if (addval.availableMstatusList?exists) && !addval.availableMstatusList.isEmpty()>

	<table>
	<tr><td width=100%><b>*#=====#* REPLY BELOW THIS LINE *#=====#*</b>                          </td></tr>
	<tr><td width=100%><b>*!=====!* REPLY ABOVE THIS LINE *!=====!*</b>                          </td></tr>
	</table>

	</#if>
	<i>${addval.filterInfo?html}</i>
	<br>
	${addval.fullreason?html}

	<@header title=titles.TASKINFO>
	<#compress>');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce0121d0011', '4028969b01b901b90101b914701d0003', 7, '<@headerline2 name=titles.TASKNUMBER value=addval.taskViewFactory.inEmailHTML(task).getNumber()/>
	<@headerline2 name=titles.PATH value=addval.taskViewFactory.inEmailHTML(task).getFullPath()/>
	<@headerline2 name=titles.TASKNAME value=addval.taskViewFactory.inEmailHTML(task).getName()/>
	<#if (task.getShortname()?exists)  && Null.isNotNull(task.getShortname())>
	<@headerline name=titles.ALIAS value=task.getShortname()/>
	</#if>
	<#if (task.getCategory()?exists)  && Null.isNotNull(task.getCategory())>
	<@headerline name=titles.CATEGORY value=addval.taskViewFactory.inEmailHTML(task).getCategory()/>
	</#if>
	<#if (task.getStatus()?exists)  && Null.isNotNull(task.getStatus())>
	<@headerline name=titles.STATUS value=addval.taskViewFactory.inEmailHTML(task).getStatus()/>
	</#if>
	<#if (task.getResolution()?exists) && Null.isNotNull(task.getResolution())>
	<@headerline name=titles.RESOLUTION value=addval.taskViewFactory.inEmailHTML(task).getResolution()/>
	</#if>
	<#if (task.getPriority()?exists) && Null.isNotNull(task.getPriority())>
	<@headerline name=titles.PRIORITY value=addval.taskViewFactory.inEmailHTML(task).getPriority()/>
	</#if>
	<#if (task.getSubmitter()?exists) && Null.isNotNull(task.getSubmitter())>
	<@headerline2 name=titles.SUBMITTER value=addval.taskViewFactory.inEmailHTML(task).getSubmitter()/>
	</#if>
	<#if ((task.getHandlerUserId()?exists) && Null.isNotNull(task.getHandlerUser())) || ((task.getHandlerGroupId()?exists) && Null.isNotNull(task.getHandlerGroup()))>
	<@headerline2 name=titles.HANDLER value=addval.taskViewFactory.inEmailHTML(task).getHandler()/>
	</#if>
	<#if (task.getSubmitdate()?exists) && Null.isNotNull(task.getSubmitdate())>
	<@headerline2 name=titles.SUBMITDATE value=addval.taskViewFactory.inEmailHTML(task).getSubmitdate()/>
	</#if>
	<#if (task.getUpdatedate()?exists) && Null.isNotNull(task.getUpdatedate())>
	<@headerline2 name=titles.UPDATEDATE value=addval.taskViewFactory.inEmailHTML(task).getUpdatedate()/>');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce0121d0012', '4028969b01b901b90101b914701d0003', 8, '</#if>
	<#if (task.getClosedate()?exists) && Null.isNotNull(task.getClosedate())>
	<@headerline2 name=titles.CLOSEDATE value=addval.taskViewFactory.inEmailHTML(task).getClosedate()/>
	</#if>
	<#if (task.getDeadline()?exists) && Null.isNotNull(task.getDeadline())>
	<@headerline2 name=titles.DEADLINE value=addval.taskViewFactory.inEmailHTML(task).getDeadline()/>
	</#if>
	<#if (task.getBudget()?exists) && Null.isNotNull(task.getBudget())>
	<@headerline2 name=titles.BUDGET value=addval.taskViewFactory.inEmailHTML(task).getBudget()/>
	</#if>
	<#if (task.getActualBudget()?exists) && Null.isNotNull(task.getActualBudget()) && (task.getActualBudget().doubleValue() > 0)>
	<@headerline2 name=titles.ABUDGET value=addval.taskViewFactory.inEmailHTML(task).getActualBudget()/>
	</#if>
	</#compress>
	<#compress>
	<#if (udfs?exists)>
	<#list udfs as y>
	<#assign udfValue = addval.taskViewFactory.inEmailHTML(task).getUDFValueView(y).getValue(task)>
	<#if  Null.isNotNull(udfValue)>
	<@headerline2 name=y.getCaption() value=udfValue/>
	</#if>
	</#list>
	</#if>
	</#compress>
	</@header>

	<#if (addval.refTaskUdfs?exists && !addval.refTaskUdfs.isEmpty())>
	<@header title=titles.REFBYTASKS>
	<#list addval.refTaskUdfs as u>
	<#assign refValue = addval.taskViewFactory.inEmailHTML(task).getReferencedTasks(addval.refTasks.get(u))>
	<#if  Null.isNotNull(refValue)>
	<@headerline2 name=u.getCaption() value=refValue/>
	</#if>
	</#list>
	</@header>
	</#if>

	<#if (addval.refUserUdfs?exists && !addval.refUserUdfs.isEmpty())>
	<@header title=titles.REFBYUSERS>
	<#list addval.refUserUdfs as u>
	<#assign refValue = addval.taskViewFactory.inEmailHTML(task).getReferencedUsers(addval.refUsers.get(u))>
	<#if  Null.isNotNull(refValue)>
	<@headerline2 name=u.getCaption() value=refValue/>
	</#if>
	</#list>
	</@header>
	</#if>

	<#if (addval.scmReferences?exists && !addval.scmReferences.isEmpty())>
	<br>
	<@header title=titles.REFSCM>
	<tr><td>
	<@commits/>');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce0121d0013', '4028969b01b901b90101b914701d0003', 9, '</td></tr>
	</@header>
	</#if>

	<#if (task.getWikiParsedDescription()?exists && task.getWikiParsedDescription().length()>0)>
	<@header title=titles.DESCRIPTION>
	<@description value=task.getWikiParsedDescription()/>
	</@header>
	</#if>

	<#if items?exists>
	<br>

	<@header title=titles.subtask.SUBTASKS>
	<tr><td>
	<@subtasks/>
	</td></tr>
	</@header>
	</#if>

	<#if atts?exists && !atts.isEmpty()>
	<br>

	<@header title=titles.attachments.ATTACHMENTS>
	<tr><td>
	<@attachments/>
	</td></tr>
	</@header>
	</#if>

	<#if msglist?exists  && !msglist.isEmpty()>
	<@header title=titles.messages.MESSAGES?html>
	<tr><td>
	<#list msglist as m>
	<@message msg=m/>
	</#list>
	</td></tr>
	</@header>
	</#if>
	<#compress>
	<#if !(addval.availableMstatusList?exists) || addval.availableMstatusList.isEmpty()>
	<div class="title">${titles.messages.NO_MESSAGES?html}</div>
	<#else>

	<div id="submitForm">
	<script type="text/javascript">

	function disableControls(sender) {
	 var el = document.forms["formX"].elements;
	 for (var i = 0; i < el.length; i++)
		if (el[i].className != "NOT_DISABLED") {
		    el[i].disabled = true;
		    if (el[i].type == "text")
			 el[i].style.backgroundColor = "#d7d7d7";
		}
	    var nde1 = sender.parentNode.parentNode.getElementsByTagName("input");
	    var nde2 = sender.parentNode.parentNode.getElementsByTagName("select");
	    for (var i = 0; i < nde1.length; i++) {
		 nde1[i].disabled = false;
		 if (nde1[i].type == "text")
		     nde1[i].style.backgroundColor = "white";
	     }
	     for (var i = 0; i < nde2.length; i++) {
		 nde2[i].disabled = false;
	     }
	 }

	 function makeSubject() {
	     var el = document.forms["formX"].elements;
	     var taskId = el["task_id"].value;
	     var userId = el["user_id"].value;
	     var mstatus = " ";
	     if(!el["MSTATUS"].length)
		mstatus = el["MSTATUS"].value
	    else
		for (var i = 0; i < el["MSTATUS"].length; i++)
		     if (el["');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce012270015', '4028969b01b901b90101b914701d0003', 11, '=4 cellspacing=1 border=0>
	<tr class="wide">
	<th>${titles.messages.MESSAGETYPE?html}</th>
	<th>${titles.DESCRIPTION?html}</th>
	<th>${titles.messages.NEXTSTATE?html}</th>
	<th>${titles.messages.HANDLER?html}</th>
	<th>${titles.messages.RESOLUTION?html}</th>
	</tr>
	<#assign firstCase = true>
	<#assign contains = false>
	<#list addval.availableMstatusList as prs0>
	<#assign contains = prs0.isDefault()>
	<#if contains><#break></#if>
	</#list>
	<#list addval.availableMstatusList as prs0>
	<#assign ischecked = prs0.isDefault() || (!contains && firstCase)>
	 <tr>
		<td>
			<#if ischecked>
			<input  checked id="${prs0.getId()}" class="NOT_DISABLED" type="radio" name="MSTATUS" onClick="disableControls(this);" onKeyDown="disableControls(this);" value="${prs0.getId()}">${prs0.getName()}
			</td>
			<#else>
			<input id="${prs0.getId()}" class="NOT_DISABLED" type="radio" name="MSTATUS" onClick="disableControls(this);" onKeyDown="disableControls(this);" value="${prs0.getId()}">${prs0.getName()}
			</td>
			</#if>
			<td>
			<#if (prs0.getDescription()?exists && !(prs0.getDescription()==""))>
			    ${prs0.getDescription()}
			</#if>
			</td>
			<td>
				<#if prs0.getNextState()?exists>
				${prs0.getNextState().getName()?html}
				</#if>
			</td>
			<td>

			<#if prs0.getHandlerList().isEmpty() && prs0.getHandlerGroupList().isEmpty()>
				${titles.messages.NO_HANDLERS?html}
			<#else>
			    <#if addval.permissions.editTaskHandler?exists>
				<#if ischecked>
				    <select name="HANDLER${prs0.getId()}">
				<#else>
				    <select name="HANDLER${prs0.getId()}" disabled>
				</#if>
				<#if ((!task.getHandlerUserId()?exists && !task.getHandlerGroupId()?exists) || !task.getCategory().isHandlerRequired())>
				    <option value=" ">${titles.messages.NOBODY?html}</option>
				</#if>
				<#if !prs0.getHandlerGroupList().isEmpty()>
				<optgroup label="${titles.messages.MSG_USER_STATUSES?html}">
				<#list prs0.getHandlerGroupLi');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce012270016', '4028969b01b901b90101b914701d0003', 12, 'st() as prs1>
				<#if task.getHandlerGroupId()?exists && prs1.getId()==task.getHandlerGroupId()>
					<option value="PR_${prs1.getId()}" selected>${prs1.getName()?html}</option>
				<#else>
					<option value="PR_${prs1.getId()}">${prs1.getName()?html}</option>
				</#if>
				</#list>
				</optgroup>
				</#if>
				<#if !prs0.getHandlerList().isEmpty()>
				<optgroup label="${titles.messages.MSG_LIST_USERS?html}">
				<#list prs0.getHandlerList() as prs1>
				<#if task.getHandlerUserId()?exists && prs1.getId()==task.getHandlerUserId()>
					<option value="${prs1.getId()}" selected>${prs1.getName()?html}</option>
				<#else>
					<option value="${prs1.getId()}">${prs1.getName()?html}</option>
				</#if>
				</#list>
				</optgroup>
				</#if>
			    <#else>
				<#if task.getHandlerGroupId()?exists>
				    task.getHandlerGroup().getEncodeName();
				<#else>
				    ${addval.taskViewFactory.inEmailHTML(task).getUserView(task.getHandlerUser()).getName()}
				</#if>
			    </#if>
			</#if>


			</td>
			<td>
			<#if !prs0.getResolutionList().isEmpty()>
				<#if ischecked>
				<select name="RESOLUTION${prs0.getId()}" style="width: 100px">
				<#else>
				<select name="RESOLUTION${prs0.getId()}" style="width: 100px" disabled>
				</#if>
			<#assign selected = false>
			<#assign hasDefault = false>
			<#list prs0.getResolutionList() as prs2>
				<#if !hasDefault>
				<#assign hasDefault = prs2.isDefault()>
				</#if>
				<#assign selected = (!prs2_has_next && !hasDefault) || prs2.isDefault()>
				<#if selected>
				<option selected value="${prs2.getId()}">${prs2.getName()}</option>
				<#else>
				<option value="${prs2.getId()}">${prs2.getName()}</option>
				</#if>
			</#list>
				</select>
			</#if>
			</td>
	</tr>
	<#assign firstCase = false>
	</#list>
	</table>
	</div>
		<#if ((addval.permissions.editTaskPriority?exists && !addval.priorityList.isEmpty()) || (addval.permissions.editTaskDeadline?exists) || (addv');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce012270017', '4028969b01b901b90101b914701d0003', 13, 'al.permissions.editTaskBudget?exists)  || (addval.permissions.editTaskActualBudget?exists))>
		<div class="general">
	    <table class="general" id="messageScheduler" cellpadding=4 cellspacing=1 border=0>
		    <#if addval.permissions.editTaskPriority?exists && !addval.priorityList.isEmpty()>
			<tr>
			<th>${titles.messages.PRIORITY?html}</th>
			<td>
				    <select class="NOT_DISABLED" name="PRIORITY">
				    <#list addval.priorityList?sort as p>
					    <#if (task.getPriority()?exists && p.getId()==task.getPriority().getId()) || (!task.getPriority()?exists && p.getId()==addval.defaultPriority)>
						<option value="${p.getId()}"  selected>${p.getName()}</option>
					<#else>
						<option value="${p.getId()}">${p.getName()}</option>
						</#if>
					</#list>
			       </select>
			    </td>
			</tr>
		    </#if>
		    <#if addval.permissions.editTaskDeadline?exists>
			<tr>
			<th>${titles.messages.DEADLINE?html}</th>
			<td>
				<input class="NOT_DISABLED" type="text" name="DEADLINE" value="${addval.taskViewFactory.inEmailText(task).getDeadline()}">
			    </td>
			</tr>
		    </#if>
		    <#if addval.permissions.editTaskBudget?exists>
			<tr>
			<th>${titles.messages.BUDGET?html}</th>
			<td>
			    <input class="NOT_DISABLED" type="text" name="BUDGET_HRS" value="${addval.taskViewFactory.inEmailText(task).getBudgetHours()}" size="6" maxlength="6"> ${titles.messages.HH?html} <input class="NOT_DISABLED" type="text" name="BUDGET_MNS" value="${addval.taskViewFactory.inEmailText(task).getBudgetMinutes()}" size=2 maxlength=2> ${titles.messages.SS?html} <input class="NOT_DISABLED" type="text" name="BUDGET_SEC" value="${addval.taskViewFactory.inEmailText(task).getBudgetSeconds()}" size=2 maxlength=2> ${titles.messages.MM?html}
			</td>
			</tr>
		    </#if>
		    <#if addval.permissions.editTaskActualBudget?exists>
			<tr>
			<th>${titles.messages.ABUDGET?html}</th>
			<td>
			    <input class="NOT_DISABLED" type="text" style=');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('402893000d9cda86010d9ce012270018', '4028969b01b901b90101b914701d0003', 14, '"font-size: 8pt; background-color: white" name="HRS" value="" size=3 maxlength=4>${titles.messages.HH?html}<input class="NOT_DISABLED" type="text" name="MNS"  style="font-size: 8pt; background-color: white" value="" size=2 maxlength=2>${titles.messages.MM?html}<input class="NOT_DISABLED" type="text" name="SEC"  style="font-size: 8pt; background-color: white" value="" size=2 maxlength=2>${titles.messages.SS?html}
			</td>
			</tr>
		    </#if>
		</table>
		</div>

	  </#if>
	<div class="controls">
	<input class="NOT_DISABLED" type="hidden" name="task_id" value="${task.getId()}">
	<input class="NOT_DISABLED" type="hidden" name="user_id" value="${user.getId()}">
	<input class="NOT_DISABLED" type="hidden" name="subject">
	<input class="NOT_DISABLED" type="submit" value="${titles.messages.CREATE}" class="iconized" name="ADDMESSAGE">
	</div>
	</form>
	</#if>
	</#compress>
	</@page>');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('4028969b01b901b90101b914701d0003', NULL, 0, '<#compress>
	<#macro page title charset>
	<#compress>
	  <html>
	  <head>
	    <title>Message - ${title?html}</title>
	    <meta http-equiv="content-type" content="text/html; charset=${charset}"/>

	<style type="text/css">
	DIV.title {
		font-family: Tahoma, Arial, Helvetica, sans-serif;
		font-size: 12px;
		font-weight: bold;
		text-align: left;
	}

	DIV.general{
	width: 100%;
	padding-top: 4px;
	padding-bottom: 4px;
	}


	TABLE.general
		{
		font-family: Tahoma, Arial, Helvetica, sans-serif;
		color: #000000;
		font-size: 12px;
			background-color: #84B0C7;
			width: 100%;
		}

	div.CAP{
		font-family: Tahoma, Arial, Helvetica, sans-serif;
		color: #000000;
		font-size: 11px;
		text-align: left;
		font-weight: normal;
		background-color: #B3CDDA;
		border-top: #84B0C7 1px solid;
		border-left: #84B0C7 1px solid;
		border-right: #84B0C7 1px solid;
		padding-left: 6px;
		padding-top: 2px;
		padding-bottom: 2px;

	}

	TABLE.general TH
		{
		color: #000000;
		text-align: right;
		font-weight: bold;
		background-color: #EBF1F2;
		vertical-align: middle;
		font-size: 11px;
		padding-right: 12px;

		}

	TABLE.general TR.WIDE TH
		{
		color: #000000;
		text-align: center;
			font-weight: bold;
		background-color:  #DAE5EB;
			vertical-align: middle;
		font-size: 11px;
		}

	TABLE.general TD
		{
			background-color: #F8F8F8;
			vertical-align: center;
			text-align: left;
		font-size: 11px;
		}



	TABLE.general TR.selected TD
		{
		background-color: #DAE5EB;
		font-weight: bold;
		font-size: 11px;
		}

	TABLE.general TR.selected TH
		{
		background-color: #A0C1D3;
		font-weight: bold;
			font-size: 11px;
		}

	TABLE.general TR.new TD
		{
				background-color: #C7D9E3;
				font-size: 11px;
		}

	TABLE.general TR.new TH
		{
				background-color: #B3CDDA;
			font-size: 11px;
					}


	TABLE.general A
		{
		color: #000000;
		font-weight: bold;
			font-size: 11px;');

insert into gr_longtext (longtext_id, longtext_reference, longtext_order, longtext_value) values ('4028969b01b901b90101b914a8670010', NULL, 0, '<#macro line1>
========================================================================
</#macro>
<#-- *********************************-->
<#macro line2>
------------------------------------------------------------------------
</#macro>
<#-- *********************************-->
<#macro getSpace str1 str2>
<#assign out=str1>
<#assign space="                                     ">
<#assign firsttime=1>
<#assign i=0>
<#list [" "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "] as x>
<#if i gt (36-str1?length)>
<#break>
</#if>
<#assign out=out+x>
<#assign i=i+1>
</#list>
<#list str2?split("\\n") as s>
<#if firsttime = 1>
${out+s}
<#assign firsttime=2>
<#else>
${space+ s}
</#if>
</#list>
</#macro>

<#-- *********************************-->
<#macro headerline name value>
<@getSpace str1=name str2=value/>
</#macro>
<#-- *********************************-->
<#macro subtasksheader item>
<#if filter.TASKNUMBER>
<@headerline name=titles.subtask.TASKNUMBER value=addval.taskViewFactory.inEmailText(item).getNumber()/>
</#if>
<#if filter.FULLPATH>
<@headerline name=titles.subtask.FULLPATH value=addval.taskViewFactory.inEmailText(item).getRelativePath(task.getId())/>
</#if>
<#if filter.NAME>
<@headerline name=titles.subtask.NAME value=addval.taskViewFactory.inEmailText(item).getName()/>
</#if>
<#if (filter.ALIAS && item.getShortname()?exists)>
<@headerline name=titles.subtask.ALIAS value=item.getShortname()/>
</#if>
<#if filter.CATEGORY>
<@headerline name=titles.subtask.CATEGORY value=item.getCategory().getName()/>
</#if>
<#if filter.STATUS>
<@headerline name=titles.subtask.STATUS value=item.getStatus().getName()/>
</#if>
<#if (filter.RESOLUTION && item.getResolution()?exists)>
<@headerline name=titles.subtask.RESOLUTION value=addval.taskViewFactory.inEmailText(item).getResolution()/>
</#if>
<#if (filter.PRIORITY && item.getPriority()?');

ALTER TABLE gr_category       ADD   CONSTRAINT fcategory_1              FOREIGN KEY (category_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_category       ADD   CONSTRAINT fcategory_2              FOREIGN KEY (category_workflow)                             REFERENCES gr_workflow (workflow_id) ;

ALTER TABLE gr_category       ADD   CONSTRAINT fcategory_3              FOREIGN KEY (category_cr_trigger)                             REFERENCES gr_trigger (trigger_id) ;

ALTER TABLE gr_category       ADD   CONSTRAINT fcategory_4              FOREIGN KEY (category_upd_trigger)                             REFERENCES gr_trigger (trigger_id) ;

ALTER TABLE gr_category       ADD   CONSTRAINT fcategory_5              FOREIGN KEY (category_template)                             REFERENCES gr_longtext (longtext_id) ;

ALTER TABLE gr_bookmark       ADD   CONSTRAINT fbookmark_1              FOREIGN KEY (bookmark_owner)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_bookmark       ADD   CONSTRAINT fbookmark_2              FOREIGN KEY (bookmark_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_bookmark       ADD   CONSTRAINT fbookmark_3              FOREIGN KEY (bookmark_user)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_bookmark       ADD   CONSTRAINT fbookmark_4              FOREIGN KEY (bookmark_filter)                             REFERENCES gr_filter (filter_id)  ;

ALTER TABLE gr_filter       ADD   CONSTRAINT ffilter_1              FOREIGN KEY (filter_owner)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_filter       ADD   CONSTRAINT ffilter_2              FOREIGN KEY (filter_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_filter       ADD   CONSTRAINT ffilter_4              FOREIGN KEY (filter_user)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_fvalue       ADD   CONSTRAINT ffvalue_1              FOREIGN KEY (fvalue_filter)                             REFERENCES gr_filter (filter_id)  ;

ALTER TABLE gr_message       ADD   CONSTRAINT fmessage_1              FOREIGN KEY (message_resolution)                             REFERENCES gr_resolution (resolution_id)  ;

ALTER TABLE gr_message       ADD   CONSTRAINT fmessage_2              FOREIGN KEY (message_handler)                             REFERENCES gr_usersource (usersource_id)  ;

ALTER TABLE gr_message       ADD   CONSTRAINT fmessage_3              FOREIGN KEY (message_mstatus)                             REFERENCES gr_mstatus (mstatus_id)  ;

ALTER TABLE gr_message       ADD   CONSTRAINT fmessage_4              FOREIGN KEY (message_submitter)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_message       ADD   CONSTRAINT fmessage_6              FOREIGN KEY (message_priority)                             REFERENCES gr_priority (priority_id)  ;

ALTER TABLE gr_message       ADD   CONSTRAINT fmessage_7              FOREIGN KEY (message_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_mprstatus       ADD   CONSTRAINT fmprstatus_1              FOREIGN KEY (mprstatus_prstatus)                             REFERENCES gr_prstatus (prstatus_id)  ;

ALTER TABLE gr_mprstatus       ADD   CONSTRAINT fmprstatus_2              FOREIGN KEY (mprstatus_mstatus)                             REFERENCES gr_mstatus (mstatus_id)  ;

ALTER TABLE gr_mstatus       ADD   CONSTRAINT fmstatus_1              FOREIGN KEY (mstatus_workflow)                             REFERENCES gr_workflow (workflow_id)  ;

ALTER TABLE gr_mstatus       ADD   CONSTRAINT fmstatus_2              FOREIGN KEY (mstatus_trigger)                             REFERENCES gr_trigger (trigger_id)  ;

ALTER TABLE gr_prstatus       ADD   CONSTRAINT fprstatus_1              FOREIGN KEY (prstatus_user)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_report       ADD   CONSTRAINT freport_1              FOREIGN KEY (report_owner)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_report       ADD   CONSTRAINT freport_2              FOREIGN KEY (report_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_report       ADD   CONSTRAINT freport_3              FOREIGN KEY (report_filter)                             REFERENCES gr_filter (filter_id)  ;

ALTER TABLE gr_resolution       ADD   CONSTRAINT fresolution_1              FOREIGN KEY (resolution_mstatus)                             REFERENCES gr_mstatus (mstatus_id)  ;

ALTER TABLE gr_rolestatus       ADD   CONSTRAINT frolestatus_2              FOREIGN KEY (rolestatus_prstatus)                             REFERENCES gr_prstatus (prstatus_id)  ;

ALTER TABLE gr_status       ADD   CONSTRAINT fstatus_1              FOREIGN KEY (status_workflow)                             REFERENCES gr_workflow (workflow_id)  ;

ALTER TABLE gr_subscription       ADD   CONSTRAINT fsubscription_1              FOREIGN KEY (subscription_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_subscription       ADD   CONSTRAINT fsubscription_2              FOREIGN KEY (subscription_filter)                             REFERENCES gr_filter (filter_id)  ;

ALTER TABLE gr_subscription       ADD   CONSTRAINT fsubscription_3              FOREIGN KEY (subscription_user)                             REFERENCES gr_usersource (usersource_id)  ;

ALTER TABLE gr_task       ADD   CONSTRAINT ftask_1              FOREIGN KEY (task_submitter)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_task       ADD   CONSTRAINT ftask_2              FOREIGN KEY (task_handler)                             REFERENCES gr_usersource (usersource_id)  ;

ALTER TABLE gr_task       ADD   CONSTRAINT ftask_3              FOREIGN KEY (task_resolution)                             REFERENCES gr_resolution (resolution_id)  ;

ALTER TABLE gr_task       ADD   CONSTRAINT ftask_4              FOREIGN KEY (task_status)                             REFERENCES gr_status (status_id)  ;

ALTER TABLE gr_task       ADD   CONSTRAINT ftask_5              FOREIGN KEY (task_category)                             REFERENCES gr_category (category_id)  ;

ALTER TABLE gr_task       ADD   CONSTRAINT ftask_6              FOREIGN KEY (task_parent)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_task       ADD   CONSTRAINT ftask_7              FOREIGN KEY (task_priority)                             REFERENCES gr_priority (priority_id)  ;

ALTER TABLE gr_transition       ADD   CONSTRAINT ftransition_1              FOREIGN KEY (transition_mstatus)                             REFERENCES gr_mstatus (mstatus_id)  ;

ALTER TABLE gr_transition       ADD   CONSTRAINT ftransition_3              FOREIGN KEY (transition_finish)                             REFERENCES gr_status (status_id)  ;

ALTER TABLE gr_transition       ADD   CONSTRAINT ftransition_4              FOREIGN KEY (transition_start)                             REFERENCES gr_status (status_id)  ;

ALTER TABLE gr_udf       ADD   CONSTRAINT fudf_1              FOREIGN KEY (udf_udfsource)                             REFERENCES gr_udfsource (udfsource_id)  ;

ALTER TABLE gr_udf       ADD   CONSTRAINT fudf_4              FOREIGN KEY (udf_initialtask)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_udf       ADD   CONSTRAINT fudf_5              FOREIGN KEY (udf_initialuser)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_udflist       ADD   CONSTRAINT fudflist_1              FOREIGN KEY (udflist_udf)                             REFERENCES gr_udf (udf_id)  ;

ALTER TABLE gr_udfsource       ADD   CONSTRAINT fudfsource_1              FOREIGN KEY (udfsource_user)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_udfsource       ADD   CONSTRAINT fudfsource_2              FOREIGN KEY (udfsource_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_udfsource       ADD   CONSTRAINT fudfsource_3              FOREIGN KEY (udfsource_workflow)                             REFERENCES gr_workflow (workflow_id)  ;

ALTER TABLE gr_udfval       ADD   CONSTRAINT fudfval_1              FOREIGN KEY (udfval_udflist)                             REFERENCES gr_udflist (udflist_id)  ;

ALTER TABLE gr_udfval       ADD   CONSTRAINT fudfval_2              FOREIGN KEY (udfval_udfsource)                             REFERENCES gr_udfsource (udfsource_id)  ;

ALTER TABLE gr_udfval       ADD   CONSTRAINT fudfval_3              FOREIGN KEY (udfval_udf)                             REFERENCES gr_udf (udf_id)  ;

ALTER TABLE gr_udfval       ADD   CONSTRAINT fudfval_4              FOREIGN KEY (udfval_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_udfval       ADD   CONSTRAINT fudfval_5              FOREIGN KEY (udfval_user)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_udfval       ADD   CONSTRAINT fudfval_6              FOREIGN KEY (udfval_longtext)                             REFERENCES gr_longtext (longtext_id)  ;

ALTER TABLE gr_user       ADD   CONSTRAINT fuser_1              FOREIGN KEY (user_prstatus)                             REFERENCES gr_prstatus (prstatus_id)  ;

ALTER TABLE gr_user       ADD   CONSTRAINT fuser_2              FOREIGN KEY (user_manager)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_user       ADD   CONSTRAINT fuser_4              FOREIGN KEY (user_default_project)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_usersource       ADD   CONSTRAINT fusersource_1              FOREIGN KEY (usersource_user)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_usersource       ADD   CONSTRAINT fusersource_2              FOREIGN KEY (usersource_prstatus)                             REFERENCES gr_prstatus (prstatus_id)  ;

ALTER TABLE gr_workflow       ADD   CONSTRAINT fworkflow_1              FOREIGN KEY (workflow_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_priority       ADD   CONSTRAINT fpriority_1              FOREIGN KEY (priority_workflow)                             REFERENCES gr_workflow (workflow_id)  ;

ALTER TABLE gr_catrelation       ADD   CONSTRAINT fcatrelation_1              FOREIGN KEY (catrelation_category)                             REFERENCES gr_category (category_id)  ;

ALTER TABLE gr_catrelation       ADD   CONSTRAINT fcatrelation_2              FOREIGN KEY (catrelation_child)                             REFERENCES gr_category (category_id)  ;

ALTER TABLE gr_cprstatus       ADD   CONSTRAINT fcprstatus_1              FOREIGN KEY (cprstatus_category)                             REFERENCES gr_category (category_id)  ;

ALTER TABLE gr_cprstatus       ADD   CONSTRAINT fcprstatus_2              FOREIGN KEY (cprstatus_prstatus)                             REFERENCES gr_prstatus (prstatus_id)  ;

ALTER TABLE gr_uprstatus       ADD   CONSTRAINT fuprstatus_1              FOREIGN KEY (uprstatus_udf)                             REFERENCES gr_udf (udf_id)  ;

ALTER TABLE gr_uprstatus       ADD   CONSTRAINT fuprstatus_2              FOREIGN KEY (uprstatus_prstatus)                             REFERENCES gr_prstatus (prstatus_id)  ;

ALTER TABLE gr_umstatus       ADD   CONSTRAINT fumstatus_1              FOREIGN KEY (umstatus_udf)                             REFERENCES gr_udf (udf_id)  ;

ALTER TABLE gr_umstatus       ADD   CONSTRAINT fumstatus_2              FOREIGN KEY (umstatus_mstatus)                             REFERENCES gr_mstatus (mstatus_id)  ;

ALTER TABLE gr_notification       ADD   CONSTRAINT fnotification_1              FOREIGN KEY (notification_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_notification       ADD   CONSTRAINT fnotification_2              FOREIGN KEY (notification_filter)                             REFERENCES gr_filter (filter_id)  ;

ALTER TABLE gr_notification       ADD   CONSTRAINT fnotification_3              FOREIGN KEY (notification_user)                             REFERENCES gr_usersource (usersource_id)  ;

ALTER TABLE gr_mailimport       ADD   CONSTRAINT fmailimport_2              FOREIGN KEY (mailimport_category)                             REFERENCES gr_category (category_id)  ;

ALTER TABLE gr_mailimport       ADD   CONSTRAINT fmailimport_4              FOREIGN KEY (mailimport_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_mailimport       ADD   CONSTRAINT fmailimport_5              FOREIGN KEY (mailimport_owner)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_mailimport       ADD   CONSTRAINT fmailimport_6              FOREIGN KEY (mailimport_mstatus)                             REFERENCES gr_mstatus (mstatus_id)  ;

ALTER TABLE gr_task       ADD   CONSTRAINT ftask_8              FOREIGN KEY (task_longtext)                             REFERENCES gr_longtext (longtext_id)  ;

ALTER TABLE gr_message       ADD   CONSTRAINT fmessage_5              FOREIGN KEY (message_longtext)                             REFERENCES gr_longtext (longtext_id)  ;

ALTER TABLE gr_longtext       ADD   CONSTRAINT flongtext_1              FOREIGN KEY (longtext_reference)                             REFERENCES gr_longtext (longtext_id)  ;

ALTER TABLE gr_acl       ADD   CONSTRAINT facl_1              FOREIGN KEY (acl_usersource)                             REFERENCES gr_usersource (usersource_id)  ;

ALTER TABLE gr_acl       ADD   CONSTRAINT facl_2              FOREIGN KEY (acl_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_acl       ADD   CONSTRAINT facl_3              FOREIGN KEY (acl_prstatus)                             REFERENCES gr_prstatus (prstatus_id)  ;

ALTER TABLE gr_acl       ADD   CONSTRAINT facl_4              FOREIGN KEY (acl_owner)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_acl       ADD   CONSTRAINT facl_5              FOREIGN KEY (acl_to_user)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_currentfilter       ADD   CONSTRAINT fcurrentfilter_1              FOREIGN KEY (currentfilter_user)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_currentfilter       ADD   CONSTRAINT fcurrentfilter_2              FOREIGN KEY (currentfilter_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_currentfilter       ADD   CONSTRAINT fcurrentfilter_3              FOREIGN KEY (currentfilter_fil)                             REFERENCES gr_filter (filter_id)  ;

ALTER TABLE gr_registration       ADD   CONSTRAINT fregistration_1              FOREIGN KEY (registration_user)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_registration       ADD   CONSTRAINT fregistration_2              FOREIGN KEY (registration_prstatus)                             REFERENCES gr_prstatus (prstatus_id)  ;

ALTER TABLE gr_registration       ADD   CONSTRAINT fregistration_3              FOREIGN KEY (registration_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_registration       ADD   CONSTRAINT fregistration_4              FOREIGN KEY (registration_category)                             REFERENCES gr_category (category_id)  ;

ALTER TABLE gr_attachment       ADD   CONSTRAINT fattachment_1              FOREIGN KEY (attachment_task)                             REFERENCES gr_task (task_id)  ;

ALTER TABLE gr_attachment       ADD   CONSTRAINT fattachment_2              FOREIGN KEY (attachment_user)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_attachment       ADD   CONSTRAINT fattachment_3              FOREIGN KEY (attachment_message)                             REFERENCES gr_message (message_id)  ;

ALTER TABLE gr_template       ADD   CONSTRAINT ftemplate_1              FOREIGN KEY (template_owner)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_template       ADD   CONSTRAINT ftemplate_2              FOREIGN KEY (template_user)                             REFERENCES gr_user (user_id)  ;

ALTER TABLE gr_template       ADD   CONSTRAINT ftemplate_3              FOREIGN KEY (template_task)                             REFERENCES gr_task (task_id)  ;

create index ifk_bookmark_owner on gr_bookmark (bookmark_owner);

create index ifk_bookmark_task on gr_bookmark (bookmark_task);

create index ifk_bookmark_user on gr_bookmark (bookmark_user);

create index ifk_bookmark_filter on gr_bookmark (bookmark_filter);

create index ifk_udf_initialtask on gr_udf (udf_initialtask);

create index ifk_udf_initialuser on gr_udf (udf_initialuser);

create index ifk_udfval_longtext on gr_udfval (udfval_longtext);

create index ifk_task_longtext on gr_task (task_longtext);

create index ifk_message_longtext on gr_message (message_longtext);

create index ifk_attachment_user on gr_attachment (attachment_user);

create index ifk_attachment_message on gr_attachment (attachment_message);

create index ifk_template_task on gr_template (template_task);

