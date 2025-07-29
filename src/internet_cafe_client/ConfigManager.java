// ConfigManager.java
package internet_cafe_client;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private Properties props;

    public ConfigManager() {
        props = new Properties();
        loadConfig();
    }

    private void loadConfig() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("No existing config found, will create new.");
        }
    }

    public void saveConfig(String pcId, String host, int port) {
        props.setProperty("pcId", pcId);
        props.setProperty("host", host);
        props.setProperty("port", String.valueOf(port));

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "PC Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPcId() {
        return props.getProperty("pcId", "pc-default");
    }

    public String getHost() {
        return props.getProperty("host", "localhost");
    }

    public int getPort() {
        return Integer.parseInt(props.getProperty("port", "5000"));
    }
}
