package com.ccsw.tutorial.prestamo.model;

import com.ccsw.tutorial.common.pagination.PageableRequest;

/**
 * @author Xavi-H
 */
public class PrestamoSearchDto {

    private PageableRequest pageable;

    /**
     * @return pageable
     */
    public PageableRequest getPageable() {
        return pageable;
    }

    /**
     * @param pageable new value of {@link #getPageable}.
     */
    public void setPageable(PageableRequest pageable) {
        this.pageable = pageable;
    }
}