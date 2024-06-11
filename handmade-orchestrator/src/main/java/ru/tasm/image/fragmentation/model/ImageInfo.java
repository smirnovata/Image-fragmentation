package ru.tasm.image.fragmentation.model;

import java.util.Map;

public record ImageInfo(Integer height, Integer weight,
                        Integer colorQuantity,
                        Map<String, Integer> colorAndCount) {
}
