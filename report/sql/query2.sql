DROP PROCEDURE IF EXISTS firstplayer;
DELIMITER $
CREATE PROCEDURE firstplayer()
BEGIN

	DECLARE name VARCHAR(50);
	DECLARE first INT;
	DECLARE second INT;
	DECLARE diff INT;

	select player.name, player.goals
	into name, first
	from player
	order by player.goals desc
	limit 0,1;

	select player.goals
	into second
	from player
	order by player.goals desc
	limit 1,1;

	SET diff = first - second;

	SELECT name as 'ΠΡΩΤΟΣ ΣΚΟΡΕΡ', diff as 'ΔΙΑΦΟΡΑ ΑΠΟ 2ο';
END$
DELIMITER ;