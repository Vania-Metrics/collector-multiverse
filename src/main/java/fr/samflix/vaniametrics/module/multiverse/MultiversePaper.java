package fr.samflix.vaniametrics.module.multiverse;

import org.bukkit.plugin.java.JavaPlugin;

import fr.samflix.vaniametrics.api.Collector;
import fr.samflix.vaniametrics.api.VaniaMetrics;
import fr.samflix.vaniametrics.api.VaniaMetricsProvider;

/**
 * Multiverse-Core — the inventory of worlds, loaded or not.
 *
 * <p>Bukkit.getWorlds() only returns LOADED worlds: an unloaded world, as far as Bukkit is
 * concerned, doesn't exist. Multiverse keeps the full registry, and that difference is what
 * matters here.
 *
 * <p>Its plugin.yml declares {@code depend: [VaniaMetrics, Multiverse-Core]}: both are
 * required, and declaring it lets Bukkit guarantee load order instead of hoping for it.
 * Removing this jar removes this integration and nothing else — that's the whole point of one
 * jar per integration.
 */
public final class MultiversePaper extends JavaPlugin {

	private Collector collector;

	@Override
	public void onEnable() {
		VaniaMetrics metrics = VaniaMetricsProvider.get();
		collector = new MultiverseCollector();
		metrics.register(collector);
	}

	@Override
	public void onDisable() {
		if (collector != null) {
			VaniaMetricsProvider.find().ifPresent(m -> m.unregister(collector));
		}
	}
}
