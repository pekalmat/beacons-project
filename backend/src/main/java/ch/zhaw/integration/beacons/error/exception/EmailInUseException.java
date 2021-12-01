package ch.zhaw.integration.beacons.error.exception;

import ch.zhaw.integration.beacons.error.messages.ErrorMessages;

public class EmailInUseException extends Exception{

    public EmailInUseException() {
        super(ErrorMessages.EMAIL_ALREADY_USED);
    }

}
