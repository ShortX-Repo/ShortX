package tornaco.apps.shortx.services.rule.actor

import tornaco.apps.shortx.core.proto.action.BreakActionExecuteScope

class BreakActionException(override val message: String, val scope: BreakActionExecuteScope) :
    RuntimeException()