package mta.Tema1;

public class CExceptions extends Throwable {
    String message;
    String cause;
    int code;

    public CExceptions() {
        message = null;
        cause = null;
        code = 0;
    }

    public CExceptions(String message, String cause, int code) {
        this.message = message;
        this.cause = cause;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

  /*  public String getCause() {
        return cause;
    }*/

    public int getCode() {
        return code;
    }
}
