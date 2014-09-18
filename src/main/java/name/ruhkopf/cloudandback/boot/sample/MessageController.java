package name.ruhkopf.cloudandback.boot.sample;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/example")
public class MessageController
{
	private final MessageRepository messageRepository;

	@Autowired
	public MessageController(final MessageRepository messageRepository)
	{
		this.messageRepository = messageRepository;
	}

	@RequestMapping
	public ModelAndView list()
	{
		final Iterable<Message> messages = this.messageRepository.findAll();
		return new ModelAndView("messages/list", "messages", messages);
	}

	@RequestMapping("{id}")
	public ModelAndView view(@PathVariable("id") final Message message)
	{
		return new ModelAndView("messages/view", "message", message);
	}

	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(@ModelAttribute final Message message)
	{
		return "messages/form";
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(@Valid Message message, final BindingResult result, final RedirectAttributes redirect)
	{
		if (result.hasErrors())
		{
			return new ModelAndView("messages/form", "formErrors", result.getAllErrors());
		}
		message = this.messageRepository.save(message);
		redirect.addFlashAttribute("globalMessage", "Successfully created a new message ROCK ON");
		return new ModelAndView("redirect:/{message.id}", "message.id", message.getId());
	}

	@RequestMapping("foo")
	public String foo()
	{
		throw new RuntimeException("Expected exception in controller");
	}

}
