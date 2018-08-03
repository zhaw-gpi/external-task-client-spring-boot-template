package ch.zhaw.gpi.externaltaskclientspringboottemplate.services;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.ApiException;
import org.springframework.social.twitter.api.TimelineOperations;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;

/**
 * Stellt eine Verbindung zu Twitter her über Spring Social Twitter und
 * exponiert zwei Methoden der Twitter API: - Tweeting (Update Status) -
 * Ausgaben einer Liste der letzten Tweets (Timeline)
 *
 * @author scep
 */
@Service
public class TwitterService {

    // Variable für ein Twitter-Objekt (Verbindung zur Twitter API)
    private Twitter twitter;

    // Variable für ein TwitterProfile-Objekt (Profil des angemeldeten Benutzers)
    private TwitterProfile twitterProfile;

    // Variable für ein TimelineOperations-Objekt (Sub-API für Timeline lesen und Tweets posten)
    private TimelineOperations timelineOperations;

    // Werte aus twitter.properties (siehe ApplicationConfiguration.java) für
    // die Authentifizierung bei der Twitter API
    @Value("${twitter.consumerKey}")
    private String consumerKey;
    @Value("${twitter.consumerSecret}")
    private String consumerSecret;
    @Value("${twitter.accessToken}")
    private String accessToken;
    @Value("${twitter.accessTokenSecret}")
    private String accessTokenSecret;

    /**
     * Baut die Verbindung zur Twitter API auf beim Initialisieren dieser
     * Service-Klasse. PostConstruct (und die init-Bezeichnung) stellen sicher,
     * dass die Methode erst ausgeführt wird, wenn z.B. @Value bereits
     * ausgeführt (injected) wurden
     */
    @PostConstruct
    private void init() {
        try {
            /**
             * Erstellen einer neuen TwitterTemplate-Instanz als Basis zur
             * Kommunikation mit der Twitter API Source:
             * https://github.com/spring-projects/spring-social-twitter/blob/master/spring-social-twitter/src/main/java/org/springframework/social/twitter/api/impl/TwitterTemplate.jav
             */
            twitter = new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);

            // TwitterProfile-Objekt setzen
            twitterProfile = twitter.userOperations().getUserProfile();

            // TimelineOperations-Objekt setzen
            timelineOperations = twitter.timelineOperations();

            // In Konsole als "Beweis", dass Anmeldung geklappt hat, einige Informationen
            // zum angemeldeten Benutzer ausgeben
            System.out.println("Anmeldung bei Twitter erfolgreich. Angemeldeter Benutzer: "
                    + twitterProfile.getScreenName()
                    + " ("
                    + twitterProfile.getStatusesCount()
                    + " Posts)");
        } catch (ApiException e) {
            System.err.println("Anmeldung bei Twitter fehlgeschlagen. Meldung: " + e.getLocalizedMessage());
        }
    }

    /**
     * Postet einen neuen Tweet auf Twitter (= statusUpdate) Source:
     * https://github.com/spring-projects/spring-social-twitter/blob/master/spring-social-twitter/src/main/java/org/springframework/social/twitter/api/TimelineOperations.java
     *
     * @param statusText Der zu postende Text
     * @throws java.lang.Exception
     */
    public void updateStatus(String statusText) throws Exception {
        // Versucht, den Tweet zu posten oder "behandelt" Fehler durch Ausgabe von Meldungen in der Konsole
        try {
            // Tweet posten
            timelineOperations.updateStatus(statusText);

            // Als Bestätigung in der Output-Konsole werden die letzten 20 Tweets zurück gegeben
            printlnLastTweets();
        } catch (ApiException e) {
            // Fehler ausgeben in Konsole (vereinfacht das Testen)
            System.err.println("Tweet posten fehlgeschlagen: " + e.getLocalizedMessage());

            // Fehler an aufrufende Methode zurück geben
            throw new Exception("Tweet posten fehlgeschlagen", e);
        }
    }

    /**
     * Gibt die letzten 20 Tweets der Timeline in die Output-Konsole aus und den
     * Link zur Twitter-Timeline
     *
     * @throws java.lang.Exception
     */
    public void printlnLastTweets() throws Exception {
        try {
            // Liste aller Tweets erhalten von Twitter API
            List<Tweet> tweets = timelineOperations.getUserTimeline();

            // Einleitungstext ausgeben
            System.out.println("Die neuesten 20 Tweets sind:");

            // Für jeden Tweet den Tweet-Text ausgeben
            for (Tweet tweet : tweets) {
                System.out.println("  - " + tweet.getText());
            }

            // Link zur Twitter-Timeline ausgeben
            System.out.println("Timeline von " + twitterProfile.getScreenName() + ": " + twitterProfile.getProfileUrl());
        } catch (ApiException e) {
            // Fehler ausgeben in Konsole (vereinfacht das Testen)
            System.err.println("Auslesen der Twitter-Timeline fehlgeschlagen: " + e.getLocalizedMessage());
        }
    }
}
