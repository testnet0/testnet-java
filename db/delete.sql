DELETE FROM project;
DELETE FROM asset_domain;
DELETE FROM asset_sub_domain;
DELETE FROM asset_port;
DELETE FROM asset_ip;
DELETE FROM asset_ip_sub_domain;
DELETE FROM asset_web;
DELETE FROM asset_api;
DELETE FROM asset_api_tree;
DELETE FROM asset_company;
DELETE FROM asset_label;
DELETE FROM asset_vul;

DELETE FROM lite_flow_task;
DELETE FROM lite_flow_sub_task;
DELETE FROM lite_flow_task_asset;
DELETE FROM client;
DELETE FROM client_config;
DELETE FROM client_tools;


DELETE FROM sys_log;
DELETE FROM sys_data_log;
DELETE FROM sys_announcement;
DELETE FROM sys_announcement_send;
DELETE FROM sys_quartz_job;

DELETE FROM qrtz_cron_triggers;
DELETE FROM qrtz_triggers;
DELETE FROM qrtz_scheduler_state;
DELETE FROM qrtz_job_details;
DELETE FROM qrtz_locks;

DELETE FROM message_config;

UPDATE lite_flow_script SET create_time = '2024-05-01 00:00:00';
UPDATE lite_flow_script SET update_time = '2024-05-01 00:00:00';

UPDATE lite_flow_chain SET create_time = '2024-05-01 00:00:00';
UPDATE lite_flow_chain SET update_time = '2024-05-01 00:00:00';

UPDATE onl_cgform_head SET create_time = '2024-05-01 00:00:00';
UPDATE onl_cgform_head SET update_time = '2024-05-01 00:00:00';

UPDATE asset_search_engine SET engine_token = 'xxxx';
UPDATE asset_search_engine SET create_time = '2024-05-01 00:00:00';
UPDATE asset_search_engine SET update_time = '2024-05-01 00:00:00';

UPDATE install_flag set installed = 0;
UPDATE sys_user SET password = '607e156cb552b660',salt='hKDEZr25' where id= '1794992342079651842';
UPDATE sys_user SET password = 'cb362cfeefbf3d8d',salt='RCGTeGiH' where id= 'e9ca23d68d884d4ebb19d07889727dae';
