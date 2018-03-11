DROP PROCEDURE IF EXISTS bestGamesFor;
DELIMITER $
CREATE PROCEDURE bestGamesFor(IN teamName VARCHAR(50))
BEGIN

	select ticket.game_date as 'ΗΜΕΡΟΜΗΝΙΑ', ticket.stadium_name as 'ΣΤΑΔΙΟ', 
	game.team_host as 'ΓΗΠΕΔΟΥΧΟΣ', game.team_guest as 'ΦΙΛΟΞΕΝΟΥΜΕΝΟΣ', 
	count(*) as 'ΕΙΣΙΤΗΡΙΑ'
	from ticket
	left join (game, fan) 
	on (ticket.game_date = game.game_date and 
		ticket.stadium_name = game.stadium_name and
		fan.id = ticket.fan_id and fan.team_name = teamName)
	where game.team_host = teamName or game.team_guest = teamName
	group by ticket.game_date, game.stadium_name
	order by count(*) desc
	limit 0,3;

END$
DELIMITER ;