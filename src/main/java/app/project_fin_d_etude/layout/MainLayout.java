package app.project_fin_d_etude.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
public class MainLayout extends AppLayout implements RouterLayout {

    public MainLayout() {
        setPrimarySection(Section.NAVBAR);
        addToNavbar(new Header());
        addClassName("main-layout");
        // Layout principal qui contiendra le contenu dynamique et le footer
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);
        mainLayout.setMargin(false);
        // Slot pour du contenu dynamique (exemple)
        // mainLayout.add(new Div()); // Ici, tu peux ajouter dynamiquement du contenu par page
        mainLayout.setFlexGrow(1.0);
        Footer footer = new Footer();
        footer.addClassName("footer-sticky");
        mainLayout.add(footer);
        setContent(mainLayout);
        // Pour i18n, prévoir une méthode getMessage(String key) à l'avenir
    }
}
