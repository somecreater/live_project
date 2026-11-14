$fnmInit = 'fnm env --use-on-cd --shell powershell | Out-String | Invoke-Expression'

if (!(Test-Path $PROFILE)) {
    New-Item -ItemType File -Path $PROFILE -Force | Out-Null
}

if (!(Select-String -Path $PROFILE -SimpleMatch $fnmInit -Quiet)) {
    Add-Content -Path $PROFILE -Value "`r`n$fnmInit"
}