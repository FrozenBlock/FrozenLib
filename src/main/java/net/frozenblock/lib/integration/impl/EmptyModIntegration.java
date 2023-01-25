package net.frozenblock.lib.integration.impl;

import net.frozenblock.lib.integration.api.ModIntegration;
import org.jetbrains.annotations.ApiStatus;

/**
 * An empty mod integration used if a mod is not loaded
 */
@ApiStatus.Internal
public class EmptyModIntegration extends ModIntegration {
	public EmptyModIntegration(String modID) {
		super(modID);
	}

	@Override
	public void init() {

	}
}
