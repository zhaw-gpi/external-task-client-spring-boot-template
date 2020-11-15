package ch.zhaw.gpi.externaltaskclientspringboottemplate.externaltaskclients;

import ch.zhaw.gpi.externaltaskclientspringboottemplate.handlers.SendTweetHandler;
import javax.annotation.PostConstruct;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Camunda External Task Client, welcher das Topic SendTweet abonniert
 * 
 * @author scep
 */
@Component
public class TweetSendingExternalTaskClient {
    // Variable für ein ExternalTaskClient-Objekt
    private ExternalTaskClient externalTaskClient;

    // Variable für das automatisch verdrahtete Handler-Objekt
    @Autowired
    private SendTweetHandler tweetSenderHandler;

    // Initiieren des ExternalTaskClients. Dank @PostConstruct (und Bezeichnung
    // init) geschieht dies erst nach dem Autowiring
    @PostConstruct
    private void init() {
        /**
         * 1. Eine neue External Task Client-Instanz erstellen und
         * konfigurieren mit dem ExternalTaskClientBuilder
         * https://github.com/camunda/camunda-external-task-client-java/blob/1.0.0/client/src/main/java/org/camunda/bpm/client/ExternalTaskClientBuilder.java
         */
        externalTaskClient = ExternalTaskClient
                .create() // Den ExternalTaskClientBuilder initiieren
                .baseUrl("http://localhost:8080/engine-rest") // URL der REST API der Process Engine
                .workerId("java-sendtweet") // Eindeutiger Name, damit die Process Engine "weiss", wer einen bestimmten Task gelocked hat
                .maxTasks(10) // Wie viele Tasks sollen maximal auf einen "Schlag" (Batch) gefetched werden
                .lockDuration(2000) // 2 Sekunden lang sind die Tasks gelocked für andere External Task Clients
                .build(); // Die External Task Client-Instanz mit den vorhergehenden Angaben erstellen

        /**
         * 2. Der External Task Client kann sich für mehrere Topics
         * registrieren, in diesem Beispiel nur für das "SendTweet"-Topic.
         * Registrieren bedeutet hierbei, dass der Client in regelmässigen
         * Abständen (siehe lockDuration oben) bei der Process Engine nach
         * neuen Tasks für den Topic anfrägt. Falls welche vorhanden sind,
         * werden diese bezogen (Fetch) und blockiert (lock), so dass kein
         * anderer Client die Aufgaben auch bearbeiten könnte (=>
         * Konflikte). Nun werden sie von einem External Task Handler (die
         * eigentliche Business Logik) abgearbeitet und der Process Engine
         * als erledigt (complete) gemeldet. Die Registration umfasst die
         * folgenden Schritte:
         */
        /**
         * a) Für jedes Topic ist eine External Task Handler-Implementation
         * anzugeben, welche wie hier gezeigt als eigene Klasse
         * SendTweetHandler implementiert sein kann und eine Instanz davon
         * hier erstellt wird. Oder wer sich mit Lambda-Expressions
         * auskennt, kann dies auch kürzer haben wie z.B. gezeigt in
         * https://docs.camunda.org/get-started/quick-start/service-task/#implement-an-external-task-worker
         * https://github.com/camunda/camunda-external-task-client-java/blob/1.0.0/client/src/main/java/org/camunda/bpm/client/topic/TopicSubscriptionBuilder.java
         */
        /**
         * b) Das Registrieren geschieht über einen Fluent Builder wie schon
         * in Schritt 1. Es ist im Folgenden zweimal aufgeführt für zwei
         * Topics. Er umfasst: - Festlegen des Topics (subscribe) - Die
         * Handler-Klasse (handler), welche gefetchte Tasks abarbeitet - Das
         * eigentliche Registrieren (open)
         */
        externalTaskClient
                .subscribe("SendTweet")
                .handler(tweetSenderHandler)
                .open();
    }
}
