#!/usr/bin/env powershell

# 脚本功能：批量发布所有 external 模块到 Maven Local 仓库

Write-Host "开始发布 external 模块到 Maven Local 仓库..." -ForegroundColor Green

# 获取所有 external 模块的路径
$externalModules = @(
    "codeditor",
    "compose-color-picker",
    "dateformatter",
    "dex-maker",
    "drawbox",
    "icons",
    "picasso",
    "reorderable",
    "rhino-android",
    "sardine-android",
    "sec_net",
    "utils"
)

# 发布每个模块
foreach ($module in $externalModules) {
    $modulePath = ":external:$module"
    Write-Host "正在发布模块: $module" -ForegroundColor Yellow
    
    # 执行发布命令
    ./gradlew "$modulePath:publishToMavenLocal"
    
    # 检查命令执行结果
    if ($LASTEXITCODE -eq 0) {
        Write-Host "模块 $module 发布成功！" -ForegroundColor Green
    } else {
        Write-Host "模块 $module 发布失败！" -ForegroundColor Red
    }
    
    Write-Host "----------------------------------------" -ForegroundColor Gray
}

Write-Host "所有 external 模块发布完成！" -ForegroundColor Green