package cryptoBalancer.Models.TCP;

import cryptoBalancer.Enums.ResponseStatus;

public class Response {
    private ResponseStatus responseStatus;
    private String responseMessage;
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
