package name.ruhkopf.cloudandback.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;

/**
 * Basic implementation of a {@link ProgressListener} that calculates a percentage complete value indicating the progress of the
 * current task.
 *
 * @author Patrick Ruhkopf
 */
@Component
@Scope("prototype")
public class LogProgressListener implements ProgressListener
{
	private static final Logger LOG = LoggerFactory.getLogger(LogProgressListener.class);

	private long contentLength = 0;
	private long bytesSent = 0;
	private double percentageComplete = 0;
	private ProgressEventType currentTask;

	@Override
	public void progressChanged(final ProgressEvent progressEvent)
	{
		switch (progressEvent.getEventType())
		{
			case REQUEST_BYTE_TRANSFER_EVENT:
				bytesSent += progressEvent.getBytes();
				if (contentLength > 0 && bytesSent > 0 && contentLength >= bytesSent)
				{
					final double ratio = (new Long(bytesSent).doubleValue() / new Long(contentLength).doubleValue());
					percentageComplete = ratio * 100;
				}
				break;

			case HTTP_REQUEST_CONTENT_RESET_EVENT:
				contentLength = progressEvent.getBytes();
				bytesSent = 0;
				break;

			case REQUEST_CONTENT_LENGTH_EVENT:
			case RESPONSE_CONTENT_LENGTH_EVENT:
				contentLength = progressEvent.getBytes();
				break;

			case CLIENT_REQUEST_STARTED_EVENT:
				bytesSent = 0;
				currentTask = ProgressEventType.CLIENT_REQUEST_STARTED_EVENT;
				break;

			case HTTP_REQUEST_STARTED_EVENT:
				currentTask = ProgressEventType.HTTP_REQUEST_STARTED_EVENT;
				break;

			case HTTP_RESPONSE_STARTED_EVENT:
				bytesSent = 0;
				currentTask = ProgressEventType.HTTP_RESPONSE_STARTED_EVENT;
				break;

			case HTTP_REQUEST_COMPLETED_EVENT:
			case HTTP_RESPONSE_COMPLETED_EVENT:
				percentageComplete = 100;
				break;

			default:
				break;
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug(progressEvent.toString() + "(" + String.format("%2.02f", percentageComplete) + "%)");
		}
	}

	/**
	 * @return A percentage value indicating the progress of the current task.
	 */
	public double getPercentageComplete()
	{
		return percentageComplete;
	}

	/**
	 * @return The current task
	 */
	public ProgressEventType getCurrentTask()
	{
		return currentTask;
	}

}
