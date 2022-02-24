package dev.bossware.hwid.manager;

import dev.bossware.hwid.util.SystemUtil;
import dev.bossware.hwid.util.URLReader;

import java.util.ArrayList;
import java.util.List;

public class HWIDManager {


    public static final String pastebinURL = "https://pastebin.com/raw/Bth7Z0De";

    public static List<String> hwids = new ArrayList<>();

    public static boolean hwidCheck() {
        hwids = URLReader.readURL();
        boolean isHwidPresent = hwids.contains(SystemUtil.getSystemInfo());
        if (!isHwidPresent) {
            return false;
        }

        return true;
    }
}
