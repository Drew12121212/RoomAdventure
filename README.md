# RoomAdventure

With the changes to the code there are now a few extra commands that are needed to interact with the game.
these include `go`, `look`, `eat`, `answer`, `talk` and `take`.

## Look

When looking at objects in the room sometimes you will find things. these are highlighted red. these will either be edibles or grabbables

## Edibles

These look the same as grabbabels in the terminal(Highlighted Red). the difference is you can look at these objects before you attempt to eat them. This is recommended because every edible
has a preset effect on health. this can usually be judged as positive or negative in the description of it. once you have determiend you wish to eat it simply use `eat [Edible]` If you attempt to eat 
a grabbable it will let you know and go ahead and add it to the inventory.

## Talk

There are people in the rooms that can be talked to by using the command `talk person`. all people are called person so this is the exact command to be used

## Answer

This is only used in the puzzle room. after looking at the tablet and getting the riddle you can attempt an answer by typing `answer [riddle answer]` for testing the answer is echo so just type
`answer echo`   to see what happens

# Game testing

this is the exact inputs to be able to test everything. this only has you look at one item to test it but to find all the items to take you would need to look at the items in the room

in the first room
`take key`

go to the next room
`go north`

take the newspaper
`take newspaper`

look at the patty and eat it
`look wall`
`look patty`
`eat patty`

go to the next room
`go east`

grab the microscope
`take microscope`
`use key`

talk to the person in the room
`talk person`

go to the next room
`go south`

kill the spider
`attack spider`

grab the items in the room
`take stick`
`take coal`

go to the next room
`go west`
`go north`
`go north`

read the tablet
`look tablet`

solve the riddle. either give the answer of decode the note and use that
 1. `answer echo`  
 or
 2. `combine note microscope` `use decoded_note`

 go to the next room
 `go south`

 combine your items and escape
 `combine key coal`
 `combine firekey stick`
 `use flaming_stick`
