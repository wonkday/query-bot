package com.rohitw.controller;

import com.rohitw.init.AppConfigConstants;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
            logger.info("SessionID: " + httpSession.getId());
            if(httpSession.isNew())
            {
                httpSession.removeAttribute(AppConfigConstants.INSTRUCTION_ACCOUNT);
            }
        }
        ModelAndView modelAndView = new ModelAndView("chat");
        modelAndView.addObject("chat", new ChatData());
        return new ModelAndView("chat");
    }

    @RequestMapping(value="/chat/{acct}", method= RequestMethod.GET)
    public ModelAndView chatPageForAccount(HttpServletRequest request,HttpSession httpSession,@PathVariable String acct)
    {
        if(httpSession != null)
        {
            logger.info("SessionID: <" + httpSession.getId() + "> with Acct: <" + acct + "> from User:" + request.getRemoteUser());
            httpSession.setAttribute(AppConfigConstants.INSTRUCTION_ACCOUNT,acct);
        }
        ModelAndView modelAndView = new ModelAndView("chat");
        modelAndView.addObject("chat", new ChatData());
        return new ModelAndView("chat");
    }

    @RequestMapping(value="/chat", method= RequestMethod.POST,produces= MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ChatData chatPage(@ModelAttribute("chatReq")String chatReq, HttpServletRequest request, HttpSession httpSession, BindingResult result)
    {
        if(httpSession != null)
        {
            logger.info("SessionID: " + httpSession.getId() + " from User:" + request.getRemoteUser());
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
