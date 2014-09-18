package name.ruhkopf.cloudandback.boot;

import static org.junit.Assert.assertNotNull;
import name.ruhkopf.cloudandback.AbstractIntegrationTest;
import name.ruhkopf.cloudandback.service.aws.AWSSettings;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Unit test for simple App.
 */
public class AppTest extends AbstractIntegrationTest
{
	@Autowired
	AWSSettings awsSettings;

	@Test
	public void shouldHaveAWSCredentials()
	{
		assertNotNull(
				"You need to provide your AWS credentials as documented here: http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html",
				awsSettings.getAccessKeyId());
		assertNotNull(
				"You need to provide your AWS credentials as documented here: http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html",
				awsSettings.getSecretKey());
	}
}
