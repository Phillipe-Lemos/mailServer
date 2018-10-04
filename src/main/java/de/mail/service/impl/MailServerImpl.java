package de.mail.service.impl;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import de.mail.domain.Mail;
import de.mail.domain.MailStatus;
import de.mail.service.MailServer;

@Service("mailServer")
public class MailServerImpl implements MailServer {
	
	private static final Logger LOG = LoggerFactory.getLogger(MailServerImpl.class);
	
	@Value("${mail.server.max.retry}")
	private Integer maxRetry;
	
	private final Queue<Mail> mailsToDeliver;
	
    private final JavaMailSender emailSender;
    
    private final ScheduledExecutorService mailExecutor;
    
    @Autowired
	public MailServerImpl(final JavaMailSender emailSender, final ScheduledExecutorService mailExecutor) {
    	this.mailsToDeliver = new ConcurrentLinkedQueue<>();
		this.emailSender = emailSender;
		this.mailExecutor = mailExecutor;
		this.mailExecutor.scheduleWithFixedDelay(() -> sendEmail(), 0, 500, TimeUnit.MILLISECONDS);
		this.mailExecutor.scheduleWithFixedDelay(() -> cleanupQueue(), 100, 1500, TimeUnit.MILLISECONDS);
    }
	
    /**
     * This method runs each 5 seconds and sends e-mails to the SMTP server configured in the 
     * application.properties. Each e-mail is sent an MailStatus that represents the success,
     * fail or not sent status.
     * 
     * Only the e-mails with {@linkplain de.mail.domain.MailStatus#NOT_SENT} status, or 
     * {@linkplain de.mail.domain.MailStatus#FAIL} that has {@linkplain de.mail.domain.Mail#getCurrentRetry()} less
     * than maxRetry will be send.
     */
	private void sendEmail() { 
		mailsToDeliver
		    .stream()
			.filter(mail -> mail.getStatus().equals(MailStatus.NOT_SENT)
        		            || 
				            (mail.getStatus().equals(MailStatus.FAIL) 
		                	 &&
		                	mail.getCurrentRetry() < maxRetry))
	        .forEach(mail -> {
				final MimeMessage message = emailSender.createMimeMessage();
				final boolean hasAttachment = mail.getAttachment().isEmpty();
				try {
					MimeMessageHelper helper = new MimeMessageHelper(message, hasAttachment);
				    helper.setTo(mail.getTo().toArray(new String[]{}));
				    helper.setSubject(mail.getSubject());
				    helper.setText(mail.getEmailText());
				    if(hasAttachment) {
				    	for(Map.Entry<String, String> node : mail.getAttachment().entrySet()) {
				    		try {
				    			helper.addAttachment(node.getKey(), new UrlResource(node.getValue()));
				    		} catch(MalformedURLException malformedURLException) {
				    			LOG.error("Erro while get resource : ", malformedURLException);
				    		}
				    	}
				    }
				    emailSender.send(message);
				    mail.setStatus(MailStatus.SUCCESS);
				} catch(MessagingException | MailException messagingException) {
					LOG.error("Error while creating MimeMessageHelper : ", messagingException);
					mail.setStatus(MailStatus.FAIL);
					mail.setCurrentRetry(mail.getCurrentRetry() + 1);
				}
		    });
	}
	
	/**
	 * Deletes all e-mails from the Queue that were successful sent or that fail 
	 * and the number of retries  had overcome the maxRetry.
	 */
	private void cleanupQueue() {
		mailsToDeliver
			.removeIf(mail -> mail.getStatus().equals(MailStatus.SUCCESS) 
						        ||
						       (mail.getStatus().equals(MailStatus.FAIL) 
						        &&
						        mail.getCurrentRetry() >= maxRetry));
	}
    
	/**
	 * Add the a new mail to a Queue.
	 * 
	 * @param Mail mail new email.
	 */
	@Override
	public void queueMessage(Mail mail) {
		mailsToDeliver.add(mail);
	}

}
