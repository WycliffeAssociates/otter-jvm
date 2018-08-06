; -- Setup.iss --
; Inno Setup script for creating a Windows Installer for Translation Recorder
; This script also generates an uninstall script and a Desktop Shortcut

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!
; ------------- www.jrsoftware.org/ishelp ------------------------

;define version number as a variable
;version number can be set in the command line during compile time
;using 'iscc /DversionNumber=someNumber'
#ifndef versionNumber
    #define versionNumber "0.0"
#endif

#ifndef outputDir
    #define outputDir
#endif

#ifndef srcDir
    #define srcDir
#endif
[Setup]
AppName=Translation Recorder
AppVersion={#versionNumber}
DefaultDirName={pf}\Translation Recorder
OutputBaseFileName=tR-installer-win-{#versionNumber}
DisableProgramGroupPage=yes
Compression=lzma2
SolidCompression=yes
SourceDir={#srcDir}
OutputDir={#outputDir}
UninstallDisplayIcon={app}\translationRecorder.exe
Uninstallable=yes
UninstallFilesDir="{app}"
UninstallDisplayName=Translation Recorder

[Files]
Source: "translationRecorder.exe"; DestDir: "{app}"
;Source: "MyProg.chm"; DestDir: "{app}"
;Source: "Readme.txt"; DestDir: "{app}"; Flags: isreadme

[Icons]
Name: "{commonprograms}\Translation Recorder"; Filename: "{app}\translationRecorder.exe"
Name: "{commondesktop}\Translation Recorder"; Filename: "{app}\translationRecorder.exe"
