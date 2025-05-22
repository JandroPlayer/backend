package com.hotelconnect.backend.logica;

import com.hotelconnect.backend.users.User;

public interface Reservable {
    boolean isPagada();
    void setPagada(boolean pagada);
    double getPreu();
    User getUser();
}

