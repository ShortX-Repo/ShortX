package tornaco.apps.shortx.services.rule.actor.mvel.handle;

@HandlerName("input")
public
interface IInput {

    boolean injectKey(int code);

    int getLastKey();

    long getLastKeyTime();
}
