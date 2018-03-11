package pl.grzegorz2047.botapi.bot.argument;


public class Argument {

    private final Object arg;

    public Argument(Object arg) {
        this.arg = arg;
    }


    public String asString() {
        return String.valueOf(arg);
    }

    public int asInt() {
        return Integer.valueOf(String.valueOf(arg));
    }

    public float asFloat() {
        return Float.valueOf(String.valueOf(arg));
    }

    public short asShort() {
        return Short.valueOf(String.valueOf(arg));
    }
}
