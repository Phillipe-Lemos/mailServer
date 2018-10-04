package de.mail.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import de.mail.domain.Mail;
import de.mail.domain.MailStatus;
import de.mail.service.impl.MailServerImpl;


@RunWith(MockitoJUnitRunner.class)
public class MailServiceImplTest {
	
	private ScheduledExecutorService mailExecutor = Executors.newScheduledThreadPool(5);
	
	@Mock
	private MimeMessage mimeMessage;
	
	@Mock
	private JavaMailSender emailSender;
	
	private MailServer mailServiceImpl;
	
	@Before
	public void setUp() {
		 MockitoAnnotations.initMocks(this);
		 this.mailServiceImpl = new MailServerImpl(emailSender, mailExecutor);
	}
	
	@Test
	public void queueMessageSuccess() throws Exception {
		when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
		final Mail mail = new Mail();
		mail.setEmailText("test");
		mail.setSender("test sender");
		mail.setSubject("subject");
		mail.setTo(Arrays.asList("to"));
		doNothing().when(emailSender).send(mimeMessage);
		mailServiceImpl.queueMessage(mail);
	    Thread.currentThread().sleep(1000);	
		assertThat(mail.getStatus(), equalTo(MailStatus.SUCCESS));
	}
	
	@Test
	public void shouldFailSendMessage() throws Exception {
		when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
		final Mail mail = new Mail();
		mail.setEmailText("test");
		mail.setSender("test sender");
		mail.setSubject("subject");
		mail.setTo(Arrays.asList("to"));
		doThrow(MailSendException.class).when(emailSender).send(mimeMessage);
		mailServiceImpl.queueMessage(mail);
		Thread.currentThread().sleep(1000);	
		assertThat(mail.getStatus(), equalTo(MailStatus.FAIL));
		assertThat(mail.getCurrentRetry(), equalTo(1));
	}
	
	@Test
	public void shouldFailDueMailAuthenticationProblems() throws Exception {
		when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
		final Mail mail = new Mail();
		mail.setEmailText("test");
		mail.setSender("test sender");
		mail.setSubject("subject");
		mail.setTo(Arrays.asList("to"));
		doThrow(MailAuthenticationException.class).when(emailSender).send(mimeMessage);
		mailServiceImpl.queueMessage(mail);
		Thread.currentThread().sleep(1000);	
		assertThat(mail.getStatus(), equalTo(MailStatus.FAIL));
		assertThat(mail.getCurrentRetry(), equalTo(1));
	}
	
	@Test
	public void shouldFailDueMailParseProblems() throws Exception {
		when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
		final Mail mail = new Mail();
		mail.setEmailText("test");
		mail.setSender("test sender");
		mail.setSubject("subject");
		mail.setTo(Arrays.asList("to"));
		doThrow(MailParseException.class).when(emailSender).send(mimeMessage);
		mailServiceImpl.queueMessage(mail);
		Thread.currentThread().sleep(1000);	
		assertThat(mail.getStatus(), equalTo(MailStatus.FAIL));
		assertThat(mail.getCurrentRetry(), equalTo(1));
	} 
	
	@Test
	public void shouldFailDueMailPreparationProblems() throws Exception {
		when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
		final Mail mail = new Mail();
		mail.setEmailText("test");
		mail.setSender("test sender");
		mail.setSubject("subject");
		mail.setTo(Arrays.asList("to"));
		doThrow(MailPreparationException.class).when(emailSender).send(mimeMessage);
		mailServiceImpl.queueMessage(mail);
		Thread.currentThread().sleep(1000);	
		assertThat(mail.getStatus(), equalTo(MailStatus.FAIL));
		assertThat(mail.getCurrentRetry(), equalTo(1));
	} 


}
