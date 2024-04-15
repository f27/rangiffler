package guru.qa.rangiffler.db.logging;

import io.qameta.allure.attachment.AttachmentData;
import lombok.Getter;

public record SqlAttachment(
        String name,
        @Getter
        String sql
) implements AttachmentData {
    @Override
    public String getName() {
        return name;
    }
}
