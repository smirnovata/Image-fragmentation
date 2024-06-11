package ru.tasm.image.fragmentation.fronted.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.quarkus.annotation.UIScoped;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ru.tasm.image.fragmentation.fronted.layout.MainLayout;
import ru.tasm.image.fragmentation.service.VaadinSessionService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@UIScoped
@PermitAll
@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {
    private final transient ExecutorService service = Executors.newCachedThreadPool();

    @Inject
    VaadinSessionService bean;

    @Inject
    UploadView uploadView;

    public MainView() {
        log.debug("init main view");
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        log.debug("[{}] detach main view", bean.getUUID());
        service.shutdown();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        setAlignItems(Alignment.CENTER);
        setAlignSelf(Alignment.CENTER);
        setWidthFull();
        init();
    }

    private void init() {
        UI current = UI.getCurrent();

        service.submit(() ->
                current.access(() -> {
                    log.debug("[{}] init main view", bean.getUUID());
                    add(uploadView);
                }));
    }
}
