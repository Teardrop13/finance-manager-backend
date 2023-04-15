package pl.teardrop.backend;

import pl.teardrop.financemanager.FinanceManagerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest(classes = FinanceManagerApplication.class)
class FinanceManagerApplicationTests {

    @Test
    void test() {
        Assert.isTrue(true);
    }
}
