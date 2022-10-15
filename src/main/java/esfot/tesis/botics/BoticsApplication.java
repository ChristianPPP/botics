package esfot.tesis.botics;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class BoticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoticsApplication.class, args);
	}

	@Autowired
	Environment environment;

	@Bean
	public Cloudinary cloudinaryConfig() {
		Cloudinary cloudinary;
		Map<String, String> config = new HashMap<>();
		config.put("cloud_name", environment.getProperty("com.cloudinary.cloud_name"));
		config.put("api_key", environment.getProperty("com.cloudinary.api_key"));
		config.put("api_secret", environment.getProperty("com.cloudinary.api_secret"));
		cloudinary = new Cloudinary(config);
		return cloudinary;
	}

	@Bean(name="messageSource")
	public ResourceBundleMessageSource bundleMessageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		return messageSource;
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("/messages");
		return messageSource;
	}

}
