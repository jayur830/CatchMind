package SendSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DataSendSocket {
    private Socket clientSock;
    private InputStream clientInput;
    private OutputStream clientOutput;

    public DataSendSocket(String ip, int port) {
        try {
            this.clientSock = new Socket(ip, port);
            this.clientInput = this.clientSock.getInputStream();
            this.clientOutput = this.clientSock.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String data) {
        try {
            this.clientOutput.write(data.getBytes());
            this.clientOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive() {
        int len = -1;
        byte[] buf = new byte[512];
        try {
            len = this.clientInput.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return len == -1 ? "" : new String(buf).substring(0, len);
        }
    }

    public int getLocalPort() {
        return this.clientSock.getLocalPort();
    }

    public void close() {
        try {
            this.clientSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
