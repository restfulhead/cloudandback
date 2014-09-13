package name.ruhkopf.cloudandback.boot.sample;


public interface MessageRepository
{

	Iterable<Message> findAll();

	Message save(Message message);

	Message findMessage(Long id);

}
