package bossware.features.modules.hud;

import bossware.features.setting.Bind;
import bossware.features.setting.Setting;
import bossware.features.modules.Module;

public class HudModule extends Module  {
    protected final Setting<Integer> posX = this.register(new Setting<Integer>("Position X", 0, 0, 1000, v -> isPositionCustom()));
    protected final Setting<Integer> posY = this.register(new Setting<Integer>("Position Y", 0, 0, 1000, v -> isPositionCustom()));

    public HudModule(String name, String description, boolean hasListener, boolean hidden, boolean alwaysListening) {
        super(name, description, Category.HUD, hasListener, hidden, alwaysListening);

        this.unregister(this.bind);
        this.unregister(this.drawn);
        this.unregister(this.displayName);
    }

    protected boolean isPositionCustom() { return true; }

    public int getPosX() { return posX.getValue(); }
    public int getPosY() { return posY.getValue(); }

    @Override
    public boolean isDrawn() {
        return false;
    }

    @Override
    public void setDrawn(boolean drawn) { }

    @Override
    public Bind getBind() {
        return new Bind(-1);
    }

    @Override
    public void setBind(int key) { }
}
