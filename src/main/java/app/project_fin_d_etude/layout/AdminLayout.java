package app.project_fin_d_etude.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.RouterLayout;

/**
 * Layout principal pour l'espace d'administration. Permet d'ajouter un menu ou
 * un header spécifique à l'admin.
 */
public class AdminLayout extends AppLayout implements RouterLayout {

    public AdminLayout() {
        setPrimarySection(Section.DRAWER);
        // TODO : Ajouter ici un header ou un menu spécifique à l'administration si besoin
    }
}
