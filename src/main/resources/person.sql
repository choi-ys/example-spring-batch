CREATE TABLE person_tb (
    id bigint primary key auto_increment,
    name varchar(50),
    age tinyint(50) unsigned,
    address varchar(50)
);

INSERT INTO
    person_tb(name, age, address)
VALUES
    ('최용석', 31, '반포'),
    ('이성욱', 31, '독산'),
    ('권성준', 31, '병점'),
    ('기호창', 30, '금정'),
    ('이하은', 29, '교대'),
    ('박재현', 29, '목동'),
    ('전성원', 27, '부천');