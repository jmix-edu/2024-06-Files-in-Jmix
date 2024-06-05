package com.company.jmixpmflowbase.view.task;

import com.company.jmixpmflowbase.entity.Task;

import com.company.jmixpmflowbase.entity.User;
import com.company.jmixpmflowbase.view.main.MainView;

import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.upload.FileStorageUploadField;
import io.jmix.flowui.component.upload.receiver.FileTemporaryStorageBuffer;
import io.jmix.flowui.download.Downloader;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.kit.component.upload.event.FileUploadSucceededEvent;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.upload.TemporaryStorage;
import io.jmix.flowui.view.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Route(value = "tasks", layout = MainView.class)
@ViewController("Task_.list")
@ViewDescriptor("task-list-view.xml")
@LookupComponent("tasksDataGrid")
@DialogMode(width = "64em")
public class TaskListView extends StandardListView<Task> {

    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private Downloader downloader;
    @Autowired
    private TemporaryStorage temporaryStorage;
    @ViewComponent
    private DataContext dataContext;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @ViewComponent
    private CollectionContainer<Task> tasksDc;

    @Supply(to = "tasksDataGrid.attachment", subject = "renderer")
    private Renderer<Task> tasksDataGridAttachmentRenderer() {
        return new ComponentRenderer<JmixButton, Task>(task -> {

            if (task.getAttachment() != null) {
                JmixButton button = uiComponents.create(JmixButton.class);
                button.setText(task.getAttachment().getFileName());
                button.setThemeName("tertiary-inline");
                button.addClickListener(clickEvent -> {
                    downloader.download(task.getAttachment());
                });
                return button;
            }
            return null;
        });
    }

    @Subscribe("tasksUploadBtn")
    public void onTasksUploadBtnFileUploadSucceeded(final FileUploadSucceededEvent<FileStorageUploadField> event) throws IOException {
        Receiver receiver = event.getReceiver();
        if (receiver instanceof FileTemporaryStorageBuffer storageBuffer) {
            UUID fileId = storageBuffer.getFileData().getFileInfo().getId();
            File file = temporaryStorage.getFile(fileId);
            if (file != null) {
                processFile(file);
                temporaryStorage.deleteFile(fileId);
            }
        }
    }

    private void processFile(File file) throws IOException {
        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
        for (String line : lines) {
            Task task = dataContext.create(Task.class);
            task.setName(line);
            final User user = (User) currentAuthentication.getUser();
            task.setAssignee(user);
            tasksDc.getMutableItems().add(task);
        }
    }
}