package cryptoBalancer.Models.TCP;

import com.google.gson.annotations.Expose;
import cryptoBalancer.Enums.ResponseStatus;
import jakarta.persistence.Id;

public class Response {
    @Expose
    private ResponseStatus responseStatus;
    @Expose
    private String responseMessage;
    @Expose
    private String responseData;

    public Response(ResponseStatus responseStatus, String responseMessage,String responseData) {
        this.responseStatus = responseStatus;
        this.responseMessage = responseMessage;
        this.responseData = responseData;
    }
    public Response(){
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public String getResponseData() {
        return responseData;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
