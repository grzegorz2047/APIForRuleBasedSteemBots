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

    public boolean asBoolean() {
        return Boolean.parseBoolean(String.valueOf(arg));
    }

    public long asLong() {
        return Long.parseLong(String.valueOf(arg));
    }

    public String[] asArray(String split) {
        return String.valueOf(arg).split(split);
    }

    public String[] castAsStringArray() {
        return (String[]) arg;
    }

}
