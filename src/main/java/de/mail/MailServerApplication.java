package de.mail;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.mail.util.LoggingInterceptor;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class MailServerApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(MailServerApplication.class, args);
	}
	
	@Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new LoggingInterceptor()).addPathPatterns("/**");
    }
	
    @Bean
    public Docket swaggerPersonApi1(){
        return new Docket(DocumentationType.SWAGGER_2)
    		.groupName("mail-api-1")
            	.select()
            	.apis(RequestHandlerSelectors.basePackage(getClass().getPackage().getName()))
            	.paths(regex("/mail/v1.*"))
            	.build()
            	.apiInfo(new ApiInfoBuilder()
            			        .version("1.0")
            			        .title("Mail API")
            			        .description("Documentation Mail API v1").build());
    }
}
