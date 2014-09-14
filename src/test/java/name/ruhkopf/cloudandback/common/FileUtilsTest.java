package name.ruhkopf.cloudandback.common;

import static org.junit.Assert.assertEquals;
import name.ruhkopf.cloudandback.common.io.FileUtils;

import org.junit.Test;

public class FileUtilsTest
{

	@Test
	public void shouldFormatFileSize()
	{
		assertEquals("1.0 MB", FileUtils.toDisplayFormat(1048576));
		assertEquals("8.0 MB", FileUtils.toDisplayFormat(8388608));
		assertEquals("1.0 GB", FileUtils.toDisplayFormat(1073741824L));
		assertEquals("100.0 GB", FileUtils.toDisplayFormat(107374182400L));
	}
}
