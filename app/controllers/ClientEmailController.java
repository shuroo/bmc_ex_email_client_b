package controllers;

/**
 * Class to represent email CLIENT controller
 * @author shiri rave
 * @date 13/05/25
 * */


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
import bl.*;

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
     * Main method to send email from end user to server.
     * Validates the email and sends it farward
     * @param inputRequest - the client request
     * @return Result ( Synchronized )
     */
    public Result sendEmail(Http.Request inputRequest) throws IOException{

        JsonNode json = inputRequest.body().asJson();

        if (json == null) {
            String msg = "Expecting JSON data";
            logger.error(msg);
            return badRequest(msg);
        }

        String emailFrom = json.findPath(Constants.FROM_ADDRESS).textValue();
        String emailTo = json.findPath(Constants.TO_ADDRESS).textValue();
        String subject = json.findPath(Constants.EMAIL_SUBJECT).textValue();
        String body = json.findPath(Constants.EMAIL_BODY).textValue();

        if(!EmailValidator.validateEmail( emailFrom,  emailTo, subject, body)){
            return badRequest("Invalid Email request, Check your data and try again");
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
                            String emailResponse = response.body().string();
                            String msg = "Email sent successfully, response:"+emailResponse;
                            logger.info(msg);
                            return ok(emailResponse);
                        } else {
                            return status(response.code(), response.body().string());
                        }
                    } catch (IOException e) {
                        String msg = "Error sending email: " + e.getMessage();
                        logger.error(msg);
                        return internalServerError(msg);
                    }

                }
                catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    String msg = "Error sending email, invalid json input detected. Message: " + e.getMessage();
                    logger.error(msg);
                    return internalServerError(msg);
                }
                catch (RuntimeException e) {
                    String msg = "Error sending email, Message: " + e.getMessage();
                    logger.error(msg);
                    return internalServerError(msg);
                }

    }
}