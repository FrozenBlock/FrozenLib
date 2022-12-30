package net.frozenblock.lib.config.frozenlib_config.getter;

public class FrozenLibConfigValues {
	public static FrozenConfigGetter CONFIG = new FrozenConfigGetter(new ConfigInterface() {
		@Override
		public boolean useWindOnNonFrozenServers() {
			return DefaultFrozenLibConfigValues.USE_WIND_ON_NON_FROZENLIB_SERVERS;
		}
	});



	public static class FrozenConfigGetter {
		public final ConfigInterface getter;

		public FrozenConfigGetter(ConfigInterface getter) {
			this.getter = getter;
		}
	}

	public static class DefaultFrozenLibConfigValues {
		public static final boolean USE_WIND_ON_NON_FROZENLIB_SERVERS = false;
	}

	public interface ConfigInterface {
		boolean useWindOnNonFrozenServers();
	}
}
