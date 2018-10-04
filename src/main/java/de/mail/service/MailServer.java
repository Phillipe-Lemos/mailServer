package de.mail.service;

import de.mail.domain.Mail;

public interface MailServer {

	void queueMessage(Mail mail);
	
}
