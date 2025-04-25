# V3.7.31 update
ALTER TABLE `jimu_report_db_field`
    ADD COLUMN `field_name_physics` varchar(200) NULL COMMENT '物理字段名（文件数据集使用，存的是excel的字段标题）' AFTER `field_name`;

CREATE TABLE `jimu_report_icon_lib` (
                                        `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
                                        `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片名称',
                                        `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片类型',
                                        `image_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '图片地址',
                                        `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建人',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '更新人',
                                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                        `tenant_id` int(11) DEFAULT NULL COMMENT '租户id',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='积木图库表';

INSERT INTO `jimu_dict`(`id`, `dict_name`, `dict_code`, `description`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `type`, `tenant_id`) VALUES ('1047797573274468352', '系统图库', 'gallery', '', 0, 'admin', '2025-02-07 19:00:19', NULL, NULL, 0, '1');
INSERT INTO `jimu_dict_item`(`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES ('1047797624512086016', '1047797573274468352', '常规', 'common', NULL, 1, 1, 'admin', '2025-02-07 19:00:31', NULL, NULL);
INSERT INTO `jimu_dict_item`(`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES ('1047797669877678080', '1047797573274468352', '指向', 'point', NULL, 1, 1, 'admin', '2025-02-07 19:00:42', '15931993294', '2025-02-07 19:01:11');
INSERT INTO `jimu_dict_item`(`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES ('1047797751893098496', '1047797573274468352', '专业', 'major', NULL, 1, 1, 'admin', '2025-02-07 19:01:01', NULL, NULL);
# V3.7.32 update
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`) VALUES ('1876220177009315842', '1473927410093187073', '表单设计页面查询', NULL, NULL, 0, NULL, NULL, 2, 'drag:design:getTotalData', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-06 18:52:03', NULL, NULL, 0, 0, '1', 0);
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`) VALUES ('1892117657990971393', '1456165677820301314', '1876220177009315842', NULL, '2025-02-19 15:42:58', '0:0:0:0:0:0:0:1');

# TestNet V2.1
# 增加资产负责人字段
ALTER TABLE `jeecg-boot`.`asset_company`
    ADD COLUMN `asset_manager`    varchar(1000) NULL COMMENT '资产负责人' AFTER `asset_label`,
    ADD COLUMN `asset_department` varchar(1000) NULL COMMENT '资产负责部门' AFTER `asset_manager`;
ALTER TABLE `jeecg-boot`.`asset_api`
    ADD COLUMN `asset_manager`    varchar(1000) NULL COMMENT '资产负责人' AFTER `asset_label`,
    ADD COLUMN `asset_department` varchar(1000) NULL COMMENT '资产负责部门' AFTER `asset_manager`;
ALTER TABLE `jeecg-boot`.`asset_port`
    ADD COLUMN `asset_manager`    varchar(1000) NULL COMMENT '资产负责人' AFTER `asset_label`,
    ADD COLUMN `asset_department` varchar(1000) NULL COMMENT '资产负责部门' AFTER `asset_manager`;
ALTER TABLE `jeecg-boot`.`asset_web`
    ADD COLUMN `asset_manager`    varchar(1000) NULL COMMENT '资产负责人' AFTER `asset_label`,
    ADD COLUMN `asset_department` varchar(1000) NULL COMMENT '资产负责部门' AFTER `asset_manager`;
ALTER TABLE `jeecg-boot`.`asset_domain`
    ADD COLUMN `asset_manager`    varchar(1000) NULL COMMENT '资产负责人' AFTER `asset_label`,
    ADD COLUMN `asset_department` varchar(1000) NULL COMMENT '资产负责部门' AFTER `asset_manager`;
ALTER TABLE `jeecg-boot`.`asset_ip`
    ADD COLUMN `asset_manager`    varchar(1000) NULL COMMENT '资产负责人' AFTER `asset_label`,
    ADD COLUMN `asset_department` varchar(1000) NULL COMMENT '资产负责部门' AFTER `asset_manager`;
ALTER TABLE `jeecg-boot`.`asset_sub_domain`
    ADD COLUMN `asset_manager`    varchar(1000) NULL COMMENT '资产负责人' AFTER `asset_label`,
    ADD COLUMN `asset_department` varchar(1000) NULL COMMENT '资产负责部门' AFTER `asset_manager`;
ALTER TABLE `jeecg-boot`.`asset_vul`
    ADD COLUMN `asset_manager`    varchar(1000) NULL COMMENT '资产负责人' AFTER `asset_label`,
    ADD COLUMN `asset_department` varchar(1000) NULL COMMENT '资产负责部门' AFTER `asset_manager`;
# 删除web资产根目录的限制
ALTER TABLE `jeecg-boot`.`asset_web`
    DROP INDEX `idx_web`;
INSERT INTO `jeecg-boot`.`sys_table_white_list` (`id`, `table_name`, `field_name`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES ('1910151746874429441', 'sys_tenant', 'name,id', '1', 'admin', '2025-03-14 00:00:00', NULL, NULL);
