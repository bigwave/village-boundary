package org.bigwave.villageboundary;


import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockRedFlower;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.village.Village;
import net.minecraft.world.World;import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class WorldTickEventHandler
{
	static int angle = 0;
	static int offsetDistance = 0;
	static int lastCheckedX = 0;
	static int lastCheckedY = 0;
	static int lastCheckedZ = 0;
	 
	@SubscribeEvent
	public void onWorldTickEvent(WorldTickEvent event)
	{
		World world = event.world;
		float nextRand = world.rand.nextFloat();
		boolean developmentEnvironment = System.getProperty("xxxx") != null;
		if (!developmentEnvironment && nextRand > 0.01F)
		{
			return;
		}
				
		if (!world.isRemote)
		{
			List players = world.playerEntities;

			for (int i = 0; i < players.size(); i++) {
				EntityPlayer player = (EntityPlayer) players.get(i);
				
				BlockPos playerPosition = player.getPosition();
				
				Village nearestVillage = world.getVillageCollection().getNearestVillage(playerPosition, 1024); //1024 = "radius" 
				if (nearestVillage == null)
				{
					return;
				}
				
				BlockPos villageCenter = nearestVillage.getCenter();
				int villageRadius = nearestVillage.getVillageRadius();
				if (world.isAreaLoaded(villageCenter, playerPosition))
				{
					// calculate point on village boundary
					int x = (int)(villageCenter.getX() + (villageRadius + offsetDistance) * Math.cos(Math.toRadians(angle)));
					int y = villageCenter.getY();
				    int z = (int)(villageCenter.getZ() + (villageRadius + offsetDistance) * Math.sin(Math.toRadians(angle)));
					
				    if (x == lastCheckedX && y == lastCheckedY && z == lastCheckedZ)
				    {						
						incrementAngleAndOffset();
						return;
				    }
				    	
					lastCheckedX = x;
					lastCheckedY = y;
					lastCheckedZ = z;

				    BlockPos blockToCheck = new BlockPos(x, y, z);

				    int groundLevel;
				    if (world.isAirBlock(blockToCheck))
				    {
				    	groundLevel = goDownToGroundLevel(world, blockToCheck);
				    }
				    else
				    {
				    	groundLevel = goUpToGroundLevel(world, blockToCheck);
				    }
				    
				    BlockPos groundLevelBlockPos = new BlockPos(x, groundLevel, z);
				    Block groundLevelBlock = world.getBlockState(groundLevelBlockPos).getBlock();
				    String groundLevelBlockName = world.getBlockState(groundLevelBlockPos).getBlock().getUnlocalizedName();
				    
				    String message = "Do nothing                     ";
					if (offsetDistance > -4 && offsetDistance < 4)
					{
						if (groundLevelBlock == Blocks.grass)
						{
							if (world.rand.nextFloat() > 0.5F)
							{
								world.setBlockState(groundLevelBlockPos.up(), Blocks.red_flower.getDefaultState());
								message = "Plant flower on boundary       ";
							}
						}
						
						if (groundLevelBlock == Blocks.red_flower)
						{
							if (world.rand.nextFloat() > 0.5F)
							{
								world.setBlockToAir(groundLevelBlockPos);
								message = "Remove flower on boundary      ";
							}
						}
					}
					else if (groundLevelBlock == Blocks.red_flower)
					{
						world.setBlockToAir(groundLevelBlockPos);
						message = "Remove flower outside boundary ";
					}

					if (developmentEnvironment)
					{
						System.out.println(
								" Angle " + angle +
								" Offset " + offsetDistance +
								" xz " + x + " " + z +
								" " + message +
								" on " + groundLevelBlockName);
					}
					
					incrementAngleAndOffset();
				}
			}
		}
	}

	private void incrementAngleAndOffset() {
		angle++;
		if (angle >360)
		{
			angle = 0;
			
			offsetDistance++;
			
			if (offsetDistance > 15)
			{
				offsetDistance = -15;
			}
		}
	}

	private int goUpToGroundLevel(World world, BlockPos blockToCheck) {
		BlockPos groundLevelBlockPos;
		for (groundLevelBlockPos = new BlockPos(blockToCheck.getX(), blockToCheck.getY(), blockToCheck.getZ());
				!world.isAirBlock(groundLevelBlockPos);
				groundLevelBlockPos = groundLevelBlockPos.up())
		{
			;
		}
		return groundLevelBlockPos.down().getY();
	}

private int goDownToGroundLevel(World world, BlockPos blockToCheck) {
	BlockPos groundLevelBlockPos;
	for (groundLevelBlockPos = new BlockPos(blockToCheck.getX(), blockToCheck.getY(), blockToCheck.getZ());
			world.isAirBlock(groundLevelBlockPos);
			groundLevelBlockPos = groundLevelBlockPos.down())
	{
		;
	}
	return groundLevelBlockPos.getY();
}
}
