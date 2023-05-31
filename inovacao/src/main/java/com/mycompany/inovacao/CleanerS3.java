package com.mycompany.inovacao;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class CleanerS3 {
    private static final String AWS_REGION = "us-east-1"; // Defina a região da AWS apropriada
    private static final String BUCKET_NAME = "easy-innovation"; // Substitua pelo nome do seu bucket na AWS

    public static void main(String[] args) {
        String diretorio = "/home/diegovieira/Downloads/Teste_arquivos"; // Substitua pelo diretório desejado

        File[] arquivos = new File(diretorio).listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                //if (isInativo(arquivo.toPath())) {
                    if (enviarParaS3(arquivo)) {
                        if (arquivo.delete()) {
                            System.out.println("Arquivo " + arquivo.getName() + " excluído.");
                        } else {
                            System.out.println("Erro ao excluir arquivo " + arquivo.getName());
                        }
                    } else {
                        System.out.println("Erro ao enviar arquivo " + arquivo.getName() + " para o S3");
                    }
                //} else {
                //    System.out.println(arquivo.getName() + " é recente.");
                //}
            }
        }
    }

    private static boolean isInativo(Path path) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            Instant lastModifiedTime = attributes.lastModifiedTime().toInstant();
            Instant now = Instant.now();
            Duration duration = Duration.between(lastModifiedTime, now);
            long diasInativos = duration.toDays();
            return diasInativos >= 30; // Defina o número de dias para considerar um arquivo como inativo
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean enviarParaS3(File arquivo) {
        try (S3Client s3Client = S3Client.builder()
                .region(Region.of(AWS_REGION))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {
            String nomeArquivo = arquivo.getName();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(nomeArquivo)
                    .build();
            RequestBody requestBody = RequestBody.fromFile(arquivo);
            s3Client.putObject(putObjectRequest, requestBody);

            return true;
        }    
    }
}
