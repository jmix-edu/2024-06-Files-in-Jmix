package com.company.jmixpmflowbase.view.user;

import com.company.jmixpmflowbase.entity.User;
import com.company.jmixpmflowbase.view.main.MainView;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.image.JmixImage;
import io.jmix.flowui.view.*;

@Route(value = "users", layout = MainView.class)
@ViewController("User.list")
@ViewDescriptor("user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
public class UserListView extends StandardListView<User> {


    @ViewComponent
    private JmixImage<byte[]> avatarImg;

    @Subscribe("usersDataGrid")
    public void onUsersDataGridItemClick(final ItemClickEvent<User> event) {
        byte[] avatarBytes = event.getItem().getAvatar();

        avatarImg.setVisible(avatarBytes != null && avatarBytes.length > 0);
    }
}