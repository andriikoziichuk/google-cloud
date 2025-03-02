package ua.koziichuk.drive.service.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

@Service
public class GoogleFormsService {

    @Autowired
    private Drive driveService;

    @Value("${forms.folder.id}")
    private String formsFolderId;

//    // Отримання відповідей з Google Forms
//    public List<FormResponse> getFormResponses(String formId) throws IOException {
//        // Використовуйте Google Forms API для отримання відповідей
//        // Приклад: https://developers.google.com/forms/api
//        return Collections.emptyList();
//    }
//
//    // Збереження відповідей у файл та завантаження на Google Drive
//    public void saveResponsesToDrive(String formId) throws IOException {
//        List<FormResponse> responses = getFormResponses(formId);
//        String fileName = "form_responses_" + formId + ".json";
//
//        java.io.File file = new java.io.File(fileName);
//        try (FileWriter writer = new FileWriter(file)) {
//            new Gson().toJson(responses, writer);
//        }
//
//        uploadFileToDrive(file, formsFolderId);
//    }

    // Завантаження файлу на Google Drive
    private void uploadFileToDrive(java.io.File file, String folderId) throws IOException {
        File fileMetadata = new File()
                .setName(file.getName())
                .setParents(Collections.singletonList(folderId));

        InputStreamContent mediaContent = new InputStreamContent(
                Files.probeContentType(file.toPath()),
                new FileInputStream(file)
        );

        driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
    }
}
