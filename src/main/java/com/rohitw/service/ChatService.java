package com.rohitw.service;

import javax.servlet.http.HttpSession;

/**
 * Created by ROHITW on 12/31/2016.
 */
public interface ChatService {
    public String processRequest(String message);

    public String processRequest(HttpSession session, String message);
}
