package ch.zhaw.gpi.externaltaskclientspringboottemplate.handlers;

import ch.zhaw.gpi.externaltaskclientspringboottemplate.services.TwitterService;
import twitter4j.TwitterException;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Enthält die Business Logik, welche für vom External Task Client gefetchte
 * Tasks abarbeitet und der Process Engine als erledigt mitteilt. In diesem
 * Beispiel umfasst dies das Senden eines Tweets über den Twitter Service.
 *
 * @author scep
 */
@Component
public class SendTweetHandler implements ExternalTaskHandler {

    // Das eigentliche Posten des Tweets ist ausgelagert an eine Service-Klasse,
    // die hier instanziert wird.
    @Autowired
    TwitterService twitterService;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        // Tweet Content aus Prozessvariable auslesen
        String tweetContent = (String) externalTask.getVariable("tweetContent");

        try {
            // Den Twitter-Service aufrufen, um einen Tweet zu posten (Status updaten)
            twitterService.updateStatus(tweetContent);

            // Den Task erledigen
            externalTaskService.complete(externalTask);
        } catch (TwitterException te) {
            // Bei einem Duplicate-Post soll ein BPMN-Error ausgelöst werden
            if(te.getErrorCode() == 187){
                externalTaskService.handleBpmnError(externalTask, "DuplicateTweet");
            } else {
                // Bei allen übrigen Fehler soll dieser an die Process Engine zurück gemeldet werden als Incident
                externalTaskService.handleFailure(externalTask, "Fehler beim Posten des Tweets", te.getLocalizedMessage(), 0, 1);
            }
        }
    }
}
