package ru.tasm.image.fragmentation.model.ui;

import lombok.Data;

@Data
public final class ProcessForm {
    private final Integer numbers;
    private final Integer height;
    private final String heightSI;
    private final Boolean trySeg;
    private final Integer backgraundColor;

    public ProcessForm(Integer numbers,
                       Integer height,
                       String heightSI,
                       Boolean trySeg,
                       Integer backgraundColor) {
        this.numbers = numbers;
        this.height = height;
        this.heightSI = heightSI;
        this.trySeg = trySeg;
        this.backgraundColor = backgraundColor;
    }
}
