package com.taxibooking.eventstreamer.models.enums;

public enum CommandType {

    PRODUCE(1), CONSUME(2);

    private final int code;

    CommandType(int type) {
        this.code = type;
    }

    public int getCode() {
        return code;
    }

    public static CommandType fromCode(int code)
    {
        for (CommandType type : CommandType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid command type code: " + code);
    }
}
