package com.spot.alert.validators;

public class ValidateResponse {
    private String msg;
    private boolean validate;
    public ValidateResponse(){

    }

    public ValidateResponse(boolean validate,String msg ) {
        this.msg = msg;
        this.validate = validate;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }
}

