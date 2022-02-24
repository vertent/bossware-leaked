package bossware.features.gui.components.items;

import bossware.BossWare;
import bossware.util.RenderUtil;

public class DescriptionDisplay extends Item {
    private String description;
    private boolean draw;

    public DescriptionDisplay(String description, float x, float y) {
        super("DescriptionDisplay");

        this.description = description;

        this.setLocation(x, y);
        this.width = BossWare.textManager.getStringWidth(this.description) + 4;
        this.height = BossWare.textManager.getFontHeight() + 4;

        this.draw = false;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.width = BossWare.textManager.getStringWidth(this.description) + 4;
        this.height = BossWare.textManager.getFontHeight() + 4;

        RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0xd6000000);
        BossWare.textManager.drawString(this.description, this.x + 2, this.y + 2, 0xffffff, true);
    }

    public boolean shouldDraw() {
        return this.draw;
    }
    public String getDescription() { return this.description; }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }
    public void setDescription(String description) { this.description = description; }
}
