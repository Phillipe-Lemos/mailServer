package de.mail.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import de.mail.domain.Mail;
import de.mail.service.MailServer;

@RestController
@RequestMapping("/mail")
public class MailController {
	
	private static final Logger LOG = LoggerFactory.getLogger(MailController.class);
	
	private final MailServer mailServer;
	
	@Autowired
	public MailController(final MailServer mailServer) {
		this.mailServer = mailServer;
	}

	@PostMapping("/v1/")
	public ResponseEntity<?> sendMail(@RequestBody @Valid Mail mail) {
		mailServer.queueMessage(mail);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
    @ExceptionHandler({IllegalArgumentException.class})
	private ResponseEntity<String> handlerIllegalArgumentException(IllegalArgumentException illegal) {
    	LOG.error(illegal.getMessage(), illegal);
		return new ResponseEntity<String>(illegal.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
    @ExceptionHandler({JsonParseException.class, 
    	               JsonMappingException.class, 
    	               InvalidFormatException.class})
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    private void handleJsonException(JsonParseException ex) {
    	LOG.error("Json problems", ex);
    }
    
    @ExceptionHandler({MismatchedInputException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    private void handleJsonFormatException(MismatchedInputException ex) {
    	LOG.error("Problems with inputstreams :", ex);
    }
	
}
