package name.ruhkopf.cloudandback.controller;

import java.util.List;

import name.ruhkopf.cloudandback.model.HomeForm;
import name.ruhkopf.cloudandback.service.FlashMessageService;
import name.ruhkopf.cloudandback.service.FlashMessageType;
import name.ruhkopf.cloudandback.service.aws.AWSServiceLocator;
import name.ruhkopf.cloudandback.service.aws.AWSSettings;
import name.ruhkopf.cloudandback.service.aws.GlacierService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	private AWSServiceLocator awsServiceFactory;

	@Autowired
	private AWSSettings awsSettings;

	@Autowired
	private FlashMessageService flashMessageService;

	@RequestMapping(method = RequestMethod.GET)
	public String home(@ModelAttribute final HomeForm homeForm)
	{
		this.glacierService = null;
		homeForm.setSettings(awsSettings);
		return showRegion(homeForm, null);
	}

	@RequestMapping(value = "regions/{region}", method = RequestMethod.GET)
	public String showRegion(@ModelAttribute final HomeForm homeForm, @PathVariable("region") final String region)
	{
		if (region == null)
		{
			this.glacierService = null;
		}
		else
		{
			final String regionName = homeForm.getRegion();
			final AWSSettings localSettings = homeForm.getSettings();
			this.glacierService = awsServiceFactory.get(Regions.fromName(regionName),
					localSettings.isEmpty() ? null : localSettings).orElse(null);
		}

		homeForm.setServerConfigured(this.glacierService != null);
		homeForm.setRegion(region);
		return "home";
	}

	@RequestMapping(value = "awsSettings", method = RequestMethod.POST)
	public String updateSettings(@ModelAttribute final HomeForm homeForm, final RedirectAttributes attr)
	{
		this.showRegion(homeForm, homeForm.getRegion());

		flashMessageService.addFlashMessage(FlashMessageType.INFO, "AWS credentials for region '" + homeForm.getRegion()
				+ "' updated.", attr);

		return "redirect:/regions/" + homeForm.getRegion();
	}

	@ModelAttribute("allRegions")
	public List<Region> populateRegions()
	{
		final List<Region> regions = RegionUtils.getRegions();
		return regions;
	}



}
