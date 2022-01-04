CREATE TABLE `citizen` (
  `aadharNumber` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `age` int NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `state_id` int NOT NULL,
  PRIMARY KEY (`aadharNumber`)
) ENGINE=InnoDB AUTO_INCREMENT=1001



CREATE TABLE `ref_citizen_loc` (
  `state_name` varchar(255) NOT NULL,
  `state_id` smallint unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`state_id`),
  UNIQUE KEY `idx_state_id` (`state_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT



