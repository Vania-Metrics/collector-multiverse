package fr.samflix.vaniametrics.module.multiverse;

import java.util.Locale;

import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import fr.samflix.vaniametrics.api.Collector;
import fr.samflix.vaniametrics.api.Gauge;
import fr.samflix.vaniametrics.api.MetricRegistry;

/** The Multiverse collector. */
public final class MultiverseCollector implements Collector {

	private Gauge worlds;
	private Gauge loaded;
	private Gauge info;

	@Override
	public String name() {
		return "multiverse";
	}

	@Override
	public String source() {
		return "Multiverse-Core";
	}

	@Override
	public boolean needsMainThread() {
		return true;
	}

	@Override
	public void declare(MetricRegistry r) {
		worlds = r.gauge("multiverse_worlds",
				"Worlds declared in Multiverse. state = loaded|unloaded.", "state");
		loaded = r.gauge("multiverse_world_loaded",
				"1 if the world is loaded, 0 if it is declared but absent from memory. "
						+ "A world thought to be loaded that isn't is a silent outage.",
				"world");
		info = r.gauge("multiverse_world_info",
				"Always 1. The world's characteristics are in the labels.",
				"world", "alias", "environment", "difficulty");
	}

	@Override
	public void collect(MetricRegistry r) {
		if (!MultiverseCoreApi.isLoaded()) {
			return;
		}
		WorldManager worldManager = MultiverseCoreApi.get().getWorldManager();

		// Worlds are created and deleted in-game: without resetting, a deleted world
		// would stay published forever.
		loaded.clear();
		info.clear();

		int loadedCount = 0;
		int unloadedCount = 0;
		for (MultiverseWorld world : worldManager.getWorlds()) {
			boolean present = world.isLoaded();
			if (present) {
				loadedCount++;
			} else {
				unloadedCount++;
			}
			loaded.set(present ? 1 : 0, world.getName());
			info.set(1, world.getName(), text(world.getAlias()),
					enumeration(world.getEnvironment()), enumeration(world.getDifficulty()));
		}
		worlds.set(loadedCount, "loaded");
		worlds.set(unloadedCount, "unloaded");
	}

	private static String text(String v) {
		return v == null || v.isBlank() ? "" : v;
	}

	private static String enumeration(Enum<?> v) {
		return v == null ? "unknown" : v.name().toLowerCase(Locale.ROOT);
	}
}
