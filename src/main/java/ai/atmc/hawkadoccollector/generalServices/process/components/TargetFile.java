package ai.atmc.hawkadoccollector.generalServices.process.components;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetFile {


    private final String ftp_host;
    private final int port;
    private final String username;
    private final String password;
    private final String absolutePath;
    private final TargetDirectory targetDirectory;
    private final String name;
    private Long size;



    private String targetFtpHost;
    private int targetPort;
    private String targetUsername;
    private String targetPassword;
    private String targetFilename;
    private String targetFilePath;

    public TargetFile(String ftp_host, int port, String username, String password, String absolutePath, TargetDirectory targetDirectory, String name, Long size) {
        this.ftp_host = ftp_host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.absolutePath = absolutePath;
        this.targetDirectory = targetDirectory;
        this.name = name;
        this.size=size;
    }
}
