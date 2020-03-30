#Config Files
Spring Application config files can be placed in the config directory at the top level folder

Refer to https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config-application-property-files

If spring.config.location contains directories (as opposed to files), they should end in / (and, at runtime, be appended with the names generated from spring.config.name before being loaded, including profile-specific file names). Files specified in spring.config.location are used as-is, with no support for profile-specific variants, and are overridden by any profile-specific properties.
Config locations are searched in reverse order. By default, the configured locations are classpath:/,classpath:/config/,file:./,file:./config/. The resulting search order is the following:

```
file:./config/
file:./
classpath:/config/
classpath:/
```