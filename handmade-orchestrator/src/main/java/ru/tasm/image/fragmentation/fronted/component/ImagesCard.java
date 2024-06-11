package ru.tasm.image.fragmentation.fronted.component;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.Getter;
import ru.tasm.image.fragmentation.fronted.views.InProgressView;
import ru.tasm.image.fragmentation.model.ImageInfo;

import java.util.Random;

public class ImagesCard extends Div {
    public Paragraph name;
    public StreamResource resultImage;
    private StreamResource resultMaskImage;
    public ImageInfo imageResultInfo;
    public Button expand = new Button("Подробнее");
    private Button showMask = new Button("Показать найденные объекты");

    public ImagesCard(String name, StreamResource resultImage,
                      StreamResource resultMaskImage, ImageInfo imageResultInfo) {
        this.name = new Paragraph(name);
        this.resultImage = resultImage;
        this.resultMaskImage = resultMaskImage;
        this.imageResultInfo = imageResultInfo;
        createCard();
    }

    private void createCard() {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("min-content");
        HorizontalLayout textAndButton = new HorizontalLayout();
        Image image = new Image();
        image.setMaxWidth("70%");
        image.setSrc(resultImage);
        textAndButton.add(name);
        card.add(textAndButton);
        card.add(image);
        card.add(expand);

        expand.addClickListener(clickEvent ->
                fireEvent(new ExpandEvent(this)));
        add(card);
    }

    static Random random = new Random();

    public static ImagesCard from(String name, StreamResource resultImage,
                                  StreamResource resultMaskImage, ImageInfo imageResultInfo) {
        ImagesCard imagesCard = new ImagesCard(name, resultImage,
                resultMaskImage, imageResultInfo);

        imagesCard.setId(String.valueOf(random.nextInt()));
        return imagesCard;
    }

    public static ImagesCard from(StreamResource img) {
        String id = String.valueOf(random.nextInt());
        return new ImagesCard(id, img, img, null);
    }

    public static ImagesCard from(StreamResource img, ImageInfo imageInfo) {
        String id = String.valueOf(random.nextInt());
        String name = "Вариант №%s".formatted(img.getName().split("_")[0]);
        return new ImagesCard(name, img, img, imageInfo);
    }


    @Getter
    public static class ExpandEvent extends ComponentEvent<ImagesCard> {
        private final ImagesCard imagesCard;

        public ExpandEvent(ImagesCard imagesCard) {
            super(imagesCard, false);
            this.imagesCard = imagesCard;
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
