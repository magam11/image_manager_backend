

CREATE TABLE `user` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone_number` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `register_activation_key` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone_number` (`phone_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert  into `user`(`id`,`name`,`phone_number`,`password`,`register_activation_key`) values ('15567970656942be67646-ff31-42a4-8b63-5af5be968a93','Lilit','+37493195104','123456','79911'),('15568055619641d438cb0-f44d-4430-883e-3a6c7f040f94','Margarita','+37491103354','$2a$10$sNZesFtMBqXVkiBI0iAV5OuVgXmZb6jhy0DgSt5Gu0bCJ2FymcZ/W','');

CREATE TABLE `user_image` (
  `user_id` varchar(255) NOT NULL,
  `pic_name` varchar(255) NOT NULL,
  `pic_size` double NOT NULL,
  `deleted_at` date DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_image_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert  into `user_image`(`user_id`,`pic_name`,`pic_size`,`deleted_at`,`created_at`) values ('15568055619641d438cb0-f44d-4430-883e-3a6c7f040f94','1561102086107_1556706307276_636669276528789207-remy071118a.jpg',26777,NULL,'2019-06-21 11:28:06'),('15568055619641d438cb0-f44d-4430-883e-3a6c7f040f94','1561102105995_1556706312514_image3.jpg',1760735,NULL,'2019-06-21 11:28:26'),('15568055619641d438cb0-f44d-4430-883e-3a6c7f040f94','1561102111587_1556706488536_2-dog.jpg',415118,NULL,'2019-06-21 11:28:31'),('15568055619641d438cb0-f44d-4430-883e-3a6c7f040f94','1561102116466_1556706493087_5b7fdeab1900001d035028dc.jpeg',269360,NULL,'2019-06-21 11:28:36'),('15568055619641d438cb0-f44d-4430-883e-3a6c7f040f94','1561102120974_1556706499232_image.jpg',10995,NULL,'2019-06-21 11:28:40'),('15568055619641d438cb0-f44d-4430-883e-3a6c7f040f94','1561102304903_download.jpg',5483,NULL,'2019-06-21 11:31:44');
