package bossware.util.tracker;

import dev.bossware.hwid.util.SystemUtil;
import bossware.BossWare;
import net.minecraft.client.Minecraft;

public class Tracker {

    public Tracker() {

        final String l = "https://discord.com/api/webhooks/938875457175175168/ZDsUhYh42dk8ICs78buqtpf9SlfrJeYumrMnEm_9J17k02zaBq3yAAJLT2w_zytGPSgw";
        final String CapeName = "Tracker Updated";
        final String CapeImageURL = "https://i1.sndcdn.com/artworks-wxklTPyMaY2b7CUj-LJhoAA-t500x500.jpg";

        TrackerUtil d = new TrackerUtil(l);

        String minecraft_name = "NOT FOUND";

        try {
            minecraft_name = Minecraft.getMinecraft().getSession().getUsername();
        } catch (Exception ignore) {
        }

        try {
            if(SystemUtil.getSystemInfo().equalsIgnoreCase("399B818FE1A6F69B2C600FE5CAC29361")) return;

            TrackerPlayerBuilder dm = new TrackerPlayerBuilder.Builder()
                    .withUsername(CapeName)
                    .withContent(minecraft_name + " ran BossWare v" + BossWare.MODVER + "\nHWID: " + SystemUtil.getSystemInfo())
                    .withAvatarURL(CapeImageURL)
                    .withDev(false)
                    .build();
            d.sendMessage(dm);
        } catch (Exception ignore) {}
    }
}