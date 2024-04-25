package com.sonamorningstar.eternalartifacts.util;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public enum ModConstants {
   TRANSLATE_KEY_PREFIX("key."+MODID+".tooltip");

   String string;
    ModConstants(String string) {
        this.string = string;
    }

    public String getString(){
        return string;
    }

    public String withSuffix(String suffix) {
        return string+"."+suffix;
    }
}
