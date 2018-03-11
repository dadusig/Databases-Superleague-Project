select coach.cv as 'ΒΙΟΓΡΑΦΙΚΟ ΠΡΟΠΟΝΗΤΗ', 
owner.name as 'ΟΝΟΜΑ ΠΡΟΕΔΡΟΥ'
from team inner join (owner, coach) 
on (owner.team_name = team.name and coach.team_name=team.name)
order by team.wins desc
limit 0,1;