package configuration;

import com.typesafe.config.Config;
import model.*;

/**
 * Class to fetch email configurations from the application.conf file.
 *
 * This class provides methods to retrieve various email-related configuration
 * settings to read from application.conf file.
 *
 * @author Shiri Rave
 * @date 13/05/2025
 */
public class Configuration {

    private final Config configuration;

    /**
     * Constructs an EmailConfiguration instance using the provided Config.
     *
     * @param configuration the configuration object that holds email settings
     */
    public Configuration(Config configuration) {
        this.configuration = configuration;
    }
    /**
     * Aid method to build server url
     * @return String
     */
    public String getServerUrl(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.configuration.getString(Constants.SERVER_URL_KEY));
        builder.append(this.configuration.getString(Constants.SERVER_ENDPOINT_KEY));
        return builder.toString();
    }
}
