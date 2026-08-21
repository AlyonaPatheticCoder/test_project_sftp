package ru.test.sftpclient.sftp;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;

public class SftpClient {
    private final String host;
    private final int port;
    private final String username;
    private final String pass;
    private ChannelSftp channel;
    private Session session;

    public SftpClient(String host, int port, String username, String pass) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.pass = pass;
    }

    public void connect() throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, port);

        session.setPassword(pass);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
    }

    public InputStream download(String path) throws IOException {
        if (channel == null || !channel.isConnected()) {
            throw new IOException("Channel is not connected");
        }

        try {
            return channel.get(path);
        } catch (Exception e) {
            throw new IOException("Failed to download file", e);
        }
    }

    public void upload(InputStream inputStream, String path) throws IOException {
        if (channel == null || !channel.isConnected()) {
            throw new IOException("Channel is not connected");
        }

        try {
            channel.put(inputStream, path);
        } catch (Exception e) {
            throw new IOException("Failed to upload file", e);
        }
    }

    public void disconnect() {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }


}
