package name.ruhkopf.cloudandback.controller;

import java.util.List;

import javax.validation.Valid;

import name.ruhkopf.cloudandback.domain.HomeForm;
import name.ruhkopf.cloudandback.service.FlashMessageService;
import name.ruhkopf.cloudandback.service.FlashMessageType;
import name.ruhkopf.cloudandback.service.aws.AWSServiceFactory;
import name.ruhkopf.cloudandback.service.aws.GlacierService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;

@Controller
@RequestMapping("/")
public class HomeController
{
	private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

	private GlacierService glacierService;

	@Autowired
	private AWSServiceFactory awsServiceFactory;

	@Autowired
	private FlashMessageService flashMessageService;

	@RequestMapping(method = RequestMethod.GET)
	public String home(@ModelAttribute final HomeForm homeForm)
	{
		return regionHome(homeForm, null);
	}

	@RequestMapping(value = "{region}", method = RequestMethod.GET)
	public String regionHome(@ModelAttribute final HomeForm homeForm, @PathVariable("region") final String region)
	{
		// FIXME how to best validate region?

		homeForm.setRegion(region);
		return "home";
	}


	@RequestMapping(method = RequestMethod.POST, params = "changeRegion")
	public ModelAndView changeRegion(@Valid final HomeForm homeForm, final BindingResult result, final RedirectAttributes redirect)
	{
		if (result.hasErrors())
		{
			return new ModelAndView("home", "formErrors", result.getAllErrors());
		}

		final String regionName = homeForm.getRegion();
		LOG.info("Changing region to '{}'", regionName);

		this.glacierService = awsServiceFactory.createService(Regions.fromName(regionName));

		flashMessageService.addFlashMessage(FlashMessageType.SUCCESS, "Active region is now " + regionName, redirect);
		return new ModelAndView("redirect:/" + regionName + "/");
	}


	@ModelAttribute("allRegions")
	public List<Region> populateRegions()
	{
		final List<Region> regions = RegionUtils.getRegions();
		return regions;
	}



}
