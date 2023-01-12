package internals;

public enum CustomCharacterData implements org.passay.CharacterData {
    Special ("INSUFFICIENT_SPECIAL", "!#$%&()*+,-./:;<=>?@[\\]^_`{|}~");

    private final String errorCode;
    private final String characters;

    CustomCharacterData (final String code, final String charString) {
        errorCode = code;
        characters = charString;
    }

    @Override
    public String getErrorCode () {
        return errorCode;
    }

    @Override
    public String getCharacters () {
        return characters;
    }
}