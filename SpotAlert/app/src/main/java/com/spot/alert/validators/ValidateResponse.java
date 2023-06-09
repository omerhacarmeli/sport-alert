package com.spot.alert.validators;

public class ValidateResponse {//קלאס זה מחזיק שני ערכים
    private String msg;//הודעה
    private boolean validate;// והולידציה של האם הוא שלילי או חיובי. כלומר תקין או לא תקין
    public ValidateResponse(){//קונסטרקטור ריק

    }
    public ValidateResponse(boolean validate,String msg ) {//קונסטרקטור
        this.msg = msg;
        this.validate = validate;
    }
    //setters and getters
    public ValidateResponse(boolean validate) {
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

