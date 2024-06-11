package ru.tasm.image.fragmentation.service.api;


import ru.tasm.image.fragmentation.model.ui.ProcessForm;

import java.io.Serializable;
@Deprecated
public interface DepImageService extends Serializable {
    int getColourCount();
    int getOrigHeight();

    void addFormResult(String id, ProcessForm form);
    ProcessForm getFormResult(String id);
}
