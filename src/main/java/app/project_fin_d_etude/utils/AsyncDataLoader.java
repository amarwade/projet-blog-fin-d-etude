package app.project_fin_d_etude.utils;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

/**
 * Utilitaire pour charger des données de façon asynchrone avec affichage de
 * loader.
 */
@Component
public class AsyncDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(AsyncDataLoader.class);
    private static final String DEFAULT_LOADING_TEXT = "Chargement en cours...";
    private static final String ERROR_UI_NULL = "Erreur interne : impossible d'accéder à l'interface utilisateur.";
    private static final String ERROR_LOADING = "Erreur lors du chargement des données : ";

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
     * @param ui Le contexte UI Vaadin
     * @param <T> Type des données
     */
    public <T> void loadData(
            HasComponents parent,
            Supplier<T> dataSupplier,
            Consumer<T> onDataReady,
            Consumer<String> onError,
            UI ui
    ) {
        loadData(parent, dataSupplier, onDataReady, onError, DEFAULT_LOADING_TEXT, ui);
    }

    /**
     * Variante de loadData avec texte de chargement personnalisé.
     *
     * @param parent Le composant où afficher le contenu ou les erreurs
     * @param dataSupplier Fournisseur de données (peut lever une exception)
     * @param onDataReady Callback pour afficher les données
     * @param onError Callback pour afficher une erreur
     * @param loadingText Texte personnalisé pour le loader
     * @param ui Le contexte UI Vaadin
     * @param <T> Type des données
     */
    public <T> void loadData(
            HasComponents parent,
            Supplier<T> dataSupplier,
            Consumer<T> onDataReady,
            Consumer<String> onError,
            String loadingText,
            UI ui
    ) {
        // Validation des paramètres
        if (parent == null) {
            logger.error("Le composant parent ne peut pas être null");
            return;
        }

        if (dataSupplier == null) {
            logger.error("Le fournisseur de données ne peut pas être null");
            return;
        }

        if (onDataReady == null) {
            logger.error("Le callback onDataReady ne peut pas être null");
            return;
        }

        if (onError == null) {
            logger.error("Le callback onError ne peut pas être null");
            return;
        }

        if (ui == null) {
            logger.error("Impossible de charger les données : contexte UI introuvable.");
            onError.accept(ERROR_UI_NULL);
            return;
        }

        // Affiche un loader temporaire
        Div loaderDiv = VaadinUtils.createLoadingOverlay(loadingText != null ? loadingText : DEFAULT_LOADING_TEXT);
        parent.removeAll();
        parent.add(loaderDiv);

        // Lance la tâche en arrière-plan
        taskExecutor.execute(() -> {
            try {
                T data = dataSupplier.get();
                ui.access(() -> {
                    try {
                        parent.removeAll();
                        onDataReady.accept(data);
                    } catch (Exception e) {
                        logger.error("Erreur lors de l'affichage des données : {}", e.getMessage(), e);
                        onError.accept("Erreur lors de l'affichage des données : " + e.getMessage());
                    }
                });
            } catch (Exception e) {
                logger.error("Erreur lors du chargement asynchrone : {}", e.getMessage(), e);
                ui.access(() -> {
                    try {
                        parent.removeAll();
                        String errorMessage = ExceptionHandler.getUserFriendlyMessage(e);
                        onError.accept(ERROR_LOADING + errorMessage);
                    } catch (Exception uiError) {
                        logger.error("Erreur lors de l'affichage de l'erreur : {}", uiError.getMessage(), uiError);
                    }
                });
            }
        });
    }
}
