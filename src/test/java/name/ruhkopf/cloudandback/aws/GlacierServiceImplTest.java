package name.ruhkopf.cloudandback.aws;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import name.ruhkopf.cloudandback.AbstractIntegrationTest;
import name.ruhkopf.cloudandback.CommonConstants;
import name.ruhkopf.cloudandback.service.aws.AWSResult;
import name.ruhkopf.cloudandback.service.aws.AWSServiceLocator;
import name.ruhkopf.cloudandback.service.aws.GlacierService;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.glacier.model.CreateVaultResult;
import com.amazonaws.services.glacier.model.DescribeJobResult;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.DescribeVaultResult;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.InvalidParameterValueException;
import com.amazonaws.services.glacier.model.ResourceNotFoundException;
import com.amazonaws.services.glacier.model.UploadArchiveResult;

/**
 * Glacier Service integration test using the real AWS web service.
 *
 * @author Patrick Ruhkopf
 */
public class GlacierServiceImplTest extends AbstractIntegrationTest
{
	private static final Logger LOG = LoggerFactory.getLogger(GlacierServiceImplTest.class);

	@Autowired
	private AWSServiceLocator glacierServiceFactory;

	private GlacierService glacierService;

	@Before
	public void before()
	{
		// make sure AWS credentials have been setup properly
		super.checkAWSCredentials();

		// create service with default region
		glacierService = glacierServiceFactory.get(null);
	}

	@Test
	public void shouldCreateGetAndDeleteVault()
	{
		// should create vault
		final String testName = "TEST-" + getRandomName();
		final CreateVaultResult createRslt = glacierService.createVault(testName);
		assertNotNull(createRslt);
		LOG.info(yaml.dump(createRslt));

		// should find vault
		final DescribeVaultResult description = glacierService.describeValut(testName);
		assertNotNull(description);
		LOG.info(yaml.dump(description));

		// should delete vault
		glacierService.deleteVault(testName);
	}

	@Test(expected = InvalidParameterValueException.class)
	public void shouldNotCreateValut()
	{
		final String invalidName = ";<>$@//";
		glacierService.createVault(invalidName);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldNotFindVault()
	{
		final String testName = "TEST-" + getRandomName();
		glacierService.describeValut(testName);
	}

	@Test
	public void shouldListVaults()
	{
		String marker = null;

		do
		{
			final AWSResult<DescribeVaultOutput> result = glacierService.describeVaults("5", marker);
			marker = result.getMarker();

			final StringBuilder sbr = new StringBuilder();
			result.getItems().forEach((vault) -> sbr.append("---").append(CommonConstants.LINE_SEPARATOR).append(yaml.dump(vault)));
			LOG.info(sbr.toString());
		}
		while (marker != null);
	}

	@Test
	@Ignore
	public void shouldInitiateInventoryJob()
	{
		// this test is difficult to automate, because glacier does not allow to initiate the inventory job for newly created
		// vaults. for now we can run this manually entering a known vault id that was created and indexed before
		final String vaultName = "*** your vault id ***";

		final InitiateJobResult inventoryResult = glacierService.initiateInventoryJob(vaultName);
		LOG.info(yaml.dump(inventoryResult));
	}

	@Test
	@Ignore
	public void shouldInitiateDownload()
	{
		// this test is difficult to automate, because glacier does not allow to initiate the inventory job for newly created
		// vaults. for now we can run this manually entering a known vault id that was created and indexed before
		final String vaultName = "*** your vault id ***";
		final String archiveId = "*** your archive ***";

		final InitiateJobResult archiveResult = glacierService.initiateDownloadJob(vaultName, archiveId, null);
		LOG.info(yaml.dump(archiveResult));
	}

	@Test
	@Ignore
	public void shouldRetriveJobStatus()
	{
		// this test is difficult to automate, because the inventory job can potentially run for multiple hours
		// for now we can run this manually.
		final String jobId = "*** your job id ***";
		final String vaultName = "*** your vault name ***";

		final DescribeJobResult result = glacierService.retrieveJobStatus(vaultName, jobId);
		LOG.info(yaml.dump(result));
	}

	@Test
	// @Ignore
	public void shouldReturnWetherJobIsComplete()
	{
		// this test is difficult to automate, because the inventory job can potentially run for multiple hours
		// for now we can run this manually.
		final String jobId = "*** your job id ***";
		final String vaultName = "*** your vault name ***";

		if (glacierService.isJobComplete(vaultName, jobId))
		{
			LOG.info("Job status is COMPLETE");
		}
		else
		{
			LOG.info("Job status is not complete yet.");
		}
	}

	@Test
	@Ignore
	public void shouldRetriveJobDetailsIfAvailable() throws IOException
	{
		final String jobId = "*** your job id ***";
		final String vaultName = "*** your vault name ***";

		final Optional<GetJobOutputResult> result = glacierService.retrieveJobResultsIfAvailable(vaultName, jobId);
		if (result.isPresent())
		{
			LOG.info(yaml.dump(result.get()));
			LOG.info(IOUtils.toString(result.get().getBody(), "UTF-8"));
		}
		else
		{
			LOG.info("Job stats not completed yet");
		}
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldNotInitiateInventoryJob()
	{
		final String testName = "TEST-" + getRandomName();
		final InitiateJobResult result = glacierService.initiateInventoryJob(testName);
		LOG.info(yaml.dump(result));
	}

	@Test
	public void shouldUploadFileAndDeleteArchive() throws URISyntaxException,
			InterruptedException, ExecutionException
	{
		// create a new vault to store the test file
		final String testName = "TEST-" + getRandomName();
		final CreateVaultResult createRslt = glacierService.createVault(testName);
		assertNotNull(createRslt);
		LOG.info(yaml.dump(createRslt));

		// upload a test file
		final Future<UploadArchiveResult> future = glacierService.uploadFile(testName,
				new File(this.getClass().getResource("/aws/GlacierServiceImplTest-testUploadFile.png").toURI()),
				"testUploadFile.png", null);
		final UploadArchiveResult result = future.get();
		LOG.info(yaml.dump(result));

		// remove it
		glacierService.deleteArchive(testName, result.getArchiveId());

		// ideally we would like to remove the test vault, but this doesn't always work because we can't delete non-empty vaults
		// and the delete archive request isn't completed immediately.
	}

	@Test(expected = InvalidParameterValueException.class)
	public void shouldNotAcceptInvalidChecksum() throws URISyntaxException, IOException
	{
		// create a new vault to store the test file
		final String testName = "TEST-" + getRandomName();
		final CreateVaultResult createRslt = glacierService.createVault(testName);
		assertNotNull(createRslt);
		LOG.info(yaml.dump(createRslt));

		// upload a test file and provide incorrect checksum
		final File fileToUpload = new File(this.getClass().getResource("/aws/GlacierServiceImplTest-testUploadFile.png").toURI());
		try (InputStream inputStream = new FileInputStream(fileToUpload))
		{
			// read file contents
			final byte[] body = new byte[(int) fileToUpload.length()];
			inputStream.read(body);

			// create checksum
			glacierService.uploadFile(testName, new ByteArrayInputStream(body), body.length, "invalid", null, null);
		}
	}

}
