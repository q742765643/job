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

 Date: 08/15/2018 14:05:42 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `htht_executesql_log`
-- ----------------------------
DROP TABLE IF EXISTS `htht_cluster_schedule_executesql_log`;
CREATE TABLE `htht_cluster_schedule_executesql_log` (
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
