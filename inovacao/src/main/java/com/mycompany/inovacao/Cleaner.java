package com.mycompany.inovacao;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cleaner {

    private static final long UM_MINUTO_MS = 60 * 1000; // um minuto em milissegundos
    private static final long UMA_HORA_MS = 60 * UM_MINUTO_MS; // uma hora em milissegundos
    private static final long UM_DIA_MS = 24 * UMA_HORA_MS; // um dia em milissegundos
    private static final long UM_MES_MS = 30 * UM_DIA_MS; // um mês em milissegundos
    private static final long UM_ANO_MS = 365 * UM_DIA_MS; // um ano em milissegundos

    private static final Map<String, Long> TEMPO_INATIVIDADES = new HashMap<>(); // mapa de tempo de inatividade por extensão

    static
    {
        TEMPO_INATIVIDADES.put("doc", UM_MES_MS); // Texto
        TEMPO_INATIVIDADES.put("docx", UM_MES_MS);
        TEMPO_INATIVIDADES.put("txt", UM_MES_MS);
        // Restante das extensões...

        // Inicialize a biblioteca do Google Drive com as credenciais
        try
        {
            Drive driveService = initializeDriveService();
        } catch (IOException | GeneralSecurityException e)
        {
            e.printStackTrace();
        }
    }

    private static Drive initializeDriveService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // Carregue as credenciais do arquivo JSON de credenciais
        InputStream in = new FileInputStream("caminho/para/arquivo-credenciais.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

        // Configure o fluxo de autorização
        List<String> scopes = Arrays.asList(DriveScopes.DRIVE);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, scopes)
                .setCredentialStore(new FileCredentialStore(new java.io.File("tokens"), jsonFactory))
                .setAccessType("offline")
                .build();

        // Autorize o aplicativo a acessar o Google Drive
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        // Crie uma instância do serviço do Google Drive
        return new Drive.Builder(httpTransport, jsonFactory, (HttpRequestInitializer) credential)
                .setApplicationName("ArquivoCleaner")
                .build();
    }

    public static void clear() {
        // Restante do código...
        // ...
        for (Map.Entry<String, List<File>> entry : arquivosPorExtensao.entrySet())
        {
            String extensao = entry.getKey();
            List<File> arquivosDaExtensao = entry.getValue();
            System.out.println(extensao.toUpperCase() + " - " + arquivosDaExtensao.size() + " arquivo(s):");
            for (File arquivo : arquivosDaExtensao)
            {
                Date dataModificacao = new Date(arquivo.getModifiedTime().getValue());
                long tempoInatividade = new Date().getTime() - dataModificacao.getTime();
                long tempoMaximo = TEMPO_INATIVIDADES.getOrDefault(extensao, UM_ANO_MS);
                if (tempoInatividade > tempoMaximo)
                {
                    // Envia o arquivo para o Google Drive
                    if (enviarParaGoogleDrive(arquivo))
                    {
                        // Se o envio for bem-sucedido, exclui o arquivo localmente
                        if (arquivo.delete())
                        {
                            System.out.println("Arquivo " + arquivo.getName() + " eliminado.");
                        } else
                        {
                            System.out.println("Erro ao eliminar arquivo " + arquivo.getName());
                        }
                    } else
                    {
                        System.out.println("Erro ao enviar arquivo " + arquivo.getName() + " para o Google Drive");
                    }
                } else
                {
                    System.out.println(arquivo.getName() + " (modificado em " + dataModificacao + ")");
                }
            }
        }
    }

    private static boolean enviarParaGoogleDrive(File arquivo) {
        try
        {
            Drive driveService = initializeDriveService();

            // Crie um objeto File para representar o arquivo a ser enviado
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(arquivo.getName());

            // Especifique a pasta de destino no Google Drive (opcional)
            fileMetadata.setParents(Collections.singletonList("FOLDER_ID"));

            // Crie um objeto FileContent a partir do arquivo
            FileContent mediaContent = new FileContent(Files.probeContentType(arquivo.toPath()), arquivo);

            // Crie o arquivo no Google Drive
            com.google.api.services.drive.model.File file = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            // Verifique se o arquivo foi criado com sucesso
            return file != null && file.getId() != null;
        } catch (IOException | GeneralSecurityException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
