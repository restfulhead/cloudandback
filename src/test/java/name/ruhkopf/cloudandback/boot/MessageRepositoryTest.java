package name.ruhkopf.cloudandback.boot;

import static org.junit.Assert.assertNotNull;
import name.ruhkopf.cloudandback.AbstractIntegrationTest;
import name.ruhkopf.cloudandback.boot.sample.Message;
import name.ruhkopf.cloudandback.boot.sample.MessageRepository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageRepositoryTest extends AbstractIntegrationTest
{
	@Autowired
	private MessageRepository repository;

	@Test
	public void shouldAddMessage()
	{
		final Message message = new Message();
		message.setText("bla bla bla");

		repository.save(message);

		assertNotNull(message.getId());
	}
}
