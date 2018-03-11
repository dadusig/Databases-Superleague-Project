select coach.cv as 'ΒΙΟΓΡΑΦΙΚΟ ΠΡΟΠΟΝΗΤΗ', owner.name as 'ΟΝΟΜΑ ΠΡΟΕΔΡΟΥ'
from team
inner join (owner, coach) 
on (owner.team_name = team.name and coach.team_name=team.name)
order by team.wins desc
limit 0,1;

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

select team.name as 'ΟΜΑΔΑ', 
team.goalsOUT - team.goalsIN as 'ΔΙΑΦΟΡΑ',
team.stadium as 'ΕΔΡΑ', 
owner.name as 'ΠΡΟΕΔΡΟΣ', 
coach.name as 'ΠΡΟΠΟΝΗΤΗΣ'
from team inner join (owner, coach) on
(team.name = owner.team_name and team.name = coach.team_name)
group by team.name
order by team.goalsOUT - team.goalsIN desc
limit 0,1;

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

DELIMITER $
CREATE TRIGGER unigueRefereesInArbitration
BEFORE INSERT ON arbitration
FOR EACH ROW
BEGIN

	IF (NEW.referee = NEW.ref1 OR
		NEW.referee = NEW.ref2 OR
		NEW.referee = NEW.fourth OR
		NEW.referee = NEW.observer OR
		NEW.ref1 = NEW.ref2 OR
		NEW.ref1 = NEW.fourth OR
		NEW.ref1 = NEW.observer OR
		NEW.ref2 = NEW.fourth OR
		NEW.ref2 = NEW.observer OR
		NEW.fourth = NEW.observer) THEN

		SIGNAL SQLSTATE VALUE '45000'
		SET MESSAGE_TEXT = 'EACH REFEREE IN ARBITRATION MUST BE UNIQUE'; 
	END IF;

END$
DELIMITER ;

DROP PROCEDURE IF EXISTS revenueOf;
DELIMITER $
CREATE PROCEDURE revenueOf(IN teamName VARCHAR(50))
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

select distinct
a.name as 'referee', 
b.name as '1st',
c.name as '2nd',
d.name as 'fourth',
e.name as 'observer'
from game
inner join referee as a on a.id = game.referee
inner join referee as b on b.id = game.ref1
inner join referee as c on c.id = game.ref2
inner join referee as d on d.id = game.fourth
inner join referee as e on e.id = game.observer;

DROP PROCEDURE IF EXISTS fanInviteToUpgrade;
DELIMITER $
CREATE PROCEDURE fanInviteToUpgrade(IN team_name VARCHAR(50), IN currentDate VARCHAR(50))
BEGIN

	DECLARE totalGames INT;

	select count(*)
	into totalGames
	from game
	where (game.team_host = team_name or game.team_guest=team_name) and game.game_date <= currentDate;

	SELECT fan.name as 'ΟΝΟΜΑ'
	from fan
	INNER JOIN ticket on (fan.id = ticket.fan_id AND fan.team_name=team_name AND ticket.game_date <= currentDate)
	GROUP BY fan.name
	having COUNT(*) >= (totalGames/2);



END$
DELIMITER ;