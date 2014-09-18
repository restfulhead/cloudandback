package name.ruhkopf.cloudandback;

import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.security.SecureRandom;

import name.ruhkopf.cloudandback.service.aws.AWSSettings;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@IntegrationTest
public abstract class AbstractIntegrationTest
{
	@Autowired
	protected AWSSettings awsSettings;

	protected Yaml yaml;

	protected SecureRandom random = new SecureRandom();

	@Before
	public void setup()
	{
		final DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yaml = new Yaml(options);
	}

	protected void checkAWSCredentials()
	{
		assertNotNull(
				"You need to provide your AWS credentials as documented here: http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html",
				awsSettings.getAccessKeyId());
		assertNotNull(
				"You need to provide your AWS credentials as documented here: http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html",
				awsSettings.getSecretKey());
	}

	protected String getRandomName()
	{
		return new BigInteger(130, random).toString(32);
	}

}
