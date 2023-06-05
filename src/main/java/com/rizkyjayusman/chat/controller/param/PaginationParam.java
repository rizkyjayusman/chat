package com.rizkyjayusman.chat.controller.param;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rizkyjayusman.chat.helper.PaginationHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaginationParam implements Serializable {

    private Integer page;

    private Integer size;

    private String[] sorts = new String[]{}; //e.g: sorts=created_date,asc&sorts=status,desc

    public Integer getPage() {
        if (page == null || page < PaginationHelper.MIN_PAGINATION_PAGE) {
            page = PaginationHelper.DEFAULT_PAGINATION_PAGE;
        }
        return page;
    }

    public Integer getSize() {
        if (size == null || size < PaginationHelper.MIN_PAGINATION_SIZE) {
            size = PaginationHelper.DEFAULT_PAGINATION_SIZE;
        }
        return size;
    }

    @JsonIgnore
    public Pageable getPageable() {
        return PaginationHelper.createPageRequest(getPage(), getSize(), PaginationHelper.getSort(sorts));
    }

}
