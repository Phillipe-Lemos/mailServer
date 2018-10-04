package de.mail.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mail implements Serializable {
	
	private static final long serialVersionUID = 3090196813272852759L;

	@NotNull
	@JsonProperty("sender")
	private String sender;
	
	@NotNull
	@JsonProperty("subject")
	private String subject;
	
	@NotNull
	@JsonProperty("emailText")
	private String emailText;
	
	@JsonProperty("attachment")
	private Map<String,String> attachment;

	@NotNull
	@JsonProperty("to")
	private List<String> to;
	
	@JsonIgnore
	private MailStatus status;
	
	@JsonIgnore
	private Integer currentRetry;
	
	public Mail() {
	    this.status = MailStatus.NOT_SENT;
	    this.currentRetry = 0;
	    this.to = new ArrayList<>();
	    this.attachment = new HashMap<>();
	}

	public Mail(String sender, String subject, String emailText, Map<String, String> attachment, List<String> to) {
		this();
		this.sender = sender;
		this.subject = subject;
		this.emailText = emailText;
		if(attachment != null) {
			this.attachment.putAll(attachment);
		}
		if(to != null && !to.isEmpty()) {
			this.to.addAll(to);	
		}
		
	}
}
