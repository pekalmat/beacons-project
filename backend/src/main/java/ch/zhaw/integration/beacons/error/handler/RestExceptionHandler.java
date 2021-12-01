package ch.zhaw.integration.beacons.error.handler;

import ch.zhaw.integration.beacons.error.exception.BadRequestException;
import ch.zhaw.integration.beacons.error.exception.EmailInUseException;
import ch.zhaw.integration.beacons.error.messages.ErrorMessages;
import ch.zhaw.integration.beacons.error.response.ApiError;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.error(ex.getClass().toString() + " : " + ex.getMessage());
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
        LOGGER.error(ex.getClass().toString() + " : " + ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(NotFoundException ex) {
        LOGGER.error(ex.getClass().toString() + " : " + ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
        LOGGER.error(ex.getClass().toString() + " : " + ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
        apiError.setMessage(ErrorMessages.EMAIL_OR_PW_WRONG);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EmailInUseException.class)
    protected ResponseEntity<Object> handleEmailInUseError(EmailInUseException ex) {
        LOGGER.error(ex.getClass().toString() + " : " + ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllOtherExceptions(Exception ex) {
        LOGGER.error(ex.getClass().toString() + " : " + ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage(ex.getMessage());
        if(ex.getMessage() == null){
            ex.printStackTrace();
        }
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<Object>(apiError, apiError.getStatus());
    }
}
