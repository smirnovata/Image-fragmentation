package ru.tasm.image.fragmentation.fronted.layout;


import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.page.Push;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout implements HasComponents, HasStyle {

    public MainLayout() {
        addTitle();
    }

    private void addTitle() {
        H1 viewTitle = new H1();
        viewTitle.addClassName("main-layout-h1-1");
        viewTitle.setText("Фрагментация изображения");
        viewTitle.setWidth("max-content");
        addToNavbar(false, viewTitle);
    }


}
