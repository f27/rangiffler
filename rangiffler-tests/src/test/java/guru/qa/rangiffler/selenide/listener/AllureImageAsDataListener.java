package guru.qa.rangiffler.selenide.listener;

import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.StringUtils;

public class AllureImageAsDataListener implements LogEventListener {

    public final static String NAME = "ImageData";

    private final static String attachmentTemplate = "<img src=\"%s\">";

    @Override
    public void afterEvent(LogEvent event) {
        if (event.getStatus().equals(LogEvent.EventStatus.FAIL)) {
            final String errorMessage = event.getError().getMessage();
            if (errorMessage.contains("data:image")) {

                final String expected = StringUtils.substringBetween(errorMessage, "image data \"", "\"");
                final String actual = StringUtils.substringBetween(errorMessage, "Actual value: ", "\n");
                if (expected != null) {
                    Allure.addAttachment("Expected", "text/html", String.format(attachmentTemplate, expected), "html");
                }
                if (actual != null) {
                    Allure.addAttachment("Actual", "text/html", String.format(attachmentTemplate, actual), "html");
                }
            }
        }
        SelenideLogger.removeListener(NAME);
    }

    @Override
    public void beforeEvent(LogEvent event) {

    }
}
