package name.ruhkopf.cloudandback.controller;

import name.ruhkopf.cloudandback.model.HomeForm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SeedStarterMngController
{

	@RequestMapping({ "/seedstartermng" })
	public String showSeedstarters(final HomeForm seedStarter)
	{
		// seedStarter.setTest("Hallo");
		return "seedstartermng";
	}

	@RequestMapping(value = "/seedstartermng", params = { "save" })
	public String saveSeedstarter(final HomeForm seedStarter, final BindingResult bindingResult, final ModelMap model)
	{
		if (bindingResult.hasErrors())
		{
			return "seedstartermng";
		}
		model.clear();
		return "redirect:/seedstartermng";
	}
}
