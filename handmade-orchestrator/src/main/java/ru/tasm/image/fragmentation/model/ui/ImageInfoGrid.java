package ru.tasm.image.fragmentation.model.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ImageInfoGrid {
    Div color;
//    String color;
    String colorCode;
    Integer pixCount;
    String square;

    public ImageInfoGrid(Div color, String colorCode, Integer pixCount, String square) {
//    public ImageInfoGrid(String color, String colorCode, Integer pixCount, double square) {
//        this.color = new Div(color);
        this.color = color;
        this.colorCode = colorCode;
        this.pixCount = pixCount;
        this.square = square;
    }
}
