package de.mail.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import de.mail.service.MailServer;

@RunWith(SpringRunner.class)
@ActiveProfiles(value="test")
@WebMvcTest(MailController.class)
public class MailControllerTest {

	private static final String CONTEXT ="/mail/v1/";
	
	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	
	@MockBean
	private MailServer mailServer;
	
	@Before
	public void setUp(){
        MockitoAnnotations.initMocks(this);
        mockMvc  = MockMvcBuilders
                     .webAppContextSetup(context)
                     .build();
	}
	
	@Test
	public void sendMailSuccessWithoutattachment() throws Exception {
		final String content = "{\"sender\":\"test@gmail.com\", \"subject\":\"test\", \"emailText\":\"email text\", \"to\":[\"to@gmail.com\"]}";
		mockMvc
	       .perform(post(CONTEXT)
		              .content(content)
		              .contentType(MediaType.APPLICATION_JSON))
	       .andExpect(MockMvcResultMatchers.status().isNoContent());
	}
	
	@Test
	public void sendMailSuccessWithattachment() throws Exception {
		final String content = "{\"sender\":\"test@gmail.com\", \"subject\":\"test\", \"emailText\":\"email text\", \"to\":[\"to@gmail.com\"], \"attachment\":{\"image\":\"http://localhost/image.jpg\"}}";
		mockMvc
	       .perform(post(CONTEXT)
		              .content(content)
		              .contentType(MediaType.APPLICATION_JSON))
	       .andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	
	@Test
	public void sendMailFailDueJson() throws Exception {
		final String content = "{\"sender\"\"test@gmail.com\", subject\":\"test\", \"emailText\":\"email text\", \"to\":[\"to@gmail.com\"]}";
		mockMvc
	       .perform(post(CONTEXT)
		              .content(content)
		              .contentType(MediaType.APPLICATION_JSON))
	       .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
	}
	
	@Test
	public void sendMailFailDueInputNotMapping() throws Exception {
		final String content = "{\"aaaa\":\"test@gmail.com\"}";
		mockMvc
	       .perform(post(CONTEXT)
		              .content(content)
		              .contentType(MediaType.APPLICATION_JSON))
	       .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
}
