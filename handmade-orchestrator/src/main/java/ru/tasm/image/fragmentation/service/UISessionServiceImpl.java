package ru.tasm.image.fragmentation.service;
import jakarta.enterprise.context.ApplicationScoped;
import com.helger.commons.collection.impl.CommonsConcurrentHashMap;
import com.vaadin.flow.component.UI;
import org.apache.commons.lang3.tuple.Pair;
import ru.tasm.image.fragmentation.service.api.UISessionService;

import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class UISessionServiceImpl implements UISessionService {
    private static final Map<UUID, UI> uiSessions =
            new CommonsConcurrentHashMap<>();

    @Override
    public void addUISession(UUID id, UI ui) {
        uiSessions.put(id, ui);
    }

    @Override
    public UI getUISession(UUID id) {
        return uiSessions.get(id);
    }
}
