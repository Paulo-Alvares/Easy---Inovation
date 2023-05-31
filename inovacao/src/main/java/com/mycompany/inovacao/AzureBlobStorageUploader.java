package com.mycompany.inovacao;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobStorageException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;

public class AzureBlobStorageUploader {
    private static final String CONNECTION_STRING = "DefaultEndpointsProtocol=https;"
            + "AccountName=easyinnovation;"
            + "AccountKey=NPsUSkgmMcyngkAlFz4JpY7MMQnvbiWfMtuyA9NYoLuMhJozxZ75rPPWOYQREVwI8LK2KcWRD9Oz+AStIrUckg==;"
            + "EndpointSuffix=core.windows.net";
    private static final String CONTAINER_NAME = "easy-innovation";

    public static void main(String[] args) {
        String diretorio = "/home/diegovieira/Downloads/Teste_arquivos"; // Substitua pelo diretório desejado

        File[] arquivos = new File(diretorio).listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                //if (isInativo(arquivo)) {
                    if (enviarParaAzureBlobStorage(arquivo)) {
                        if (arquivo.delete()) {
                            System.out.println("Arquivo " + arquivo.getName() + " excluído.");
                        } else {
                            System.out.println("Erro ao excluir arquivo " + arquivo.getName());
                        }
                    } else {
                        System.out.println("Erro ao enviar arquivo " + arquivo.getName() + " para o Azure Blob Storage");
                    }
                //} else {
                //    System.out.println(arquivo.getName() + " é recente.");
                //}
            }
        }
    }

    private static boolean isInativo(File arquivo) {
        Instant lastModifiedTime = Instant.ofEpochMilli(arquivo.lastModified());
        Instant now = Instant.now();
        Duration duration = Duration.between(lastModifiedTime, now);
        long diasInativos = duration.toDays();
        return diasInativos >= 30; // Defina o número de dias para considerar um arquivo como inativo
    }

    private static boolean enviarParaAzureBlobStorage(File arquivo) {
        BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                .connectionString(CONNECTION_STRING)
                .containerName(CONTAINER_NAME)
                .buildClient();

        String blobName = arquivo.getName();

        try {
            BlobClientBuilder blobClientBuilder = new BlobClientBuilder()
                    .connectionString(CONNECTION_STRING)
                    .containerName(CONTAINER_NAME)
                    .blobName(blobName);

            Path arquivoTemporario = Files.createTempFile(null, null);
            Files.copy(arquivo.toPath(), arquivoTemporario, StandardCopyOption.REPLACE_EXISTING);

            blobContainerClient.getBlobClient(blobName)
                    .uploadFromFile(arquivoTemporario.toString());

            return true;
        } catch (IOException | BlobStorageException e) {
            e.printStackTrace();
            return false;
        }
    }
}
