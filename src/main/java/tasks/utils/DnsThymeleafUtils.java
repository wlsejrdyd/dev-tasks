package tasks.utils;

import org.springframework.stereotype.Component;

@Component("dns")
public class DnsThymeleafUtils {

    public String extractDomain(String host) {
        if (host == null || !host.contains(".")) return "";
        String[] parts = host.split("\\.");
        if (parts.length < 2) return host;
        return parts[parts.length - 2] + "." + parts[parts.length - 1];
    }
}
