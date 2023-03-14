package me.in1978.tools.anywhere.model;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import me.in1978.tools.anywhere.util.CloneBySerializable;
import me.in1978.tools.anywhere.util.StringUtils;
import me.in1978.tools.anywhere.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Data
@Accessors(chain = true)
@Slf4j
public class EngineModel implements CloneBySerializable<EngineModel> {

    public static final Duration DEFAULT_CHECK_INTERVAL = Duration.ofMinutes(1);

    private String type = "jsch";

    private String identifyFile;
    private String identifyValue;
    private Duration checkInterval = DEFAULT_CHECK_INTERVAL;

    public String retrieveIdentifyFile() {
        if (StringUtils.hasText(identifyFile)) {
            return identifyFile;
        }

        if (!StringUtils.hasText(identifyValue)) {
            return null;
        }

        try {
            File file = new File(String.format("%s/id_%s",
                    System.getProperty("java.io.tmpdir"), Utils.digest(identifyValue)));
            log.info("save identify to {}", file.getAbsolutePath());
            try (
                    FileOutputStream fos = new FileOutputStream(file);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                osw.write(identifyValue);
            }

            return file.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Duration retrieveCheckInterval() {
        return Optional.ofNullable(checkInterval).orElse(DEFAULT_CHECK_INTERVAL);
    }
}
