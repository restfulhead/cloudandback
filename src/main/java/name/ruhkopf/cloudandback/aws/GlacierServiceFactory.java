package name.ruhkopf.cloudandback.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.google.common.base.Preconditions;

@Service
public class GlacierServiceFactory
{
	@Autowired
	private AWSSettings awsSettings;

	/**
	 * Requests a new {@link GlacierService} instance and sets the region. Because setting the region is not thread-safe, you
	 * should use one service instance per region.
	 *
	 * @param region
	 * @return A new service instance
	 */
	public GlacierService createService(final Regions region)
	{
		Region regionLookup = null;
		if (region != null)
		{
			regionLookup = Region.getRegion(region);
			Preconditions.checkNotNull(regionLookup, "Region %s not found", region);
		}

		return createService(awsSettings, regionLookup);
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
