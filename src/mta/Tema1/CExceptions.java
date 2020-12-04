package mta.Tema1;

class CExceptions {
    String messaje;
    String cause;
    int code;

    public CExceptions(String messaje, String cause, int code) {
        this.messaje = messaje;
        this.cause = cause;
        this.code = code;
    }

    public String getMessaje() {
        return messaje;
    }

    public String getCause() {
        return cause;
    }

    public int getCode() {
        return code;
    }
}
