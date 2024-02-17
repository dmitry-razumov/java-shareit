package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestCustom extends PageRequest {
    public PageRequestCustom(int pageNumber, int size, Sort sort) {
        super(pageNumber, size, sort);
    }

    public static PageRequestCustom get(int from, int size) {
        return new PageRequestCustom(from / size, size, Sort.unsorted());
    }

    public static PageRequestCustom get(int from, int size, String order) {
        return new PageRequestCustom(from / size, size, Sort.by(order));
    }
}
