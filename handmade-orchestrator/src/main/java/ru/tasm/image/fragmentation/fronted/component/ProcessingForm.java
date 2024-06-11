package ru.tasm.image.fragmentation.fronted.component;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.tasm.image.fragmentation.model.ImageInfo;
import ru.tasm.image.fragmentation.model.ui.ProcessForm;

@Slf4j
public class ProcessingForm extends FormLayout {
    public final IntegerField numbers = new IntegerField("Выберете допустимое количество цветов");
    public final Select<String> select = new Select<>();
    public final IntegerField height = new IntegerField("Выберете требуемую высоту " +
            "изделия");
    public final Checkbox trySeg = new Checkbox("Попробовать выделить главный объект");
    public final IntegerField mainObjectColor = new IntegerField(
            "Процент цветов для отображения фона");

    public final Button processing = new Button("Обработать");

    public ProcessingForm(ImageInfo info) {
        addClassName("processing-form");
        processing.addClickListener(event -> processing());
        configNumbers(info);
        configHeight(info);
        configTrySeg();
        configMainObjectColor();
        add(numbers,
                height,
                trySeg,
                mainObjectColor,
                processing);
    }

    private void processing() {
        ProcessForm processingForm = new ProcessForm(
                numbers.getValue(),
                getValue(height.getValue(), select.getValue(), "пиксель(ей)"),
                select.getValue(),
                trySeg.getValue(),
                mainObjectColor.getValue()
        );
        fireEvent(new StartEvent(this, processingForm));
    }

    private void configMainObjectColor() {
        mainObjectColor.setVisible(false);
        mainObjectColor.setSuffixComponent(new Div("%"));
        mainObjectColor.setValue(20);
        mainObjectColor.setMin(1);
        mainObjectColor.setMax(99);
    }

    private void configTrySeg() {
        trySeg.addValueChangeListener(event -> mainObjectColor
                .setVisible(!mainObjectColor.isVisible()));
    }

    private void configHeight(ImageInfo info) {
        height.setMin(1);
        height.setValue(info.height());
        height.setHelperText("Ширина считается относительно" +
                " пропорции оригинального изображения");

        String help = "Количество пикселей задаётся в настройках разрешения экрана. " +
                "Это полностью синтетическая и производная единица измерения. Здесь " +
                "используется значение равное 1 сантиметр = 5 пикселей." +
                "Максимальный допустимый размер поля: 10 м (100 см, 5000 пикселей)";
        height.setTooltipText(help);
        Icon icon = VaadinIcon.QUESTION_CIRCLE.create();
        icon.addClickListener(event -> Notification.show(height.getTooltip().getText()));
        HorizontalLayout component = new HorizontalLayout(select, icon);
        component.setAlignItems(FlexComponent.Alignment.CENTER);
        height.setSuffixComponent(component);
        height.setStepButtonsVisible(true);
        height.setMax(5 * 10 * 100);

        select.setItems("см", "м", "пиксель(ей)");
        select.setValue("пиксель(ей)");

        select.addValueChangeListener(event -> {
            height.setValue(getValue(height.getValue(), event.getOldValue(),
                    event.getValue()));
            switch (event.getValue()) {
                case "см" -> height.setMax(100);
                case "м" -> height.setMax(10);
                default -> height.setMax(5 * 10 * 100);
            }
        });
    }

    private Integer getValue(int value, String oldSi, String newSi) {
        if ("см".equals(oldSi)) {
            if ("м".equals(newSi)) {
                return (int) (value / 100L);
            } else if ("пиксель(ей)".equals(newSi)){
                return value * 5;
            }
        } else if ("м".equals(oldSi)) {
            if ("см".equals(newSi)) {
                return value * 100;
            } else if ("пиксель(ей)".equals(newSi)){
                return value * 500;
            }
        } else if ("пиксель(ей)".equals(oldSi)) {
            if ("см".equals(newSi)) {
                return (int) (value / 5L);
            } else if ("м".equals(newSi)){
                return (int) (value / 500L);
            }
        }
        return value;
    }

    private void configNumbers(ImageInfo info) {
        numbers.setMax(info.colorQuantity());
        numbers.setValue(Math.min(info.colorQuantity(), 100));
        numbers.setStepButtonsVisible(true);
        numbers.setMin(1);
    }

    @Getter
    public abstract static class ProcessingFormEvent extends ComponentEvent<ProcessingForm> {
        private final transient ProcessForm processForm;

        protected ProcessingFormEvent(ProcessingForm source, ProcessForm contact) {
            super(source, false);
            this.processForm = contact;
        }
    }

    public static class StartEvent extends ProcessingFormEvent {
        public StartEvent(ProcessingForm source, ProcessForm processForm) {
            super(source, processForm);
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
