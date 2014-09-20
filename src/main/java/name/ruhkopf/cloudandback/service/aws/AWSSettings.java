package name.ruhkopf.cloudandback.service.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import com.amazonaws.auth.AWSCredentials;

@Component
@ConfigurationProperties(prefix = "aws")
public class AWSSettings implements AWSCredentials
{
	private String accessKeyId;
	private String secretKey;

	public String getAccessKeyId()
	{
		return accessKeyId;
	}

	public void setAccessKeyId(final String accessKeyId)
	{
		this.accessKeyId = accessKeyId;
	}

	public String getSecretKey()
	{
		return secretKey;
	}

	public void setSecretKey(final String secretKey)
	{
		this.secretKey = secretKey;
	}

	@Override
	public String getAWSAccessKeyId()
	{
		return getAccessKeyId();
	}

	@Override
	public String getAWSSecretKey()
	{
		return getSecretKey();
	}

	public boolean isEmpty()
	{
		return StringUtils.isEmpty(getSecretKey()) || StringUtils.isEmpty(getAWSAccessKeyId());
	}
}
