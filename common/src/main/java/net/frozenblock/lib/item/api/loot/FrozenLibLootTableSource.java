package net.frozenblock.lib.item.api.loot;

public enum FrozenLibLootTableSource {
	VANILLA(true),
	MOD(true),
	DATA_PACK(false),
	REPLACED(false);

	private final boolean builtin;

	FrozenLibLootTableSource(boolean builtin) {
		this.builtin = builtin;
	}

	public boolean isBuiltin() {
		return this.builtin;
	}
}
