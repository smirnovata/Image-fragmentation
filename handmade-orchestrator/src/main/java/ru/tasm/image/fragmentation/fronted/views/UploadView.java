package ru.tasm.image.fragmentation.fronted.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.quarkus.annotation.UIScoped;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ru.tasm.image.fragmentation.fronted.component.ImagesCard;
import ru.tasm.image.fragmentation.fronted.component.ProcessingForm;
import ru.tasm.image.fragmentation.fronted.component.SmallImagesGallery;
import ru.tasm.image.fragmentation.model.IFFile;
import ru.tasm.image.fragmentation.model.ImageInfo;
import ru.tasm.image.fragmentation.model.Processing;
import ru.tasm.image.fragmentation.model.Status;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;
import ru.tasm.image.fragmentation.model.exception.FrontedException;
import ru.tasm.image.fragmentation.service.VaadinSessionService;
import ru.tasm.image.fragmentation.service.api.FileService;
import ru.tasm.image.fragmentation.service.api.ProcessingService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@UIScoped
@PermitAll
public class UploadView extends VerticalLayout implements HasComponents, HasStyle {
    private final transient ExecutorService service = Executors.newCachedThreadPool();

    @Inject
    VaadinSessionService bean;
    @Inject
    FileService fileService;
    @Inject
    ProcessingService processingService;

    private Div output;
    private VerticalLayout vertTextUpload;
    private VerticalLayout mainVerticalLayout;
    private Upload upload;
    private ProcessingForm form;
    private Button reUpload;
    private transient InProgressView.Task task;
    private VerticalLayout resultView;
    private VerticalLayout imageView;

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        log.debug("[{}] detach upload view", bean.getUUID());
        service.shutdown();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        init();
    }

    void init() {
        log.debug("[{}] init upload view", bean.getUUID());
        constructUI();
    }

    private void constructUI() {
        this.addClassName(LumoUtility.AlignContent.CENTER);
        mainVerticalLayout = new VerticalLayout();
        HorizontalLayout imageResultRow = new HorizontalLayout();
        output = new Div();
        resultView = new VerticalLayout();
        resultView.addClassNames(LumoUtility.MaxWidth.SCREEN_SMALL);
        createVertTextUpload();
        imageResultRow.add(vertTextUpload, output);
        mainVerticalLayout.add(imageResultRow);
        add(mainVerticalLayout);

        addListener(InProgressView.FinishedProcessEvent.class, this::showResult);
        imageView = new VerticalLayout();
        imageView.setWidthFull();
        imageView.addClassNames("max-content");
        imageView.addClassNames(LumoUtility.AlignItems.CENTER);

        updateIfExistsOrigFile();
    }

    private void updateIfExistsOrigFile() {
        try {
            IFFile origFile = fileService.getOrigFile(bean.getUUID());
            if (origFile != null) {
                log.debug("[{}] update UI with existing image", bean.getUUID());
                updateOutput();
                upload.setVisible(false);
                createReUploadButton();
                createProcessingView(origFile);
                updateIfInProgressing();
                updateIfExistsResult();
                mainVerticalLayout.add(resultView);
            }
        } catch (DataBaseException e) {
            Notification.show("Произошла ошибка при загрузке страницы.");
            throw new FrontedException(e);
        }
    }

    private void updateIfInProgressing() throws DataBaseException {
        Processing processing = processingService
                .getInProgressProcessing(bean.getUUID());
        if (processing != null) {
            form.processing.setEnabled(false);
            reUpload.setEnabled(false);
            form.height.setValue(processing.form().getHeight());
            form.numbers.setValue(processing.form().getNumbers());
            form.trySeg.setValue(processing.form().getTrySeg());
            form.mainObjectColor.setValue(processing.form().getBackgraundColor());
            startBackgroundJob(new ProcessingForm.StartEvent(form, processing.form()));
        }
    }

    private void updateIfExistsResult() {
        showResult();
    }

    private void createVertTextUpload() {
        vertTextUpload = new VerticalLayout();
        mainVerticalLayout.setAlignItems(Alignment.CENTER);
        vertTextUpload.setWidthFull();
        MultiFileBuffer buffer = new MultiFileBuffer();
        createImageUpload(buffer);
        Paragraph text = getTextInfo();
        vertTextUpload.add(text, upload);
    }

    private void createImageUpload(MultiFileBuffer buffer) {
        upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setMaxFileSize(10485760);
        Button button = new Button();
        upload.setMaxFiles(1);
        button.setText("Загрузите ");
        upload.setUploadButton(button);
        upload.setDropLabel(new Div(new Text("или перетащите изображение...")));
        addImageUploadListeners(buffer);
    }

    private static Paragraph getTextInfo() {
        Paragraph text = new Paragraph();
        text.add(getParagraph("Загрузите изображение."),
                getParagraph("Выберите необходимые параметры."),
                getParagraph("Скачайте полученные результаты."));
        return text;
    }

    private static Paragraph getParagraph(String text) {
        Paragraph paragraph = new Paragraph(text);
        paragraph.setWidthFull();
        return paragraph;
    }

    private void addImageUploadListeners(MultiFileBuffer buffer) {
        upload.addFileRejectedListener(event ->
                Notification.show("Не удалось загрузить файл." +
                                " " +
                                "Проверьте разрешение файла(jpeg, jpg, png, gif и т.п)." +
                                " " +
                                "Допустимый размер файла - 10 MB", 5000,
                        Notification.Position.TOP_CENTER));

        upload.addSucceededListener(event -> {
            try {
                String uploadFileName = event.getFileName();
                log.info("[{}] Upload file: {}", bean.getUUID(), uploadFileName);
                fileService.clearSessionFolder(bean.getUUID());
                IFFile file = new IFFile(bean.getUUID(), event.getFileName(),
                        buffer.getFileData(uploadFileName).getFile());
                log.debug("[{}] Upload file: {}", bean.getUUID(), buffer.getFileData(uploadFileName).getFile());
                fileService.saveOrigFile(file);
                updateOutput();
                IFFile origFile = fileService.getOrigFile(bean.getUUID());
                createProcessingView(origFile);
                upload.clearFileList();
                upload.setVisible(false);
                createReUploadButton();
            } catch (DataBaseException e) {
                Notification.show("Не получилось загрузить файл.");
                throw new FrontedException(e);
            }
        });
    }

    private void createReUploadButton() {
        reUpload = new Button();
        reUpload.setText("Загрузить другое изображение");
        vertTextUpload.add(reUpload);

        reUpload.addClickListener(clickEvent -> {
            try {
                fileService.clearSessionFolder(bean.getUUID());
                output.removeAll();
                form.removeAll();
                reUpload.setVisible(false);
                upload.setVisible(true);
                resultView.removeAll();
            } catch (DataBaseException e) {
                Notification.show("Не получилось обновить вид.");
                throw new FrontedException(e);
            }
        });
    }

    private void updateOutput() throws DataBaseException {
        IFFile origFile = fileService.getOrigFile(bean.getUUID());
        StreamResource src = fileService.getStreamResourceFromFile(origFile);
        Image image = new Image();
        image.setSrc(src);
        image.setMaxHeight("300px");
        output.removeAll();
        output.add(image);
    }

    private void createProcessingView(IFFile origFile) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setHeight("min-content");
        verticalLayout.addClassNames(LumoUtility.Display.GRID        );
        ImageInfo imageInfo = processingService.getImageInfo(origFile.file().getPath());
        form = new ProcessingForm(imageInfo);
        form.getStyle().set("flex-grow", "1");
        form.addListener(ProcessingForm.StartEvent.class, this::startProcessing);
        form.addListener(ProcessingForm.StartEvent.class, this::startBackgroundJob);
        verticalLayout.add(form);
        mainVerticalLayout.add(verticalLayout);
        mainVerticalLayout.add(resultView);
    }

    private void startBackgroundJob(ProcessingForm.StartEvent startEvent) {
        log.debug("here 3");
        resultView.removeAll();
        getUI().ifPresent(ui -> {
            try {
                Processing inProgressProcessing = processingService
                        .getInProgressProcessing(bean.getUUID());
                boolean isProcessing = inProgressProcessing != null;
                ProgressBar progressBarFinal = new ProgressBar();
                InProgressView inProgressView = new InProgressView(progressBarFinal);
                inProgressView.addClassNames(LumoUtility.MaxWidth.SCREEN_SMALL);
                resultView.add(inProgressView);
                Runnable onUpdate = () -> ui.access(() -> {
                    log.info("onUpdate {}", bean.getUUID());
                    try {
                        updateTaskProgress(inProgressView);
                    } catch (DataBaseException e) {
                        throw new FrontedException(e);
                    }
                });
                task = new InProgressView.Task(isProcessing, onUpdate);
                service.submit(task);
            } catch (DataBaseException e) {
                throw new FrontedException(e);
            }
        });
    }

    private void updateTaskProgress(InProgressView inProgressView) throws DataBaseException {
        Processing inProgressProcessing = processingService.getInProgressProcessing(bean.getUUID());
        log.debug("inProgressProcessing={}", inProgressProcessing);
        if (inProgressProcessing == null) {
            inProgressView.getProgressBar().setIndeterminate(false);
            task.setProgressing(false);
            inProgressView.getProgressBar().setMax(1);
            inProgressView.getProgressBar().setValue(1);
            inProgressView.getText().setVisible(false);
            inProgressView.getProgressBar().addThemeVariants(ProgressBarVariant.LUMO_CONTRAST);
            reUpload.setEnabled(true);
            form.processing.setEnabled(true);
            fireEvent(new InProgressView.FinishedProcessEvent(inProgressView));
        }
    }

    private void startProcessing(ProcessingForm.StartEvent event) {
        try {
            log.debug("startProcessing {}", event.getProcessForm());
            form.processing.setEnabled(false);
            reUpload.setEnabled(false);
            processingService.startProcessing(new Processing(
                    bean.getUUID(),
                    Status.IN_PROGRESS,
                    event.getProcessForm()
            ));
        } catch (DataBaseException e) {
            log.error(e.getMessage(), e);
            Notification.show("Произошла ошибка. Повтори еще раз");
        }
    }

    private void showResult(InProgressView.FinishedProcessEvent finishedEvent) {
        this.showResult();
    }

    private void showResult() {
        File folder = fileService.getResultFolder(bean.getUUID());
        String resultFiles = folder.getAbsolutePath() + File.separator + "result";
        File[] files = new File(resultFiles).listFiles();
        List<ImagesCard> imagesCards = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (!file.getName().contains("json") && !file.getName().contains("mask")) {
                    ImageInfo imageInfo = processingService
                            .getImageInfo(file.getPath());
                    StreamResource src = fileService.getStreamResourceFromFile(file);
                    ImagesCard card = ImagesCard.from(src, imageInfo);
                    card.addListener(ImagesCard.ExpandEvent.class, this::expandResult2);
                    imagesCards.add(card);
                }
            }
        }

        resultView.add(new SmallImagesGallery(imagesCards));
    }

    private void expandResult2(ImagesCard.ExpandEvent expandEvent) {
        log.info("here2 {}", expandEvent);
        ImagesCard imagesCard = expandEvent.getImagesCard();
        log.info("here {}", imagesCard);
        imageView.removeAll();
        Paragraph name = imagesCard.name;
        imageView.add(new ImageResultView(
                imagesCard,
                fileService.getOneResultZipFile(bean.getUUID(), name.getText())));
        resultView.add(imageView);
    }

    private void tempButton() {
        Button textButton = new Button("test");
        textButton.addClickListener(event -> {
            try {
                processingService.updateStatus(bean.getUUID(), Status.FINISHED);
            } catch (DataBaseException e) {
                throw new FrontedException(e);
            }
        });
        add(textButton);
    }
}
