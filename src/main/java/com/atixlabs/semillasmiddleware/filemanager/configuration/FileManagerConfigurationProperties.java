package com.atixlabs.semillasmiddleware.filemanager.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "filemanager")
@Getter
@Setter
public class FileManagerConfigurationProperties {

    private String workPathDirectory;
}
