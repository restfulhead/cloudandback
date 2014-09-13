package name.ruhkopf.cloudandback.aws;

import name.ruhkopf.cloudandback.AbstractIntegrationTest;
import name.ruhkopf.cloudandback.aws.AWSResult;
import name.ruhkopf.cloudandback.aws.GlacierService;
import name.ruhkopf.cloudandback.aws.GlacierServiceFactory;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.glacier.model.DescribeVaultOutput;

/**
 * Glacier Service integration test using the real AWS web service.
 *
 * @author Patrick Ruhkopf
 */
public class GlacierServiceTest extends AbstractIntegrationTest
{
	private static final Logger LOG = LoggerFactory.getLogger(GlacierServiceTest.class);

	@Autowired
	private GlacierServiceFactory glacierServiceFactory;

	private GlacierService glacierService;

	@Before
	public void before()
	{
		// make sure AWS credentials have been setup properly
		super.checkAWSCredentials();

		// create service with default region
		glacierService = glacierServiceFactory.createService(null);
	}

	@Test
	public void shouldListVaults()
	{
		String marker = null;

		do
		{
			final AWSResult<DescribeVaultOutput> result = glacierService.listVaults("5", marker);
			marker = result.getMarker();

			result.forEach((vault) -> LOG.info(yaml.dump(vault)));
		}
		while (marker != null);
	}
}
