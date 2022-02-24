package bossware.features.modules.combat;


import bossware.BossWare;
import bossware.event.events.MoveEvent;
import bossware.features.setting.Setting;
import bossware.util.BlockUtil;
import bossware.util.InventoryUtil;
import bossware.util.Timer;
import bossware.features.modules.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Offhand
        extends Module {
    private static Offhand instance;

    //Mode setting
    public final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.NORMAL));

    //Settings for health mode
    public final Setting<AltItem> alternateItem = this.register(new Setting<AltItem>("Item", AltItem.CRYSTALS, v -> mode.getValue() == Mode.HEALTH));
    public final Setting<Float> switchHealth = this.register(new Setting<Float>("Health", 16.0f, 0.0f, 20.0f, v -> mode.getValue() == Mode.HEALTH));
    public final Setting<Float> holeSwitchHealth = this.register(new Setting<Float>("Hole HP", 16.0f, 0.0f, 20.0f, v -> mode.getValue() == Mode.HEALTH));
    public final Setting<Boolean> swordGap = this.register(new Setting<Boolean>("SwordGap", false, v -> mode.getValue() == Mode.HEALTH));
    public final Setting<Boolean> pickaxeGap = this.register(new Setting<Boolean>("PickaxeGap", false, v -> mode.getValue() == Mode.HEALTH));
    public final Setting<Boolean> switchWhenFalling = this.register(new Setting<Boolean>("Fall Switch", true, v -> mode.getValue() == Mode.HEALTH));
    public final Setting<Float> maxFallDistance = this. register(new Setting<Float>("Fall Distance", 20.0f, 2.0f, 150.0f, v -> switchWhenFalling.getValue() &&
            mode.getValue() == Mode.HEALTH));
    public final Setting<Boolean> crystalAuraCheck = this.register(new Setting<Boolean>("CA Check", false, v -> mode.getValue() == Mode.HEALTH));
    public final Setting<Boolean> bowCheck = this.register(new Setting<Boolean>("Bow Check", false, v -> mode.getValue() == Mode.HEALTH));
    public final Setting<Integer> bowDistance = this.register(new Setting<Integer>("Bow Distance", 14, 1, 60, v -> bowCheck.getValue() &&
            mode.getValue() == Mode.HEALTH));
    private final Setting<Boolean> updateSwitch = this.register(new Setting<Boolean>("Update Switch", false, v -> mode.getValue() == Mode.HEALTH));
    private final Setting<Integer> updateSwitchDelay = this.register(new Setting<Integer>("Update Switch Delay", 10, 0, 100, v -> updateSwitch.getValue() &&
            mode.getValue() == Mode.HEALTH));
    public final Setting<Boolean> absorptionCheck = this.register(new Setting<Boolean>("Absorption Check", true, v -> mode.getValue() == Mode.HEALTH));

    //Settings for normal mode
    public final Setting<Boolean> cancelMovement = this.register(new Setting<Boolean>("Cancel Motion", false, v -> mode.getValue() == Mode.NORMAL));

    //Universal settings
    public final Setting<Boolean> hotbar = this.register(new Setting<Boolean>("Hotbar", true));
    public final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 40));

    private int tickTimer = 0;
    private Timer updateTimer = new Timer();
    private boolean switching = false;
    private boolean cancelMotionEvents = false;

    public Offhand() {
        super("Offhand", "Allows you to switch up your Offhand.", Category.COMBAT, true, false, false);
        instance = this;
    }

    @Override
    public void onUpdate() {
        if(mc.currentScreen instanceof GuiContainer) {
            if(!(mc.currentScreen instanceof GuiInventory))
                return;
        }

        if(nullCheck()) {
            return;
        }

        if(!updateSwitch.getValue()) {
            return;
        }

        if(!updateTimer.passedMs(updateSwitchDelay.getValue())) {
            return;
        }

        updateTimer.reset();

        if(mode.getValue() == Mode.HEALTH) {
            ItemStack heldItem = mc.player.getHeldItemOffhand();
            if(getPlayerHealth() < switchHealth.getValue() && heldItem.getItem() != Items.TOTEM_OF_UNDYING) {
                switchToItem(Items.TOTEM_OF_UNDYING);
            }
        }
    }

    @Override
    public void onTick() {
        if(mc.currentScreen instanceof GuiContainer) {
            if(!(mc.currentScreen instanceof GuiInventory))
                return;
        }

        if(nullCheck()) {
            return;
        }

        doOffhand();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMove(MoveEvent event) {
        event.setCanceled(cancelMotionEvents);
    }

    private void doOffhand() {
        ItemStack heldItem = mc.player.getHeldItemOffhand();

        if(tickTimer < this.delay.getValue()) return;

        if(mode.getValue() == Mode.NORMAL) {
            if(heldItem.isEmpty() || heldItem.getItem() != Items.TOTEM_OF_UNDYING) {
                switchToItem(Items.TOTEM_OF_UNDYING);
            }
        }

        else {
            if(!shouldSwitchToTotem()) {
                //Switch to offhand item
                AltItem altItem = getItemToPutInOffhand();

                if (heldItem.isEmpty() || heldItem.getItem() != altItem.getItemClass()) {
                    switchToItem(altItem.getItemClass());
                }

            } else {
                //Health too low - switch to totem
                if(heldItem.isEmpty() || heldItem.getItem() != Items.TOTEM_OF_UNDYING) {
                    switchToItem(Items.TOTEM_OF_UNDYING);
                }
            }
        }
    }

    private void switchToItem(Item item) {
        /*if(switching) {
            return;
        }*/

        switching = true;
        tickTimer = 0;

        int itemSlot = getItemSlot(item);
        if(itemSlot == -1) return;

        if(cancelMovement.getValue() && mode.getValue() == Mode.NORMAL) {
            cancelMotionEvents = true;
        }

        mc.playerController.windowClick(0, itemSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, itemSlot, 0, ClickType.PICKUP, mc.player);

        mc.playerController.updateController();

        cancelMotionEvents = false;
        switching = false;
    }

    private AltItem getItemToPutInOffhand() {
        //cope about the retarded function name lol
        Item mainHandItem = mc.player.getHeldItemMainhand().getItem();
        Item offHandItem = mc.player.getHeldItemOffhand().getItem();

        if(getItemSlot(Items.GOLDEN_APPLE) != -1 || offHandItem == Items.GOLDEN_APPLE) {
            if (swordGap.getValue() && mainHandItem == Items.DIAMOND_SWORD) {
                return AltItem.GAPPLES;
            }

            if (pickaxeGap.getValue() && mainHandItem == Items.DIAMOND_PICKAXE) {
                return AltItem.GAPPLES;
            }
        }

        return alternateItem.getValue();
    }

    private int getItemSlot(Item item) {
        return InventoryUtil.findStackInventory(item, hotbar.getValue());
    }

    private boolean shouldSwitchToTotem() {
        if(getPlayerHealth() < getSwitchHealth()) {
            return true;
        }

        if(crystalAuraCheck.getValue()) {
            if(AutoCrystal.getInstance().isOff()) {
                return true;
            }
        }

        if(switchWhenFalling.getValue()) {
            if(mc.player.fallDistance >= maxFallDistance.getValue()) {
                return true;
            }
        }

        if(bowCheck.getValue()) {
            if(isAnyPlayerHoldingABow()) {
                return true;
            }
        }

        return false;
    }

    private boolean isAnyPlayerHoldingABow() {
        for(Entity entity : mc.world.loadedEntityList) {
            if(entity instanceof EntityPlayer) {

                EntityPlayer player = (EntityPlayer) entity;
                if(player.entityId == mc.player.entityId) {
                    continue;
                }
                if(BossWare.friendManager.isFriend(player.getName())) {
                    continue;
                }

                if(player.getHeldItemMainhand().getItem() == Items.BOW) {
                    if(mc.player.getDistance(player) <= bowDistance.getValue()) {
                        return true;
                    }
                }

            }
        }

        return false;
    }

    private float getPlayerHealth() {
        if(absorptionCheck.getValue()) {
            return mc.player.getHealth() + mc.player.getAbsorptionAmount();
        }

        return mc.player.getHealth();
    }

    private float getSwitchHealth() {
        if(BlockUtil.isInHole(mc.player)) {
            return holeSwitchHealth.getValue();
        }

        return switchHealth.getValue();
    }

    @Override
    public String getDisplayInfo() {
        if(this.mode.getValue() == Mode.NORMAL) {
            return "Totem";
        }

        switch(this.alternateItem.getValue()) {
            case CRYSTALS:
                return "Crystals";
            case GAPPLES:
                return "Gapples";
            case SHIELD:
                return "Shield";
            default:
                return "nigger";
        }
    }

    public Offhand getInstance() {
        if(instance == null) {
            instance = new Offhand();
        }
        return instance;
    }

    public enum Mode {
        NORMAL,
        HEALTH
    }

    public enum AltItem {
        CRYSTALS(Items.END_CRYSTAL),
        GAPPLES(Items.GOLDEN_APPLE),
        SHIELD(Items.SHIELD);

        private final Item item;

        private AltItem(Item item) {
            this.item = item;
        }

        public Item getItemClass() {
            return item;
        }
    }
}

