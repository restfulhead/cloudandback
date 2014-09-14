package name.ruhkopf.cloudandback.aws;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.Future;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.glacier.model.CreateVaultResult;
import com.amazonaws.services.glacier.model.DescribeJobResult;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.DescribeVaultResult;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.UploadArchiveResult;

/**
 * Service client providing access to Amazon Glacier.
 *
 * @author Patrick Ruhkopf
 */
public interface GlacierService
{
	/**
	 * Returns all vaults including their properties.
	 *
	 * @param limit The maximum number of results to return
	 * @param marker The marker specifies the vault ARN after which the listing of vaults should begin.
	 * @return A list of results and marker
	 */
	AWSResult<DescribeVaultOutput> describeVaults(String limit, String marker);

	/**
	 * Returns the properties of the vault identified by the given name.
	 *
	 * @param name The name of the vault
	 * @return The properties of the vault
	 */
	DescribeVaultResult describeValut(String name);

	/**
	 * Creates a new vault with the specified name. The name of the vault must be unique within a region for an AWS account.
	 *
	 * You must use the following guidelines when naming a vault.
	 * <ul>
	 * <li>Names can be between 1 and 255 characters long.</li>
	 * <li>Allowed characters are a-z, A-Z, 0-9, '_' (underscore), '-' (hyphen), and '.' (period).
	 * </ul>
	 *
	 * This operation is idempotent.
	 *
	 * @param name The name of the vault must be unique within a region for an AWS account.
	 * @return the result
	 */
	CreateVaultResult createVault(String name);

	/**
	 * Deletes a vault. Amazon Glacier will delete a vault only if there are no archives in the vault as of the last inventory and
	 * there have been no writes to the vault since the last inventory. If either of these conditions is not satisfied, the vault
	 * deletion fails (that is, the vault is not removed) and Amazon Glacier returns an error.
	 *
	 * This operation is idempotent.
	 *
	 * @param name The name of the vault to delete
	 */
	void deleteVault(String name);

	/**
	 * Initiates a job to retrieve a vault inventory (a list of archives in a vault).
	 *
	 * @param vaultName the vault name
	 * @return the result including the job id
	 */
	InitiateJobResult initiateInventoryJob(final String vaultName);

	/**
	 * Initiates the archive retrieval job.
	 *
	 * @param vaultName the vault name
	 * @param archive the archive id
	 * @param byteRange optional byte range to request downloading only part of the archive
	 * @return the initiate job result
	 */
	public InitiateJobResult initiateDownloadJob(final String vaultName, final String archive, final String byteRange);

	/**
	 * Returns information about a job you previously initiated, including the job initiation date, the user who initiated the
	 * job, the job status code/message.
	 *
	 * @param vaultName the vault name
	 * @param jobId the job id
	 * @return the job description
	 */
	DescribeJobResult retrieveJobStatus(final String vaultName, final String jobId);

	/**
	 * Interprets the result of {@link #retrieveJobStatus(String, String)} and returns whether the inventory job
	 * complete.
	 *
	 * @param vaultName the vault name
	 * @param jobId the job id
	 * @return true, if is inventory job complete
	 */
	boolean isJobComplete(final String vaultName, final String jobId);

	/**
	 * Downloads the output of the job you initiated using {@link #initiateInventoryJob(String)} or
	 * {@link #initiateInventoryJob(String, String)}. Depending on the job type you specified when you initiated the job, the
	 * output will be either a vault inventory or the content of an archive.
	 *
	 * A job ID will not expire for at least 24 hours after Amazon Glacier completes the job. That is, you can download the job
	 * output within the 24 hours period after Amazon Glacier completes the job.
	 *
	 * @param vaultName the vault name
	 * @param jobId the job id
	 * @return the gets the job output result
	 */
	GetJobOutputResult retrieveJobResults(final String vaultName, final String jobId);

	/**
	 * Retrieves the inventory job results (see {@link #retrieveJobResults(String, String)} only if
	 * {@link #isJobComplete(String, String)} returns true.
	 *
	 * @param vaultName the vault name
	 * @param jobId the job id
	 * @return either the inventory job results if available or nothing
	 */
	Optional<GetJobOutputResult> retrieveJobResultsIfAvailable(final String vaultName, final String jobId);

	/**
	 * Uploads a new file and adds it as an archive to a vault. The upload is invoked asynchronously and progress can be monitored
	 * by passing in a {@link ProgressListener} instance.
	 *
	 * Amazons upload operation itself is synchronous and for a successful upload, your data is durably persisted. Amazon Glacier
	 * returns the archive ID in the x-amz-archive-id header of the response.
	 *
	 * Use this for files up to 100MB.
	 *
	 * @param vaultName the vault name
	 * @param fileToUpload the file to upload
	 * @param description the description of the archive
	 * @param progressListener an optional progress listener
	 * @return the result
	 */
	Future<UploadArchiveResult> uploadFile(final String vaultName, final File fileToUpload, String description,
			ProgressListener progressListener);

	/**
	 * Uploads data to a new archive that is added to a vault. The upload is invoked asynchronously and progress can be monitored
	 * by passing in a {@link ProgressListener} instance.
	 *
	 * Amazons upload operation itself is synchronous and for a successful upload, your data is durably persisted. Amazon Glacier
	 * returns the archive ID in the x-amz-archive-id header of the response.
	 *
	 * Use this for files up to 100MB.
	 *
	 * @param vaultName the vault name
	 * @param toUpload the data stream to upload
	 * @param length the length of the data
	 * @param checksum calculated checksum of the data that is used to verify a successful upload
	 * @param description the description An optional description of the archive
	 * @param progressListener an optional progress listener
	 * @return the result of the upload
	 */
	Future<UploadArchiveResult> uploadFile(final String vaultName, final InputStream toUpload, final long length,
			final String checksum, final String description, ProgressListener progressListener);

	/**
	 * Deletes an archive from a vault. Subsequent requests to initiate a retrieval of this archive will fail. Archive retrievals
	 * that are in progress for this archive ID may or may not succeed.
	 *
	 * This operation is idempotent. Attempting to delete an already-deleted archive does not result in an error.
	 *
	 * @param vaultName the vault name
	 * @param archiveId the archive id
	 */
	void deleteArchive(String vaultName, String archiveId);
}