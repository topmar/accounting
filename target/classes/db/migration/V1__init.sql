CREATE TABLE IF NOT EXISTS `users`
(
    `id`                      INT          NOT NULL AUTO_INCREMENT,
    `user_name`               VARCHAR(20)  NOT NULL,
    `password`                VARCHAR(255) NOT NULL,
    `first_name`              VARCHAR(100) NOT NULL,
    `last_name`               VARCHAR(100) NOT NULL,
    `tel`                     VARCHAR(22),
    `email`                   VARCHAR(255) NOT NULL,
    `creation_date`           VARCHAR(19),
    `role`                    VARCHAR(20)  NOT NULL,
    `enabled`                 BOOLEAN,
    `account_non_expired`     BOOLEAN,
    `account_non_locked`      BOOLEAN,
    `credentials_non_expired` BOOLEAN,
    PRIMARY KEY (`id`)
);
CREATE TABLE IF NOT EXISTS `tokens`
(
    `token`   VARCHAR(255) NOT NULL,
    `user_id` INT          NOT NULL,
    PRIMARY KEY (`token`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);
CREATE TABLE IF NOT EXISTS `persistent_logins`
(
    `username`  VARCHAR(64) NOT NULL,
    `series`    VARCHAR(64) NOT NULL,
    `token`     VARCHAR(64) NOT NULL,
    `last_used` TIMESTAMP   NOT NULL,
    PRIMARY KEY (`series`)
);
