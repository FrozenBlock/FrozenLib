package net.frozenblock.lib.datafix.impl;

import com.mojang.datafixers.DataFixer;

public interface DataFixerHoldingGameInstance {
	default DataFixer frozenLib$getDataFixer() {
		throw new AssertionError();
	}
}
