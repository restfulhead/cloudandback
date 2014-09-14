package name.ruhkopf.cloudandback.aws;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Region;
import com.amazonaws.services.elasticache.model.InvalidParameterValueException;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.TreeHashGenerator;
import com.amazonaws.services.glacier.model.CreateVaultRequest;
import com.amazonaws.services.glacier.model.CreateVaultResult;
import com.amazonaws.services.glacier.model.DeleteArchiveRequest;
import com.amazonaws.services.glacier.model.DeleteVaultRequest;
import com.amazonaws.services.glacier.model.DescribeJobRequest;
import com.amazonaws.services.glacier.model.DescribeJobResult;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.DescribeVaultRequest;
import com.amazonaws.services.glacier.model.DescribeVaultResult;
import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.JobParameters;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.glacier.model.ListVaultsResult;
import com.amazonaws.services.glacier.model.UploadArchiveRequest;
import com.amazonaws.services.glacier.model.UploadArchiveResult;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * Thin wrapper around {@link AmazonGlacierClient}.
 *
 * @author Patrick Ruhkopf
 */
@Service
@Scope("prototype")
public class GlacierServiceImpl implements GlacierService
{
	@Autowired
	private LogProgressListenerFactory processListenerFactory;

	private final AmazonGlacierClient client;

	private static final long SINGLE_PART_UPLOAD_MAX_BYTES = 107374182400L;

	public GlacierServiceImpl(final AWSSettings awsSettings)
	{
		this(awsSettings, null);
	}

	public GlacierServiceImpl(final AWSSettings awsSettings, final Region region)
	{
		client = new AmazonGlacierClient(awsSettings);

		if (region != null)
		{
			client.setRegion(region);
		}
	}

	@Override
	public AWSResult<DescribeVaultOutput> describeVaults(final String limit, final String marker)
	{
		final ListVaultsRequest request = new ListVaultsRequest().withLimit(limit).withMarker(marker);
		final ListVaultsResult listVaultsResult = client.listVaults(request);

		final List<DescribeVaultOutput> vaultList = listVaultsResult.getVaultList();
		return new AWSResult<DescribeVaultOutput>(listVaultsResult.getMarker(), vaultList);
	}

	@Override
	public CreateVaultResult createVault(final String name)
	{
		final CreateVaultRequest request = new CreateVaultRequest().withVaultName(name);
		return client.createVault(request);
	}

	@Override
	public DescribeVaultResult describeValut(final String name)
	{
		final DescribeVaultRequest request = new DescribeVaultRequest().withVaultName(name);

		return client.describeVault(request);
	}

	@Override
	public void deleteVault(final String name)
	{
		final DeleteVaultRequest request = new DeleteVaultRequest().withVaultName(name);
		client.deleteVault(request);
	}

	@Override
	public InitiateJobResult initiateInventoryJob(final String vaultName)
	{
		final InitiateJobRequest initJobRequest = new InitiateJobRequest().withVaultName(vaultName).withJobParameters(
				new JobParameters().withType("inventory-retrieval"));
		return client.initiateJob(initJobRequest);
	}

	@Override
	public DescribeJobResult retrieveJobStatus(final String vaultName, final String jobId)
	{
		final DescribeJobRequest describeJobRequest = new DescribeJobRequest(vaultName, jobId);
		return client.describeJob(describeJobRequest);
	}

	@Override
	public boolean isJobComplete(final String vaultName, final String jobId)
	{
		final DescribeJobResult result = retrieveJobStatus(vaultName, jobId);
		return result.getCompleted();
	}

	@Override
	public GetJobOutputResult retrieveJobResults(final String vaultName, final String jobId)
	{
		final GetJobOutputRequest jobOutputRequest = new GetJobOutputRequest().withVaultName(vaultName).withJobId(jobId);
		return client.getJobOutput(jobOutputRequest);
	}

	@Override
	public Optional<GetJobOutputResult> retrieveJobResultsIfAvailable(final String vaultName, final String jobId)
	{
		if (isJobComplete(vaultName, jobId))
		{
			return Optional.of(retrieveJobResults(vaultName, jobId));
		}
		else
		{
			return Optional.empty();
		}
	}

	@Override
	@Async
	public Future<UploadArchiveResult> uploadFile(final String vaultName, final File fileToUpload, final String description,
			final ProgressListener progressListener)
	{
		try (InputStream inputStream = new FileInputStream(fileToUpload))
		{
			// create checksum
			final String checksum = TreeHashGenerator.calculateTreeHash(fileToUpload);
			return uploadFile(vaultName, inputStream, fileToUpload.length(), checksum, description, progressListener);
		}
		catch (final IOException e)
		{
			Throwables.propagate(e);
			return null; // never happens
		}
	}

	@Override
	@Async
	public Future<UploadArchiveResult> uploadFile(final String vaultName, final InputStream toUpload, final long length,
			final String checksum, final String description, final ProgressListener progressListener)
	{
		Preconditions.checkNotNull(vaultName, "vaultName");
		Preconditions.checkNotNull(toUpload, "toUpload");
		Preconditions.checkNotNull(length, "length");
		Preconditions.checkNotNull(checksum, "checksum");

		// amazon recommends to use multipart upload for files bigger than 100 MB
		if (length > SINGLE_PART_UPLOAD_MAX_BYTES)
		{
			// TODO implement me;
			throw new UnsupportedOperationException();
		}
		else
		{
			return new AsyncResult<UploadArchiveResult>(uploadSinglePart(vaultName, toUpload, length, checksum, description,
					progressListener));
		}
	}

	protected UploadArchiveResult uploadSinglePart(final String vaultName, final InputStream toUpload, final long length,
			final String checksum, final String description, final ProgressListener progressListener)
	{
		final UploadArchiveRequest request = new UploadArchiveRequest().withVaultName(vaultName)
				.withArchiveDescription(description).withChecksum(checksum).withBody(toUpload).withContentLength(length);

		// either use provided log listener or use default one
		if (progressListener != null)
		{
			request.withGeneralProgressListener(progressListener);
		}
		else
		{
			request.withGeneralProgressListener(processListenerFactory.createListener());
		}

		final UploadArchiveResult result = client.uploadArchive(request);

		// this check is already done on the server-side by AWS, but let's double check just in case
		if (!checksum.equals(result.getChecksum()))
		{
			final StringBuilder message = new StringBuilder();
			message.append("The expected checksum '").append(checksum)
					.append("' of the uploaded file does not match with the checksum returned by the server '")
					.append(result.getChecksum()).append("'. Your file upload might be corrupt! Details: ")
					.append(new Yaml().dump(result));

			throw new InvalidParameterValueException(message.toString());
		}

		return result;
	}

	@Override
	public InitiateJobResult initiateDownloadJob(final String vaultName, final String archvieId, final String byteRange)
	{
		final JobParameters jobParameters = new JobParameters().withArchiveId(archvieId).withType("archive-retrieval");

		if (!StringUtils.isEmpty(byteRange))
		{
			jobParameters.withDescription("archive retrieval").withRetrievalByteRange(byteRange);
		}

		return client.initiateJob(new InitiateJobRequest().withJobParameters(jobParameters)
				.withVaultName(vaultName));
	}

	@Override
	public void deleteArchive(final String vaultName, final String archiveId)
	{
		final DeleteArchiveRequest request = new DeleteArchiveRequest().withVaultName(vaultName).withArchiveId(archiveId);
		client.deleteArchive(request);
	}

}
