package name.ruhkopf.cloudandback.controller;

import java.util.List;

import name.ruhkopf.cloudandback.model.HomeForm;
import name.ruhkopf.cloudandback.service.FlashMessageService;
import name.ruhkopf.cloudandback.service.aws.AWSServiceLocator;
import name.ruhkopf.cloudandback.service.aws.GlacierService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	private FlashMessageService flashMessageService;

	@RequestMapping(method = RequestMethod.GET)
	public String home(@ModelAttribute final HomeForm homeForm)
	{
		return regionHome(homeForm, null);
	}

	@RequestMapping(value = "{region}", method = RequestMethod.GET)
	public String regionHome(@ModelAttribute final HomeForm homeForm, @PathVariable("region") final String region)
	{
		if (region == null)
		{
			this.glacierService = null;
		}
		else
		{
			final String regionName = homeForm.getRegion();
			this.glacierService = awsServiceFactory.get(Regions.fromName(regionName));
		}

		homeForm.setRegion(region);
		return "home";
	}


	@ModelAttribute("allRegions")
	public List<Region> populateRegions()
	{
		final List<Region> regions = RegionUtils.getRegions();
		return regions;
	}



}
