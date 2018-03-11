CREATE USER 'league_admin'@'localhost'
IDENTIFIED BY '12345678';
GRANT ALL ON superleague.* TO 'league_admin'@'localhost';