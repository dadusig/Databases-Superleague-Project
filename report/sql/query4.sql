DROP PROCEDURE IF EXISTS totalSeasonTickets;
DELIMITER $
CREATE PROCEDURE totalSeasonTickets()
BEGIN
	DECLARE not_found INT;
	DECLARE ticketCount INT;
	DECLARE teamName VARCHAR(50);
	DECLARE teamcursor CURSOR FOR
	select team.name, count(season_ticket.team_name)
	from team
	left join season_ticket on season_ticket.team_name=team.name
	group by team.name
	order by count(season_ticket.team_name) desc;
	DECLARE CONTINUE HANDLER FOR NOT FOUND
	SET not_found=1;

	SET not_found = 0;

	OPEN teamcursor;
	REPEAT
		FETCH teamcursor INTO teamName, ticketCount;
		IF (not_found=0) THEN
			select teamName as 'ΟΝΟΜΑ ΟΜΑΔΑΣ', ticketCount as 'ΠΛΗΘΟΣ ΔΙΑΡΚΕΙΑΣ';
			select fan.name as 'ΚΑΤΟΧΟΙ ΔΙΑΡΚΕΙΑΣ'
			from team
			inner join (season_ticket, fan) 
			on (season_ticket.team_name=team.name and fan.id = season_ticket.fan_id)
			where fan.team_name=teamName;
		END IF;
	UNTIL(not_found=1)
	END REPEAT;
	CLOSE teamcursor;
END$
DELIMITER ;