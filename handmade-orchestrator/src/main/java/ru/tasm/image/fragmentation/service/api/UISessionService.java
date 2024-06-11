package ru.tasm.image.fragmentation.service.api;


import com.vaadin.flow.component.UI;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.UUID;

public interface UISessionService extends Serializable {

    void addUISession(UUID id, UI view);

    UI getUISession(UUID id);
}
