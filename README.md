# Goal
Design and write a MicroService responsible of Asynchronous sending of Emails. It will be used by
various other components to send mail to end customers.

- REST API with synchronous acknowledgment with only one method for sending new Mail
- Mail with attachment should be possible. (Attachment Content will be provided in the request by a
URI pointing to the actual document binaries)
- Queuing until successful response from SMTP Server. Max Retry configurable.
- No Authentication required.

- API Definition using Swagger
- Implementation using Maven/Gradle, Spring Boot, and any other frameworks you may think
useful.

# How to build the application
mvn clean package

# How to run the application
1) Running application through maven : mvn spring-boot:run

2) Running application through java command line : java -jar target/mailServer-0.0.1-SNAPSHOT.jar 

# Documentation
You can find the documentation from the API here http://localhost:8080/swagger-ui.html

#Json input examples :
Witout attachment

	{
	  "sender":"sender@gmail.com",
	  "subject":"This is a test",
	  "emailText":"This is the body of the test",
	  "to":["receiver@gmail.com"]
	}
	
With attachment

	{
	  "sender":"sender@gmail.com",
	  "subject":"This is a test",
	  "emailText":"This is the body of the test",
	  "to":["receiver@gmail.com"],
	  "attachment":{"name of attachment":"URL from attachment"}
	}	

# SMTP server configuration
Before run the application it is necessary to configure the SMTP server details in application.properties 
file.
You should provide the follow information :
	spring.mail.host = SMTP server host
	spring.mail.port = SMTP server port
	spring.mail.username = user name
	spring.mail.password = password
	  