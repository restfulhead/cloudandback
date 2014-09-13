package name.ruhkopf.cloudandback.aws;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.glacier.model.ListVaultsResult;

@Service
@Scope("prototype")
public class GlacierService
{
	private final AmazonGlacierClient client;

	public GlacierService(final AWSSettings awsSettings)
	{
		this(awsSettings, null);
	}

	public GlacierService(final AWSSettings awsSettings, final Region region)
	{
		client = new AmazonGlacierClient(awsSettings);

		if (region != null)
		{
			client.setRegion(region);
		}
	}

	public AWSResult<DescribeVaultOutput> listVaults(final String limit, final String marker)
	{
		final ListVaultsRequest request = new ListVaultsRequest().withLimit(limit).withMarker(marker);
		final ListVaultsResult listVaultsResult = client.listVaults(request);

		final List<DescribeVaultOutput> vaultList = listVaultsResult.getVaultList();
		return new AWSResult<DescribeVaultOutput>(listVaultsResult.getMarker(), vaultList);
	}
}
