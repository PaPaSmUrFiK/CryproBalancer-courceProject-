package cryptoBalancer.Models.TCP;

import cryptoBalancer.Enums.RequestType;

public class Request {
    private RequestType requestType;
    private String requestMessage;

    public Request() {
    }

    public Request(RequestType requestType, String requestMessage) {
        this.requestType = requestType;
        this.requestMessage = requestMessage;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
