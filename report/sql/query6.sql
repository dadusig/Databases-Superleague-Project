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