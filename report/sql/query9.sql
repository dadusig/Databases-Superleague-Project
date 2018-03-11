DROP PROCEDURE IF EXISTS refenueOf;
DELIMITER $
CREATE PROCEDURE refenueOf(IN teamName VARCHAR(50))
BEGIN

	DECLARE simpleRevenue INT; 
	DECLARE simple INT; 
	DECLARE seasonRevenue INT; 
	DECLARE season INT; 

	SELECT  count(*)
	INTO simple
	FROM ticket
	INNER JOIN fan ON ticket.fan_id = fan.id
	WHERE fan.team_name=teamName;

	SELECT COUNT(*)
	INTO season
	FROM season_ticket
	WHERE team_name=teamName;

	SET simpleRevenue = simple * 10;
	SET seasonRevenue = season * 200;

	SELECT teamName as 'ΟΜΑΔΑ', 
	simpleRevenue as 'ΕΣΟΔΑ ΑΠΟ ΑΠΛΑ', 
	seasonRevenue as 'ΕΣΟΔΑ ΑΠΟ ΔΙΑΡΚΕΙΑΣ',
	seasonRevenue+simpleRevenue as 'ΣΥΝΟΛΟ';

END$
DELIMITER ;