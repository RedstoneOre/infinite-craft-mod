package com.infinite_craft.process;

import net.minecraft.server.network.ServerPlayerEntity;

public class LoadingState {
	public ServerPlayerEntity player;
	public double progressStart;
	public double progressCompleteRate;
	public double progressTarget;
	public LoadingState(ServerPlayerEntity _player, double _progressStart, double _progressCompleteRate, double _progressTarget){
		player=_player;
		progressStart=_progressStart;
		progressCompleteRate=_progressCompleteRate;
		progressTarget=_progressTarget;
	}
	public void newLoadingProcess(int exceptedTicks){
		player.getEntityWorld().getServer().executeSync(()->{
			InfiniteCraftFakeProgressTask.interrupt(player);
			double progressEnd = progressStart + (progressTarget-progressStart)*progressCompleteRate;
			InfiniteCraftFakeProgressTask.start(player, progressStart, progressEnd, exceptedTicks);
			progressStart=progressEnd;
		});
	}
	public void newLoadingProcessCustomEnd(int exceptedTicks, double progressEnd){
		player.getEntityWorld().getServer().executeSync(()->{
			InfiniteCraftFakeProgressTask.interrupt(player);
			InfiniteCraftFakeProgressTask.start(player, progressStart, progressEnd, exceptedTicks);
			progressStart=progressEnd;
		});
	}
	public void complete(int exceptedTicks){
		player.getEntityWorld().getServer().executeSync(()->{
			InfiniteCraftFakeProgressTask.interrupt(player);
			InfiniteCraftFakeProgressTask.start(player, progressStart, progressTarget, exceptedTicks);
		});
	}
}