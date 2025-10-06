package warehouse_management.com.warehouse_management.utils;

import org.springframework.data.domain.Sort;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.integration.anabase.dto.response.BaseListResponse;
import warehouse_management.com.warehouse_management.integration.anabase.dto.response.BaseListDataDto;

import java.util.List;

/**
 * Utility class để convert PageOptionsDto sang SieveModel query string
 * Cho .NET API với SieveModel parameter
 */
public class AnaConverterUtils {

    /**
     * Convert PageOptionsDto sang SieveModel query string
     * 
     * @param pageOptions PageOptionsDto từ React
     * @return Query string cho .NET API SieveModel
     * VD: "page=1&pageSize=10&sort=name&filters=status==ACTIVE&priority>5"
     */
    public static String convertToSieveQueryString(PageOptionsDto pageOptions) {
        if (pageOptions == null) {
            return "page=1&pageSize=10";
        }

        StringBuilder queryBuilder = new StringBuilder();

        // Page
        Integer page = pageOptions.getPage() != null ? pageOptions.getPage() : 1;
        queryBuilder.append("page=").append(page);

        // PageSize
        Integer pageSize = pageOptions.getSize() != null ? pageOptions.getSize() : 10;
        queryBuilder.append("&pageSize=").append(pageSize);

        // Sort
        if (pageOptions.getSortBy() != null && !pageOptions.getSortBy().isEmpty() && pageOptions.getDirection() != null) {
            String sortString = buildSortString(pageOptions.getSortBy(), pageOptions.getDirection());
            if (!sortString.isEmpty()) {
                queryBuilder.append("&sort=").append(sortString);
            }
        }

        // Filters - Convert RSQL to SieveModel format
        if (pageOptions.getFilter() != null && !pageOptions.getFilter().isEmpty()) {
            String sieveFilter = convertRsqlToSieve(pageOptions.getFilter());
            if (!sieveFilter.isEmpty()) {
                queryBuilder.append("&filters=").append(sieveFilter);
            }
        }

        return queryBuilder.toString();
    }

    /**
     * Convert React QueryBuilder filter string sang SieveModel format
     * 
     * @param queryBuilderFilter Filter string từ React QueryBuilder
     * @return SieveModel filter string
     * VD: "status=='ACTIVE' and priority>5 and name=='*john*'" → "status==ACTIVE&priority>5&name@=john"
     */
    public static String convertRsqlToSieve(String queryBuilderFilter) {
        if (queryBuilderFilter == null || queryBuilderFilter.trim().isEmpty()) {
            return "";
        }

        // Clean input
        String cleanFilter = queryBuilderFilter.trim();
        
        // Step 1: Convert LIKE with wildcards first (most specific)
        String sieveFilter = cleanFilter
            .replaceAll("==\\*([^*]+)\\*", "@=$1");
        
        // Step 2: Convert "Starts With" - field=='text*' → field_=text
        sieveFilter = sieveFilter
            .replaceAll("=='([^*]+)\\*'", "_=$1");
        
        // Step 3: Convert LIKE without wildcards
        sieveFilter = sieveFilter
            .replaceAll("=='([^']*)'", "@=$1");
        
        // Step 4: Convert exact match (remove quotes)
        sieveFilter = sieveFilter
            .replaceAll("=='([^']*)'", "==$1");
        
        // Step 5: Convert not equal (remove quotes)
        sieveFilter = sieveFilter
            .replaceAll("!='([^']*)'", "!=$1");
        
        // Step 6: Convert IN clause
        sieveFilter = sieveFilter
            .replaceAll(" IN \\(([^)]+)\\)", "=in=($1");
        
        // Step 7: Convert IS NULL
        sieveFilter = sieveFilter
            .replaceAll(" IS NULL", "==null");
        
        // Step 8: Convert IS NOT NULL
        sieveFilter = sieveFilter
            .replaceAll(" IS NOT NULL", "!=null");
        
        // Step 9: Convert logical operators
        sieveFilter = sieveFilter
            .replaceAll(" and ", "&")  // RSQL uses ; for AND, but React QueryBuilder uses " and "
            .replaceAll(" or ", "|");  // Sieve uses | for OR, not ,
        
        // Step 10: Remove any remaining quotes
        sieveFilter = sieveFilter.replaceAll("'", "");
        
        // Step 11: Clean up any remaining wildcards in @= operations
        sieveFilter = sieveFilter.replaceAll("@=\\*([^*]+)\\*", "@=$1");
        
        // Step 12: Convert OR pattern for same value across multiple fields
        // (field1@=value|field2@=value|field3@=value) → field1|field2|field3@=value
        sieveFilter = convertOrPatternForSameValue(sieveFilter);

        return sieveFilter;
    }

    /**
     * Convert OR pattern for same value across multiple fields
     * (field1@=value|field2@=value|field3@=value) → field1|field2|field3@=value
     * 
     * @param filter Filter string
     * @return Converted filter string
     */
    private static String convertOrPatternForSameValue(String filter) {
        // Pattern to match: (field1@=value|field2@=value|field3@=value)
        // Use a simpler approach with multiple patterns
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\(([^@]+)@=([^|)]+)\\|([^@]+)@=([^|)]+)\\|([^@]+)@=([^)]+)\\)");
        java.util.regex.Matcher matcher = pattern.matcher(filter);
        
        if (matcher.find()) {
            String field1 = matcher.group(1);
            String value1 = matcher.group(2);
            String field2 = matcher.group(3);
            String value2 = matcher.group(4);
            String field3 = matcher.group(5);
            String value3 = matcher.group(6);
            
            // Check if all values are the same
            if (value1.equals(value2) && value2.equals(value3)) {
                String replacement = "(" + field1 + "|" + field2 + "|" + field3 + ")@=" + value1;
                return matcher.replaceFirst(java.util.regex.Matcher.quoteReplacement(replacement));
            }
        }
        
        return filter;
    }

    /**
     * Build sort string từ sortBy list và direction
     * 
     * @param sortBy List of fields to sort by
     * @param direction Sort direction (ASC/DESC)
     * @return Sort string cho SieveModel
     * VD: ["name", "email"] + ASC → "name,email"
     * VD: ["name", "email"] + DESC → "-name,-email"
     */
    private static String buildSortString(List<String> sortBy, Sort.Direction direction) {
        if (sortBy == null || sortBy.isEmpty()) {
            return "";
        }

        String prefix = direction == Sort.Direction.DESC ? "-" : "";
        return sortBy.stream()
                .map(field -> prefix + field)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }

    /**
     * Convert với default values
     * 
     * @param pageOptions PageOptionsDto từ React
     * @param defaultPage Default page nếu null
     * @param defaultPageSize Default page size nếu null
     * @return Query string với defaults
     */
    public static String convertToSieveQueryStringWithDefaults(PageOptionsDto pageOptions, int defaultPage, int defaultPageSize) {
        if (pageOptions == null) {
            return String.format("page=%d&pageSize=%d", defaultPage, defaultPageSize);
        }

        StringBuilder queryBuilder = new StringBuilder();

        // Page với default
        Integer page = pageOptions.getPage() != null ? pageOptions.getPage() : defaultPage;
        queryBuilder.append("page=").append(page);

        // PageSize với default
        Integer pageSize = pageOptions.getSize() != null ? pageOptions.getSize() : defaultPageSize;
        queryBuilder.append("&pageSize=").append(pageSize);

        // Sort
        if (pageOptions.getSortBy() != null && !pageOptions.getSortBy().isEmpty() && pageOptions.getDirection() != null) {
            String sortString = buildSortString(pageOptions.getSortBy(), pageOptions.getDirection());
            if (!sortString.isEmpty()) {
                queryBuilder.append("&sort=").append(sortString);
            }
        }

        // Filters - Convert RSQL to SieveModel format
        if (pageOptions.getFilter() != null && !pageOptions.getFilter().isEmpty()) {
            String sieveFilter = convertRsqlToSieve(pageOptions.getFilter());
            if (!sieveFilter.isEmpty()) {
                queryBuilder.append("&filters=").append(sieveFilter);
            }
        }

        return queryBuilder.toString();
    }

    /**
     * Validate và clean filter string
     * 
     * @param filter Raw filter string từ React
     * @return Cleaned filter string
     */
    public static String cleanFilterString(String filter) {
        if (filter == null || filter.trim().isEmpty()) {
            return "";
        }

        // Remove extra spaces và clean up
        return filter.trim()
                .replaceAll("\\s+", " ")  // Replace multiple spaces with single space
                .replaceAll("\\s*==\\s*", "==")  // Clean around ==
                .replaceAll("\\s*!=\\s*", "!=")  // Clean around !=
                .replaceAll("\\s*>\\s*", ">")    // Clean around >
                .replaceAll("\\s*<\\s*", "<")    // Clean around <
                .replaceAll("\\s*>=\\s*", ">=")  // Clean around >=
                .replaceAll("\\s*<=\\s*", "<=")  // Clean around <=
                .replaceAll("\\s*and\\s*", " and ")  // Standardize and
                .replaceAll("\\s*or\\s*", " or ");   // Standardize or
    }

    /**
     * Convert BaseListResponse sang PageInfoDto
     * Convert từ .NET API response sang Spring pagination format
     * 
     * @param baseListResponse BaseListResponse từ .NET API
     * @param pageOptions PageOptionsDto từ request để tính toán pagination
     * @param <T> Generic type cho data
     * @return PageInfoDto với pagination info
     */
    public static <T> PageInfoDto<T> convertBaseListResponseToPageInfo(BaseListResponse<T> baseListResponse, PageOptionsDto pageOptions) {
        if (baseListResponse == null || baseListResponse.getData() == null) {
            return createEmptyPageInfo(pageOptions);
        }

        BaseListDataDto<T> data = baseListResponse.getData();
        List<T> collection = data.getCollection();
        
        // Lấy pagination info từ BaseListDataDto
        Integer total = data.getTotal() != null ? data.getTotal() : 0;
        Integer pageSize = data.getPageSize() != null ? data.getPageSize() : (pageOptions.getSize() != null ? pageOptions.getSize() : 10);
        Integer pageIndex = data.getPageIndex() != null ? data.getPageIndex() : (pageOptions.getPage() != null ? pageOptions.getPage() : 1);
        
        // Tính toán pagination info
        Integer currentPage = pageIndex;
        Integer totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
        Boolean hasPreviousPage = currentPage > 1;
        Boolean hasNextPage = currentPage < totalPages;
        Integer elementsLength = collection != null ? collection.size() : 0;

        // Tạo PageInfoDto
        PageInfoDto<T> pageInfo = new PageInfoDto<>();
        pageInfo.setPage(currentPage);
        pageInfo.setSize(pageSize);
        pageInfo.setElementsLength(elementsLength);
        pageInfo.setTotalPages(totalPages);
        pageInfo.setHasPreviousPage(hasPreviousPage);
        pageInfo.setHasNextPage(hasNextPage);
        pageInfo.setElements(collection);

        return pageInfo;
    }

    /**
     * Convert BaseListResponse sang PageInfoDto với default values
     * 
     * @param baseListResponse BaseListResponse từ .NET API
     * @param defaultPage Default page nếu không có
     * @param defaultPageSize Default page size nếu không có
     * @param <T> Generic type cho data
     * @return PageInfoDto với pagination info
     */
    public static <T> PageInfoDto<T> convertBaseListResponseToPageInfoWithDefaults(BaseListResponse<T> baseListResponse, int defaultPage, int defaultPageSize) {
        if (baseListResponse == null || baseListResponse.getData() == null) {
            return createEmptyPageInfoWithDefaults(defaultPage, defaultPageSize);
        }

        BaseListDataDto<T> data = baseListResponse.getData();
        List<T> collection = data.getCollection();
        
        // Lấy pagination info từ BaseListDataDto với defaults
        Integer total = data.getTotal() != null ? data.getTotal() : 0;
        Integer pageSize = data.getPageSize() != null ? data.getPageSize() : defaultPageSize;
        Integer pageIndex = data.getPageIndex() != null ? data.getPageIndex() : defaultPage;
        
        // Tính toán pagination info
        Integer currentPage = pageIndex;
        Integer totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
        Boolean hasPreviousPage = currentPage > 1;
        Boolean hasNextPage = currentPage < totalPages;
        Integer elementsLength = collection != null ? collection.size() : 0;

        // Tạo PageInfoDto
        PageInfoDto<T> pageInfo = new PageInfoDto<>();
        pageInfo.setPage(currentPage);
        pageInfo.setSize(pageSize);
        pageInfo.setElementsLength(elementsLength);
        pageInfo.setTotalPages(totalPages);
        pageInfo.setHasPreviousPage(hasPreviousPage);
        pageInfo.setHasNextPage(hasNextPage);
        pageInfo.setElements(collection);

        return pageInfo;
    }

    /**
     * Tạo empty PageInfoDto từ PageOptionsDto
     */
    private static <T> PageInfoDto<T> createEmptyPageInfo(PageOptionsDto pageOptions) {
        PageInfoDto<T> pageInfo = new PageInfoDto<>();
        pageInfo.setPage(pageOptions != null && pageOptions.getPage() != null ? pageOptions.getPage() : 1);
        pageInfo.setSize(pageOptions != null && pageOptions.getSize() != null ? pageOptions.getSize() : 10);
        pageInfo.setElementsLength(0);
        pageInfo.setTotalPages(0);
        pageInfo.setHasPreviousPage(false);
        pageInfo.setHasNextPage(false);
        pageInfo.setElements(List.of());
        return pageInfo;
    }

    /**
     * Tạo empty PageInfoDto với default values
     */
    private static <T> PageInfoDto<T> createEmptyPageInfoWithDefaults(int defaultPage, int defaultPageSize) {
        PageInfoDto<T> pageInfo = new PageInfoDto<>();
        pageInfo.setPage(defaultPage);
        pageInfo.setSize(defaultPageSize);
        pageInfo.setElementsLength(0);
        pageInfo.setTotalPages(0);
        pageInfo.setHasPreviousPage(false);
        pageInfo.setHasNextPage(false);
        pageInfo.setElements(List.of());
        return pageInfo;
    }
}
