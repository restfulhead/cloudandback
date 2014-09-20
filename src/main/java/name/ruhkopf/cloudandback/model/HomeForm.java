package name.ruhkopf.cloudandback.model;

import java.util.ArrayList;

import name.ruhkopf.cloudandback.service.aws.AWSResult;
import name.ruhkopf.cloudandback.service.aws.AWSSettings;

import com.amazonaws.services.glacier.model.DescribeVaultOutput;


public class HomeForm
{
	private String region;
	private AWSResult<DescribeVaultOutput> vaults = new AWSResult<>(null, new ArrayList<>());
	private AWSSettings settings = new AWSSettings();
	private boolean serverConfigured;

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

	public AWSSettings getSettings()
	{
		return settings;
	}

	public void setSettings(final AWSSettings settings)
	{
		this.settings = settings;
	}

	public boolean isServerConfigured()
	{
		return serverConfigured;
	}

	public void setServerConfigured(final boolean serverConfigured)
	{
		this.serverConfigured = serverConfigured;
	}

}
