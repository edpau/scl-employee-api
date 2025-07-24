# --- !Ups
CREATE TABLE sample (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL
);

# --- !Downs
DROP TABLE sample;
