package ai.atmc.hawkadoccollector.domain.enums;

public enum CollectorTypeEnum {
    COLLECTOR_INTERNAL_SERVER("collector_internal_server"),
    COLLECTOR_SFTP_FTP("collector_sftp_ftp"),

    COLLECTOR_LOCAL_UPLOAD("collector_local_upload")
    ;


    private String type;

    CollectorTypeEnum(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
