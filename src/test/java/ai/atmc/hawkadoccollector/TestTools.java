package ai.atmc.hawkadoccollector;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;


public class TestTools {
    /**
     * For converting string content to object or vice versa
     */
    protected ObjectMapper objectMapper;

    /**
     * Parent folder for all resource that used for testing
     */
    protected String RESOURCE_DIR;

    protected String INTERNAL_SERVER_TEST_DATA;

    public TestTools(){
        this.objectMapper = new ObjectMapper();
        this.RESOURCE_DIR =  System.getProperty("user.dir") + File.separator + "src" + File.separator +
        "test" + File.separator + "resources";

        this.INTERNAL_SERVER_TEST_DATA = "/data/test/temp";
    }
}
