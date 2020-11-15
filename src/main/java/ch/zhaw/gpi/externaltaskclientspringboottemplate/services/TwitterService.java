package ch.zhaw.gpi.externaltaskclientspringboottemplate.services;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.Status;

/**
 * Stellt eine Verbindung zu Twitter her über Twitter4J und
 * exponiert zwei Methoden der Twitter API: - Tweeting (Update Status) -
 * Ausgaben einer Liste der letzten Tweets (Timeline)
 *
 * @author scep
 */
@Service
public class TwitterService {

    // Variable für ein Twitter-Objekt (Verbindung zur Twitter API mittels Twitter4J)
    private Twitter twitter;

    /**
     * Baut die Verbindung zur Twitter API auf beim Initialisieren dieser
     * Service-Klasse. PostConstruct (und die init-Bezeichnung) stellen sicher,
     * dass die Methode erst ausgeführt wird, wenn z.B. die twitter4j.properties-Datei
     * mit den Zugangsinformationen bereits ausgelesen wurde
     */
    @PostConstruct
    private void init() {
        try {
            /**
             * Über die TwitterFactory kann man sich eine Singleton-Instanz des
             * Twitter-Objekts erstellen lassen, welche auf den in twitter4j.properties
             * hinterlegten Authentifizierungsinformationen basierend erstellt wird
             */
            twitter = TwitterFactory.getSingleton();

            // In Konsole als "Beweis", dass Anmeldung geklappt hat, einige Informationen
            // zum angemeldeten Benutzer ausgeben
            List<Status> statuses = twitter.getHomeTimeline();
            System.out.println("TWITTER: Erfolgreich angemeldet. Im Folgenden die letzten maximal 20 Tweets");
            for (Status status : statuses) {
                System.out.println("TWITTER: " + status.getText());
            }
        } catch (Exception e) {
            System.err.println("TWITTER: Anmeldung fehlgeschlagen. Meldung: " + e.getLocalizedMessage());
        }
    }

    /**
     * Postet einen neuen Tweet auf Twitter (= statusUpdate)
     *
     * @param statusText Der zu postende Text
     * @throws java.lang.Exception
     */
    public void updateStatus(String statusText) throws TwitterException {
            // Tweet posten
            Status status = twitter.updateStatus(statusText);

            // Als Bestätigung in der Output-Konsole den geposteten Tweet zurück gegeben
            System.out.println("TWITTER: Erfolgreich getweetet:" + status.getText());
    }
}
