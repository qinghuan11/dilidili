DROP TABLE IF EXISTS t_danmu;
DROP TABLE IF EXISTS t_video;
DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user (
                        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        password VARCHAR(100) NOT NULL,
                        email VARCHAR(100),
                        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE t_video (
                         id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(100) NOT NULL,
                         description TEXT,
                         file_path VARCHAR(255) NOT NULL,
                         user_id BIGINT NOT NULL,
                         upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES t_user(id)
);

CREATE TABLE t_danmu (
                         id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         video_id BIGINT NOT NULL,
                         content VARCHAR(255),
                         timestamp INT NOT NULL,
                         user_id BIGINT NOT NULL,
                         create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (video_id) REFERENCES t_video(id),
                         FOREIGN KEY (user_id) REFERENCES t_user(id)
);