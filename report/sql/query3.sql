DROP PROCEDURE IF EXISTS threeWithFans;
DELIMITER $
CREATE PROCEDURE threeWithFans()
BEGIN
	DECLARE teamName VARCHAR(50);
	DECLARE playerName VARCHAR(50);

	DECLARE not_found INT;
	DECLARE playerID INT;
	DECLARE playerGoals INT;
	DECLARE player_not_found INT;

	DECLARE playercursor CURSOR FOR
	select id, name, goals
	from player
	where team_name like teamName
	order by goals desc
	limit 0,3;
	

	DECLARE teamcursor CURSOR FOR
	select team.name
	from team;
	DECLARE CONTINUE HANDLER FOR NOT FOUND
	SET not_found=1;

	SET not_found = 0;
	OPEN teamcursor;
		REPEAT
			FETCH teamcursor INTO teamName;
			IF (not_found=0) THEN
				select teamName as 'ΟΝΟΜΑ ΟΜΑΔΑΣ';
				OPEN playercursor;
					REPEAT
						FETCH playercursor INTO playerID, playerName, playerGoals;
						IF (not_found=0) THEN
							select playerName as 'ΟΝΟΜΑ ΠΑΙΚΤΗ', playerGoals as 'GOALS';
							select fan.name as 'ΟΝΟΜΑΤΑ ΘΑΥΜΑΣΤΩΝ'
							from fan
							inner join fan_admires on fan.id = fan_admires.fan_id
							where player_id=playerID;
						END IF;
					UNTIL(not_found=1)
					END REPEAT;
					SET not_found = 0;
				CLOSE playercursor;
			END IF;
		UNTIL(not_found=1)
		END REPEAT;
	CLOSE teamcursor;
END$
DELIMITER ;