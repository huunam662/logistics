package warehouse_management.com.warehouse_management.common.pagination.res;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
public class PageInfoRes<T> {

    private Integer page;
    private Integer size;
    private Integer elementsLength;
    private Integer totalPages;
    private Boolean hasPreviousPage;
    private Boolean hasNextPage;
    private List<T> elements;

    public PageInfoRes(Page<T> pageObject){
        this.page = pageObject.getNumber() + 1;
        this.size = pageObject.getSize();
        this.elements = pageObject.getContent();
        this.elementsLength = elements.size();
        this.totalPages = pageObject.getTotalPages();
        this.hasPreviousPage = pageObject.hasPrevious();
        this.hasNextPage = pageObject.hasNext();
    }

    public PageInfoRes(List<T> elements, Page<?> pageObject){
        this.page = pageObject.getNumber() + 1;
        this.size = pageObject.getSize();
        this.elements = elements;
        this.elementsLength = elements.size();
        this.totalPages = pageObject.getTotalPages();
        this.hasPreviousPage = pageObject.hasPrevious();
        this.hasNextPage = pageObject.hasNext();
    }

}
