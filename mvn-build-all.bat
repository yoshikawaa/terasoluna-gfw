@echo off

setlocal enabledelayedexpansion

set ARTIFACT_ID_PREFIX=terasoluna-gfw-
set TARGETS=parent common jodatime web security-core security-web jpa mybatis3 mybatis2 recommended-dependencies recommended-web-dependencies
set DEFAULT_GOALS=clean install

set commandArgs=%DEFAULT_GOALS%

if not "%*" == "" (
    set commandArgs=%*
)

echo [INFO] Start a build.

echo [DEBUG] Command arguments : "%commandArgs%"

for %%i in (%TARGETS%) do (
    set pomFile=%ARTIFACT_ID_PREFIX%%%i\pom.xml
    if exist !pomFile! (
        call mvn -U -f !pomFile! %commandArgs%
        if not !ERRORLEVEL! == 0 (
            echo [ERROR] Failed a build.
            exit /B !ERRORLEVEL!
        )
    )
)

echo [INFO] Finish a build.