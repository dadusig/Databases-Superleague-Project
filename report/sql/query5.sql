DROP PROCEDURE IF EXISTS bestFan;
DELIMITER $
CREATE PROCEDURE bestFan()
BEGIN

	DECLARE fanID INT;
	DECLARE count INT;
	DECLARE totalGames INT;
	DECLARE fanName VARCHAR(50);
	DECLARE teamName VARCHAR(50);
	DECLARE msg VARCHAR(150);

	SET msg = 'ΔΕΝ ΕΧΕΙ ΣΤΑΛΕΙ ΑΙΤΗΜΑ ΑΝΑΝΕΩΣΗΣ';

	select fan.team_name, fan.id, fan.name, count(season_ticket_history.ticket_id)
	into teamName, fanID, fanName, count
	from fan
	inner join (season_ticket, season_ticket_history) on
	(fan.id = season_ticket.fan_id and season_ticket_history.ticket_id = season_ticket.id)
	group by fan.id
	order by count(season_ticket_history.ticket_id) desc
	limit 0,1;

	select fanID as ID, fanName as 'ΟΝΟΜΑ ΦΙΛΑΘΛΟΥ', count as 'ΧΡΗΣΗ ΔΙΑΡΚΕΙΑΣ';

	select game.game_date as 'ΗΜ/ΝΙΕΣ ΑΓΩΝΩΝ ΠΟΥ ΑΠΟΥΣΙΑΖΕ', game.team_host as 'ΓΗΠΕΔΟΥΧΟΣ', 
	game.team_guest as 'ΦΙΛΟΞΕΝΟΥΜΕΝΟΣ'
	from game
	left join (season_ticket_history, season_ticket) on
	(game.game_date = season_ticket_history.game_date and 
	game.stadium_name = season_ticket_history.stadium_name and
	season_ticket_history.ticket_id = season_ticket.id and
	season_ticket.fan_id=fanID)
	WHERE (game.team_host = teamName or game.team_guest=teamName) AND 
	season_ticket_history.ticket_id IS NULL;	

	select count(*)
	into totalGames
	from game
	where game.team_host = teamName or game.team_guest=teamName;

	IF (totalGames > 0) THEN
		IF (count >= totalGames/2) THEN
			SET msg = 'ΕΧΕΙ ΑΠΟΣΤΑΛΕΙ ΠΡΟΣΚΛΗΣΗ ΑΝΑΝΕΩΣΗΣ ΣΥΝΔΡΩΜΗΣ';
		END IF;
	END IF;

	SELECT msg as 'ΚΑΤΑΣΤΑΣΗ';

END$
DELIMITER ;