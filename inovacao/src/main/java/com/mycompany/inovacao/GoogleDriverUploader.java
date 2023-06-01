package com.mycompany.inovacao;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleDriverUploader {
    private static final String APPLICATION_NAME = "easy-innovation";
    private static final String CREDENTIALS_FILE_PATH = "";
    private static final String FOLDER_ID = ""; // Substitua pelo ID da pasta desejada

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        String diretorio = "/home/diegovieira/Downloads/Teste_arquivos"; // Substitua pelo diretório desejado

        java.io.File[] arquivos = new java.io.File(diretorio).listFiles();
        if (arquivos != null) {
            for (java.io.File arquivo : arquivos) {
                if (enviarParaGoogleDrive(arquivo)) {
                    if (arquivo.delete()) {
                        System.out.println("Arquivo " + arquivo.getName() + " excluído.");
                    } else {
                        System.out.println("Erro ao excluir arquivo " + arquivo.getName());
                    }
                } else {
                    System.out.println("Erro ao enviar arquivo " + arquivo.getName() + " para o Google Drive");
                }
            }
        }
    }

    private static boolean enviarParaGoogleDrive(java.io.File arquivo) throws IOException, GeneralSecurityException {
        Drive driveService = createDriveService();

        File fileMetadata = new File();
        fileMetadata.setName(arquivo.getName());
        fileMetadata.setParents(Collections.singletonList(FOLDER_ID));

        FileContent mediaContent = new FileContent(null, arquivo);

        File uploadedFile = driveService.files()
                .create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        return uploadedFile != null && uploadedFile.getId() != null;
    }

    private static Drive createDriveService() throws IOException, GeneralSecurityException {
        GoogleCredentials credential = GoogleCredentials.fromStream(
                new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), (HttpRequestInitializer) credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
