package ru.tasm.image.fragmentation.model;

import ru.tasm.image.fragmentation.model.ui.ProcessForm;

import java.util.UUID;

public record Processing(UUID id, Status status, ProcessForm form) {

   public Processing updateStatus(Status status){
       return new Processing(id, status, form);
   }
}
