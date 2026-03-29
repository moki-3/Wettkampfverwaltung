Diesen Boolean verwende ich um einen Bug zu beheben:

Wenn man einen Waza ari vergeben hat, und dieser Kämpfer dann mit einem Oasei-Komi gewonnen hat, ist man in einer ewigen Schleife gewesen, in der [[void checkWinner()]] und [[void continueToNextFight()]] sich gegenseitig (wahrscheinlich indirekt) aufgerufen haben.

Diesen Bug habe ich mit [[boolean hasCheckWinnerAlreadyBeenCalled]] gelöst, indem in [[void checkWinner()]] geschaut wird, ob [[boolean hasCheckWinnerAlreadyBeenCalled]] true ist. Wenn ja, wird die Methode sofort verlassen (return). In jedem fall wird [[boolean hasCheckWinnerAlreadyBeenCalled]] auf true gesetzt.
In [[void continueToNextFight()]] wird [[boolean hasCheckWinnerAlreadyBeenCalled]] für den nächsten Kampf wieder auf false gesetzt.
wird auch in [[void startGoldeScore]] resettet