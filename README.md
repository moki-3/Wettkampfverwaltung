Download the .jar file from the releases page. 
```shell
java -jar /path/to/.jar/file
```
and you need Java 21

and a .csv file with the following data:
name01,club01,name02,club02,age class,weight class

(age class can be either U10 or U12)

example data:

Sophie Pfaller,JudoWien,Alexander Haas,JudoLinz,U10,38Kg

(I included an example .csv file in the releases)

Setup video (~2:20min):

https://github.com/moki-3/Wettkampfverwaltung/releases/download/v1.1/Setup.mov


---
# What does this program do?

My local judo club often manages and organises judo competitions. We used old displays, that rarely did what they were supposed to do. So I decided to write a program that replaces these displays.
So it is a replacement. And it automates stuff like counting the points for the different clubs. All the ratings can either be
added by the buttons or the keyboard. The according keys are written on the buttons. 
By clicking the "R" key you enter R-Mode which lets you subtract a rating. 

The Program consists of two windows: ControlStage and ViewStage. In ControlStage the user can manage the fight. On the left is
a list of all the fights. Completed fights have a red border, the current one has an orange border and fights yet to come have a green border.
You can only choose a fight if there is no current fight. Only U10 and U12 are supported as of 4.4.2026.

In ViewStage, the viewers can see all the data. With big fonts. Its for the viewers and the judge. There is also a sound playing
after the fight is done.

That's it I think, the people most likely to use this program can ask me how it works. For anybody else I left it Open Source so you can learn from my mistakes ;)
