package name.ruhkopf.cloudandback.service.aws;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;

public class AWSResult<T> implements Iterable<T>
{
	private final List<T> internalList;
	private final String marker;

	public AWSResult(final String marker, final List<T> list)
	{
		super();
		this.marker = marker;

		Preconditions.checkNotNull(list, "list must not be null");
		this.internalList = list;
	}

	@Override
	public Iterator<T> iterator()
	{
		return internalList.iterator();
	}

	public String getMarker()
	{
		return marker;
	}

}
