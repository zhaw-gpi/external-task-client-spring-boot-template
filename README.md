Björn Scheppler, 16.8.2019

# Camunda External Task Client Spring Boot Template (external-task-client-spring-boot-template)
Dieses Maven-Projekt kann genutzt werden als Startpunkt für Spring Boot-Projekte, 
bei welchen Tasks, die von einer Camunda Process Engine "veröffentlicht" werden, abzuarbeiten. 
Als Basis wird dafür einerseits eine klassische Spring Boot-Applikation genutzt und
als primäre weitere Abhängigkeit der Java External Task Client von Camunda, welcher 
im Prinzip nichts anderes tut, als die REST API-Calls für External Tasks zu kapseln. 
Im konkreten Beispiel wird das Senden eines Tweets durchgeführt, wofür die Twitter4J-
Library zur Verfügung steht.

Entsprechend enthält das Projekt folgende Komponenten
1. Abhängigkeiten:
    1. Spring Boot Starter
    2. Twitter4J
    3. Camunda External Task Client 
2. Properties:
    1. application.properties: URL der Process Engine REST-Schnittstelle
    2. twitter4j(-template).properties: Zugangsdaten zu Twitter, welche nicht auf Github versioniert werden. Das Template-File kann als Vorlage genutzt werden.
3. Source-Code:
    1. ExternalTaskClientSpringBootTemplateApplication: Enthält die Main-Methode,
       um die Spring Boot-Applikation zu starten
    2. TwitterService: Stellt eine Verbindung zu Twitter her über Twitter4J und 
       exponiert die zwei Methoden der Twitter API (Tweeting und Ausgeben einer Liste 
       der letzten Tweets)
    3. TweetSendingExternalTaskClient: Baut Verbindung zu Camunda Process Engine
       auf, abonniert das Topic "SendTweet" und verweist auf SendTweetHandler, um
       gefetchte Tasks auch tatsächlich abzuarbeiten
    4. SendTweetHandler: Wird von TweetSendingExternalTaskClient genutzt. Kommuniziert
       mit TwitterService, um den Tweet-Content zu posten und den Task als erledigt
       zu kommunizieren an Camunda

Siehe auch https://github.com/zhaw-gpi/external-task-client-mocking-template
für das Pendant zu diesem Projekt-Template, welches eine minimalistische Version
des External Task Clients für das gleiche Topic hat, welche aber das Senden von
Tweets nur mockt.

## Deployment
1. **Erstmalig** oder bei Problemen `mvn clean install` durchführen. Zudem ein twitter4j.properties-File anlegen und mit den Credentials von Twitter ausfüllen. Als Template die Datei twitter4j-template.properties verwenden.
2. Bei Änderungen am POM-File oder bei **(Neu)kompilierungsbedarf** genügt ein `mvn install`
3. **Vor dem Start** muss zunächst die **Process Engine** laufen, bei welcher man sich registrieren will, das heisst z.B. das Camunda Projekttemplate (https://github.com/zhaw-gpi/project-template)
4. Für den **Start** ist ein `java -jar .\target\NAME DES JAR-FILES.jar` (Cmd) erforderlich. Dabei wird die Main-Methode in ExternalTaskClientSpringBootTemplateApplication.java ausgeführt.
5. Das **Beenden** geschieht mit **CTRL+C**

## Grundlegende Nutzung
1. Damit man den Client in Aktion sieht, muss mindestens eine Aufgabe vom Topic "SendTweet" zu erledigen sein.
2. Hierzu steht im Camunda Projekttemplate ein Prozess für das Verarbeiten von Tweet-Anfragen zur Verfügung.
3. Damit es wirklich etwas zu posten gibt, muss beim Schritt "Tweet-Anfrage prüfen" der Status auf "Genehmigt" gesetzt werden.
4. Nach einiger Verzögerung sollte nun in der Output-Konsole die letzten 20 Tweets, zuoberst der neuste, ausgegeben werden.

## Fortgeschrittene Nutzung (Duplikat-Tweet-Fehler und Behebung)
Siehe hierzu den Text im gleich lautenden Abschnitt unter https://github.com/zhaw-gpi/project-template 