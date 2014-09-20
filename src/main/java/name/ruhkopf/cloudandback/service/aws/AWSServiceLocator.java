package name.ruhkopf.cloudandback.service.aws;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.google.common.base.Preconditions;

@Service
public class AWSServiceLocator
{
	private static final Logger LOG = LoggerFactory.getLogger(AWSServiceLocator.class);

	@Autowired
	private AWSSettings awsSettings;

	private final Map<Region, GlacierService> servicesByRegion = new HashMap<>();

	/**
	 * Returns a {@link GlacierServiceImpl} instance for the given region. Because setting the region is not thread-safe, you
	 * should use one service instance per region. This function therefore instantiates a new instance and sets the region if
	 * necessary.
	 *
	 * @param region The region
	 * @param localSettings Optional AWS settings to pass in
	 * @return A new service instance
	 */
	public Optional<GlacierService> get(final Regions region, final AWSSettings localSettings)
	{
		Preconditions.checkNotNull(region, "region must not be null");

		Region regionLookup = null;
		if (region != null)
		{
			regionLookup = Region.getRegion(region);
			Preconditions.checkNotNull(regionLookup, "Region %s not found", region);
		}

		if (servicesByRegion.get(regionLookup) != null)
		{
			return Optional.of(servicesByRegion.get(regionLookup));
		}
		else
		{
			// prefer local settings
			GlacierService service;
			if (localSettings != null && !StringUtils.isEmpty(localSettings.getAccessKeyId())
					&& !StringUtils.isEmpty(localSettings.getSecretKey()))
			{
				LOG.info("Instantiating a new service for {} with local settings", regionLookup);
				service = createService(localSettings, regionLookup);
			}
			else if (awsSettings != null && !StringUtils.isEmpty(awsSettings.getAccessKeyId())
					&& !StringUtils.isEmpty(awsSettings.getSecretKey()))
			{
				LOG.info("Instantiating a new service for {} with global settings", regionLookup);
				service = createService(awsSettings, regionLookup);
			}
			else
			{
				LOG.info("Cannot instantiate new service because settings are missing.");
				service = null;
			}

			servicesByRegion.put(regionLookup, service);
			return service == null ? Optional.empty() : Optional.of(service);
		}
	}

	/**
	 * Factory method to be implemented by Spring.
	 *
	 * @param settings AWS settings
	 * @param region The region
	 * @return a new service instance for the given region and configured with the given settings
	 */
	@Lookup
	protected GlacierService createService(final AWSSettings settings, final Region region)
	{
		throw new UnsupportedOperationException();
	}
}
