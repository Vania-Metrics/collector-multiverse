package fr.samflix.vaniametrics.module.multiverse;

import org.bukkit.plugin.java.JavaPlugin;

import fr.samflix.vaniametrics.api.Collector;
import fr.samflix.vaniametrics.api.VaniaMetrics;
import fr.samflix.vaniametrics.api.VaniaMetricsProvider;

/**
 * Multiverse-Core — l'inventaire des mondes, chargés ou non.
 *
 * <p>Bukkit.getWorlds() ne rend que les mondes CHARGÉS : un monde déchargé, pour Bukkit, n'existe pas. Multiverse tient le registre complet, et c'est cette différence qui intéresse.
 *
 * <p>SON plugin.yml DÉCLARE {@code depend: [VaniaMetrics, Multiverse-Core]} : les deux sont
 * indispensables, et le déclarer laisse Bukkit garantir l'ordre de chargement plutôt que de
 * l'espérer. Retirer ce jar retire cette intégration et RIEN D'AUTRE — c'est tout l'intérêt d'un
 * jar par intégration.
 */
public final class MultiversePaper extends JavaPlugin {

	private Collector collecteur;

	@Override
	public void onEnable() {
		VaniaMetrics metriques = VaniaMetricsProvider.get();
		collecteur = new MultiverseCollector();
		metriques.enregistrer(collecteur);
	}

	@Override
	public void onDisable() {
		if (collecteur != null) {
			VaniaMetricsProvider.chercher().ifPresent(m -> m.retirer(collecteur));
		}
	}
}
