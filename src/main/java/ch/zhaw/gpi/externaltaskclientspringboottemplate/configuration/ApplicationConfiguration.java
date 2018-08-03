package ch.zhaw.gpi.externaltaskclientspringboottemplate.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Verschiedene Einstellungen für die Spring-Applikation
 * @Configuration stellt sicher, dass Spring diese Klasse als Einstellungs-
 * Klasse erkennt
 * 
 * @author scep
 */
@Configuration
/**
 * In der Datei twitter.properties in src/main/resources sind die Authentifizierungs-
 * Angaben zu Twitter enthalten. Diese Datei wird nicht in Github versioniert 
 * (in .gitignore enthalten).
 * 
 * @PropertySource stellt sicher, dass die Werte aus dieser Datei z.B. in
 * TwitterService zur Verfügung steht über @Value-Annotationen
 * 
 * Das twitter.properties-File ist wie folgt aufzubauen (nach dem = Zeichen sind
 * die entsprechenden Werte einzutragen aus https://developer.twitter.com/en/apps -> 
 * App auswählen > "Keys and Tokens".
 * twitter.consumerKey=
 * twitter.consumerSecret=
 * twitter.accessToken=
 * twitter.accessTokenSecret=
 */
@PropertySource("classpath:twitter.properties")
public class ApplicationConfiguration {
    
}
