package name.ruhkopf.cloudandback.common.io;

public class FileUtils
{

	/**
	 * Returns a human readable String based on the given number of bytes.
	 *
	 * @param bytes the number of bytes
	 * @return the string representing the size
	 */
	public static String toDisplayFormat(final long bytes)
	{
		final int unit = 1024;
		if (bytes < unit)
		{
			return bytes + " B";
		}
		final int exp = (int) (Math.log(bytes) / Math.log(unit));
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), ("KMGTPE").charAt(exp - 1));
	}
}
