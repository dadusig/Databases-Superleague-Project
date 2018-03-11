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