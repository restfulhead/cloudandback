package name.ruhkopf.cloudandback.model;

import java.util.ArrayList;

import name.ruhkopf.cloudandback.service.aws.AWSResult;

import com.amazonaws.services.glacier.model.DescribeVaultOutput;


public class HomeForm
{
	private String region;
	private AWSResult<DescribeVaultOutput> vaults = new AWSResult<>(null, new ArrayList<>());

	public String getRegion()
	{
		return region;
	}

	public void setRegion(final String region)
	{
		this.region = region;
	}

	public AWSResult<DescribeVaultOutput> getVaults()
	{
		return vaults;
	}

	public void setVaults(final AWSResult<DescribeVaultOutput> vaults)
	{
		this.vaults = vaults;
	}

}
