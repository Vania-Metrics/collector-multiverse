package fr.samflix.vaniametrics.module.multiverse;

import java.util.Locale;

import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import fr.samflix.vaniametrics.api.Collector;
import fr.samflix.vaniametrics.api.Gauge;
import fr.samflix.vaniametrics.api.MetricRegistry;

/** Le relevé Multiverse. */
public final class MultiverseCollector implements Collector {

	private Gauge mondes;
	private Gauge charge;
	private Gauge info;

	@Override
	public String nom() {
		return "multiverse";
	}

	@Override
	public String origine() {
		return "Multiverse-Core";
	}

	@Override
	public boolean filPrincipal() {
		return true;
	}

	@Override
	public void declarer(MetricRegistry r) {
		mondes = r.gauge("multiverse_worlds",
				"Mondes déclarés dans Multiverse. state = loaded|unloaded.", "state");
		charge = r.gauge("multiverse_world_loaded",
				"1 si le monde est chargé, 0 s'il est déclaré mais absent de la mémoire. "
						+ "Un monde qu'on croyait chargé et qui ne l'est pas est une panne muette.",
				"world");
		info = r.gauge("multiverse_world_info",
				"Toujours 1. Les caractéristiques du monde sont dans les étiquettes.",
				"world", "alias", "environment", "difficulty");
	}

	@Override
	public void relever(MetricRegistry r) {
		if (!MultiverseCoreApi.isLoaded()) {
			return;
		}
		WorldManager gestionnaire = MultiverseCoreApi.get().getWorldManager();

		// Les mondes se créent et se suppriment en jeu : sans remise à zéro, un monde supprimé
		// resterait publié pour toujours.
		charge.clear();
		info.clear();

		int charges = 0;
		int decharges = 0;
		for (MultiverseWorld monde : gestionnaire.getWorlds()) {
			boolean present = monde.isLoaded();
			if (present) {
				charges++;
			} else {
				decharges++;
			}
			charge.set(present ? 1 : 0, monde.getName());
			info.set(1, monde.getName(), texte(monde.getAlias()),
					enumeration(monde.getEnvironment()), enumeration(monde.getDifficulty()));
		}
		mondes.set(charges, "loaded");
		mondes.set(decharges, "unloaded");
	}

	private static String texte(String v) {
		return v == null || v.isBlank() ? "" : v;
	}

	private static String enumeration(Enum<?> v) {
		return v == null ? "unknown" : v.name().toLowerCase(Locale.ROOT);
	}
}
