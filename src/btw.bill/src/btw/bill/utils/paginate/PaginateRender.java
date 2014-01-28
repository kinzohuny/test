package btw.bill.utils.paginate;

import java.util.List;

public interface PaginateRender<facade> {
	public void render(List<facade> records);
}
