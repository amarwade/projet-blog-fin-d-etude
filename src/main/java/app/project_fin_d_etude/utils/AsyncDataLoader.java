package app.project_fin_d_etude.utils;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utilitaire pour charger des données de façon asynchrone avec affichage de
 * loader.
 */
@Component
public class AsyncDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(AsyncDataLoader.class);
    private static final String DEFAULT_LOADING_TEXT = "Chargement en cours...";

    private final Executor taskExecutor;

    @Autowired
    public AsyncDataLoader(@Qualifier("taskExecutor") Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * Charge des données en asynchrone avec gestion de loader et d'erreur.
     *
     * @param parent Le composant où afficher le contenu ou les erreurs
     * @param dataSupplier Fournisseur de données (peut lever une exception)
     * @param onDataReady Callback pour afficher les données
     * @param onError Callback pour afficher une erreur
     * @param <T> Type des données
     */
    public <T> void loadData(
            HasComponents parent,
            Supplier<T> dataSupplier,
            Consumer<T> onDataReady,
            Consumer<String> onError
    ) {
        loadData(parent, dataSupplier, onDataReady, onError, DEFAULT_LOADING_TEXT);
    }

    /**
     * Variante de loadData avec texte de chargement personnalisé.
     */
    public <T> void loadData(
            HasComponents parent,
            Supplier<T> dataSupplier,
            Consumer<T> onDataReady,
            Consumer<String> onError,
            String loadingText
    ) {
        UI currentUI = UI.getCurrent();
        if (currentUI == null) {
            logger.error("Impossible de charger les données : contexte UI introuvable.");
            onError.accept("Erreur interne : impossible d'accéder à l'interface utilisateur.");
            return;
        }

        // Affiche un loader temporaire
        Div loaderDiv = VaadinUtils.createLoadingOverlay(loadingText);
        parent.removeAll();
        parent.add(loaderDiv);

        // Lance la tâche en arrière-plan
        taskExecutor.execute(() -> {
            try {
                T data = dataSupplier.get();
                currentUI.access(() -> {
                    parent.removeAll();
                    onDataReady.accept(data);
                });
            } catch (Exception e) {
                logger.error("Erreur lors du chargement asynchrone : {}", e.getMessage(), e);
                currentUI.access(() -> {
                    parent.removeAll();
                    onError.accept("Erreur lors du chargement des données : " + e.getMessage());
                });
            }
        });
    }
}
