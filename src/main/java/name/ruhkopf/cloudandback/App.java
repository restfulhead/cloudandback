package name.ruhkopf.cloudandback;

import name.ruhkopf.cloudandback.boot.sample.InMemoryMessageRespository;
import name.ruhkopf.cloudandback.boot.sample.Message;
import name.ruhkopf.cloudandback.boot.sample.MessageRepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAutoConfiguration
@EnableAsync
@ComponentScan
public class App
{
	@Bean
	public MessageRepository messageRepository()
	{
		return new InMemoryMessageRespository();
	}

	// public MessageR

	@Bean
	public Converter<String, Message> messageConverter()
	{
		return new Converter<String, Message>() {
			@Override
			public Message convert(final String id) {
				return messageRepository().findMessage(Long.valueOf(id));
			}
		};
		//return id -> messageRepository().findMessage(Long.valueOf(id));
	}

	public static void main(final String[] args) throws Exception
	{
		// Setting the JVM TTL for DNS Name Lookups
		// see http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/java-dg-jvm-ttl.html
		java.security.Security.setProperty("networkaddress.cache.ttl", "60");

		SpringApplication.run(App.class, args);
	}

}
