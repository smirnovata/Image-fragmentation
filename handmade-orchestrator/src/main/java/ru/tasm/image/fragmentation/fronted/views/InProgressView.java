package ru.tasm.image.fragmentation.fronted.views;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class InProgressView extends VerticalLayout implements HasComponents, HasStyle {
    private final ProgressBar progressBar;
    private Paragraph text;

    public InProgressView(ProgressBar progressBar) {
        this.progressBar = progressBar;
        constructUI();
    }

    void constructUI() {
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        add(progressBar);
        text = new Paragraph();
        text.add("Пожалуйста, подождите...");
        add(text, progressBar);
    }

    public static class FinishedProcessEvent extends ComponentEvent<InProgressView> {
        public FinishedProcessEvent(InProgressView source) {
            super(source, false);
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Setter
    @Getter
    public static class Task implements Runnable {
        private boolean isProgressing;
        private final Runnable updater;

        Task(boolean isProgressing, Runnable updater) {
            this.isProgressing = isProgressing;
            this.updater = updater;
        }

        @Override
        public void run() {
            log.debug("executionTime {}", isProgressing);
            while (this.isProgressing()) {
                // Sleep to emulate background work
                try {
                    Thread.sleep(3000);
                    updater.run();
                } catch (InterruptedException e) {
                    // Ignore
                }
            }
        }
    }
}