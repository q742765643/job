/*
 Navicat MySQL Data Transfer

 Source Server         : 10.37.129.4
 Source Server Type    : MySQL
 Source Server Version : 50633
 Source Host           : 10.37.129.4
 Source Database       : htht_job

 Target Server Type    : MySQL
 Target Server Version : 50633
 File Encoding         : utf-8

 Date: 08/15/2018 12:03:53 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `htht_cluster_schedule_dict_code`
-- ----------------------------
DROP TABLE IF EXISTS `htht_cluster_schedule_dict_code`;
CREATE TABLE `htht_cluster_schedule_dict_code` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `create_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `dict_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `dict_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `is_tree` int(11) DEFAULT NULL,
  `memo` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `parent_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sort_order` int(11) DEFAULT NULL,
  `update_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of htht_cluster_schedule_dict_code
-- ----------------------------
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('08665127b0b54bcd80f292ebd0377035', '2018-08-23 04:28:44', '2018-08-23 04:28:44', '0', null, 'testHandler', 'testHandler', null, '', '18e554df22ab4bf4a7d4616ea357545e', null, null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('17b938271d444dd4a4fac3833344e031', '2018-08-24 17:51:30', '2018-08-24 17:51:30', '0', null, 'systemTitleManger', '标题管理', null, '', '0', null, null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('18e554df22ab4bf4a7d4616ea357545e', '2018-07-17 16:16:59', '2018-07-17 16:16:59', '0', null, 'ordinaryHandler', '模型标识', null, '11', '0', '11', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('1f4f297f242043488c1f8e800dad8754', '2018-08-24 17:31:19', '2018-08-24 17:31:19', '0', null, '0/10 * * * * ?', '每10s一次', null, '', 'd9f06d7664de49bfbc49818bb0c5d28d', null, null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('2bcea153447747f09e1fa7b063523a76', '2018-08-20 05:49:27', '2018-08-20 05:49:27', '0', null, 'D:\\soa_deploy\\EXE', 'windows算法执行根路径', null, '11', '66c3fa403c244fc994cdde6b1881e121', '11', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('55efd332d07a4534bfea10a2c27793a9', '2018-07-13 15:57:07', '2018-07-13 15:57:07', '0', null, '0 0 15 * * ?', '每天15点', null, '11', 'd9f06d7664de49bfbc49818bb0c5d28d', '1', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('66c3fa403c244fc994cdde6b1881e121', '2018-08-20 05:46:55', '2018-08-20 05:46:55', '0', null, 'windows电脑配置', 'windows电脑配置', null, '11', '0', '11', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('80ee06c56bbd42af945f22d8698eab97', '2018-08-20 09:00:12', '2018-08-20 09:00:47', '1', null, '0 0 0/2 * * ?', '0 0 0/2 * * ?', null, '11', 'd9f06d7664de49bfbc49818bb0c5d28d', '1', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('9c0072b6cfd6486cbe699d2f38ef29c7', '2018-07-27 11:07:23', '2018-07-27 11:07:23', '0', null, 'redlineHandler', '红线测试执行器', null, '11', '18e554df22ab4bf4a7d4616ea357545e', '11', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('aee7a7a1a57446cda779a31c9c33e1c3', '2018-08-20 05:51:50', '2018-08-20 05:54:52', '2', null, 'D:\\soa_share', 'windows共享目录根路径', null, '11', '66c3fa403c244fc994cdde6b1881e121', '11', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('b9f5894187dc46a18a3d057f88f9864f', '2018-08-24 17:51:46', '2018-08-24 17:52:08', '1', null, 'bigTitle', '并行支撑平台', null, '1', '17b938271d444dd4a4fac3833344e031', '1', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('c5e5b1ba9ae9422784040d0f5a37948b', '2018-08-17 15:26:20', '2018-08-17 15:26:20', '0', null, 'createProjHandler', 'createProjHandler', null, '11', '18e554df22ab4bf4a7d4616ea357545e', '11', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('c60a914cee8c4e9ba68633394fd1f351', '2018-08-20 02:06:21', '2018-08-20 02:06:21', '0', null, 'preDataHandler', 'preDataHandler', null, '11', '18e554df22ab4bf4a7d4616ea357545e', '11', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('d9f06d7664de49bfbc49818bb0c5d28d', '2018-07-13 15:56:23', '2018-07-13 15:56:23', '0', null, 'key', '执行策略', null, '11', '0', '11', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('ec2465e6d44947afa1c3180451badd62', '2018-08-23 04:30:01', '2018-08-23 04:30:16', '1', null, '0 */1 * * * ? ', '每分钟执行一次', null, '1', 'd9f06d7664de49bfbc49818bb0c5d28d', '1', null);
INSERT INTO `htht_cluster_schedule_dict_code` VALUES ('fbc843881eab477ab1e8023bfabe2eb4', '2018-07-17 16:19:14', '2018-07-17 16:19:14', '0', null, 'ordinaryHandler', 'ordinaryHandler', null, '111', '18e554df22ab4bf4a7d4616ea357545e', '11', null);

-- ----------------------------
--  Table structure for `htht_cluster_schedule_resource`
-- ----------------------------
DROP TABLE IF EXISTS `htht_cluster_schedule_role_resource`;
DROP TABLE IF EXISTS `htht_cluster_schedule_user_role`;

DROP TABLE IF EXISTS `htht_cluster_schedule_resource`;

CREATE TABLE `htht_cluster_schedule_resource` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `icon` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `is_hide` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `parent_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `source_key` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `source_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `htht_cluster_schedule_resource`
-- ----------------------------

INSERT INTO `htht_cluster_schedule_resource` VALUES ('0763e20f2266458a86c7e23802353ad5', '2018-01-29 22:05:13', '2018-01-29 22:05:13', '0', null, null, null, null, '编辑', 'd0fb7a830f584eb4a0d21eb3dcba4e51', '2', 'jobgroupedit', null, '2'), ('09307f7051b54b9b9b0da6484fc7e51e', '2018-01-29 22:38:58', '2018-01-29 22:38:58', '0', null, null, null, null, '删除', 'e43a49b8159a4cdaa266ae1d325b4fd5', '2', 'productfileinfodel', null, '2'), ('09b9d85b7edc490393b00b56605ac60b', '2018-01-29 17:04:12', '2018-01-29 17:04:12', '0', null, null, null, null, '删除', '64ec1407fc8b451080755ca0d7ce2bce', '6', 'taskdel', null, '2'), ('1097a405f4a84fd1a094ea5a17c5f879', '2018-01-29 22:05:36', '2018-01-29 22:05:36', '0', null, null, null, null, '删除', 'd0fb7a830f584eb4a0d21eb3dcba4e51', '3', 'jobgroupdel', null, '2'), ('21018e8040364ffdb4e6e958a727267c', '2018-01-29 23:05:15', '2018-01-29 23:05:15', '0', null, null, null, null, '删除', 'f9642f1afca94fa7b4bfbcc12213efcb', '3', 'userdel', null, '2'), ('241eb76df1a5435aa801ea5a18e468ae', '2018-01-28 21:04:38', '2018-01-29 10:14:30', '1', null, null, null, null, '任务管理', '0', '1', 'jobinfo', null, '0'), ('2580ab307d7546e4aac48378edd6de5d', '2018-01-29 22:48:27', '2018-01-29 22:55:01', '1', null, null, null, null, '删除', '3497eb339e4c4bd6ba050ef516ba5c22', '3', 'roledel', null, '2'), ('276b81c784bb4071a5b0647634f55eae', '2018-01-29 23:05:39', '2018-01-29 23:05:39', '0', null, null, null, null, '分配角色', '835ba57b8e644bd0bb30116faf553093', '4', 'grantrole', null, '2'), ('2de1b461b7e84181a6feaa86f7a5b24a', '2018-01-29 10:22:35', '2018-01-29 10:22:35', '0', null, null, null, null, '系统管理', '0', null, 'system', null, '0'), ('3497eb339e4c4bd6ba050ef516ba5c22', '2018-01-29 10:23:19', '2018-01-29 10:23:25', '1', null, null, null, null, '角色管理', '2de1b461b7e84181a6feaa86f7a5b24a', null, 'role', null, '1'), ('396e8540a0d243b7af2afc4f51ff5ffe', '2018-01-29 22:13:12', '2018-01-29 22:13:12', '0', null, null, null, null, '添加', 'e49f4ea690664ffd8b1e763ec14c7a6c', '1', 'productmodeladd', null, '2'), ('425d586fc48346a7bc1d6ac11c0c3327', '2018-01-29 22:13:59', '2018-01-29 22:13:59', '0', null, null, null, null, '删除', 'e49f4ea690664ffd8b1e763ec14c7a6c', '3', 'productmodeldel', null, '2'), ('45fa2c46ddc54fe5b8189fabaafe2b46', '2018-01-29 22:13:40', '2018-01-29 22:13:40', '0', null, null, null, null, '编辑', 'e49f4ea690664ffd8b1e763ec14c7a6c', '2', 'productmodeledit', null, '2'), ('552c6f405a3c4065a2f20fd0e4234818', '2018-01-29 22:04:24', '2018-01-29 22:04:24', '0', null, null, null, null, '添加', 'd0fb7a830f584eb4a0d21eb3dcba4e51', '1', 'jobgroupadd', null, '2'), ('585c5d6b454a414292d99c6236366467', '2018-01-29 10:17:51', '2018-01-29 10:17:59', '1', null, null, null, null, '产品', '0', '3', 'product', null, '1'), ('61d9b5be40784bcc8267f9ae960c96c7', '2018-01-29 10:16:12', '2018-01-29 10:16:19', '1', null, null, null, null, '调度日志', '0', '1', 'joblog', null, '1'), ('64ec1407fc8b451080755ca0d7ce2bce', '2018-01-29 10:15:14', '2018-01-29 10:15:14', '0', null, null, null, null, '算法任务管理', '241eb76df1a5435aa801ea5a18e468ae', '1', 'arithmeticjobinfo', null, '1'), ('720380e804664e19a3f8736253721312', '2018-01-29 22:23:44', '2018-01-29 22:23:44', '0', null, null, null, null, '编辑', '585c5d6b454a414292d99c6236366467', '2', 'productedit', null, '2'), ('835ba57b8e644bd0bb30116faf553093', '2018-01-29 10:23:45', '2018-01-29 10:23:45', '0', null, null, null, null, '资源管理', '2de1b461b7e84181a6feaa86f7a5b24a', null, 'resource', null, '1'), ('8cd095188f1e4667846df243f6ae71b6', '2018-01-29 22:54:30', '2018-01-29 22:54:30', '0', null, null, null, null, '删除', '835ba57b8e644bd0bb30116faf553093', '3', 'resourcedel', null, '2'), ('989d40f0ea5f4fbeb70fd14524e5a9d1', '2018-01-29 17:55:00', '2018-01-29 17:55:00', '0', null, null, null, null, '清理', '61d9b5be40784bcc8267f9ae960c96c7', null, 'clean', null, '2'), ('a4913eee554c472da0e810452e4c38fa', '2018-01-29 22:49:19', '2018-01-29 23:00:18', '1', null, null, null, null, '分配资源', '3497eb339e4c4bd6ba050ef516ba5c22', '4', 'grantresource', null, '2'), ('ac6252bb50784a198050261fb2b75254', '2018-01-29 22:24:03', '2018-01-29 22:24:03', '0', null, null, null, null, '删除', '585c5d6b454a414292d99c6236366467', '3', 'productdel', null, '2'), ('ad2740ca48704bccb6e573eb5d825db4', '2018-01-29 23:04:42', '2018-01-29 23:04:42', '0', null, null, null, null, '编辑', 'f9642f1afca94fa7b4bfbcc12213efcb', '2', 'useredit', null, '2'), ('aecfea5cc8eb4b69a66360f06a7cc8cc', '2018-01-29 22:52:51', '2018-01-29 22:52:51', '0', null, null, null, null, '添加', '835ba57b8e644bd0bb30116faf553093', '1', 'resourceadd', null, '2'), ('b2eafd738fc24739902205ea3480545b', '2018-01-29 10:15:34', '2018-01-29 10:15:34', '0', null, null, null, null, '下载任务管理', '241eb76df1a5435aa801ea5a18e468ae', '2', 'downjobinfo', null, '0'), ('c32290979a9448c2b36fef3ba06f5d2b', '2018-01-29 17:03:24', '2018-01-29 17:03:24', '0', null, null, null, null, '任务日志', '64ec1407fc8b451080755ca0d7ce2bce', '3', 'tasklog', null, '2'), ('c5e3519ace5748b29461f224a7dccf5a', '2018-01-29 17:01:45', '2018-01-29 17:01:45', '0', null, null, null, null, '执行一次', '64ec1407fc8b451080755ca0d7ce2bce', '1', 'executeonce', null, '2'), ('c9ddf5119c7b46f1b020cdd18cb1cb32', '2018-01-29 22:54:10', '2018-01-29 22:54:10', '0', null, null, null, null, '编辑', '835ba57b8e644bd0bb30116faf553093', '2', 'resourceedit', null, '2'), ('d0fb7a830f584eb4a0d21eb3dcba4e51', '2018-01-29 14:15:26', '2018-01-29 14:38:42', '1', null, null, null, null, '执行器管理', '0', '5', 'jobgroup', null, '1'), ('d6968e6f1671420e99523d8f2c21f20c', '2018-01-29 22:23:14', '2018-01-29 22:23:14', '0', null, null, null, null, '添加', '585c5d6b454a414292d99c6236366467', '1', 'productadd', null, '2'), ('dc778278342b4beb8d7a0c41cda8e8ae', '2018-01-29 22:47:05', '2018-01-29 22:55:23', '1', null, null, null, null, '添加', '3497eb339e4c4bd6ba050ef516ba5c22', '1', 'roleadd', null, '2'), ('e168c7097bae4883ab79dbfb4dc47aa2', '2018-01-29 22:47:46', '2018-01-29 22:55:13', '1', null, null, null, null, '编辑', '3497eb339e4c4bd6ba050ef516ba5c22', '2', 'roleedit', null, '2'), ('e2b3cb040d9542c0a3325b057e0feafa', '2018-01-29 17:02:04', '2018-01-29 17:42:52', '2', null, null, null, null, '运行/暂停', '64ec1407fc8b451080755ca0d7ce2bce', '2', 'executepause', null, '2'), ('e4167f0a02f541d28a176c42483ec8fa', '2018-01-29 17:46:05', '2018-01-29 17:46:05', '0', null, null, null, null, '添加', '64ec1407fc8b451080755ca0d7ce2bce', '9', 'taskadd', null, '2'), ('e43a49b8159a4cdaa266ae1d325b4fd5', '2018-01-29 10:22:05', '2018-01-29 10:22:41', '1', null, null, null, null, '产品结果', '0', null, 'productinfofile', null, '1'), ('e49f4ea690664ffd8b1e763ec14c7a6c', '2018-01-29 10:16:53', '2018-01-29 10:20:27', '1', null, null, null, null, '产品模型', '0', '4', 'productmodel', null, '1'), ('e82bf666ec7d4034957de455db80b96c', '2018-01-29 22:38:23', '2018-01-29 22:38:23', '0', null, null, null, null, '查看', 'e43a49b8159a4cdaa266ae1d325b4fd5', '1', 'productfileinfosearch', null, '2'), ('f1b6e7fb41944955a8de3d5247dd22fe', '2018-01-29 23:04:15', '2018-01-29 23:04:15', '0', null, null, null, null, '添加', 'f9642f1afca94fa7b4bfbcc12213efcb', '1', 'useradd', null, '2'), ('f8f9e289bf7b4802869fa387f8d62a79', '2018-01-29 17:03:53', '2018-01-29 17:03:53', '0', null, null, null, null, '编辑', '64ec1407fc8b451080755ca0d7ce2bce', '5', 'taskedit', null, '2'), ('f9642f1afca94fa7b4bfbcc12213efcb', '2018-01-29 10:23:06', '2018-01-29 10:23:06', '0', null, null, null, null, '用户管理', '2de1b461b7e84181a6feaa86f7a5b24a', null, 'user', null, '1');

-- ----------------------------
--  Table structure for `htht_cluster_schedule_role`
-- ----------------------------
DROP TABLE IF EXISTS `htht_cluster_schedule_role`;
CREATE TABLE `htht_cluster_schedule_role` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `role_key` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `htht_cluster_schedule_role`
-- ----------------------------

INSERT INTO `htht_cluster_schedule_role` VALUES ('ad145a6dc304448b8827d26d265b7126', '2018-01-28 18:24:23', '2018-01-28 20:45:20', '61', '', '超级管理员', 'administrator', '0');

-- ----------------------------
--  Table structure for `htht_cluster_schedule_role_resource`
-- ----------------------------
CREATE TABLE `htht_cluster_schedule_role_resource` (
  `role_id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `resource_id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`role_id`,`resource_id`),
  KEY `FK7nfn7qfjd4ixj8vopri3iivgn` (`resource_id`),
  CONSTRAINT `FK7nfn7qfjd4ixj8vopri3iivgn` FOREIGN KEY (`resource_id`) REFERENCES `htht_cluster_schedule_resource` (`id`),
  CONSTRAINT `FKsnhsnpb8jlumnny1m4wh5q987` FOREIGN KEY (`role_id`) REFERENCES `htht_cluster_schedule_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `htht_cluster_schedule_role_resource`
-- ----------------------------

INSERT INTO `htht_cluster_schedule_role_resource` VALUES ('ad145a6dc304448b8827d26d265b7126', '0763e20f2266458a86c7e23802353ad5'), ('ad145a6dc304448b8827d26d265b7126', '09307f7051b54b9b9b0da6484fc7e51e'), ('ad145a6dc304448b8827d26d265b7126', '09b9d85b7edc490393b00b56605ac60b'), ('ad145a6dc304448b8827d26d265b7126', '1097a405f4a84fd1a094ea5a17c5f879'), ('ad145a6dc304448b8827d26d265b7126', '21018e8040364ffdb4e6e958a727267c'), ('ad145a6dc304448b8827d26d265b7126', '241eb76df1a5435aa801ea5a18e468ae'), ('ad145a6dc304448b8827d26d265b7126', '2580ab307d7546e4aac48378edd6de5d'), ('ad145a6dc304448b8827d26d265b7126', '276b81c784bb4071a5b0647634f55eae'), ('ad145a6dc304448b8827d26d265b7126', '2de1b461b7e84181a6feaa86f7a5b24a'), ('ad145a6dc304448b8827d26d265b7126', '3497eb339e4c4bd6ba050ef516ba5c22'), ('ad145a6dc304448b8827d26d265b7126', '396e8540a0d243b7af2afc4f51ff5ffe'), ('ad145a6dc304448b8827d26d265b7126', '425d586fc48346a7bc1d6ac11c0c3327'), ('ad145a6dc304448b8827d26d265b7126', '45fa2c46ddc54fe5b8189fabaafe2b46'), ('ad145a6dc304448b8827d26d265b7126', '552c6f405a3c4065a2f20fd0e4234818'), ('ad145a6dc304448b8827d26d265b7126', '585c5d6b454a414292d99c6236366467'), ('ad145a6dc304448b8827d26d265b7126', '61d9b5be40784bcc8267f9ae960c96c7'), ('ad145a6dc304448b8827d26d265b7126', '64ec1407fc8b451080755ca0d7ce2bce'), ('ad145a6dc304448b8827d26d265b7126', '720380e804664e19a3f8736253721312'), ('ad145a6dc304448b8827d26d265b7126', '835ba57b8e644bd0bb30116faf553093'), ('ad145a6dc304448b8827d26d265b7126', '8cd095188f1e4667846df243f6ae71b6'), ('ad145a6dc304448b8827d26d265b7126', '989d40f0ea5f4fbeb70fd14524e5a9d1'), ('ad145a6dc304448b8827d26d265b7126', 'a4913eee554c472da0e810452e4c38fa'), ('ad145a6dc304448b8827d26d265b7126', 'ac6252bb50784a198050261fb2b75254'), ('ad145a6dc304448b8827d26d265b7126', 'ad2740ca48704bccb6e573eb5d825db4'), ('ad145a6dc304448b8827d26d265b7126', 'aecfea5cc8eb4b69a66360f06a7cc8cc'), ('ad145a6dc304448b8827d26d265b7126', 'b2eafd738fc24739902205ea3480545b'), ('ad145a6dc304448b8827d26d265b7126', 'c32290979a9448c2b36fef3ba06f5d2b'), ('ad145a6dc304448b8827d26d265b7126', 'c5e3519ace5748b29461f224a7dccf5a'), ('ad145a6dc304448b8827d26d265b7126', 'c9ddf5119c7b46f1b020cdd18cb1cb32'), ('ad145a6dc304448b8827d26d265b7126', 'd0fb7a830f584eb4a0d21eb3dcba4e51'), ('ad145a6dc304448b8827d26d265b7126', 'd6968e6f1671420e99523d8f2c21f20c'), ('ad145a6dc304448b8827d26d265b7126', 'dc778278342b4beb8d7a0c41cda8e8ae'), ('ad145a6dc304448b8827d26d265b7126', 'e168c7097bae4883ab79dbfb4dc47aa2'), ('ad145a6dc304448b8827d26d265b7126', 'e2b3cb040d9542c0a3325b057e0feafa'), ('ad145a6dc304448b8827d26d265b7126', 'e4167f0a02f541d28a176c42483ec8fa'), ('ad145a6dc304448b8827d26d265b7126', 'e43a49b8159a4cdaa266ae1d325b4fd5'), ('ad145a6dc304448b8827d26d265b7126', 'e49f4ea690664ffd8b1e763ec14c7a6c'), ('ad145a6dc304448b8827d26d265b7126', 'e82bf666ec7d4034957de455db80b96c'), ('ad145a6dc304448b8827d26d265b7126', 'f1b6e7fb41944955a8de3d5247dd22fe'), ('ad145a6dc304448b8827d26d265b7126', 'f8f9e289bf7b4802869fa387f8d62a79'), ('ad145a6dc304448b8827d26d265b7126', 'f9642f1afca94fa7b4bfbcc12213efcb');

-- ----------------------------
--  Table structure for `htht_cluster_schedule_user`
-- ----------------------------
DROP TABLE IF EXISTS `htht_cluster_schedule_user`;
CREATE TABLE `htht_cluster_schedule_user` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `address` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `delete_status` int(11) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `locked` int(11) DEFAULT NULL,
  `nick_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sex` int(11) DEFAULT NULL,
  `telephone` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `user_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `htht_cluster_schedule_user`
-- ----------------------------
INSERT INTO `htht_cluster_schedule_user` VALUES ('2db76bcf45f34c6884380c7828449adf', '2018-01-28 18:23:13', '2018-01-28 18:23:13', '5', null, null, '0', null, null, '0', '超级管理员', '3931MUEQD1939MQMLM4AISPVNE', null, null, 'admin');

-- ----------------------------
--  Table structure for `htht_cluster_schedule_user_role`
-- ----------------------------
CREATE TABLE `htht_cluster_schedule_user_role` (
  `user_id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `role_id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKrv55439wijbbpnbte9cm5xiqp` (`role_id`),
  CONSTRAINT `FKn13bmcb3eeqn1uhobr55fub0f` FOREIGN KEY (`user_id`) REFERENCES `htht_cluster_schedule_user` (`id`),
  CONSTRAINT `FKrv55439wijbbpnbte9cm5xiqp` FOREIGN KEY (`role_id`) REFERENCES `htht_cluster_schedule_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `htht_cluster_schedule_user_role`
-- ----------------------------

INSERT INTO `htht_cluster_schedule_user_role` VALUES ('2db76bcf45f34c6884380c7828449adf', 'ad145a6dc304448b8827d26d265b7126');

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `htht_cluster_schedule_dict_code` (`id`, `create_time`, `update_time`, `version`, `create_by`, `dict_code`, `dict_name`, `is_tree`, `memo`, `parent_id`, `sort_order`, `update_by`) VALUES ('a0a10e1863ce425ab9b761122b94175f', '2018-8-28 16:36:34', '2018-8-28 16:36:34', 0, NULL, 'D:\\soa_deploy\\bin\\AlgoLoader.exe', 'windows遥感算法执行器路径', NULL, '', '66c3fa403c244fc994cdde6b1881e121', NULL, NULL);
insert into `htht_cluster_schedule_resource` ( `id`, `source_key`, `is_hide`, `version`, `parent_id`, `description`, `type`, `level`, `source_url`, `icon`, `create_time`, `sort`, `update_time`, `name`) values ( 'e3144595045f4b069f2c71a6e25b65ee', 'alogparametershow', null, '0', 'd0fb7a830f584eb4a0d21eb3dcba4e51', null, '0', null, '', null, '2018-09-03 10:40:37', '1', '2018-09-03 10:40:37', '加载形式');
insert into `htht_cluster_schedule_resource` ( `id`, `source_key`, `is_hide`, `version`, `parent_id`, `description`, `type`, `level`, `source_url`, `icon`, `create_time`, `sort`, `update_time`, `name`) values ( '04bd316485f647f8b516ad91bd70e2f7', 'productmodeladd', null, '0', 'd0fb7a830f584eb4a0d21eb3dcba4e51', null, '0', null, '', null, '2018-09-03 10:40:07', '1', '2018-09-03 10:40:07', '固定参数');