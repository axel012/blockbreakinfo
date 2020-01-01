package com.github.axel012.blockbreakinfo;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.ReflectionHelper;


import java.lang.reflect.Field;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("blockbreakinfo")
public class BlockBreakInfo
{
    private final Minecraft mc = Minecraft.getInstance();
    
    private float cDmg = 0;
    private String blockName;
    double timeLeft;
    boolean canHarvest = false; 
    Field curDamage;
    
    public BlockBreakInfo() {
        MinecraftForge.EVENT_BUS.register(this);
        //curBlockDamageMP - map -> field_78770_f
        curDamage = ReflectionHelper.findField(PlayerController.class, "field_78770_f");
        curDamage.setAccessible(true);
    }

    
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
    	if(mc.playerController.getIsHittingBlock() &&     	
    			event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE){
    		mc.fontRenderer.drawString("Breaking block: " + blockName, 4, 4, 0xffFFFFFF);
    		mc.fontRenderer.drawStringWithShadow("Block damage: " + String.valueOf(cDmg) + "/1", 4, 14, 0xffFFFFFF);
    		mc.fontRenderer.drawStringWithShadow("Progress: " + String.valueOf(cDmg/1) + "%", 4, 24, 0xffFFFFFF);
    		mc.fontRenderer.drawStringWithShadow("Ticks left: " + String.valueOf(timeLeft), 4, 34, 0xffFFFFFF);
    		mc.fontRenderer.drawStringWithShadow("Can be harvested: " + String.valueOf(canHarvest), 4, 44, 0xffFFFFFF);
    	}
    	
    }
    
    @SubscribeEvent
    public void onBlockBreak(PlayerInteractEvent.LeftClickBlock event) {
    	try {
    		cDmg = curDamage.getFloat(mc.playerController);
    		BlockState blockState = mc.world.getBlockState(event.getPos());
    		float breakSpeed = blockState.getPlayerRelativeBlockHardness(mc.player, mc.player.world, event.getPos());
    		blockName = blockState.getBlock().getNameTextComponent().getString();
    		timeLeft = (1 - cDmg) / breakSpeed;
    		canHarvest = blockState.canHarvestBlock(mc.world, event.getPos(), mc.player);
  		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    }
    
}
