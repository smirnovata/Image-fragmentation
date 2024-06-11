package ru.tasm.image.fragmentation.fronted.component;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SmallImagesGallery extends FlexLayout implements HasComponents, HasStyle {

    public SmallImagesGallery(List<ImagesCard> cards) {
        this.setWidthFull();
        setFlexWrap(FlexWrap.WRAP);
        setJustifyContentMode(JustifyContentMode.CENTER);
        cards.forEach(this::add);
        log.info("cards.size = {}", cards.size());
        log.info("cards.size = {}", cards.stream()
                .map(c -> c.name.getText())
                .collect(Collectors.joining()));
    }

    private static HorizontalLayout getHorizontalLayout() {
        HorizontalLayout imageContainer = new HorizontalLayout();
        return imageContainer;
    }
}
