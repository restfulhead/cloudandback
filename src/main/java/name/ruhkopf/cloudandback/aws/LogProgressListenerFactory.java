package name.ruhkopf.cloudandback.aws;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public class LogProgressListenerFactory
{
	/**
	 * Factory method to be implemented by Spring.
	 *
	 * @param settings AWS settings
	 * @param region The region
	 * @return a new service instance for the given region and configured with the given settings
	 */
	@Lookup
	protected LogProgressListener createListener()
	{
		throw new UnsupportedOperationException();
	}
}
