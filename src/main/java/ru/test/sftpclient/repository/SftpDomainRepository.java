package ru.test.sftpclient.repository;

import ru.test.sftpclient.model.DomainIp;
import ru.test.sftpclient.sftp.SftpClient;
import ru.test.sftpclient.util.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SftpDomainRepository implements DomainRepository {

    private final SftpClient sftpClient;
    private final String path;

    public SftpDomainRepository(SftpClient sftpClient, String path) {
        this.sftpClient = sftpClient;
        this.path = path;
    }

    @Override
    public List<DomainIp> findAll() throws IOException {
        InputStream stream = sftpClient.download(path);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = stream.read(buffer)) != -1) {
                outputStream.write(buffer,0, bytesRead);
            }

            String json = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            return JsonParser.parse(json);
        } finally {
            stream.close();
        }
    }

    @Override
    public void save(List<DomainIp> domainIps) throws IOException {
        String json = JsonParser.toJson(domainIps);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        InputStream stream = new ByteArrayInputStream(bytes);

        try {
            sftpClient.upload(stream, path);
        } finally {
            stream.close();
        }
    }
}
