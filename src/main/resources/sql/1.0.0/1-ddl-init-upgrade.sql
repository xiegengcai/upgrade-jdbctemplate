CREATE TABLE `sys_module_version` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `module_name` VARCHAR(50) NOT NULL COMMENT '模块名称',
  `version` VARCHAR(30) NOT NULL COMMENT '模块当前版本号',
  `updated_on` TIMESTAMP NOT NULL COMMENT '更新于',
  INDEX `id` (`id`),
  UNIQUE INDEX `module_name` (`module_name`)
) COMMENT='模块版本表';

CREATE TABLE `sys_module_script` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `module_name` VARCHAR(50) NOT NULL COMMENT '模块名称',
  `ddl_script` TEXT NULL COMMENT '对应版本ddl脚本内容',
  `dml_script` TEXT NULL COMMENT '对应版本dml脚本',
  `version` VARCHAR(30) NOT NULL COMMENT '模块当前版本号',
  `updated_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新于',
  INDEX `id` (`id`),
  INDEX `idx_sys_module_script_name_version` (`module_name`, `version`)
)COMMENT='模块版本脚本表';
