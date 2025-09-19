package tornaco.apps.shortx.core.rule.script.api;

import com.google.protobuf.Message;

import java.util.List;
import java.util.Map;

import tornaco.apps.shortx.core.annotations.DoNotStrip;
import tornaco.apps.shortx.core.proto.da.DirectAction;
import tornaco.apps.shortx.core.proto.rule.Rule;

@DoNotStrip
public interface ScriptShortXApi {

    /**
     * Return String for primitive type.
     * Return {@link List<String>} for List type.
     */
    Object readGlobalVar(String varName);

    void writeGlobalVar(String varName, String varValue);

    /**
     * Op value: one of below op enum numbers, e.g. 3 for override.
     * See {@link tornaco.apps.shortx.core.proto.action.WriteGlobalVarOp}
     */
    void writeGlobalVarWithOp(String varName, String varValue, int op);

    List<String> queryRuleIdByTitleAndDescription(String title, String description);

    List<String> queryDAIdByTitleAndDescription(String title, String description);

    List<String> queryRuleIdByTitle(String title);

    List<String> queryDAIdByTitle(String title);

    Rule queryRuleById(String id);

    DirectAction queryDAById(String id);

    void executeDAById(String id);

    void enableDisableRuleById(String id, boolean enable);

    Result executeAction(Message action);

    Result executeActions(List<Message> actions);

    @DoNotStrip
    class Result {
        /**
         * Context data from the actions.
         */
        public Map<String, Object> contextData;
    }
}
