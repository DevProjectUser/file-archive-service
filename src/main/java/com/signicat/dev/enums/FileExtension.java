package com.signicat.dev.enums;

import lombok.Getter;

@Getter
public enum FileExtension {
    EXE(".exe"),
    BAT(".bat"),
    CMD(".cmd"),
    SH(".sh"),
    COM(".com"),
    VBS(".vbs"),
    VBE(".vbe"),
    JS(".js"),
    JSE(".jse"),
    JAR(".jar"),
    WS(".ws"),
    WSF(".wsf"),
    SCR(".scr"),
    MSI(".msi"),
    REG(".reg"),
    DLL(".dll"),
    SYS(".sys"),
    HTA(".hta"),
    PIF(".pif"),
    SCT(".sct"),
    CSH(".csh"),
    PS1(".ps1"),
    PS1XML(".ps1xml"),
    PS2(".ps2"),
    PS2XML(".ps2xml"),
    PSC1(".psc1"),
    PSC2(".psc2"),
    MSH(".msh"),
    MSH1(".msh1"),
    MSH2(".msh2"),
    MSHXML(".mshxml");

    private final String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }

    public static boolean isUnsafeFileExtension(String fileName) {
        for (FileExtension fileExtension : FileExtension.values()) {
            if (fileName.endsWith(fileExtension.getExtension())) return true;
        }
        return false;
    }
}
