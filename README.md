# How to install the program

1. Head to [Releases](https://github.com/moki-3/Wettkampfverwaltung/releases)
2. Go to the latest release
3. follow the instruction

---
# What does this program do?

This is a tool to manage judo competitions. I made it for my judo club and it 
won't work for any competition. 

I underestimated the complexity of it and it is probably the ugliest code I have ever 
written, but I am currently refactoring and simplifying the code. It works at least.

---

# JAR zur App machen

## Schritte für neuen release:

1. Versionsnummer ändern

   pom.xml

    ```xml
    <version>neue nummer</version>
    <app.version>neue nummer</app.version>
    ```

   für version suche nach <name>Wettkampfverwaltung</name> und dann die Zeile drüber ändern

2. git commit und push
3. auf mac

    ```xml
    git pull
    mvn clean package
    mvn javafx:jlink
    jpackage \
      --type dmg \
      --name "Wettkampfverwaltung" \
      --app-version version \
      --runtime-image target/app \
      --input target \
      --main-jar Wettkampfverwaltung-version.jar \
      --main-class org.example.wettkampfverwaltung.Launcher \
      --dest target/dist
    ```

   —app-version und —main-jar mit der aktuellen Versionsnummer anpassen!

   mit ls target/*.jar kann ich nachschauen, wie die File heißt


1. auf Windows

    ```xml
    git pull
    .\mvnw.cmd clean package
    .\mvnw.cmd javafx:jlink
    jpackage --type exe --name "Wettkampfverwaltung" --app-version versuib --runtime-image target\app --input target --main-jar Wettkampfverwaltung-version.jar --main-class org.example.wettkampfverwaltung.Launcher --dest target\dist --win-shortcut
    ```