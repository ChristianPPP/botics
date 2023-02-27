package esfot.tesis.botics;

import com.cloudinary.Cloudinary;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
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
	GroupedOpenApi reserveApis() {
		return GroupedOpenApi.builder().group("reserve").pathsToMatch("/**/reserve/**").build();
	}

	@Bean
	GroupedOpenApi commentaryApis() {
		return GroupedOpenApi.builder().group("commentary").pathsToMatch("/**/commentary/**").build();
	}

	@Bean
	GroupedOpenApi inventoryApis() {
		return GroupedOpenApi.builder().group("inventory").pathsToMatch("/**/inventory/**").build();
	}

	@Bean
	GroupedOpenApi ComputerApis() {
		return GroupedOpenApi.builder().group("computer").pathsToMatch("/**/computer/**").build();
	}

	@Bean
	GroupedOpenApi internApis() {
		return GroupedOpenApi.builder().group("intern").pathsToMatch("/**/intern/**").build();
	}

	@Bean
	GroupedOpenApi ticketsApis() {
		return GroupedOpenApi.builder().group("ticket").pathsToMatch("/**/ticket/**").build();
	}

	@Bean
	GroupedOpenApi allApis() {
		return GroupedOpenApi.builder().group("").pathsToMatch("/docs").build();
	}

	@Bean
	GroupedOpenApi labApis() {
		return GroupedOpenApi.builder().group("lab").pathsToMatch("/**/lab/**").build();
	}

	@Bean
	GroupedOpenApi profileApis() {
		return GroupedOpenApi.builder().group("profile").pathsToMatch("/**/profile/**").build();
	}

	@Bean
	GroupedOpenApi authApis() {
		return GroupedOpenApi.builder().group("auth").pathsToMatch("/**/auth/**").build();
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("/messages");
		return messageSource;
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().addServersItem(new Server().url("https://botics.azurewebsites.net"))
				.components(new Components().addSecuritySchemes("Access Token",
						new SecurityScheme().type(SecurityScheme.Type.APIKEY)
								.in(SecurityScheme.In.HEADER).bearerFormat("jwt").name("Authorization")))
				.info(new Info().title("BOTICS API").version("0.0.1").description("Backend para la gestión de laboratorios de la Escuela de Formación de Tecnólogos")
						.license(new License().name("ESFOT").url("https://esfot.epn.edu.ec")))
				.addSecurityItem(new SecurityRequirement().addList("Access Token"));
	}
}
