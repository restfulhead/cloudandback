package name.ruhkopf.cloudandback.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class FlashMessageService
{
	public void addFlashMessage(final FlashMessageType type, final String message, final RedirectAttributes attr)
	{
		final String key = "globalFlashMessages" + type;
		@SuppressWarnings("unchecked")
		final List<String> existingMessages = (List<String>) attr.getFlashAttributes().get(key);

		final List<String> messages = existingMessages == null ? new ArrayList<>() : new ArrayList<>(existingMessages);
		messages.add(message);
		attr.addFlashAttribute(key, messages);
	}

}
