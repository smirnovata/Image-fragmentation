package ru.tasm.image.fragmentation.service;

import com.vaadin.quarkus.annotation.VaadinSessionScoped;
import ru.tasm.image.fragmentation.model.Status;

import java.io.Serializable;
import java.util.UUID;


@VaadinSessionScoped
public class VaadinSessionService implements Serializable {
    private final UUID id = UUID.randomUUID();

    public UUID getUUID(){
        return id;
    }

    public String getTextId(){
        return id.toString();
    }
}