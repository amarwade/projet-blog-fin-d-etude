package app.project_fin_d_etude.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;

public class MainLayout extends AppLayout implements RouterLayout {

    public MainLayout() {
        setPrimarySection(Section.NAVBAR);
        addToNavbar(new Header());

        // Layout principal qui contiendra le contenu dynamique et le footer
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);
        mainLayout.setMargin(false);

        // Le contenu dynamique sera injecté automatiquement par Vaadin dans ce layout
        mainLayout.setFlexGrow(1.0);

        // Ajoute le footer à la fin
        Footer footer = new Footer();
        footer.addClassName("footer-sticky");
        mainLayout.add(footer);

        setContent(mainLayout);
    }
}
