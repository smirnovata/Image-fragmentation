package ru.tasm.image.fragmentation.config;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Getter
@ApplicationScoped
public class Config {
    @ConfigProperty(name = "handmade.orchestrator.temp.folder.path")
    String folder;
}
