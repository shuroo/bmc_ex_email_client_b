package controllers;

/**
 * Class to represent email client controller
 */


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import okhttp3.*;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import java.io.IOException;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.typesafe.config.Config;
import configuration.*;
import model.*;

public class ClientEmailController extends Controller {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Config configuration;
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public ClientEmailController(Config configuration) {
        this.configuration = configuration;
    }



    /**
     * Update the server response and return as a json
     * @param response - the response to handle
     * @return Result
     */
    private Result updateServerResponse(Response response) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJson = mapper.readTree(response.body().string());

        // Add data to the JSON response
        ObjectNode updatedJson = (ObjectNode) responseJson;
        updatedJson.put("Response", "sending email to the server succeeded!");
        updatedJson.put("status", "success");

        return ok(Json.toJson(updatedJson));
    }

    /**
     * Main method to send email from end user to server.
     * Validates the email and sends it farward
     * @param inputRequest - the client request
     * @return Result ( Synchronized )
     */
    public Result sendEmail(Http.Request inputRequest) throws IOException{

        JsonNode json = inputRequest.body().asJson();

                if (json == null) {
                    return badRequest("Expecting JSON data");
                }

                try {
                    String emailJson = objectMapper.writeValueAsString(json);
                    Configuration emailConfig = new Configuration(this.configuration);
                    String endpointUrl = emailConfig.getServerUrl();
                    Request requestToSend = new Request.Builder()
                            .url(endpointUrl)
                            .post(RequestBody.create(emailJson, MediaType.parse(Constants.CONTENT_TYPE)))
                            .build();

                    try (Response response = client.newCall(requestToSend).execute()) {
                        if (response.isSuccessful()) {
                            return updateServerResponse(response);
                        } else {
                            return status(response.code(), response.body().string());
                        }
                    } catch (IOException e) {
                        return internalServerError("Error sending email: " + e.getMessage());
                    }

                }
                catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    return internalServerError("Error sending email, invalid json input detected. Message: " + e.getMessage());
                }
                catch (RuntimeException e) {
                    return internalServerError("Error sending email, Message: " + e.getMessage());
                }

    }
}