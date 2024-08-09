package net.risesoft;

import java.io.InputStream;

import org.apache.james.mime4j.dom.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import net.risesoft.james.service.impl.ImportEmlServiceImpl;

/**
 * 基本的eml文件解析示例
 *
 * @author chendd
 * @date 2023/2/11 19:26
 */
public class EmlBasicTest {

    @InjectMocks
    private ImportEmlServiceImpl importEmlService;

    @Test
    public void importEml() {
        try (InputStream inputStream = EmlBasicTest.class.getResourceAsStream("/新用户收件.eml")) {
            Message message = Message.Builder.of(inputStream).build();
            importEmlService.importEmailByEml(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach()
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

}
