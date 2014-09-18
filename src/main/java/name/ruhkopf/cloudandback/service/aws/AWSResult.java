package name.ruhkopf.cloudandback.service.aws;

import java.util.List;

import com.google.common.base.Preconditions;

public class AWSResult<T>
{
	private final List<T> list;
	private final String marker;

	public AWSResult(final String marker, final List<T> list)
	{
		super();
		this.marker = marker;

		Preconditions.checkNotNull(list, "list must not be null");
		this.list = list;
	}

	public List<T> getItems()
	{
		return list;
	}

	public String getMarker()
	{
		return marker;
	}

}
