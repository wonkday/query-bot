package com.rohitw.controller;

import com.rohitw.model.ChatData;
import com.rohitw.service.ChatService;
import com.rohitw.service.ChatServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class LinkController
{
    private Logger logger = Logger.getLogger(LinkController.class);

	@RequestMapping(value="/")
	public ModelAndView mainPage()
    {
		return new ModelAndView("home");
	}
	
	@RequestMapping(value="/index")
	public ModelAndView indexPage()
    {
		return new ModelAndView("home");
	}

    @RequestMapping(value="/login")
    public ModelAndView loginPage()
    {
        return new ModelAndView("home");
    }

    @RequestMapping(value="/chat", method= RequestMethod.GET)
    public ModelAndView chatPage(HttpSession httpSession)
    {
        if(httpSession != null)
        {
            System.out.println("SessionID: " + httpSession.getId());
        }
        ModelAndView modelAndView = new ModelAndView("chat");
        modelAndView.addObject("chat", new ChatData());
        return new ModelAndView("chat");
    }

    @RequestMapping(value="/chat", method= RequestMethod.POST,produces= MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ChatData chatPage(@ModelAttribute("chatReq")String chatReq, HttpSession httpSession, BindingResult result)
    {
        if(httpSession != null)
        {
            logger.info("SessionID: " + httpSession.getId());
        }

        logger.info("request: " + chatReq);

        ChatService chatService = new ChatServiceImpl();
        String resultStr = chatService.processRequest(httpSession,chatReq);

        ChatData chatData = new ChatData();
        chatData.setChatResp(resultStr);

        logger.info("response: " + resultStr);
        return chatData;
    }
}
