package tornaco.apps.shortx.core;

interface IShortX {
    Version version();
    String fingerprint();

    void addRule(in ByteArrayWrapper rule);
    void deleteRule(in ByteArrayWrapper rule);
    List<ByteArrayWrapper> getAllRules();
}