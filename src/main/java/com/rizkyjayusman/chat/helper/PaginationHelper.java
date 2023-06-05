package com.rizkyjayusman.chat.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public final class PaginationHelper {
    public static final Integer MIN_PAGINATION_PAGE = 1;
    public static final Integer MIN_PAGINATION_SIZE = 1;

    public static final Integer DEFAULT_PAGINATION_PAGE = 1;
    public static final Integer DEFAULT_PAGINATION_SIZE = 10;

    private PaginationHelper() {
    }

    public static void validatePagingParam(int page, int size) {
        if (page < MIN_PAGINATION_PAGE) {
            throw new RuntimeException("param page invalid");
        }

        if (size < MIN_PAGINATION_SIZE) {
            throw new RuntimeException("param size invalid");
        }
    }

    public static PageRequest createPageRequest(Integer page, Integer size) {
        return createPageRequest(page, size, Sort.by(new Sort.Order(Sort.Direction.DESC, "id")));
    }

    public static PageRequest createPageRequest(Integer page, Integer size, Sort sort) {
        PaginationHelper.validatePagingParam(page, size);
        return PageRequest.of(page - 1, size, sort);
    }

    public static Sort getSort(String sort) {
        if (sort == null || sort.equals("")) {
            return Sort.unsorted();
        }
        String[] propAndDirection = sort.split(",", 2);

        if (propAndDirection.length > 1)
            return Sort.by(Sort.Direction.fromString(propAndDirection[1]), propAndDirection[0]);

        return Sort.by(Sort.Direction.ASC, propAndDirection[0]);
    }

    public static Sort getSort(String[] sorts) {
        Sort sort = Sort.unsorted();

        if (sorts == null) {
            return sort;
        }

        if (sorts.length == 2) {
            if (sorts[1].equalsIgnoreCase("asc") || sorts[1].equalsIgnoreCase("desc")) {
                return getSort(String.format("%s,%s", sorts[0], sorts[1]));
            }
        }

        for (String strSort : sorts) {
            if (strSort == null || strSort.equals("")) {
                continue;
            }
            sort = sort.and(getSort(strSort));
        }

        return sort;
    }
}
