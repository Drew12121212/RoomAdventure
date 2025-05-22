# RoomAdventure

With the changes to the code there are now a few extra commands that are needed to interact with the game.
these include `go`, `look`, `eat`, `answer`, `talk` and `take`.

# Look

When looking at objects in the room sometimes you will find things. these are highlighted red. these will either be edibles or grabbables

# Edibles

These look the same as grabbabels in the terminal(Highlighted Red). the difference is you can look at these objects before you attempt to eat them. This is recommended because every edible
has a preset effect on health. this can usually be judged as positive or negative in the description of it. once you have determiend you wish to eat it simply use `eat [Edible]` If you attempt to eat 
a grabbable it will let you know and go ahead and add it to the inventory.

# Talk

There are people in the rooms that can be talked to by using the command `talk person`. all people are called person so this is the exact command to be used

# Answer

This is only used in the puzzle room. after looking at the tablet and getting the riddle you can attempt an answer by typing `answer [riddle answer]` for testing the answer is echo so just type
`answer echo`   to see what happens