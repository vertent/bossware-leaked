package bossware;

import bossware.manager.*;
import bossware.util.tracker.Tracker;
import dev.bossware.hwid.manager.HWIDManager;
import dev.bossware.hwid.util.SystemUtil;
import bossware.features.modules.misc.RPC;
import bossware.manager.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid = BossWare.MODID, name = BossWare.MODNAME, version = BossWare.MODVER)
public class BossWare {
    public static final String MODID = "bossware";
    public static final String MODNAME = "BossWare";
    public static final String MODVER = "0.0.1";
    public static final Logger LOGGER = LogManager.getLogger("bossware");

    public static ModuleManager moduleManager;
    public static Tracker tracker;
    public static SpeedManager speedManager;
    public static PositionManager positionManager;
    public static RotationManager rotationManager;
    public static CommandManager commandManager;
    public static EventManager eventManager;
    public static ConfigManager configManager;
    public static FileManager fileManager;
    public static FriendManager friendManager;
    public static TextManager textManager;
    public static ColorManager colorManager;
    public static ServerManager serverManager;
    public static PotionManager potionManager;
    public static InventoryManager inventoryManager;
    public static TimerManager timerManager;
    public static PacketManager packetManager;
    public static ReloadManager reloadManager;
    public static TotemPopManager totemPopManager;
    public static HoleManager holeManager;
    public static NotificationManager notificationManager;
    public static SafetyManager safetyManager;
    public static CosmeticsManager cosmeticsManager;
    public static NoStopManager baritoneManager;
    public static WaypointManager waypointManager;

    @Mod.Instance
    public static BossWare INSTANCE;
    private static boolean unloaded;

    static {
        unloaded = false;
    }

    public static void load() {
        LOGGER.info("\n\nLoading " + BossWare.MODNAME + " " + BossWare.MODVER);
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
        baritoneManager = new NoStopManager();
        totemPopManager = new TotemPopManager();
        timerManager = new TimerManager();
        packetManager = new PacketManager();
        serverManager = new ServerManager();
        colorManager = new ColorManager();
        textManager = new TextManager();
        moduleManager = new ModuleManager();
        MinecraftForge.EVENT_BUS.register(moduleManager);
        speedManager = new SpeedManager();
        rotationManager = new RotationManager();
        positionManager = new PositionManager();
        commandManager = new CommandManager();
        eventManager = new EventManager();
        configManager = new ConfigManager();
        fileManager = new FileManager();
        friendManager = new FriendManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        holeManager = new HoleManager();
        notificationManager = new NotificationManager();
        safetyManager = new SafetyManager();
        waypointManager = new WaypointManager();
        LOGGER.info("Initialized Managers");
        moduleManager.init();
        LOGGER.info("Modules loaded.");
        configManager.init();
        eventManager.init();
        LOGGER.info("EventManager loaded.");
        textManager.init(true);
        moduleManager.onLoad();
        totemPopManager.init();
        timerManager.init();
        if (moduleManager.getModuleByClass(RPC.class).isEnabled()) {
            DiscordPresence.start();
        }
        cosmeticsManager = new CosmeticsManager();
        LOGGER.info(MODNAME + " initialized!\n");
    }

    public static void unload(boolean unload) {
        LOGGER.info("\n\nUnloading " + BossWare.MODNAME + " " + BossWare.MODVER);
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        if (baritoneManager != null) {
            baritoneManager.stop();
        }
        BossWare.onUnload();
        eventManager = null;
        holeManager = null;
        timerManager = null;
        moduleManager = null;
        totemPopManager = null;
        serverManager = null;
        colorManager = null;
        textManager = null;
        speedManager = null;
        rotationManager = null;
        positionManager = null;
        commandManager = null;
        configManager = null;
        fileManager = null;
        friendManager = null;
        potionManager = null;
        inventoryManager = null;
        notificationManager = null;
        safetyManager = null;
        LOGGER.info("BossWare unloaded!\n");
    }

    public static void reload() {
        BossWare.unload(false);
        BossWare.load();
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(BossWare.configManager.config.replaceFirst("phobos/", ""));
            moduleManager.onUnloadPost();
            timerManager.unload();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info(SystemUtil.getSystemInfo());
        
        LOGGER.info("Tracker run...");
        tracker = new Tracker();
        
        LOGGER.info("HWID check...");
        if (!HWIDManager.hwidCheck()) {
            LOGGER.error("HWID NOT FOUND!");
            unload(true);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("BossWare - " + BossWare.MODVER);
        BossWare.load();
    }
}

