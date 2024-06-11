package ru.tasm.image.fragmentation.fronted.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.io.FileUtils;
import ru.tasm.image.fragmentation.fronted.component.ImagesCard;
import ru.tasm.image.fragmentation.model.exception.FileRuntimeException;
import ru.tasm.image.fragmentation.model.ui.ImageInfoGrid;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ImageResultView extends VerticalLayout {
    public ImagesCard imagesCard;
    private Grid<ImageInfoGrid> grid;
    private File zipFile;

    public ImageResultView(ImagesCard imagesCard, File zipFile) {
        this.imagesCard = imagesCard;
        this.zipFile = zipFile;
        this.createImageInfoUI();
    }

    private void createImageInfoUI() {
        HorizontalLayout imageText = new HorizontalLayout();
        Image image = new Image();
        image.setMaxHeight("40%");
        image.setSrc(imagesCard.resultImage);
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(imagesCard.name);

        Map<String, Integer> colorAndCount = imagesCard.imageResultInfo.colorAndCount();
        Anchor anchor = addLinkToFile(zipFile);
        verticalLayout.add(anchor);
        imageText.setWidthFull();
        Paragraph cq =
                new Paragraph("Общее количество цветов:" + imagesCard.imageResultInfo.colorQuantity());

        Paragraph size = new Paragraph("Общий размер: %s см на %s см".formatted(
                (int)imagesCard.imageResultInfo.height()/5,
                (int)imagesCard.imageResultInfo.weight()/5));
        verticalLayout.setWidthFull();
        verticalLayout.add(cq, size);
        verticalLayout.add(createGrid());
        imageText.add(image, verticalLayout);
        add(imageText);
    }

    private Component createGrid() {
        grid = new Grid<>(ImageInfoGrid.class, false);

        grid.addColumn(new ComponentRenderer<>(imageInfoGrid -> {
            imageInfoGrid.getColor().getStyle().setBackgroundColor(imageInfoGrid.getColorCode());
            return imageInfoGrid.getColor();
        })).setHeader("Цвет");

        grid.addColumn("colorCode").setAutoWidth(true).setHeader("Код цвета. HEX (RGB)");
        grid.addColumn("pixCount").setAutoWidth(true).setHeader("Площадь(кв. см)");

        grid.addColumn("square").setAutoWidth(true).setHeader("Площадь(%)");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        int square = imagesCard.imageResultInfo.height()/5
                * imagesCard.imageResultInfo.weight()/5;
        grid.setItems(new ListDataProvider<>(imagesCard.imageResultInfo
                .colorAndCount()
                .entrySet()
                .stream()
                .map(x -> {
                    String color = x.getKey();
                    Integer count = x.getValue()/5/5;
                    Paragraph paragraph = new Paragraph(color);
                    paragraph.getStyle().setColor(color);
                    Div div = new Div(paragraph);
                    div.getStyle().setBackgroundColor(color);
                    Span span = new Span();
                    span.getStyle().setBackgroundColor(color);
                    double resultS =
                            Math.round(
                                    ((float) count / square * 100) * 100 / 100);
                    return new ImageInfoGrid(div, color,
                            count, "%.2f%%".formatted(resultS));
                }).toList()));
        grid.sort(GridSortOrder.desc(grid.getColumnByKey("pixCount")).build());
        return grid;
    }

    private Anchor addLinkToFile(File file) {
        StreamResource streamResource = new StreamResource(file.getName(), () -> getStream(file));
        Anchor link = new Anchor(streamResource, String.format("%s (%d KB)",
                "Скачать", (int) file.length() / 1024));
        return link;
    }

    private InputStream getStream(File file) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stream;
    }

    private StreamResource getStreamResourceFromFile(File file) {
        return new StreamResource(
                "name",
                () -> {
                    try {
                        return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
                    } catch (IOException e) {
                        throw new FileRuntimeException(e);
                    }
                });
    }
}
