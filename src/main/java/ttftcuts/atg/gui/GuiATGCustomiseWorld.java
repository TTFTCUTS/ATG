package ttftcuts.atg.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiATGCustomiseWorld extends GuiScreen {
	
	private GuiCreateWorld parent;
	
	private GuiButton doneButton;
	private GuiButton defaultButton;
	
	public GuiATGCustomiseWorld(GuiCreateWorld parent, String settings) {
		this.parent = parent;
	}
	
	@Override
	public void initGui() {
		this.buttonList.clear();
		
		this.doneButton = new GuiButton(0, this.width / 2 + 98, this.height - 27, 90, 20, I18n.format("gui.done"));
		this.buttonList.add(this.doneButton);
		
		this.defaultButton = new GuiButton(0, this.width / 2 - 187, this.height - 27, 90, 20, I18n.format("createWorld.customize.custom.defaults"));
		this.buttonList.add(this.defaultButton);
	}
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
	
	@Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (!button.enabled) { return; }
        
        if (button == this.doneButton) {
        	this.parent.chunkProviderSettingsJson = "";
        	this.mc.displayGuiScreen(this.parent);
        }
    }
}
