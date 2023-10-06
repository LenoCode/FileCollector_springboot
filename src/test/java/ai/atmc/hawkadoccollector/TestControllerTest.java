/*
package ai.atmc.hawkadoccollector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(TestController.class)
class TestControllerTest extends TestTools{

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TestRepository testRepository;


    @Test
    void hello() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/v1/test/test");
        MvcResult result = mvc.perform(request).andReturn();

        Page<DocumentCollectorDao> documents = objectMapper.readValue(result.getResponse().getContentAsString(),Page.class);

        assertEquals(0,result.getResponse().getContentAsString());
    }
    @Test
    void hello1() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/v1/test/paged");
        MvcResult result = mvc.perform(request).andReturn();

        Page<DocumentCollectorDao> documents = objectMapper.readValue(result.getResponse().getContentAsString(),Page.class);

        assertEquals(0,result.getResponse().getContentAsString());
    }
}
*/
