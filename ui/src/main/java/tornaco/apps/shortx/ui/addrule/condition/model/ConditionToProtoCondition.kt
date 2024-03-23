package tornaco.apps.shortx.ui.addrule.condition.model

import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.condition.AppHasAudioFocus
import tornaco.apps.shortx.core.proto.condition.AppHasNotification
import tornaco.apps.shortx.core.proto.condition.AppHasTask
import tornaco.apps.shortx.core.proto.condition.AppHasWindowFocus
import tornaco.apps.shortx.core.proto.condition.AppIsNotRunning
import tornaco.apps.shortx.core.proto.condition.AppIsRunning
import tornaco.apps.shortx.core.proto.condition.BatteryPercent
import tornaco.apps.shortx.core.proto.condition.ChargeState
import tornaco.apps.shortx.core.proto.condition.ConnectedWifiSignal
import tornaco.apps.shortx.core.proto.condition.CurrentActivity
import tornaco.apps.shortx.core.proto.condition.CurrentPkgList
import tornaco.apps.shortx.core.proto.condition.CurrentPkgListByPkg
import tornaco.apps.shortx.core.proto.condition.EvaluateContextVar
import tornaco.apps.shortx.core.proto.condition.EvaluateGlobalVar
import tornaco.apps.shortx.core.proto.condition.EvaluateLocalVar
import tornaco.apps.shortx.core.proto.condition.HasNodeOnScreen
import tornaco.apps.shortx.core.proto.condition.IsHeadsetPlug
import tornaco.apps.shortx.core.proto.condition.IsInCall
import tornaco.apps.shortx.core.proto.condition.IsRinging
import tornaco.apps.shortx.core.proto.condition.IsRuleEnabled
import tornaco.apps.shortx.core.proto.condition.KeyguardIsLocked
import tornaco.apps.shortx.core.proto.condition.MatchJS
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.condition.PlugState
import tornaco.apps.shortx.core.proto.condition.RequireAPMMode
import tornaco.apps.shortx.core.proto.condition.RequireBTConnected
import tornaco.apps.shortx.core.proto.condition.RequireBTDeviceFound
import tornaco.apps.shortx.core.proto.condition.RequireBTDisconnected
import tornaco.apps.shortx.core.proto.condition.RequireDelay
import tornaco.apps.shortx.core.proto.condition.RequireFactTag
import tornaco.apps.shortx.core.proto.condition.RequireIMEVisibility
import tornaco.apps.shortx.core.proto.condition.RequireMobileDataEnabled
import tornaco.apps.shortx.core.proto.condition.RequireScreenRotate
import tornaco.apps.shortx.core.proto.condition.RequireSensorOff
import tornaco.apps.shortx.core.proto.condition.RequireTileState
import tornaco.apps.shortx.core.proto.condition.RequireWifiConnected
import tornaco.apps.shortx.core.proto.condition.RequireWifiDisconnected
import tornaco.apps.shortx.core.proto.condition.RequireWindowRotation
import tornaco.apps.shortx.core.proto.condition.ScreenIsOn
import tornaco.apps.shortx.core.proto.condition.ScreenOrientationIsPort
import tornaco.apps.shortx.core.proto.condition.ServiceIsRunning
import tornaco.apps.shortx.core.proto.condition.TheXXTimeToday
import tornaco.apps.shortx.core.proto.condition.TimeInRange
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.condition.VPNIsConnected
import tornaco.apps.shortx.core.proto.fact.NodeMatcherText
import tornaco.apps.shortx.core.proto.fact.NodeMatcherViewId
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.ui.addrule.condition.op.toConditionOperator
import tornaco.apps.shortx.ui.addrule.fact.model.NodeMatcher


fun Condition.toProtoCondition(overrideId: String? = null): ProtoMessage {
    return when (this) {
        is Condition.CurrentForegroundApp -> {
            CurrentPkgList.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .addAllPkgs(
                    apps.map { app ->
                        AppPkg.newBuilder()
                            .setPkgName(app.pkg.pkgName)
                            .setUserId(app.pkg.userId)
                            .build()
                    }
                )
                .addAllPkgSets(pkgSets.map {
                    it.label
                })
                .setOp(op.toConditionOperator())
                .build()
        }

        is Condition.CurrentForegroundAppByPkg -> {
            CurrentPkgListByPkg.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .addAllPkgAndUsers(stringPairs)
                .setOp(op.toConditionOperator())
                .build()
        }

        is Condition.AppHasNotification -> {
            AppHasNotification.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .addAllPkgs(
                    apps.map { app ->
                        AppPkg.newBuilder()
                            .setPkgName(app.pkg.pkgName)
                            .setUserId(app.pkg.userId)
                            .build()
                    }
                )
                .addAllPkgSets(pkgSets.map {
                    it.label
                })
                .setOp(op.toConditionOperator())
                .build()
        }

        is Condition.AppHasTask -> {
            AppHasTask.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .addAllPkgs(
                    apps.map { app ->
                        AppPkg.newBuilder()
                            .setPkgName(app.pkg.pkgName)
                            .setUserId(app.pkg.userId)
                            .build()
                    }
                )
                .addAllPkgSets(pkgSets.map {
                    it.label
                })
                .setOp(op.toConditionOperator())
                .build()
        }

        is Condition.AppIsRunning -> {
            AppIsRunning.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .addAllPkgs(
                    apps.map { app ->
                        AppPkg.newBuilder()
                            .setPkgName(app.pkg.pkgName)
                            .setUserId(app.pkg.userId)
                            .build()
                    }
                )
                .addAllPkgSets(pkgSets.map {
                    it.label
                })
                .setOp(op.toConditionOperator())
                .build()
        }

        is Condition.ServiceIsRunning -> {
            ServiceIsRunning.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setOp(op.toConditionOperator())
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .addAllServices(services)
                .build()
        }

        is Condition.CurrentActivity -> {
            CurrentActivity.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .addAllActivities(activities)
                .setOp(op.toConditionOperator())
                .build()
        }

        is Condition.AppIsNotRunning -> {
            AppIsNotRunning.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .addAllPkgs(
                    apps.map { app ->
                        AppPkg.newBuilder()
                            .setPkgName(app.pkg.pkgName)
                            .setUserId(app.pkg.userId)
                            .build()
                    }
                )
                .addAllPkgSets(pkgSets.map {
                    it.label
                })
                .setOp(op.toConditionOperator())
                .build()
        }

        is Condition.AppHasAudioFocus -> {
            AppHasAudioFocus.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .addAllPkgs(
                    apps.map { app ->
                        AppPkg.newBuilder()
                            .setPkgName(app.pkg.pkgName)
                            .setUserId(app.pkg.userId)
                            .build()
                    }
                )
                .addAllPkgSets(pkgSets.map {
                    it.label
                })
                .setOp(op.toConditionOperator())
                .build()
        }

        is Condition.AppHasWindowFocus -> {
            AppHasWindowFocus.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .addAllPkgs(
                    apps.map { app ->
                        AppPkg.newBuilder()
                            .setPkgName(app.pkg.pkgName)
                            .setUserId(app.pkg.userId)
                            .build()
                    }
                )
                .addAllPkgSets(pkgSets.map {
                    it.label
                })
                .setOp(op.toConditionOperator())
                .build()
        }

        is Condition.MVEL -> {
            MatchMVEL.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey).setExpression(expression).build()
        }

        is Condition.JS -> {
            MatchJS.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey).setExpression(expression).build()
        }

        is Condition.TRUE -> {
            True.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Condition.EvaluateGlobalVar -> {
            EvaluateGlobalVar.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setVarName(varName)
                .setOp(op)
                .setPayload(payload)
                .build()
        }

        is Condition.EvaluateContextVar -> {
            EvaluateContextVar.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setVarName(varName)
                .setOp(op)
                .setPayload(payload)
                .build()
        }

        is Condition.EvaluateLocalVar -> {
            EvaluateLocalVar.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setVarName(varName)
                .setOp(op)
                .setPayload(payload)
                .build()
        }

        is Condition.BatteryPercent -> {
            BatteryPercent.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setValue(value)
                .setOp(op)
                .build()
        }

        is Condition.ConnectedWifiSignal -> {
            ConnectedWifiSignal.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setLevel(level)
                .setOp(op)
                .build()
        }

        is Condition.ChargeState -> {
            ChargeState.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setRequireIsCharge(requireIsCharge)
                .build()
        }

        is Condition.PlugState -> {
            PlugState.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setType(type)
                .build()
        }

        is Condition.ScreenIsOn -> {
            ScreenIsOn.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Condition.VPNIsConnected -> {
            VPNIsConnected.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Condition.TimeInRange -> {
            TimeInRange.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setRange(range)
                .build()
        }

        is Condition.RequireWifiConnected -> {
            RequireWifiConnected.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setRequiredSSID(requiredSSID)
                .build()
        }

        is Condition.RequireWifiDisconnected -> {
            RequireWifiDisconnected.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Condition.RequireBTConnected -> {
            RequireBTConnected.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setDevice(requiredDevice)
                .build()
        }

        is Condition.RequireBTDisconnected -> {
            RequireBTDisconnected.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Condition.RequireBTDeviceFound -> {
            RequireBTDeviceFound.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setDevice(device)
                .setAddress(address)
                .setTimeout(timeout)
                .build()
        }

        is Condition.RequireMobileDataEnabled -> {
            RequireMobileDataEnabled.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setSlotId(slot)
                .build()
        }

        is Condition.KeyguardIsLocked -> {
            KeyguardIsLocked.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Condition.ScreenOrientationIsPort -> {
            ScreenOrientationIsPort.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Condition.IsInCall -> {
            IsInCall.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Condition.IsRinging -> {
            IsRinging.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Condition.HasNodeOnScreen -> {
            HasNodeOnScreen.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setPackageName(packageName)
                .setComponentName(componentName)
                .setDetectTimeout(detectTimeout)
                .addAllMatchers(matchers.mapNotNull {
                    when (it) {
                        is NodeMatcher.Text -> {
                            NodeMatcherText
                                .newBuilder()
                                .setText(it.text)
                                .setIsRegex(it.isRegex)
                                .setRegexOptions(it.regexOptions)
                                .build()
                                .pack_()
                        }

                        is NodeMatcher.ViewId -> {
                            NodeMatcherViewId.newBuilder().setId(it.viewId).build().pack_()
                        }

                        else -> null
                    }
                })
                .build()
        }

        is Condition.IsRuleEnabled -> {
            IsRuleEnabled.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setRuleId(ruleId)
                .setIsEnabled(isEnabled)
                .build()
        }

        is Condition.IsHeadsetPlug -> {
            IsHeadsetPlug.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setIsPlug(isPlug)
                .build()
        }

        is Condition.RequireScreenRotate -> {
            RequireScreenRotate.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setDegree(degree)
                .build()
        }

        is Condition.RequireWindowRotation -> {
            RequireWindowRotation.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setDegree(degree)
                .build()
        }

        is Condition.RequireDelay -> {
            RequireDelay.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setTimeString(timeString)
                .setUseAlarm(isAlarm)
                .build()
        }

        is Condition.RequireSensorOff -> {
            RequireSensorOff.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setIsRequireOn(isRequireOn)
                .build()
        }

        is Condition.RequireTileState -> {
            RequireTileState.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setState(state)
                .setTileSpec(spec)
                .build()
        }

        is Condition.RequireFactTag -> {
            RequireFactTag.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setTag(tag)
                .build()
        }

        is Condition.RequireAPMMode -> {
            RequireAPMMode.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setIsAPMEnable(isEnableAPM)
                .build()
        }

        is Condition.RequireIMEVisibility -> {
            RequireIMEVisibility.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setIsShown(isShown)
                .build()
        }

        is Condition.TheXXTimeToday -> {
            TheXXTimeToday.newBuilder()
                .setId(overrideId ?: id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setIsDisabled(isDisabled)
                .setCustomContextDataKey(customContextDataKey)
                .setWhat(what)
                .setScope(scope)
                .setTime(time)
                .setOp(op)
                .build()
        }
    }
}